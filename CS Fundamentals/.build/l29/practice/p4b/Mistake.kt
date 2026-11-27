import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

// A callback API you can't change. Callbacks arrive later, on another thread.
fun interface Lookup { fun cancel() }
fun interface PlaceListener { fun onPlace(name: String) }

interface Geocoder {
    // Calls exactly one of onResult or onError. cancel() stops the lookup.
    fun lookup(address: String, onResult: (Double, Double) -> Unit, onError: (Exception) -> Unit): Lookup
    // Calls the listener for every new place until it is removed.
    fun addListener(listener: PlaceListener)
    fun removeListener(listener: PlaceListener)
}

// Works until someone cancels: the lookup keeps running for nobody.
suspend fun Geocoder.find(address: String): Pair<Double, Double> =
    suspendCancellableCoroutine { cont ->
        lookup(address,
            onResult = { lat, lon -> cont.resume(lat to lon) },
            onError = { e -> cont.resumeWithException(e) })
    }

fun Geocoder.places(): Flow<String> = callbackFlow {
    val listener = PlaceListener { trySend(it) }
    addListener(listener)
    awaitClose { removeListener(listener) }
}
