import Foundation

enum NetworkError: Error, Equatable {
    case invalidResponse
    case httpStatus(Int)
}

func validateHTTPResponse(data: Data, response: URLResponse) throws -> Data {
    // TODO: 1. Guard cast response as? HTTPURLResponse else throw NetworkError.invalidResponse
    // TODO: 2. Check if (200...299).contains(httpResponse.statusCode) -> return data
    // TODO: 3. Else throw NetworkError.httpStatus(httpResponse.statusCode)
    fatalError("TODO: Implement validateHTTPResponse")
}

func runTests() {
    print("🧪 Running Lesson 2.8 Verification Tests...")
    let dummyURL = URL(string: "https://api.example.com/data")!
    let sampleData = "{\"status\":\"ok\"}".data(using: .utf8)!

    // Test 1: 200 OK
    let resp200 = HTTPURLResponse(url: dummyURL, statusCode: 200, httpVersion: nil, headerFields: nil)!
    do {
        let valid = try validateHTTPResponse(data: sampleData, response: resp200)
        assert(valid == sampleData, "Test 1 Failed")
        print("  ✅ Challenge 1 Passed: 200 OK validated")
    } catch { fatalError() }

    // Test 2: 404 Error
    let resp404 = HTTPURLResponse(url: dummyURL, statusCode: 404, httpVersion: nil, headerFields: nil)!
    do {
        _ = try validateHTTPResponse(data: sampleData, response: resp404)
        fatalError("Test 2 Failed: Should have thrown 404 error")
    } catch NetworkError.httpStatus(let code) {
        assert(code == 404, "Test 2 Failed")
        print("  ✅ Challenge 2 Passed: 404 Status error correctly thrown")
    } catch { fatalError() }
}
runTests()