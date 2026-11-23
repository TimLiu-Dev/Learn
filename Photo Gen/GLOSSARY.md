# AI Photo & Video Generation Glossary

Canonical terminology for this workspace. Every lesson and reference sheet adheres to these definitions.

## Core Pipeline & Math
- **Latent Space:** A compressed tensor representation produced by a VAE encoder (typically 8× smaller spatially than RGB pixels; e.g., `1024×1024` RGB → `128×128` latent).
- **VAE (Variational Autoencoder):** Compresses RGB images/videos into latent tensors (`VAEEncode`) and reconstructs latent tensors back into viewable pixels (`VAEDecode`). Image VAEs use 4 channels (SDXL) or 16 channels (FLUX/Wan); 3D video VAEs also compress time by ~4× (`4k + 1` frame rule).
- **Rectified Flow (Flow Matching):** Training objective used by FLUX.1/FLUX.2 and Wan 2.1/2.2 where the denoiser predicts a velocity field along a near-straight trajectory between Gaussian noise (`t=1`) and data (`t=0`), enabling convergence in fewer Euler steps.
- **CFG (Classifier-Free Guidance):** Guidance scale comparing prompt-conditioned vs. unconditioned predictions. Classic SDXL uses true dual-pass CFG (`5.0–7.5`); guidance-distilled FLUX embeds a guidance scalar (`2.5–4.0`) while leaving true CFG at `1.0` (making `negative_prompt` a no-op unless true CFG is forced).
- **Seed:** Deterministic PRNG seed initializing the starting Gaussian noise tensor `z_T`. Must be locked during prompt or LoRA A/B tests.

## Open-Weight Ecosystem, Quantization & Hardware
- **GGUF (`Q4_K_M`, `Q5_K_M`, `Q8_0`):** Block-quantized weight format ideal for bandwidth-bound Apple Silicon (M5 Max MPS) unified memory.
- **FP8 (`E4M3`):** 8-bit floating-point quantization (~1 byte/param) accelerated on modern NVIDIA Ada/Hopper and AMD ROCm 7.x (`gfx1151`), but avoided on macOS MPS.
- **UMA (Unified Memory Architecture):** Shared CPU/GPU physical memory pool (128GB on M5 Max and Strix Halo). Eliminates VRAM-capacity limits for inference, making **BlockSwap** harmful overhead rather than an optimization.
- **`.safetensors` vs. `.ckpt`:** `.safetensors` stores pure tensor weights + JSON metadata safely; legacy `.ckpt`/`.pt` files use Python `pickle` and can execute arbitrary code on load.

## Conditioning, Control & Training
- **LoRA (Low-Rank Adaptation):** Lightweight (`40MB–300MB`) `A × B` adapter weights injected into frozen base model attention layers to lock a specific person, pet, or style.
- **Zero-Shot Identity Conditioning:** Injecting identity or visual traits from reference images at inference time without training a LoRA (e.g., FLUX.2 native multi-ref, PuLID, IP-Adapter).
- **ControlNet:** Structural conditioning adapter (Depth, OpenPose, Canny) that locks pose, framing, or geometry from a reference image.
- **Inpainting / Outpainting:** Re-denoising only a masked region (`VAEEncodeForInpaint` / `FaceDetailer`) to fix eyes/hands/seams, or extending canvas borders outward to reframe tight portraits.
- **Aspect-Ratio Bucketing:** Grouping training photos by native aspect ratio into ~1 megapixel buckets during LoRA training so full-body and portrait shots never get square-cropped.

## Video & Production Ops
- **MoE Dual-Expert Video (Wan 2.2 A14B):** Split Mixture-of-Experts architecture using a **High-Noise Expert** for early layout/motion steps and a **Low-Noise Expert** for late texture/detail steps, requiring matched `_high_noise` and `_low_noise` LoRA pairs.
- **I2V / FLF2V:** Image-to-Video (animating a single keyframe) and First-Last-Frame-to-Video (interpolating motion smoothly between two target keyframes).
- **RIFE:** Real-Time Intermediate Flow Estimation — optical-flow frame interpolation turning 16fps/24fps AI video into 32fps/48fps playback.
- **ArcFace / InsightFace:** 512-D facial embedding model used to compute objective cosine similarity between reference selfies and generated candidates.
- **C2PA / SynthID:** Cryptographic provenance manifests (C2PA) and invisible perceptual watermarks (Google SynthID) used to track AI media origin.
