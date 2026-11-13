package sse

import (
	"net/http"
)

// StreamHandler streams tokens as Server-Sent Events using Go's http.NewResponseController
// so it works even when wrapped by logging/metrics middleware.
func StreamHandler(w http.ResponseWriter, r *http.Request, tokenStream <-chan string) {
	// TODO:
	// 1. Set headers: Content-Type: text/event-stream, Cache-Control: no-cache
	// 2. rc := http.NewResponseController(w); rc.Flush()
	// 3. Select on <-r.Context().Done() and <-tokenStream, formatting "data: %s\n\n" and flushing each token.
	//    When tokenStream closes, emit "data: [DONE]\n\n", flush, and return.
}
