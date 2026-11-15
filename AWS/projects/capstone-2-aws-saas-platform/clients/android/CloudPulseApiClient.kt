package com.cloudpulse.client

import android.content.ContentResolver
import android.net.Uri
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.source
import org.json.JSONObject
import java.io.IOException
import java.io.InterruptedIOException
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * End-to-end Android client integration for the CloudPulse AWS SaaS platform.
 *
 * Aligns mobile networking with CloudPulse's AWS ingress stack:
 * - Cognito OIDC PKCE JWT (`Authorization: Bearer <jwt>`) validated by API Gateway HTTP API.
 * - `Idempotency-Key` UUIDs for safe WorkManager / mobile retries across ECS & Step Functions.
 * - W3C `traceparent` propagation for end-to-end distributed traces (Android -> API GW -> ALB -> ECS -> X-Ray/ADOT).
 * - Two-step direct-to-S3 Pre-Signed PUT upload so large binaries never traverse API GW, ALB, or ECS Fargate.
 * - Bounded Full Jitter exponential backoff for `429 Retry-After` and `503` responses.
 */
fun interface CognitoTokenProvider {
    /** Returns a valid Cognito PKCE access/ID JWT (e.g. managed by AppAuth / Credential Manager). */
    fun getAccessToken(): String
}

data class PresignedUploadSession(
    val uploadUrl: String,
    val objectKey: String,
    val expiresInSeconds: Long,
    val requiredHeaders: Map<String, String> = emptyMap(),
)

data class TelemetryEvent(
    val tenantId: String,
    val deviceId: String,
    val metricName: String,
    val value: Double,
    val attachmentKey: String? = null,
)

class ApiException(
    val statusCode: Int,
    val responseBody: String,
    message: String = "CloudPulse HTTP $statusCode",
) : IOException("$message: $responseBody")

/**
 * Generates W3C Trace Context `traceparent` headers (`00-<trace-id>-<span-id>-01`).
 */
object W3cTraceContext {
    private val random = SecureRandom()

    fun newTraceparent(sampled: Boolean = true): String {
        val traceId = randomNonZeroHex(16) // 16 bytes = 32 lowercase hex chars
        val spanId = randomNonZeroHex(8)   // 8 bytes  = 16 lowercase hex chars
        val flags = if (sampled) "01" else "00"
        return "00-$traceId-$spanId-$flags"
    }

    private fun randomNonZeroHex(byteCount: Int): String {
        val bytes = ByteArray(byteCount)
        do {
            random.nextBytes(bytes)
        } while (bytes.all { it == 0.toByte() })
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }
}

/**
 * Attaches Cognito PKCE Bearer auth, W3C `traceparent`, and an `Idempotency-Key` UUID
 * for mutating requests before retries occur so every retry reuses the exact same key.
 */
class CloudPulseHeadersInterceptor(
    private val tokenProvider: CognitoTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Authorization", "Bearer ${tokenProvider.getAccessToken()}")

        if (original.header(HEADER_TRACEPARENT) == null) {
            builder.header(HEADER_TRACEPARENT, W3cTraceContext.newTraceparent())
        }

        if (original.method in MUTATING_METHODS && original.header(HEADER_IDEMPOTENCY_KEY) == null) {
            builder.header(HEADER_IDEMPOTENCY_KEY, UUID.randomUUID().toString())
        }

        return chain.proceed(builder.build())
    }

    companion object {
        const val HEADER_TRACEPARENT = "traceparent"
        const val HEADER_IDEMPOTENCY_KEY = "Idempotency-Key"
        private val MUTATING_METHODS = setOf("POST", "PUT", "PATCH", "DELETE")
    }
}

/**
 * Handles HTTP `429` (API Gateway / WAFv2 rate limit) and `503` (transient ALB/ECS/S3 overload)
 * using server-provided `Retry-After` headers when present, or bounded Full Jitter exponential
 * backoff (`[0, min(maxBackoffMs, baseDelayMs * 2^attempt)]`) otherwise.
 */
class RetryAfterBackoffInterceptor(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 250L,
    private val maxBackoffMs: Long = 16_000L,
    private val allowStatelessPutRetry: Boolean = false,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var attempt = 0

        while (response.code in RETRYABLE_STATUS_CODES &&
            attempt < maxRetries &&
            isSafeToRetry(request)
        ) {
            val retryAfterMs = parseRetryAfterMs(response.header("Retry-After"))
            response.close() // Release pooled socket before sleeping

            val delayMs = retryAfterMs ?: fullJitterBackoffMs(attempt)
            sleepSafely(delayMs)

            attempt++
            response = chain.proceed(request)
        }

        return response
    }

    private fun isSafeToRetry(request: Request): Boolean {
        if (request.method in IDEMPOTENT_METHODS) return true
        if (request.header(CloudPulseHeadersInterceptor.HEADER_IDEMPOTENCY_KEY) != null) return true
        return allowStatelessPutRetry && request.method == "PUT"
    }

    private fun fullJitterBackoffMs(attempt: Int): Long {
        val shift = attempt.coerceAtMost(10)
        val capMs = minOf(maxBackoffMs, baseDelayMs * (1L shl shift))
        return Random.nextLong(0L, capMs + 1L)
    }

    private fun parseRetryAfterMs(headerValue: String?): Long? {
        if (headerValue.isNullOrBlank()) return null
        val trimmed = headerValue.trim()

        // 1. Delay-seconds form: e.g. "5"
        trimmed.toLongOrNull()?.let { seconds ->
            return (seconds * 1_000L).coerceIn(0L, maxBackoffMs)
        }

        // 2. HTTP-date form (RFC 1123): e.g. "Wed, 21 Oct 2026 07:28:00 GMT"
        return runCatching {
            val target = ZonedDateTime.parse(trimmed, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()
            Duration.between(Instant.now(), target).toMillis().coerceIn(0L, maxBackoffMs)
        }.getOrNull()
    }

    private fun sleepSafely(delayMs: Long) {
        if (delayMs <= 0L) return
        try {
            Thread.sleep(delayMs)
        } catch (interrupted: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException("Retry backoff interrupted").apply { initCause(interrupted) }
        }
    }

    companion object {
        private val RETRYABLE_STATUS_CODES = setOf(429, 503)
        private val IDEMPOTENT_METHODS = setOf("GET", "HEAD", "OPTIONS")
    }
}

/**
 * Streams a `ContentResolver` `Uri` straight into OkHttp's `BufferedSink` without loading
 * the entire file into the Android JVM heap.
 */
class UriStreamingRequestBody(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
    private val mimeType: String,
    private val exactByteLength: Long,
) : RequestBody() {

    // Exact MediaType without appended charset so SigV4 signed Content-Type matches S3's signature.
    override fun contentType(): MediaType = mimeType.toMediaType()

    override fun contentLength(): Long = exactByteLength

    override fun writeTo(sink: BufferedSink) {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to open input stream for $uri")
        inputStream.source().use { source ->
            sink.writeAll(source)
        }
    }
}

/**
 * Process-wide singleton HTTP pool aligned with CloudPulse's AWS ingress timeouts:
 * - ConnectionPool keep-alive = 55s (< ALB default 60s idle timeout) to prevent 502 close races.
 * - API Gateway HTTP API callTimeout = 28s (< API GW 30s hard integration limit).
 * - S3 upload client shares the same ConnectionPool & Dispatcher via `newBuilder()`, while
 *   omitting the Cognito `Authorization` header (which would conflict with SigV4 query auth)
 *   and relaxing `writeTimeout`/`callTimeout` for large binary streaming.
 */
object CloudPulseHttpClients {
    val sharedPool = ConnectionPool(
        maxIdleConnections = 5,
        keepAliveDuration = 55,
        TimeUnit.SECONDS,
    )

    val baseClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectionPool(sharedPool)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(28, TimeUnit.SECONDS)
            .build()
    }

    fun newApiClient(tokenProvider: CognitoTokenProvider): OkHttpClient =
        baseClient.newBuilder()
            .addInterceptor(CloudPulseHeadersInterceptor(tokenProvider))
            .addInterceptor(RetryAfterBackoffInterceptor())
            .build()

    fun newS3UploadClient(): OkHttpClient =
        baseClient.newBuilder()
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)
            .addInterceptor(RetryAfterBackoffInterceptor(allowStatelessPutRetry = true))
            .build()
}

/**
 * CloudPulse Android API client demonstrating:
 * 1. Authenticated, idempotent, traced API Gateway calls (`ingestTelemetry`, `requestUploadUrl`).
 * 2. Two-step direct-to-S3 pre-signed PUT upload (`requestUploadUrl` -> `uploadFileToS3PresignedUrl`).
 */
class CloudPulseApiClient(
    apiBaseUrl: String,
    tokenProvider: CognitoTokenProvider,
    private val apiHttpClient: OkHttpClient = CloudPulseHttpClients.newApiClient(tokenProvider),
    private val s3HttpClient: OkHttpClient = CloudPulseHttpClients.newS3UploadClient(),
) {
    private val normalizedBaseUrl = apiBaseUrl.trimEnd('/')

    /**
     * Step 1 of direct-to-S3 upload: asks the CloudPulse API (via API GW -> ALB -> ECS Fargate)
     * to mint a short-lived SigV4 pre-signed PUT URL scoped to the caller's `tenant_id`.
     */
    @Throws(IOException::class)
    fun requestUploadUrl(
        fileName: String,
        contentType: String,
        contentLengthBytes: Long,
        idempotencyKey: String = UUID.randomUUID().toString(),
        traceparent: String = W3cTraceContext.newTraceparent(),
    ): PresignedUploadSession {
        require(contentLengthBytes > 0L) { "contentLengthBytes must be positive" }

        val payload = JSONObject()
            .put("fileName", fileName)
            .put("contentType", contentType)
            .put("contentLengthBytes", contentLengthBytes)
            .toString()

        val request = Request.Builder()
            .url("$normalizedBaseUrl/v1/uploads/presign")
            .header(CloudPulseHeadersInterceptor.HEADER_IDEMPOTENCY_KEY, idempotencyKey)
            .header(CloudPulseHeadersInterceptor.HEADER_TRACEPARENT, traceparent)
            .post(payload.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        apiHttpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw ApiException(response.code, body, "Failed to obtain pre-signed S3 upload URL")
            }

            val json = JSONObject(body)
            val headersJson = json.optJSONObject("requiredHeaders")
            val requiredHeaders = buildMap {
                headersJson?.keys()?.forEach { key ->
                    put(key, headersJson.getString(key))
                }
            }

            return PresignedUploadSession(
                uploadUrl = json.getString("uploadUrl"),
                objectKey = json.getString("objectKey"),
                expiresInSeconds = json.optLong("expiresInSeconds", 300L),
                requiredHeaders = requiredHeaders,
            )
        }
    }

    /**
     * Step 2 of direct-to-S3 upload: streams file bytes directly from Android `ContentResolver`
     * to S3 via the pre-signed PUT URL, bypassing API Gateway, ALB, and ECS Fargate completely.
     */
    @Throws(IOException::class)
    fun uploadFileToS3PresignedUrl(
        session: PresignedUploadSession,
        contentResolver: ContentResolver,
        fileUri: Uri,
        contentType: String,
        contentLengthBytes: Long,
    ) {
        val streamingBody = UriStreamingRequestBody(
            contentResolver = contentResolver,
            uri = fileUri,
            mimeType = contentType,
            exactByteLength = contentLengthBytes,
        )

        val requestBuilder = Request.Builder()
            .url(session.uploadUrl)
            .put(streamingBody)

        // Apply backend-enforced SigV4 signed headers (e.g., x-amz-server-side-encryption);
        // Content-Type and Content-Length are emitted directly by UriStreamingRequestBody.
        session.requiredHeaders.forEach { (name, value) ->
            if (!name.equals("Content-Type", ignoreCase = true) &&
                !name.equals("Content-Length", ignoreCase = true)
            ) {
                requestBuilder.header(name, value)
            }
        }

        s3HttpClient.newCall(requestBuilder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                throw ApiException(response.code, errorBody, "Direct S3 pre-signed PUT failed")
            }
        }
    }

    /**
     * Convenience wrapper running Step 1 (`requestUploadUrl`) followed by Step 2
     * (`uploadFileToS3PresignedUrl`) and returning the server-assigned S3 object key.
     */
    @Throws(IOException::class)
    fun uploadAttachment(
        contentResolver: ContentResolver,
        fileUri: Uri,
        fileName: String,
        contentType: String,
        contentLengthBytes: Long,
        idempotencyKey: String = UUID.randomUUID().toString(),
    ): String {
        val session = requestUploadUrl(
            fileName = fileName,
            contentType = contentType,
            contentLengthBytes = contentLengthBytes,
            idempotencyKey = idempotencyKey,
        )
        uploadFileToS3PresignedUrl(
            session = session,
            contentResolver = contentResolver,
            fileUri = fileUri,
            contentType = contentType,
            contentLengthBytes = contentLengthBytes,
        )
        return session.objectKey
    }

    /**
     * Sends a telemetry event to `/v1/telemetry` with Cognito JWT, W3C `traceparent`,
     * and caller-controlled `Idempotency-Key` (safe for WorkManager retries).
     */
    @Throws(IOException::class)
    fun ingestTelemetry(
        event: TelemetryEvent,
        idempotencyKey: String = UUID.randomUUID().toString(),
        traceparent: String = W3cTraceContext.newTraceparent(),
    ) {
        val payload = JSONObject()
            .put("tenantId", event.tenantId)
            .put("deviceId", event.deviceId)
            .put("metricName", event.metricName)
            .put("value", event.value)
            .apply {
                event.attachmentKey?.let { put("attachmentKey", it) }
            }
            .toString()

        val request = Request.Builder()
            .url("$normalizedBaseUrl/v1/telemetry")
            .header(CloudPulseHeadersInterceptor.HEADER_IDEMPOTENCY_KEY, idempotencyKey)
            .header(CloudPulseHeadersInterceptor.HEADER_TRACEPARENT, traceparent)
            .post(payload.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        apiHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                throw ApiException(response.code, errorBody, "Telemetry ingestion failed")
            }
        }
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
