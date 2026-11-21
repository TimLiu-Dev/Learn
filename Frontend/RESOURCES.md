# Web Frontend Engineering Resources

## Knowledge

- [Roadmap: Frontend Developer Roadmap (`roadmap.sh/frontend`)](https://roadmap.sh/frontend)
  High-level industry curriculum map covering Internet, HTML, CSS, JavaScript, VCS, TypeScript, Frameworks, Testing, Security, SSR, and Performance. Use for: checking end-to-end topic coverage.
- [Roadmap: JavaScript Developer Roadmap (`roadmap.sh/javascript`)](https://roadmap.sh/javascript)
  Deep JS runtime curriculum covering closures, prototypes, Event Loop, memory management/GC, iterators/generators, and ESM. Use for: Phase 2 and Lesson 21 memory profiling depth.
- [Roadmap: UX Design Roadmap (`roadmap.sh/ux-design`)](https://roadmap.sh/ux-design)
  Filtered for engineering-relevant UX craft: cognitive load, progressive disclosure, clear feedback loops, Figma handoff, and A/B usability measurement. Use for: Lessons 03, 12, 13, 14, and 23.
- [Roadmap: TypeScript Roadmap (`roadmap.sh/typescript`)](https://roadmap.sh/typescript)
  Strict compiler options (`noUncheckedIndexedAccess`, `exactOptionalPropertyTypes`), narrowing, `as const`/`satisfies`, utility/recursive types, and `.d.ts` module augmentation. Use for: Lessons 08–09.
- [Roadmap: React Roadmap (`roadmap.sh/react`)](https://roadmap.sh/react) & [React Official Documentation](https://react.dev/learn)
  React 19 Fiber/hooks, Suspense/Error Boundaries, TanStack Query/Zustand, TanStack/React Router, React Hook Form + Zod, and Next.js App Router/RSC. Use for: Primary Track R (Lessons 11R–16R).
- [Roadmap: Angular Roadmap (`roadmap.sh/angular`)](https://roadmap.sh/angular) & [Angular Official Documentation](https://angular.dev)
  Modern standalone components, Signals, Zoneless change detection, hierarchical DI (`inject()`), RxJS interop (`toSignal`), `@defer`, Angular SSR, and typed reactive forms. Use for: Enterprise Track A (Lessons 11A–16A).
- [Roadmap: HTML (`roadmap.sh/html`)](https://roadmap.sh/html), [CSS (`roadmap.sh/css`)](https://roadmap.sh/css) & [Next.js (`roadmap.sh/nextjs`)](https://roadmap.sh/nextjs)
  Detailed checklists for semantic HTML (`<dialog>`, `popover`, `<picture>`/`srcset`), CSS stacking contexts/logical properties/`@layer`, and Next.js App Router caching/Server Actions/`instrumentation.ts`. Use for: Lessons 02, 03, and 15R.
- [Roadmap: Node.js Roadmap (`roadmap.sh/nodejs`)](https://roadmap.sh/nodejs) & [Node.js Official Documentation](https://nodejs.org/docs/latest/api/)
  Authoritative backend path covering `libuv` Event Loop, core modules, Streams, `worker_threads`, Fastify/Hono/NestJS, PostgreSQL + Drizzle/Prisma, security, testing, and Docker ops. Use for: Part 2 Backend (Lessons B01–B06).
- [MDN Web Docs: Critical Rendering Path](https://developer.mozilla.org/en-US/docs/Web/Performance/Critical_rendering_path)
  Primary web platform reference on DOM, CSSOM, Render Tree, Layout, Paint, and parser-blocking resources. Use for: browser rendering internals.
- [web.dev: Populating the page — how browsers work](https://web.dev/articles/howbrowserswork) & [web.dev: Render-blocking resources](https://web.dev/learn/performance/understanding-the-critical-path)
  Google Chrome team guidance on browser parsing, preload scanner, critical path optimization, and Core Web Vitals (`LCP`, `INP`, `CLS`). Use for: performance engineering and DevTools profiling.
- [MDN Web Docs: HTTP Caching](https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching) & [RFC 9111: HTTP Caching](https://httpwg.org/specs/rfc9111.html)
  Authoritative specification and practical guide for `Cache-Control`, `ETag`, `no-cache` vs `no-store`, and `stale-while-revalidate`. Use for: Lesson 01 and Lesson 07 caching semantics.
- [MDN Web Docs: `<script>` element (`async` & `defer`)](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script)
  Exact behavioral specification for parser-blocking scripts vs `async` vs `defer` vs `type="module"`. Use for: script loading order and `DOMContentLoaded` timing.
- [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)
  Official reference for structural typing, narrowing, generics, and type-level programming. Use for: Phase 3 lessons.
- [React Official Documentation](https://react.dev/learn)
  Authoritative guide to React rendering mental models, hooks, effects, and Server Components. Use for: Phase 4 & 5 lessons.
- [OWASP Cross-Site Scripting Prevention & Web Security Cheat Sheets](https://cheatsheetseries.owasp.org/)
  Industry-standard security reference for XSS, CSRF, CSP, and authentication flows. Use for: Phase 6 security lessons.

## Wisdom (Communities)

- [Web Performance Working Group / web.dev Case Studies](https://web.dev/case-studies)
  Real-world engineering writeups diagnosing production `LCP`, `INP`, and `CLS` regressions. Use for: comparing synthetic lab theory with production telemetry.
- [Frontend Masters / React & TypeScript RFC Discussions (GitHub)](https://github.com/reactjs/rfcs)
  Public design tradeoff debates among framework maintainers and senior engineers. Use for: understanding *why* architectural patterns exist before adopting them.
- [r/reactjs](https://www.reddit.com/r/reactjs/) & [r/typescript](https://www.reddit.com/r/typescript/)
  Active practitioner discussions on real-world ecosystem patterns, tooling migrations, and hiring expectations. Use for: calibrating 2027 ecosystem trends (verify claims against primary docs).
