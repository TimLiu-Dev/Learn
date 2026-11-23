# AI Photo & Video Generation Resources

## Knowledge

### Core Architecture & Code (Python / Diffusers)
- [Hugging Face Diffusers Documentation](https://huggingface.co/docs/diffusers)
  Primary Python library and conceptual reference for diffusion pipelines, schedulers, VAEs, LoRA loading, and memory optimization. Use for: Lessons 01–02 and all Python inference scripts.
- [Hugging Face Diffusers GitHub](https://github.com/huggingface/diffusers)
  Source code for modern pipelines (FLUX, SDXL, SD 3.5, Wan, HunyuanVideo). Use for: inspecting exact pipeline signatures and scheduler implementations.

### Commercial APIs (Late 2026 Current Standards)
- [Google Gemini API — Imagen 4, Gemini Flash Image & Veo 3.1 Docs](https://ai.google.dev/gemini-api/docs)
  Official `google-genai` SDK documentation covering `imagen-4.0-generate-001`, `gemini-3.1-flash-image` (Nano Banana 2), `veo-3.1-generate-001`, and `gemini-omni-1.2-flash`. Use for: Module 1 commercial image/video APIs.
- [OpenAI Platform — Images & Responses API Docs](https://platform.openai.com/docs/guides/images)
  Current reference for GPT-Image-2.6 (`gpt-image-2.6-sunburst`, `gpt-image-2.6-flare`) conversational and programmatic image editing (note: `gpt-image-1` and Sora APIs were retired in 2026). Use for: Module 1 OpenAI image pipelines.
- [ByteDance Seed / Seedance API Gateways (fal.ai)](https://www.fal.ai/)
  Primary developer gateway for Seedance 2.0 / 2.5 (joint audio-video, multi-reference up to 50 assets, 30s clips) and Seedream image models. Use for: Module 1 & Cat Video API projects.

### Open-Weight Models, ComfyUI & Node Workflows
- [ComfyUI Official Documentation](https://docs.comfy.org)
  Official manual for ComfyUI node graphs, headless JSON/WebSocket API execution, custom nodes, and memory management. Use for: Modules 2–6 local/cloud node pipelines.
- [ComfyUI Core Repository (Comfy-Org)](https://github.com/Comfy-Org/ComfyUI)
  Primary codebase and release tracker for native FLUX.2, Wan 2.2 MoE, HunyuanVideo, and GGUF/FP8 support. Use for: installation, troubleshooting, and API automation.
- [ComfyUI-Manager](https://github.com/ltdrdata/ComfyUI-Manager)
  Standard package manager for installing custom node packs (`ComfyUI-GGUF`, `ComfyUI-Impact-Pack`, `ComfyUI-VideoHelperSuite`, `ComfyUI-KJNodes`, `ComfyUI-MMAudio`).
- [Black Forest Labs — FLUX.2 Documentation](https://docs.bfl.ml/)
  Official technical docs for FLUX.2 (`pro`, `max`, `flex`, `dev` 32B open-weight, and `klein` 4B/9B distilled/base) unified generation and multi-reference editing. Use for: Module 2 & LinkedIn/Tinder photorealistic portrait workflows.
- [Wan-Video Official Organization (Alibaba)](https://github.com/Wan-Video)
  Official open-weight Wan 2.1 / 2.2 (A14B MoE & TI2V-5B) text-to-video and image-to-video repositories and tooling. Use for: Module 5 open-weight cat video and hybrid keyframe-to-video pipelines.

### Custom Training, Local 128GB Hardware, Evals & Deployment
- [Ostris AI-Toolkit (`ostris/ai-toolkit`)](https://github.com/ostris/ai-toolkit)
  De facto standard YAML-driven training suite for FLUX.1 / FLUX.2 (`klein` & `dev`) and Wan video LoRAs with on-the-fly quantization. Use for: Module 3 & 4 identity/pet LoRA training.
- [Kohya_ss (`kohya-ss/sd-scripts`)](https://github.com/kohya-ss/sd-scripts)
  Canonical trainer for SDXL, Pony V6 XL, and Illustrious LoRAs.
- [Strix Halo Wiki — Ryzen AI Max+ 395 Local AI](https://strixhalo.wiki/)
  Community hardware/software reference for `gfx1151` Radeon 8060S UMA VRAM allocation, ROCm 7.x, and FP8/GGUF ComfyUI tuning. Use for: ROG Flow Z13 local setup.
- [RunPod Documentation](https://docs.runpod.io/)
  Cloud GPU pod templates, network volumes, `runpodctl`, and serverless endpoint deployment. Use for: Module 3 fast cloud LoRA training and Module 6 serverless inference.
- [InsightFace (`deepinsight/insightface`)](https://github.com/deepinsight/insightface)
  Standard ArcFace face embedding toolkit (`buffalo_l`) for automated cosine-similarity identity evaluation across portrait sweeps.

## Wisdom (Communities)
- [r/StableDiffusion](https://www.reddit.com/r/StableDiffusion/)
  Broad practitioner subreddit for model comparisons, LoRA training recipes, hardware benchmarks, and workflow critiques.
- [r/ComfyUI](https://www.reddit.com/r/ComfyUI/)
  Focused troubleshooting and node-graph sharing community for ComfyUI workflows, Wan 2.2 MoE templates, VRAM tuning, and custom nodes.
- [Banodoco (GitHub & Community)](https://github.com/banodoco)
  High-signal open-source AI video & creative tooling collective. Use for: open-weight AI video craft, consistency tricks, and practitioner wisdom.
- [Civitai](https://civitai.com/)
  Central community hub for open-weight SDXL, Pony/Illustrious, FLUX, and Wan checkpoints, LoRAs, and shared ComfyUI workflows (filter strictly to `.safetensors`).

## Confirmed Learner Environment
- **Local Hardware:** MacBook Pro M5 Max (128GB UMA) + ASUS ROG Flow Z13 Ryzen AI Max+ 395 / Strix Halo (128GB UMA).
- **Cloud Compute:** On-demand RunPod / Vast.ai (e.g., L40S 48GB or H100 80GB) for rapid LoRA backpropagation and batch rendering.
- **Target Capstones:** (1) LinkedIn Headshots, (2) Tinder Lifestyle Photos, (3) Multi-Shot Cat Photos & Videos, plus (Appendix) Private Uncensored Local Sandbox.
