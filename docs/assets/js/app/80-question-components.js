function renderQuizReviewLinks(questions, label) {
  if (questions.length === 0) {
    return "";
  }

  return `
    <div class="quiz-review-group">
      <strong>${escapeHtml(label)} (${questions.length})</strong>
      <div>
        ${questions
          .slice(0, 16)
          .map((question) => `<a href="#quiz-${escapeHtml(question.id)}">${question.order}</a>`)
          .join("")}
        ${questions.length > 16 ? `<span>+${questions.length - 16}</span>` : ""}
      </div>
    </div>
  `;
}

function renderQuestionCard(question, keyword = "", extraMeta = [], displayNumber = "") {
  const normalizedQuestion = normalizeQuestionDetails(question);
  const questionId = question.id || `${question.question}-${displayNumber}`;
  const metaPills = [question.level, question.kind, ...extraMeta]
    .filter(Boolean)
    .map((item) => `<span class="meta-pill">${escapeHtml(item)}</span>`)
    .join("");
  const answerTitle = normalizedQuestion.explanation.length > 0 ? commonText().answer : commonText().answerFallback;
  const explanationHtml = normalizedQuestion.explanation
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const fullAnswerHtml = normalizedQuestion.fullAnswer
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const applyHtml = normalizedQuestion.applyOrPitfalls
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const codeHtml = question.codeExample
    ? `
      <div class="code-study-note">
        <span>${currentLanguage === "vi" ? "Code thực hành" : "Practice code"}</span>
        <small>${currentLanguage === "vi" ? "Đọc docblock trước, sau đó chạy và sửa input để hiểu behavior." : "Read the docblock first, then run it and change inputs to understand the behavior."}</small>
      </div>
      <pre class="code-sample"><code>${highlightText(question.codeExample, keyword)}</code></pre>
    `
    : "";
  const practiceHtml = Array.isArray(question.practice) && question.practice.length > 0
    ? `
      <div class="practice-block">
        <h4>${commonText().practice}</h4>
        <ul>
          ${question.practice.map((item) => `<li>${highlightText(item, keyword)}</li>`).join("")}
        </ul>
      </div>
    `
    : "";

  return `
    <article class="question-card" data-study-question-id="${escapeHtml(questionId)}">
      <div class="question-card-head">
        ${
          displayNumber
            ? `<span class="question-card-index" aria-label="${currentLanguage === "vi" ? "Số thứ tự câu hỏi" : "Question number"}">${escapeHtml(displayNumber)}</span>`
            : ""
        }
        <div class="question-meta">${metaPills}</div>
        <button class="study-toggle" type="button" data-study-toggle aria-pressed="false">
          ${currentLanguage === "vi" ? "Đánh dấu đã học" : "Mark studied"}
        </button>
        <h3>${highlightText(question.question, keyword)}</h3>
      </div>
      <section class="answer-snapshot">
        <span>${answerTitle}</span>
        <p>${highlightText(normalizedQuestion.answerShort, keyword)}</p>
      </section>
      <div class="qa-accordion">
        ${
          fullAnswerHtml
            ? `
              <details class="qa-detail" data-detail-type="answer">
                <summary>${currentLanguage === "vi" ? "Câu trả lời đầy đủ" : "Full answer"}</summary>
                <ul class="answer-list">${fullAnswerHtml}</ul>
              </details>
            `
            : ""
        }
        <details class="qa-detail" data-detail-type="explanation">
          <summary>${commonText().explanation}</summary>
          <ul class="answer-list">${explanationHtml}</ul>
        </details>
        ${
          normalizedQuestion.applyOrPitfalls.length > 0
            ? `
              <details class="qa-detail" data-detail-type="apply">
                <summary>${commonText().apply}</summary>
                <ul class="answer-list">${applyHtml}</ul>
              </details>
            `
            : ""
        }
        ${
          codeHtml
            ? `
              <details class="qa-detail" data-detail-type="code">
                <summary>Code</summary>
                ${codeHtml}
              </details>
            `
            : ""
        }
        ${
          practiceHtml
            ? `
              <details class="qa-detail" data-detail-type="practice">
                <summary>${commonText().practice}</summary>
                ${practiceHtml}
              </details>
            `
            : ""
        }
      </div>
    </article>
  `;
}

function getQuestionSearchText(question) {
  const normalizedQuestion = normalizeQuestionDetails(question);
  return [
    question.track,
    question.topicTitle,
    question.topicSummary,
    question.question,
    ...(question.answer || []),
    normalizedQuestion.answerShort,
    ...(normalizedQuestion.explanation || []),
    ...(normalizedQuestion.applyOrPitfalls || []),
    ...(question.practice || []),
    question.codeExample
  ]
    .filter(Boolean)
    .join(" ");
}

function mountQuestionStudyActions(scope = document) {
  const cards = Array.from(scope.querySelectorAll("[data-study-question-id]"));
  if (cards.length === 0) {
    return;
  }

  const studiedIds = readStudiedQuestionIds();

  cards.forEach((card) => {
    setStudyCardState(card, studiedIds);
    const button = card.querySelector("[data-study-toggle]");
    if (!button || button.dataset.studyBound === "true") {
      return;
    }
    button.dataset.studyBound = "true";
    button.addEventListener("click", () => {
      const questionId = card.dataset.studyQuestionId || "";
      if (!questionId) {
        return;
      }
      const nextStudiedIds = readStudiedQuestionIds();
      if (nextStudiedIds.has(questionId)) {
        nextStudiedIds.delete(questionId);
      } else {
        nextStudiedIds.add(questionId);
      }
      writeStudiedQuestionIds(nextStudiedIds);
      setStudyCardState(card, nextStudiedIds);
      updateStudyProgressSummary(scope);
    });
  });

  updateStudyProgressSummary(scope);
}

function getStudyProgressKey() {
  return stateKey(`study-progress-${currentLanguage}`);
}

function readStudiedQuestionIds() {
  try {
    const value = JSON.parse(localStorage.getItem(getStudyProgressKey()) || "[]");
    return new Set(Array.isArray(value) ? value : []);
  } catch {
    return new Set();
  }
}

function writeStudiedQuestionIds(ids) {
  localStorage.setItem(getStudyProgressKey(), JSON.stringify([...ids]));
}

function setStudyCardState(card, studiedIds) {
  const questionId = card.dataset.studyQuestionId || "";
  const button = card.querySelector("[data-study-toggle]");
  const isStudied = studiedIds.has(questionId);
  card.classList.toggle("is-studied", isStudied);
  if (!button) {
    return;
  }
  button.setAttribute("aria-pressed", String(isStudied));
  button.textContent = isStudied
    ? currentLanguage === "vi" ? "Đã học" : "Studied"
    : currentLanguage === "vi" ? "Đánh dấu đã học" : "Mark studied";
}

function updateStudyProgressSummary(scope = document) {
  const cards = Array.from(scope.querySelectorAll("[data-study-question-id]"));
  if (cards.length === 0) {
    return;
  }

  const studiedCount = cards.filter((card) => card.classList.contains("is-studied")).length;
  const answerCount = cards.filter((card) => card.querySelector('[data-detail-type="answer"]')).length;
  const codeCount = cards.filter((card) => card.querySelector('[data-detail-type="code"]')).length;
  const practiceCount = cards.filter((card) => card.querySelector('[data-detail-type="practice"]')).length;
  const existing = scope.querySelector(".study-progress-summary");
  const summary = existing || document.createElement("div");
  summary.className = "study-progress-summary";
  summary.removeAttribute("role");
  summary.innerHTML = `
    <span class="study-progress-text" aria-live="polite">${
      currentLanguage === "vi"
        ? `Tiến độ học: ${studiedCount}/${cards.length} câu đã đánh dấu.`
        : `Study progress: ${studiedCount}/${cards.length} questions marked.`
    }</span>
    <span class="study-progress-actions">
      <button class="study-mini-button" type="button" data-study-next>${currentLanguage === "vi" ? "Câu tiếp" : "Next"}</button>
      ${
        answerCount > 0
          ? `<button class="study-mini-button" type="button" data-open-detail="answer">${currentLanguage === "vi" ? `Mở trả lời (${answerCount})` : `Open answers (${answerCount})`}</button>`
          : ""
      }
      ${
        codeCount > 0
          ? `<button class="study-mini-button" type="button" data-open-detail="code">${currentLanguage === "vi" ? `Mở code (${codeCount})` : `Open code (${codeCount})`}</button>`
          : ""
      }
      ${
        practiceCount > 0
          ? `<button class="study-mini-button" type="button" data-open-detail="practice">${currentLanguage === "vi" ? `Mở bài tập (${practiceCount})` : `Open practice (${practiceCount})`}</button>`
          : ""
      }
      <button class="study-mini-button" type="button" data-study-reset>${currentLanguage === "vi" ? "Reset phần này" : "Reset visible"}</button>
    </span>
  `;

  if (!existing) {
    scope.prepend(summary);
  }

  summary.querySelector("[data-study-next]")?.addEventListener("click", () => {
    const nextCard = cards.find((card) => !card.classList.contains("is-studied")) || cards[0];
    if (!nextCard) {
      return;
    }
    nextCard.querySelector("details")?.setAttribute("open", "");
    scrollToElement(nextCard, "center");
    nextCard.querySelector("[data-study-toggle]")?.focus({ preventScroll: true });
    nextCard.classList.remove("study-focus-pulse");
    window.requestAnimationFrame(() => {
      nextCard.classList.add("study-focus-pulse");
      window.setTimeout(() => nextCard.classList.remove("study-focus-pulse"), 900);
    });
  });

  summary.querySelector("[data-study-reset]")?.addEventListener("click", () => {
    const studiedIds = readStudiedQuestionIds();

    cards.forEach((card) => {
      const questionId = card.dataset.studyQuestionId || "";
      studiedIds.delete(questionId);
      setStudyCardState(card, studiedIds);
    });

    writeStudiedQuestionIds(studiedIds);
    updateStudyProgressSummary(scope);
  });

  summary.querySelectorAll("[data-open-detail]").forEach((button) => {
    button.addEventListener("click", () => {
      openQuestionDetails(scope, button.getAttribute("data-open-detail") || "");
    });
  });
}

function openQuestionDetails(scope, detailType) {
  if (!detailType) {
    return;
  }

  const details = Array.from(scope.querySelectorAll(`[data-detail-type="${CSS.escape(detailType)}"]`));
  if (details.length === 0) {
    return;
  }

  details.forEach((detail) => {
    detail.open = true;
  });

  const firstCard = details[0].closest(".question-card") || details[0];
  scrollToElement(firstCard, "center");
  details[0].querySelector("summary")?.focus({ preventScroll: true });
}

function normalizeQuestionDetails(question) {
  const answer = Array.isArray(question.answer) ? question.answer.filter(Boolean) : [];
  const explanation = Array.isArray(question.explanation) ? question.explanation.filter(Boolean) : [];
  const applyOrPitfalls = Array.isArray(question.applyOrPitfalls)
    ? question.applyOrPitfalls.filter(Boolean)
    : [];
  const genericApplyHints = new Set([
    "Hãy gắn khái niệm này với một đoạn code thật hoặc một lỗi production để nhớ lâu hơn.",
    "Áp dụng khi bạn cần đọc code cũ, giải thích cho người khác hoặc quyết định chọn giải pháp nào trong backend Java.",
    "Dễ sai khi chỉ nhớ định nghĩa mà không nối nó với hành vi runtime, dữ liệu thật và trade-off kỹ thuật."
  ]);
  const filteredApplyHints = applyOrPitfalls.filter((item) => !genericApplyHints.has(item));
  const answerShort =
    question.answerShort ||
    answer[0] ||
    (currentLanguage === "vi"
      ? "Câu hỏi này cần được trả lời theo ngữ cảnh cụ thể của Java backend và tình huống thực tế."
      : "This question should be answered in the specific context of Java backend work and a real-world scenario.");
  const fullAnswer = answer.filter((item, index) => {
    if (!item || item === answerShort) {
      return false;
    }
    return answer.indexOf(item) === index;
  });

  return {
    answerShort,
    fullAnswer,
    explanation:
      explanation.length > 0
        ? explanation
        : answer.length > 1
          ? answer.slice(1)
          : [
              currentLanguage === "vi"
                ? "Hãy đọc thêm code, test và flow chạy thực tế để hiểu rõ bản chất của câu hỏi này."
                : "Read the code, tests, and runtime flow to understand the core idea behind this question."
            ],
    applyOrPitfalls:
      filteredApplyHints.length > 0
        ? filteredApplyHints
        : []
  };
}

function renderError(error) {
  const targets = [
    document.getElementById("homeStats"),
    document.getElementById("homePreviewGrid"),
    document.getElementById("featuredQuestions"),
    document.getElementById("questionSections"),
    document.getElementById("levelNav"),
    document.getElementById("levelSections"),
    document.getElementById("knowledgeTree"),
    document.getElementById("roadmapBasics"),
    document.getElementById("roadmapPhases"),
    document.getElementById("technologyMap"),
    document.getElementById("integrationFlow"),
    document.getElementById("practiceMatrix"),
    document.getElementById("roadmapPitfalls"),
    document.getElementById("roadmapVisual"),
    document.getElementById("quizBundles"),
    document.getElementById("quizWorkspace")
  ].filter(Boolean);

  targets.forEach((target) => {
    target.innerHTML = `
      <div class="empty-state error-state">
        <strong>${commonText().jsonError}</strong>
        <span>${escapeHtml(error.message)}</span>
        <button class="btn btn-primary" type="button" data-retry-load>${currentLanguage === "vi" ? "Tải lại" : "Retry"}</button>
      </div>
    `;
  });

  document.querySelectorAll("[data-retry-load]").forEach((button) => {
    button.addEventListener("click", () => {
      window.location.reload();
    });
  });
}

function escapeHtml(value) {
  return String(cleanVietnameseText(value))
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function cleanVietnameseText(value) {
  const text = String(value ?? "");
  if (currentLanguage !== "vi" || !/[ÃÄÆá]/.test(text)) {
    return text;
  }

  try {
    return decodeURIComponent(escape(text));
  } catch {
    return text;
  }
}

function cleanVietnameseMojibakeInDom() {
  if (currentLanguage !== "vi") {
    return;
  }

  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT);
  const textNodes = [];
  while (walker.nextNode()) {
    textNodes.push(walker.currentNode);
  }

  textNodes.forEach((node) => {
    const cleaned = cleanVietnameseText(node.nodeValue);
    if (cleaned !== node.nodeValue) {
      node.nodeValue = cleaned;
    }
  });

  document.querySelectorAll("[aria-label], [title], [placeholder]").forEach((element) => {
    ["aria-label", "title", "placeholder"].forEach((attribute) => {
      if (!element.hasAttribute(attribute)) {
        return;
      }
      const currentValue = element.getAttribute(attribute);
      const cleaned = cleanVietnameseText(currentValue);
      if (cleaned !== currentValue) {
        element.setAttribute(attribute, cleaned);
      }
    });
  });
}

function compactTrackLabel(track) {
  const mapping = {
    "Java Core": "Java Core",
    Data: "Data",
    Engineering: "Engineering",
    "Spring Boot": "Spring Boot",
    Architecture: "Architecture",
    Practice: "Practice"
  };

  return mapping[track] || track;
}

function sortQuestions(questions, mode) {
  const levelRank = {
    "Cơ bản": 1,
    "Trung cấp": 2,
    "Nâng cao": 3,
    Basic: 1,
    Intermediate: 2,
    Advanced: 3
  };

  const sorted = [...questions];

  if (mode === "level-asc") {
    sorted.sort((a, b) => (levelRank[a.level] || 99) - (levelRank[b.level] || 99));
  } else if (mode === "level-desc") {
    sorted.sort((a, b) => (levelRank[b.level] || 0) - (levelRank[a.level] || 0));
  } else if (mode === "title-asc") {
    sorted.sort((a, b) => a.question.localeCompare(b.question, "vi"));
  }

  return sorted;
}

function summarizeQuestionGroup(questions, fields) {
  const labels = {
    level: currentLanguage === "vi" ? "Level" : "Level",
    kind: currentLanguage === "vi" ? "Loại" : "Type",
    track: "Track",
    topicTitle: currentLanguage === "vi" ? "Topic" : "Topic"
  };

  return fields
    .map((field) => {
      const values = [...new Set(questions.map((question) => question[field]).filter(Boolean))];
      if (values.length === 0) {
        return "";
      }

      const preview = values.slice(0, 3).join(", ");
      const suffix = values.length > 3 ? ` +${values.length - 3}` : "";
      return `${labels[field] || field}: ${preview}${suffix}`;
    })
    .filter(Boolean);
}

function highlightText(value, keyword) {
  const text = String(value);
  if (!keyword) {
    return escapeHtml(text);
  }

  const lowerText = text.toLowerCase();
  const lowerKeyword = keyword.toLowerCase();
  const parts = [];
  let start = 0;
  let index = lowerText.indexOf(lowerKeyword, start);

  while (index !== -1) {
    parts.push(escapeHtml(text.slice(start, index)));
    parts.push(
      `<mark class="search-highlight">${escapeHtml(text.slice(index, index + keyword.length))}</mark>`
    );
    start = index + keyword.length;
    index = lowerText.indexOf(lowerKeyword, start);
  }

  parts.push(escapeHtml(text.slice(start)));
  return parts.join("");
}

function renderQuizQuestionCard(question) {
  return `
    <article class="quiz-question-card" data-question-id="${escapeHtml(question.id)}">
      <div class="quiz-question-head" id="quiz-${escapeHtml(question.id)}">
        <span class="question-number">${question.order}</span>
        <p class="eyebrow">${escapeHtml(question.topic)}</p>
        <h3>${escapeHtml(question.prompt)}</h3>
      </div>
      <div class="quiz-options">
        ${question.orderedOptions
          .map(
            (option, index) => `
              <label class="quiz-option">
                <input type="radio" name="${escapeHtml(question.id)}" value="${index}" />
                <span>${escapeHtml(option)}</span>
              </label>
            `
          )
          .join("")}
      </div>
    </article>
  `;
}
