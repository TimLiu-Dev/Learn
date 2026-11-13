# ==============================================================================
# Build Stage: Hardened, Multi-Stage, PGO-Enabled Go Compiler Container
# ==============================================================================
FROM golang:1.27-alpine AS builder

WORKDIR /build

RUN apk add --no-cache ca-certificates git

COPY go.mod ./
COPY . .

# Compile static binary with optimizations:
# - CGO_ENABLED=0: Fully static binary without dynamic libc dependencies
# - -ldflags="-s -w": Strip debugging symbols & DWARF tables
# - -pgo=auto: Uses default.pgo if present in cmd/server/
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -pgo=auto \
    -ldflags="-s -w -X main.version=2027.1.0" \
    -trimpath \
    -o /build/ledger-service ./cmd/server

# ==============================================================================
# Final Stage: Google Distroless Static (Minimal Attack Surface, <15MB)
# ==============================================================================
FROM gcr.io/distroless/static-debian12:nonroot

USER 65532:65532

WORKDIR /app
COPY --from=builder /build/ledger-service /app/ledger-service
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

EXPOSE 8080 9090

ENTRYPOINT ["/app/ledger-service"]
