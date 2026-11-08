// Enhanced Multiple-Choice Retrieval Quiz with per-choice feedback
// Supports:
//   <div class="quiz">
//     <div class="q" data-answer="1">
//       <p class="prompt">Question prompt?</p>
//       <ol class="choices">
//         <li data-why="Why option A is wrong...">Choice A</li>
//         <li data-why="Why option B is correct...">Choice B</li>
//         <li data-why="Why option C is wrong...">Choice C</li>
//         <li data-why="Why option D is wrong...">Choice D</li>
//       </ol>
//       <p class="why">General explanation</p>
//     </div>
//   </div>

document.querySelectorAll(".quiz").forEach((quiz) => {
  const questions = [...quiz.querySelectorAll(".q")];
  const score = document.createElement("p");
  score.className = "quiz-score";
  quiz.appendChild(score);
  let answered = 0, right = 0;

  questions.forEach((q) => {
    const answer = Number(q.dataset.answer);
    const items = [...q.querySelectorAll(".choices li")];
    const explanations = items.map((li) => li.getAttribute("data-why") || "");
    const generalWhy = q.querySelector(".why") ? q.querySelector(".why").innerHTML : "";

    const feedbackBox = document.createElement("div");
    feedbackBox.className = "quiz-feedback-box";
    feedbackBox.style.display = "none";
    q.appendChild(feedbackBox);

    const buttons = items.map((li, i) => {
      const b = document.createElement("button");
      b.type = "button";
      b.innerHTML = li.innerHTML;
      li.replaceChildren(b);

      b.addEventListener("click", () => {
        buttons.forEach((x) => (x.disabled = true));
        buttons[answer].classList.add("right");
        buttons[answer].setAttribute("aria-label", "Correct answer");

        const isCorrect = (i === answer);
        if (!isCorrect) {
          b.classList.add("wrong");
          b.setAttribute("aria-label", "Your incorrect answer");
        }
        q.classList.add("answered");
        answered++;
        if (isCorrect) right++;

        // Render comprehensive "Why Correct & Why Wrong" breakdown
        let feedbackHTML = "";
        if (isCorrect) {
          feedbackHTML += `<div class="callout ok" style="margin:.8rem 0 .4rem;">
            <p><strong>✓ Correct!</strong> ${explanations[i] || generalWhy}</p>
          </div>`;
        } else {
          const userWhy = explanations[i] ? `<p><strong>✗ Your choice:</strong> ${explanations[i]}</p>` : "";
          const correctWhy = explanations[answer] ? `<p><strong>✓ Correct answer:</strong> ${explanations[answer]}</p>` : "";
          feedbackHTML += `<div class="callout warn" style="margin:.8rem 0 .4rem;">
            ${userWhy}
            ${correctWhy}
          </div>`;
        }

        // Full choice breakdown
        let breakdownList = items.map((item, idx) => {
          const isAns = (idx === answer);
          const icon = isAns ? "✓" : "✗";
          const badgeColor = isAns ? "var(--ok)" : "var(--accent)";
          const text = explanations[idx] || (isAns ? generalWhy : "Incorrect choice");
          const choiceText = buttons[idx].innerText.trim();
          return `<li style="margin:.35rem 0; font-size:.9rem;">
            <span style="font-weight:700; color:${badgeColor}; margin-right:.3rem;">${icon}</span>
            <strong>${choiceText}:</strong> ${text}
          </li>`;
        }).join("");

        feedbackHTML += `<details class="why-breakdown" style="margin:.6rem 0; font-size:.88rem;">
          <summary style="cursor:pointer; color:var(--ink-soft); font-family:var(--sans); font-size:.8rem; font-weight:600;">
            Review why each choice is correct or wrong
          </summary>
          <ul style="padding-left:1.2rem; margin:.4rem 0;">${breakdownList}</ul>
        </details>`;

        feedbackBox.innerHTML = feedbackHTML;
        feedbackBox.style.display = "block";

        if (answered === questions.length) {
          score.textContent = `${right} / ${questions.length} correct` +
            (right < questions.length ? " — review the explanations above for the misses." : " — masterfully answered!");
        }
      });
      return b;
    });
  });
});
