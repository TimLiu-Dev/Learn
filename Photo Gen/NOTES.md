# Working Notes & Learner Preferences

- **Background:** Junior Android developer (2026) targeting Senior AI Photo & Video Generation practitioner and engineer in 2027.
- **Confirmed Hardware (Dual 128GB Unified Memory):**
  - **MacBook Pro M5 Max (128GB Unified Memory):** Apple Silicon MPS (`PYTORCH_MPS_HIGH_WATERMARK_RATIO=0.0`, use GGUF `Q4_K_M`/`Q5_K_M`/`Q8_0` or native BF16 on M5 Max, avoid FP8 on MPS and avoid blind `--force-fp16` on FLUX/Wan to prevent NaNs, disable BlockSwap, use step-distilled LoRAs like LightX2V/Wan-Lightning for Wan 2.2).
  - **ASUS ROG Flow Z13 (AMD Ryzen AI Max+ 395 / Strix Halo, Radeon 8060S `gfx1151`, 128GB Unified Memory):** Carve 96GB–112GB VRAM in BIOS/Adrenalin, ROCm 7.x / custom Strix Halo ComfyUI setups, FP8/GGUF quants. Great for local Windows/Linux ComfyUI & private unrestricted local workflows.
- **Confirmed Subscriptions:** Google AI Pro (consumer web/Gemini/Veo access; clarify Google AI Studio / Vertex AI API key billing separately when writing Python SDK code). Open to adding other APIs/subscriptions as needed.
- **Confirmed Capstones:**
  - Disregard building an Android/Web software app capstone.
  - **Public Portfolio Capstones (Packs 1–3):** (1) LinkedIn photos, (2) Tinder dating photos, (3) Multi-shot Cat photos & videos.
  - **Private Optional Side Track (Appendix):** (4) Private uncensored/NSFW local photos & videos (kept strictly offline/encrypted, outside public recruiter repos).
- **Curriculum Structure:** 17 core lessons (Modules 0–6) + 1 isolated Appendix lesson (`appendix-01-uncensored-local-workflows.html`), each paired with runnable code/configs, hands-on verification labs, and 4–5 diagnostic multiple-choice quizzes.
