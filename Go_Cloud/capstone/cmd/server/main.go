package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"runtime"
	"sync"
	"syscall"
	"time"

	"gocloud/capstone/domain"
	"gocloud/capstone/outbox"
	"gocloud/capstone/pool"
)

var version = "2027.1.0"

type transferRequest struct {
	IdempotencyKey string `json:"idempotency_key"`
	FromAccountID  int64  `json:"from_account_id"`
	ToAccountID    int64  `json:"to_account_id"`
	AmountCents    int64  `json:"amount_cents"`
}

func main() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelInfo}))
	slog.SetDefault(logger)

	poolCfg := pool.DefaultProductionSettings()
	if err := pool.ValidatePoolConfig(poolCfg, 2.0); err != nil {
		logger.Error("invalid pgx pool configuration", slog.String("error", err.Error()))
		os.Exit(1)
	}

	engine := domain.NewLedgerEngine()
	engine.RegisterAccount(1001, 1_000_000)
	engine.RegisterAccount(1002, 500_000)

	publisher := &outbox.MockKafkaPublisher{}
	processor := outbox.NewOutboxProcessor(publisher, 100, 100*time.Millisecond)

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	var wg sync.WaitGroup
	wg.Add(1)
	go func() {
		defer wg.Done()
		_ = processor.Run(ctx)
	}()

	apiMux := http.NewServeMux()
	apiMux.HandleFunc("POST /v1/ledger/transfer", func(w http.ResponseWriter, r *http.Request) {
		var req transferRequest
		if err := json.NewDecoder(http.MaxBytesReader(w, r.Body, 1<<20)).Decode(&req); err != nil {
			http.Error(w, "invalid JSON payload", http.StatusBadRequest)
			return
		}
		tx := domain.Transaction{
			IdempotencyKey: req.IdempotencyKey,
			CreatedAt:      time.Now(),
			Entries: []domain.LedgerEntry{
				{AccountID: req.FromAccountID, Amount: req.AmountCents, Type: domain.Debit},
				{AccountID: req.ToAccountID, Amount: req.AmountCents, Type: domain.Credit},
			},
		}
		if err := engine.ExecuteTransaction(tx); err != nil {
			status := http.StatusUnprocessableEntity
			if errors.Is(err, domain.ErrDuplicateIdempotency) {
				status = http.StatusConflict
			}
			http.Error(w, err.Error(), status)
			return
		}
		payload, _ := json.Marshal(req)
		processor.Enqueue(fmt.Sprintf("tx-%s", req.IdempotencyKey), "transfer.completed", payload)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		_ = json.NewEncoder(w).Encode(map[string]any{"status": "committed"})
	})

	metricsMux := http.NewServeMux()
	metricsMux.HandleFunc("GET /healthz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte("ok"))
	})
	metricsMux.HandleFunc("GET /readyz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte("ready"))
	})
	metricsMux.HandleFunc("GET /metrics", func(w http.ResponseWriter, r *http.Request) {
		pub, fail := processor.Stats()
		w.Header().Set("Content-Type", "text/plain; version=0.0.4")
		_, _ = fmt.Fprintf(w, "cloudscale_transactions_total %d\ncloudscale_outbox_published_total %d\ncloudscale_outbox_failed_total %d\n",
			engine.TotalTransactionsProcessed(), pub, fail)
	})

	apiSrv := &http.Server{Addr: ":8080", Handler: apiMux, ReadHeaderTimeout: 5 * time.Second}
	metricsSrv := &http.Server{Addr: ":9090", Handler: metricsMux, ReadHeaderTimeout: 5 * time.Second}

	go func() {
		if err := apiSrv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			logger.Error("api server failed", slog.String("error", err.Error()))
		}
	}()
	go func() {
		if err := metricsSrv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			logger.Error("metrics server failed", slog.String("error", err.Error()))
		}
	}()

	logger.Info("CloudScale Ledger booted",
		slog.String("version", version),
		slog.Int("gomaxprocs", runtime.GOMAXPROCS(0)),
	)

	<-ctx.Done()
	logger.Info("Shutting down gracefully")
	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	_ = apiSrv.Shutdown(shutdownCtx)
	_ = metricsSrv.Shutdown(shutdownCtx)
	wg.Wait()
}
