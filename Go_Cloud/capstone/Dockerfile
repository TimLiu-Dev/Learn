# ==============================================================================
# Build Stage: Hardened, Multi-Stage, PGO-Enabled Go Compiler Container
# ==============================================================================
FROM golang:1.24-alpine AS builder

WORKDIR /build

# Install security certificates & build essentials
RUN apk add --no-cache ca-certificates git

# Cache Go modules layer
COPY go.mod ./
# RUN go mod download (if remote modules exist)

# Copy source tree and PGO profile (if present in repo)
COPY . .

# Compile static binary with optimizations:
# - CGO_ENABLED=0: Fully static binary without dynamic libc dependencies
# - -ldflags="-s -w": Strip debugging symbols & DWARF tables (reduces size by 40%)
# - -pgo=auto: Profile-Guided Optimization enabled
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -pgo=auto \
    -ldflags="-s -w -X main.version=2027.1.0" \
    -trimpath \
    -o /build/ledger-service .

# ==============================================================================
# Final Stage: Google Distroless Scratch (Minimal Attack Surface, <15MB)
# ==============================================================================
FROM gcr.io/distroless/static-debian12:nonroot

# Run as non-root user (UID 65532) for zero-trust Kubernetes security context
USER 65532:65532

WORKDIR /app
COPY --from=builder /build/ledger-service /app/ledger-service
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

# Expose gRPC/ConnectRPC and Prometheus metrics ports
EXPOSE 8080 9090

ENTRYPOINT ["/app/ledger-service"]
