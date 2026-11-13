package interceptors

import (
	"net/http"
)

type Limiter interface {
	Allow(procedure string) bool
}

// RateLimitingMiddleware mirrors ConnectRPC's HTTP-native unary interceptor pattern:
// if !limiter.Allow(r.URL.Path), reject with 429 Too Many Requests and Connect-compatible JSON error.
func RateLimitingMiddleware(limiter Limiter, next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		// TODO: Check limiter.Allow(r.URL.Path). On denial, set Content-Type application/json,
		// return 429 with {"code":"resource_exhausted","message":"rate limit exceeded"}.
		next.ServeHTTP(w, r)
	})
}
