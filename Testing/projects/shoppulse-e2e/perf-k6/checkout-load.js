import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 25 },
    { duration: "1m", target: 50 },
    { duration: "15s", target: 0 }
  ],
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<300", "p(99)<600"]
  }
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const res = http.get(`${BASE_URL}/api/catalog`);
  check(res, {
    "status is 200": (r) => r.status === 200,
    "body is non-empty": (r) => r.body && r.body.length > 2
  });
  sleep(0.5);
}
