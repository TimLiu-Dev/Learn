import Foundation

func fetchPrice(symbol: String) async -> (String, Double) {
    try? await Task.sleep(nanoseconds: 5_000_000)
    let mockPrices: [String: Double] = ["AAPL": 220.5, "GOOG": 175.0, "NVDA": 125.0]
    return (symbol, mockPrices[symbol] ?? 100.0)
}

func fetchBatchPrices(symbols: [String]) async -> [String: Double] {
    // TODO: Use withTaskGroup(of: (String, Double).self)
    // 1. For each symbol in symbols, group.addTask { await fetchPrice(symbol: symbol) }
    // 2. Iterate for await (sym, price) in group and aggregate into dictionary
    // 3. Return dictionary
    fatalError("TODO: Implement fetchBatchPrices")
}

func runTests() async {
    print("🧪 Running Lesson 2.2 Verification Tests...")
    let symbols = ["AAPL", "GOOG", "NVDA"]
    let prices = await fetchBatchPrices(symbols: symbols)
    assert(prices.count == 3, "Test 1A Failed: Incomplete prices")
    assert(prices["AAPL"] == 220.5, "Test 1B Failed")
    print("  ✅ Challenge Passed: Structured concurrency TaskGroup aggregation verified")
}

await runTests()