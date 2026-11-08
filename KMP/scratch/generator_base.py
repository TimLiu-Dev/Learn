import os
import json

LESSON_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Lesson __NUM_PADDED__: __TITLE__</title>
  <link rel="stylesheet" href="../assets/style.css">
  <script src="../assets/quiz.js"></script>
  <script src="../assets/code-runner.js"></script>
</head>
<body>

<div class="container">

  <!-- Header -->
  <header class="lesson-header">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
      <span class="lesson-tag">__PHASE_TAG__ &bull; Lesson __NUM_PADDED__</span>
      <span><a href="../index.html">← Course Legend</a></span>
    </div>
    <h1>__TITLE__</h1>
    <p style="color: var(--text-muted); font-size: 1.05rem;">
      __SUBTITLE__
    </p>
  </header>

  <!-- Section 1: The Why & Mental Model -->
  <section>
    <h2>1. The Why & Architectural Mental Model</h2>
    __MENTAL_MODEL__
  </section>

  <!-- Section 2: Production Code Example -->
  <section>
    <h2>2. Production Code Examples</h2>
    __PRODUCTION_CODE__
  </section>

  <!-- Section 3: Pitfalls & Anti-Patterns -->
  <section>
    <h2>3. Production Pitfalls & Anti-Patterns</h2>
    __PITFALLS__
  </section>

  <!-- Section 4: In-Browser Hands-On Practice -->
  <section>
    <h2>4. Interactive Hands-On Practice</h2>
    <p>
      Write your code inside the editor below. When ready, click <strong>▶ Run & Check Code</strong> to run the automated verification test suite.
    </p>
    <div id="code-practice-container"></div>
  </section>

  <!-- Section 5: Retrieval Practice Quizzes -->
  <section>
    <h2>5. Retrieval Practice (Deep-Dive Quizzes)</h2>
    <p>
      Strengthen your long-term storage strength. Select your answer to receive immediate feedback, including a complete breakdown of why the correct option is right and why each alternative is wrong.
    </p>
    <div id="quiz-container-1"></div>
    <div id="quiz-container-2"></div>
  </section>

  <!-- Section 6: Primary Sources & Reference -->
  <section>
    <h2>6. Primary Sources & Next Steps</h2>
    <div class="source-card">
      <span class="source-card-badge">Primary Source</span>
      <div>
        <strong>__SOURCE_TITLE__:</strong>
        <a href="__SOURCE_URL__" target="_blank">__SOURCE_LINK_TEXT__</a>
        <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.25rem;">
          __SOURCE_NOTE__
        </p>
      </div>
    </div>

    <div class="callout note">
      <div class="callout-title">Have Follow-Up Questions?</div>
      I am here as your personal pair-programming mentor. If anything in this lesson feels unclear or you want to review alternative designs, ask in the chat!
    </div>
  </section>

  <!-- Footer Navigation -->
  <footer class="lesson-footer">
    <div>__REF_LINK__</div>
    <div class="nav-links">
      __PREV_LINK__
      <a href="../index.html">Course Legend</a>
      __NEXT_LINK__
    </div>
  </footer>

</div>

<!-- Initialize Interactive Components -->
<script>
  // 1. Initialize Code Runner Practice
  new KmpCodeRunner('code-practice-container', {
    title: __CODE_TITLE_JSON__,
    fileName: __CODE_FILENAME_JSON__,
    instructions: __CODE_INSTRUCTIONS_JSON__,
    starterCode: __STARTER_CODE_JSON__,
    hints: __HINTS_JSON__,
    finalSolution: __FINAL_SOLUTION_JSON__,
    solutionExplanation: __SOLUTION_EXPLANATION_JSON__,
    execute: __EXECUTE_FN_JS__
  });

  // 2. Initialize Retrieval Quiz 1
  new KmpQuiz('quiz-container-1', {
    question: __Q1_QUESTION_JSON__,
    codeSnippet: __Q1_CODE_SNIPPET__,
    options: __Q1_OPTIONS_JSON__,
    correctIndex: __Q1_CORRECT_INDEX__,
    explanation: __Q1_EXPLANATION_JSON__
  });

  // 3. Initialize Retrieval Quiz 2
  new KmpQuiz('quiz-container-2', {
    question: __Q2_QUESTION_JSON__,
    codeSnippet: __Q2_CODE_SNIPPET__,
    options: __Q2_OPTIONS_JSON__,
    correctIndex: __Q2_CORRECT_INDEX__,
    explanation: __Q2_EXPLANATION_JSON__
  });
</script>

</body>
</html>
"""

REFERENCE_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reference: __TITLE__</title>
  <link rel="stylesheet" href="../assets/style.css">
  <style>
    @media print {
      body { padding: 0.5in; font-size: 10pt; }
      .no-print { display: none; }
      pre { page-break-inside: avoid; }
    }
  </style>
</head>
<body>

<div class="container">
  <header class="lesson-header">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
      <span class="lesson-tag">Reference Guide __NUM_PADDED__</span>
      <span class="no-print"><a href="../index.html">← Back to Curriculum</a></span>
    </div>
    <h1>__TITLE__</h1>
    <p style="color: var(--text-muted); font-size: 0.95rem;">
      __SUMMARY__
    </p>
  </header>

  <main>
    __CONTENT__
  </main>

  <footer class="lesson-footer">
    <div>KMP Architectural Reference &bull; Print Ready</div>
    <div class="no-print"><a href="../lessons/__LESSON_SLUG__.html">Proceed to Lesson __NUM_PADDED__ →</a></div>
  </footer>
</div>

</body>
</html>
"""

def generate_lesson_file(data):
    num_padded = f"{data['num']:04d}"
    slug = f"{num_padded}-{data['slug']}"
    
    # Prev / Next links
    prev_link = f'<a href="./{data["prev_slug"]}.html">← Previous</a>' if data.get('prev_slug') else ''
    next_link = f'<a href="./{data["next_slug"]}.html">Next →</a>' if data.get('next_slug') else ''
    ref_slug = data.get('ref_slug', f"{data['num']:02d}-{data['slug']}")
    ref_link = f'<a href="../reference/{ref_slug}.html">📖 Open Reference Cheatsheet</a>' if data.get('ref_content') else ''

    html_content = LESSON_TEMPLATE
    replacements = {
        "__NUM_PADDED__": num_padded,
        "__TITLE__": data['title'],
        "__PHASE_TAG__": data['phase_tag'],
        "__SUBTITLE__": data['subtitle'],
        "__MENTAL_MODEL__": data['mental_model'],
        "__PRODUCTION_CODE__": data['production_code'],
        "__PITFALLS__": data['pitfalls'],
        "__SOURCE_TITLE__": data['source_title'],
        "__SOURCE_URL__": data['source_url'],
        "__SOURCE_LINK_TEXT__": data['source_link_text'],
        "__SOURCE_NOTE__": data['source_note'],
        "__REF_LINK__": ref_link,
        "__PREV_LINK__": prev_link,
        "__NEXT_LINK__": next_link,
        "__CODE_TITLE_JSON__": json.dumps(data['code_title']),
        "__CODE_FILENAME_JSON__": json.dumps(data['code_filename']),
        "__CODE_INSTRUCTIONS_JSON__": json.dumps(data['code_instructions']),
        "__STARTER_CODE_JSON__": json.dumps(data['starter_code']),
        "__HINTS_JSON__": json.dumps(data['hints']),
        "__FINAL_SOLUTION_JSON__": json.dumps(data['final_solution']),
        "__SOLUTION_EXPLANATION_JSON__": json.dumps(data['solution_explanation']),
        "__EXECUTE_FN_JS__": data['execute_fn_js'],
        "__Q1_QUESTION_JSON__": json.dumps(data['q1']['question']),
        "__Q1_CODE_SNIPPET__": json.dumps(data['q1']['code_snippet']) if data.get('q1', {}).get('code_snippet') else "null",
        "__Q1_OPTIONS_JSON__": json.dumps(data['q1']['options']),
        "__Q1_CORRECT_INDEX__": str(data['q1']['correct_index']),
        "__Q1_EXPLANATION_JSON__": json.dumps(data['q1']['explanation']),
        "__Q2_QUESTION_JSON__": json.dumps(data['q2']['question']),
        "__Q2_CODE_SNIPPET__": json.dumps(data['q2']['code_snippet']) if data.get('q2', {}).get('code_snippet') else "null",
        "__Q2_OPTIONS_JSON__": json.dumps(data['q2']['options']),
        "__Q2_CORRECT_INDEX__": str(data['q2']['correct_index']),
        "__Q2_EXPLANATION_JSON__": json.dumps(data['q2']['explanation'])
    }

    for key, val in replacements.items():
        html_content = html_content.replace(key, val)

    out_path = f"lessons/{slug}.html"
    with open(out_path, "w", encoding="utf-8") as f:
        f.write(html_content)
    print(f"Generated lesson: {out_path}")

    # Generate reference document if content provided
    if data.get('ref_content'):
        ref_path = f"reference/{ref_slug}.html"
        ref_html = REFERENCE_TEMPLATE
        ref_replacements = {
            "__TITLE__": data['ref_title'] if data.get('ref_title') else data['title'],
            "__NUM_PADDED__": f"{data['num']:02d}",
            "__SUMMARY__": data['subtitle'],
            "__CONTENT__": data['ref_content'],
            "__LESSON_SLUG__": slug
        }
        for key, val in ref_replacements.items():
            ref_html = ref_html.replace(key, val)

        with open(ref_path, "w", encoding="utf-8") as f:
            f.write(ref_html)
        print(f"Generated reference: {ref_path}")
