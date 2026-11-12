import React, { useReducer, useState } from "react";
import { INITIAL_WORKBENCH_STATE, reduceWorkbenchEvent } from "./reducer";

export function App() {
  const [question, setQuestion] = useState("");
  const [state, dispatch] = useReducer(reduceWorkbenchEvent, INITIAL_WORKBENCH_STATE);

  async function runQuery(e: React.FormEvent) {
    e.preventDefault();
    if (!question.trim()) return;
    dispatch({ type: "trace", traceId: "trace-local-preview" });
    dispatch({ type: "delta", text: "Grounded response ready once connected to FastAPI /v1/copilot/query SSE." });
    dispatch({ type: "done" });
  }

  return (
    <main style={{ maxWidth: 880, margin: "2rem auto", fontFamily: "system-ui, sans-serif" }}>
      <h1>AegisOps Operator Workbench</h1>
      <form onSubmit={runQuery} style={{ display: "flex", gap: "0.5rem" }}>
        <input
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Ask about SLA exceptions, SKU errors, or order drift..."
          style={{ flex: 1, padding: "0.6rem" }}
        />
        <button type="submit">Query Copilot</button>
      </form>

      <section style={{ marginTop: "1rem", padding: "1rem", border: "1px solid #ddd", borderRadius: 8 }}>
        <div>Status: <strong>{state.status}</strong> {state.traceId ? `(trace: ${state.traceId})` : ""}</div>
        <p>{state.streamingText || "Submit a query to inspect streaming citations and HITL write approvals."}</p>
        {state.citations.length > 0 && (
          <div>Citations: {state.citations.join(", ")}</div>
        )}
        {state.pendingHitl && (
          <div style={{ marginTop: "0.75rem", padding: "0.75rem", background: "#fffbeb" }}>
            Pending HITL Approval: <code>{state.pendingHitl.toolName}</code> (${state.pendingHitl.usd})
          </div>
        )}
      </section>
    </main>
  );
}
export default App;
