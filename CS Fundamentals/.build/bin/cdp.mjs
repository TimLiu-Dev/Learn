// Drive headless Chrome over the DevTools protocol (Node 22+, no packages).
// usage: node cdp.mjs <url> <steps.js> [shot.png] [width]
// steps.js is the body of an async function run in the page; its return value is printed as JSON.
import { spawn } from "node:child_process";
import { readFileSync, writeFileSync, mkdtempSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

const [url, stepsFile, shot, width = "900"] = process.argv.slice(2);
const CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
const port = 9300 + Math.floor(Math.random() * 500);
const chrome = spawn(CHROME, ["--headless=new", "--disable-gpu", `--remote-debugging-port=${port}`,
  `--user-data-dir=${mkdtempSync(join(tmpdir(), "cdp-"))}`, `--window-size=${width},1200`, "about:blank"], { stdio: "ignore" });
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
let target;
for (let i = 0; i < 50 && !target; i++) {
  await sleep(200);
  try { target = (await (await fetch(`http://127.0.0.1:${port}/json`)).json()).find((t) => t.type === "page"); } catch {}
}
const ws = new WebSocket(target.webSocketDebuggerUrl);
await new Promise((r) => ws.addEventListener("open", r));
let id = 0; const waiting = new Map(); const logs = [];
ws.addEventListener("message", (e) => {
  const m = JSON.parse(e.data);
  if (m.id && waiting.has(m.id)) { waiting.get(m.id)(m); waiting.delete(m.id); }
  if (m.method === "Runtime.exceptionThrown") logs.push("EXC " + (m.params.exceptionDetails.exception?.description || m.params.exceptionDetails.text));
  if (m.method === "Runtime.consoleAPICalled" && m.params.type === "error") logs.push("console.error " + m.params.args.map((a) => a.value ?? a.description).join(" "));
});
const send = (method, params = {}) => new Promise((r) => { const i = ++id; waiting.set(i, r); ws.send(JSON.stringify({ id: i, method, params })); });
await send("Runtime.enable"); await send("Page.enable");
await send("Page.navigate", { url });
await sleep(2500);
const body = readFileSync(stepsFile, "utf8");
const res = await send("Runtime.evaluate", { expression: `(async () => { const sleep = (ms) => new Promise(r => setTimeout(r, ms)); ${body} })()`, awaitPromise: true, returnByValue: true, timeout: 120000 });
console.log(JSON.stringify(res.result?.result?.value ?? res.result, null, 1));
if (logs.length) console.log("PAGE ERRORS:", logs);
if (shot) {
  const lm = await send("Page.getLayoutMetrics");
  const clipTo = res.result?.result?.value?.clip;
  const params = { format: "png", captureBeyondViewport: true };
  if (clipTo) params.clip = { ...clipTo, scale: 1 };
  const s = await send("Page.captureScreenshot", params);
  writeFileSync(shot, Buffer.from(s.result.data, "base64"));
}
chrome.kill();
process.exit(0);
