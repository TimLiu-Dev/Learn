/**
 * Reusable Interactive Latent Diffusion & Guidance Simulator
 * Visualizes denoising from noise (t=T) to clean latent/pixel output (t=0),
 * plus the effect of Step Count, CFG / Guidance Scale, and Model Family.
 */
(function (global) {
  "use strict";

  function seededNoise(x, y, seed) {
    const n = Math.sin(x * 12.9898 + y * 78.233 + seed * 43.12) * 43758.5453;
    return n - Math.floor(n);
  }

  function drawScene(ctx, w, h, subject, progress, cfg, isLatent) {
    const img = ctx.createImageData(w, h);
    const data = img.data;

    // Over-guidance artifact factor when CFG exceeds sweet spot
    const overCfg = Math.max(0, (cfg - 9.0) / 11.0);
    const underCfg = Math.max(0, (3.5 - cfg) / 3.5);
    const effectiveClarity = Math.max(0, Math.min(1, progress * (1 - 0.35 * underCfg)));

    for (let y = 0; y < h; y++) {
      for (let x = 0; x < w; x++) {
        const nx = (x / w) * 2 - 1;
        const ny = (y / h) * 2 - 1;

        let r = 215, g = 210, b = 198; // warm studio backdrop

        if (subject === "cat") {
          // Cat face silhouette + ears + emerald eyes
          const head = nx * nx * 1.3 + (ny - 0.12) * (ny - 0.12) * 1.45 < 0.36;
          const leftEar = ny < -0.15 && ny > -0.68 && nx > -0.52 && nx < -0.1 && (ny + 0.68) > Math.abs(nx + 0.31) * 1.3;
          const rightEar = ny < -0.15 && ny > -0.68 && nx < 0.52 && nx > 0.1 && (ny + 0.68) > Math.abs(nx - 0.31) * 1.3;
          const leftEye = (nx + 0.19) ** 2 + (ny - 0.05) ** 2 < 0.009;
          const rightEye = (nx - 0.19) ** 2 + (ny - 0.05) ** 2 < 0.009;
          const nose = Math.abs(nx) < 0.055 && ny > 0.17 && ny < 0.25;

          if (head || leftEar || rightEar) {
            r = 228; g = 148; b = 74; // ginger tabby coat
            if ((Math.floor((nx + 1) * 14) + Math.floor((ny + 1) * 8)) % 3 === 0) {
              r = 186; g = 104; b = 45;
            }
          }
          if (leftEye || rightEye) {
            r = 56; g = 178; b = 102;
          }
          if (nose) {
            r = 216; g = 114; b = 128;
          }
        } else {
          // LinkedIn portrait silhouette: navy blazer + crisp collar + warm skin
          const bgGrad = (ny + 1) * 0.5;
          r = 225 - bgGrad * 30;
          g = 228 - bgGrad * 28;
          b = 232 - bgGrad * 20;

          const shoulders = ny > 0.32 && (nx * nx) / 0.72 + ((ny - 0.95) * (ny - 0.95)) / 0.45 < 1.0;
          const shirt = ny > 0.34 && Math.abs(nx) < 0.16 && ny < 0.85;
          const neck = ny > 0.16 && ny <= 0.42 && Math.abs(nx) < 0.13;
          const head = (nx * nx) / 0.11 + ((ny + 0.18) * (ny + 0.18)) / 0.17 < 1.0;
          const hair = (nx * nx) / 0.125 + ((ny + 0.29) * (ny + 0.29)) / 0.14 < 1.0 && ny < -0.15;

          if (shoulders) { r = 30; g = 46; b = 74; }
          if (shirt) { r = 246; g = 247; b = 250; }
          if (neck || head) { r = 218; g = 174; b = 144; }
          if (hair) { r = 46; g = 35; b = 29; }
        }

        // Apply CFG contrast/saturation push (oversaturated artifacts at extreme CFG)
        if (overCfg > 0) {
          const boost = 1 + overCfg * 1.9;
          r = 128 + (r - 128) * boost;
          g = 128 + (g - 128) * boost;
          b = 128 + (b - 128) * boost;
        }

        // Mix with Gaussian-like noise based on denoising progress
        const nR = seededNoise(x, y, 1) * 255;
        const nG = seededNoise(x, y, 2) * 255;
        const nB = seededNoise(x, y, 3) * 255;
        const mix = effectiveClarity;

        let outR = r * mix + nR * (1 - mix);
        let outG = g * mix + nG * (1 - mix);
        let outB = b * mix + nB * (1 - mix);

        if (isLatent) {
          // Latent channels look like low-res false-color feature blocks
          outR = (outR * 0.65 + outB * 0.35);
          outG = (outG * 0.55 + 60);
          outB = (outB * 0.8 + 45);
        }

        const idx = (y * w + x) * 4;
        data[idx] = Math.max(0, Math.min(255, outR));
        data[idx + 1] = Math.max(0, Math.min(255, outG));
        data[idx + 2] = Math.max(0, Math.min(255, outB));
        data[idx + 3] = 255;
      }
    }
    ctx.putImageData(img, 0, 0);
  }

  function init(containerId) {
    const root = document.getElementById(containerId);
    if (!root) return;

    root.innerHTML = `
      <div class="sim-card">
        <div class="sim-header">
          <div class="sim-title">Interactive Lab: Latent Denoising, Steps &amp; CFG Guidance</div>
          <span class="badge badge-next">Live Intuition</span>
        </div>
        <div class="sim-grid">
          <div class="sim-controls">
            <label for="sim-subject">Target Prompt</label>
            <select id="sim-subject">
              <option value="portrait">LinkedIn Portrait ("studio headshot, navy blazer")</option>
              <option value="cat">Cat Keyframe ("ginger tabby cat, emerald eyes")</option>
            </select>

            <label for="sim-arch">Denoiser Family</label>
            <select id="sim-arch">
              <option value="flow">Rectified Flow (FLUX.2 / Wan 2.2)</option>
              <option value="ddpm">Classic Diffusion (SDXL / SD 1.5)</option>
            </select>

            <label for="sim-steps">Total Inference Steps: <span id="val-steps">24</span></label>
            <input id="sim-steps" type="range" min="4" max="50" value="24" />

            <label for="sim-t">Current Denoise Step: <span id="val-t">24</span></label>
            <input id="sim-t" type="range" min="0" max="24" value="24" />

            <label for="sim-cfg">CFG / Guidance Scale: <span id="val-cfg">7.0</span></label>
            <input id="sim-cfg" type="range" min="1" max="20" step="0.5" value="7.0" />
          </div>

          <div>
            <div class="sim-canvas-row">
              <div class="sim-canvas-box">
                <canvas id="canvas-latent" width="48" height="48"></canvas>
                <div class="sim-caption"><strong>1. Latent Space</strong><br/><span id="latent-shape">16 &times; 64 &times; 64</span></div>
              </div>
              <div class="sim-canvas-box">
                <canvas id="canvas-pixel" width="160" height="160"></canvas>
                <div class="sim-caption"><strong>2. VAE Decoded RGB</strong><br/><span>3 &times; 512 &times; 512</span></div>
              </div>
            </div>
            <div class="sim-readout" id="sim-readout"></div>
          </div>
        </div>
      </div>
    `;

    const subjectEl = root.querySelector("#sim-subject");
    const archEl = root.querySelector("#sim-arch");
    const stepsEl = root.querySelector("#sim-steps");
    const tEl = root.querySelector("#sim-t");
    const cfgEl = root.querySelector("#sim-cfg");

    const valSteps = root.querySelector("#val-steps");
    const valT = root.querySelector("#val-t");
    const valCfg = root.querySelector("#val-cfg");
    const latentShapeEl = root.querySelector("#latent-shape");
    const readoutEl = root.querySelector("#sim-readout");

    const cLatent = root.querySelector("#canvas-latent").getContext("2d");
    const cPixel = root.querySelector("#canvas-pixel").getContext("2d");

    function update() {
      const steps = Number(stepsEl.value);
      tEl.max = String(steps);
      if (Number(tEl.value) > steps) tEl.value = String(steps);

      const currentStep = Number(tEl.value);
      const cfg = Number(cfgEl.value);
      const arch = archEl.value;
      const subject = subjectEl.value;

      valSteps.textContent = String(steps);
      valT.textContent = `${currentStep} / ${steps}`;
      valCfg.textContent = cfg.toFixed(1);
      latentShapeEl.textContent = arch === "flow" ? "16 × 64 × 64 (FLUX/Wan VAE)" : "4 × 64 × 64 (SDXL VAE)";

      // Rectified Flow reaches structure faster in fewer steps than classic DDPM
      const rawRatio = steps === 0 ? 0 : currentStep / steps;
      const archCurve = arch === "flow" ? Math.pow(rawRatio, 0.72) : Math.pow(rawRatio, 1.15);
      const stepPenalty = steps < 8 && arch === "ddpm" ? 0.55 : steps < 4 ? 0.65 : 1.0;
      const progress = Math.min(1, archCurve * stepPenalty);

      drawScene(cLatent, 48, 48, subject, progress, cfg, true);
      drawScene(cPixel, 160, 160, subject, progress, cfg, false);

      let statusNote = "Balanced trajectory: clean subject structure and natural contrast.";
      if (currentStep === 0) {
        statusNote = "Step 0: Pure Gaussian noise tensor z_T. The prompt has not steered the latent yet.";
      } else if (steps < 8 && arch === "ddpm") {
        statusNote = "Too few steps for Classic Diffusion: step size Δt is too coarse, leaving residual grain.";
      } else if (cfg < 3.0) {
        statusNote = "Low CFG (< 3.0): Weak prompt adherence — the denoiser drifts toward generic averages.";
      } else if (cfg > 12.0) {
        statusNote = "High CFG (> 12.0): Over-steered! Colors clip and contrast burns ('fried' artifacts).";
      } else if (arch === "flow" && steps <= 20) {
        statusNote = "Rectified Flow advantage: Straighter ODE paths converge cleanly even at 12–20 steps.";
      }

      readoutEl.innerHTML = `<strong>Diagnostics:</strong> ${statusNote}`;
    }

    [subjectEl, archEl, stepsEl, tEl, cfgEl].forEach((el) => {
      el.addEventListener("input", update);
    });
    update();
  }

  global.DiffusionSim = { init };
})(window);
