export type HitlProposal = {
  approvalId: string;
  toolName: string;
  orderId: string;
  usd: number;
};

export type WorkbenchState = {
  status: "idle" | "streaming" | "awaiting_hitl" | "done" | "error";
  streamingText: string;
  citations: string[];
  pendingHitl: HitlProposal | null;
  traceId: string | null;
  errorMessage: string | null;
};

export type SseEvent =
  | { type: "trace"; traceId: string }
  | { type: "delta"; text: string }
  | { type: "citation"; chunkId: string }
  | { type: "hitl_required"; proposal: HitlProposal }
  | { type: "done" }
  | { type: "error"; message: string };

export const INITIAL_WORKBENCH_STATE: WorkbenchState = {
  status: "idle",
  streamingText: "",
  citations: [],
  pendingHitl: null,
  traceId: null,
  errorMessage: null,
};

/** Strip remote Markdown image beacons (`![...](https://...)`) to block browser GET exfiltration. */
export function stripExternalMarkdownImages(markdown: string): string {
  return markdown.replace(/!\[([^\]]*)\]\(https?:\/\/[^)]+\)/gi, "[BLOCKED_EXTERNAL_IMAGE:$1]");
}

/** Pure unidirectional SSE reducer mirroring Jetpack Compose StateFlow semantics. */
export function reduceWorkbenchEvent(state: WorkbenchState, evt: SseEvent): WorkbenchState {
  switch (evt.type) {
    case "trace":
      return { ...state, status: "streaming", traceId: evt.traceId, errorMessage: null };
    case "delta":
      return {
        ...state,
        status: "streaming",
        streamingText: stripExternalMarkdownImages(state.streamingText + evt.text),
      };
    case "citation":
      return state.citations.includes(evt.chunkId)
        ? state
        : { ...state, citations: [...state.citations, evt.chunkId] };
    case "hitl_required":
      return { ...state, status: "awaiting_hitl", pendingHitl: evt.proposal };
    case "done":
      return { ...state, status: state.pendingHitl ? "awaiting_hitl" : "done" };
    case "error":
      return { ...state, status: "error", errorMessage: evt.message };
  }
}
