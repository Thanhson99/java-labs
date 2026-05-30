function renderQuiz(quizBank) {
  const quizBundleCount = document.getElementById("quizBundleCount");
  const quizQuestionPoolCount = document.getElementById("quizQuestionPoolCount");
  const quizBundles = document.getElementById("quizBundles");
  const quizWorkspace = document.getElementById("quizWorkspace");

  if (!quizBundles || !quizWorkspace) {
    return;
  }

  const questionMap = new Map(quizBank.questions.map((question) => [question.id, question]));

  if (quizBundleCount) {
    quizBundleCount.textContent = String(quizBank.bundles.length);
  }

  if (quizQuestionPoolCount) {
    quizQuestionPoolCount.textContent = String(quizBank.questions.length);
  }

  const savedQuizState = {
    ...readPageState("quiz"),
    ...readQueryState(["bundleId"])
  };
  let activeBundleId = quizBank.bundles.some((bundle) => bundle.id === savedQuizState.bundleId)
    ? savedQuizState.bundleId
    : quizBank.bundles[0]?.id || "";
  let currentAttempt = null;
  let savedAnswers = readPageState("quiz-answers");

  const startAttempt = (bundleId) => {
    const bundle = quizBank.bundles.find((item) => item.id === bundleId);
    if (!bundle) {
      return;
    }

    activeBundleId = bundleId;
    writePageState("quiz", { bundleId });
    writeQueryState({ bundleId }, ["bundleId"]);
    const poolQuestions = bundle.questionIds
      .map((id) => questionMap.get(id))
      .filter(Boolean);
    const selectedQuestions = poolQuestions;

    currentAttempt = {
      bundle,
      questions: selectedQuestions.map((question, index) => {
        return {
          ...question,
          order: index + 1,
          orderedOptions: question.options,
          orderedCorrectIndex: question.correctIndex
        };
      })
    };

    renderBundles();
    renderQuizWorkspace();
  };

  const renderBundles = () => {
    quizBundles.innerHTML = quizBank.bundles
      .map(
        (bundle) => {
          const savedAnswerCount = Object.keys(savedAnswers[bundle.id] || {}).length;
          const hasProgress = savedAnswerCount > 0;
          const isActive = bundle.id === activeBundleId;
          return `
          <article class="quiz-bundle-card${bundle.id === activeBundleId ? " active" : ""}">
            <div class="quiz-bundle-head">
              <p class="eyebrow">${escapeHtml(bundle.track)}</p>
              <span class="bundle-status">${
                isActive
                  ? currentLanguage === "vi" ? "Đang chọn" : "Selected"
                  : hasProgress
                    ? currentLanguage === "vi" ? "Đang làm" : "In progress"
                    : currentLanguage === "vi" ? "Bộ đề" : "Set"
              }</span>
            </div>
            <h3>${escapeHtml(bundle.title)}</h3>
            <p>${escapeHtml(bundle.description)}</p>
            <div class="quiz-bundle-meta">
              <span><small>${commonText().drawCount}</small><strong>${bundle.questionIds.length} ${currentLanguage === "vi" ? "câu" : "questions"}</strong></span>
              <span><small>${commonText().level}</small><strong>${escapeHtml(bundle.level)}</strong></span>
              <span><small>${commonText().suggestedTime}</small><strong>${escapeHtml(bundle.suggestedTime)}</strong></span>
            </div>
            ${
              hasProgress
                ? `
                  <div class="bundle-resume-note">
                    <span>${currentLanguage === "vi" ? "Tiến độ đã lưu" : "Saved progress"}</span>
                    <strong>${savedAnswerCount}/${bundle.questionIds.length}</strong>
                  </div>
                `
                : ""
            }
            <button class="btn btn-primary bundle-button" type="button" data-bundle-id="${escapeHtml(bundle.id)}" aria-pressed="${bundle.id === activeBundleId}">
              ${isActive
                ? hasProgress
                  ? currentLanguage === "vi" ? "Tiếp tục bộ đề" : "Continue bundle"
                  : currentLanguage === "vi" ? "Làm bộ đề này" : "Current bundle"
                : hasProgress
                  ? currentLanguage === "vi" ? "Tiếp tục" : "Resume"
                : commonText().startBundle}
            </button>
          </article>
        `;
        }
      )
      .join("");

    quizBundles.querySelectorAll("[data-bundle-id]").forEach((button) => {
      button.addEventListener("click", () => {
        startAttempt(button.getAttribute("data-bundle-id") || "");
      });
    });
  };

  const renderQuizWorkspace = () => {
    if (!currentAttempt) {
      quizWorkspace.innerHTML = `<div class="empty-state">${commonText().chooseBundle}</div>`;
      return;
    }

    const { bundle, questions } = currentAttempt;

    quizWorkspace.innerHTML = `
      <div class="quiz-summary-card">
        <div class="quiz-summary-head">
          <div>
            <p class="eyebrow">${escapeHtml(bundle.track)} / ${escapeHtml(bundle.title)}</p>
            <h3>${commonText().drawCount}: ${questions.length} ${currentLanguage === "vi" ? "câu" : "questions"}</h3>
          </div>
          <span class="bundle-status">${escapeHtml(bundle.level)}</span>
        </div>
        <div class="quiz-progress" aria-label="Quiz progress">
          <span id="quizProgressBar"></span>
        </div>
        <div class="quiz-summary-grid">
          <article><span>${commonText().totalQuestions}</span><strong>${questions.length}</strong></article>
          <article><span>${commonText().pool}</span><strong>${bundle.questionIds.length}</strong></article>
          <article><span>${commonText().suggestedTime}</span><strong>${escapeHtml(bundle.suggestedTime)}</strong></article>
          <article><span>${commonText().level}</span><strong>${escapeHtml(bundle.level)}</strong></article>
          <article><span>${currentLanguage === "vi" ? "Đã trả lời" : "Answered"}</span><strong id="quizAnsweredLive">0/${questions.length}</strong></article>
        </div>
        <div class="quiz-jump-list">
          ${questions.map((question) => `<a href="#quiz-${escapeHtml(question.id)}" data-jump-question="${escapeHtml(question.id)}">${question.order}</a>`).join("")}
        </div>
        <div class="quiz-action-row">
          <button class="btn btn-secondary" type="button" data-quiz-next-unanswered>
            ${currentLanguage === "vi" ? "Câu chưa trả lời tiếp theo" : "Next unanswered"}
          </button>
          <button class="btn btn-primary" type="button" data-quiz-submit>${commonText().submit}</button>
          <button class="btn btn-secondary" type="button" data-quiz-retry>${commonText().retry}</button>
        </div>
      </div>
      <div id="quizResult"></div>
      <div class="quiz-actions">
        ${questions.map((question) => renderQuizQuestionCard(question)).join("")}
      </div>
      <div class="quiz-action-row quiz-action-row-bottom">
        <button class="btn btn-secondary" type="button" data-quiz-next-unanswered>
          ${currentLanguage === "vi" ? "Câu chưa trả lời tiếp theo" : "Next unanswered"}
        </button>
        <button class="btn btn-primary" type="button" data-quiz-submit>${commonText().submit}</button>
        <button class="btn btn-secondary" type="button" data-quiz-retry>${commonText().retry}</button>
      </div>
    `;

    quizWorkspace.querySelectorAll("[data-quiz-submit]").forEach((button) => button.addEventListener("click", () => {
      submitQuizAttempt(bundle);
    }));

    quizWorkspace.querySelectorAll("[data-quiz-retry]").forEach((button) => button.addEventListener("click", () => {
      delete savedAnswers[bundle.id];
      writePageState("quiz-answers", savedAnswers);
      startAttempt(bundle.id);
    }));

    quizWorkspace.querySelectorAll("[data-quiz-next-unanswered]").forEach((button) => {
      button.addEventListener("click", scrollToNextUnansweredQuizQuestion);
    });

    quizWorkspace.onchange = (event) => {
      const target = event.target;
      if (target instanceof HTMLInputElement && target.type === "radio") {
        savedAnswers[activeBundleId] = {
          ...(savedAnswers[activeBundleId] || {}),
          [target.name]: target.value
        };
        writePageState("quiz-answers", savedAnswers);
      }
      updateQuizProgress();
    };
    mountActiveLinkFeedback(quizWorkspace);
    restoreQuizAnswers();
    updateQuizProgress();
  };

  const restoreQuizAnswers = () => {
    const answers = savedAnswers[activeBundleId] || {};
    Object.entries(answers).forEach(([questionId, answerIndex]) => {
      const input = quizWorkspace.querySelector(
        `input[type="radio"][name="${CSS.escape(questionId)}"][value="${CSS.escape(String(answerIndex))}"]`
      );
      if (input) {
        input.checked = true;
      }
    });
  };

  const updateQuizProgress = () => {
    if (!currentAttempt) {
      return;
    }

    const cards = Array.from(document.querySelectorAll(".quiz-question-card"));
    const answeredCount = cards.reduce((count, card) => {
      const answered = Boolean(card.querySelector("input[type='radio']:checked"));
      card.classList.toggle("answered", answered);
      const questionId = card.getAttribute("data-question-id") || "";
      const jump = document.querySelector(`[data-jump-question="${CSS.escape(questionId)}"]`);
      jump?.classList.toggle("answered", answered);
      return count + (answered ? 1 : 0);
    }, 0);
    const target = document.getElementById("quizAnsweredLive");
    if (target) {
      target.textContent = `${answeredCount}/${currentAttempt.questions.length}`;
    }
    const progressBar = document.getElementById("quizProgressBar");
    if (progressBar) {
      const percent = currentAttempt.questions.length
        ? (answeredCount / currentAttempt.questions.length) * 100
        : 0;
      progressBar.style.width = `${percent}%`;
    }
    const allAnswered = answeredCount >= currentAttempt.questions.length;
    quizWorkspace.querySelectorAll("[data-quiz-next-unanswered]").forEach((button) => {
      button.disabled = allAnswered;
      button.textContent = allAnswered
        ? currentLanguage === "vi" ? "Đã trả lời hết" : "All answered"
        : currentLanguage === "vi" ? "Câu chưa trả lời tiếp theo" : "Next unanswered";
    });
  };

  const scrollToNextUnansweredQuizQuestion = () => {
    const nextCard = Array.from(quizWorkspace.querySelectorAll(".quiz-question-card"))
      .find((card) => !card.querySelector("input[type='radio']:checked"));
    if (!nextCard) {
      return;
    }

    scrollToElement(nextCard, "center");
    const firstOption = nextCard.querySelector("input[type='radio']");
    firstOption?.focus({ preventScroll: true });
    nextCard.classList.remove("quiz-focus-pulse");
    window.requestAnimationFrame(() => {
      nextCard.classList.add("quiz-focus-pulse");
      window.setTimeout(() => nextCard.classList.remove("quiz-focus-pulse"), 900);
    });
  };

  const submitQuizAttempt = (bundle) => {
    if (!currentAttempt) {
      return;
    }

    const cards = Array.from(document.querySelectorAll(".quiz-question-card"));
    let correctCount = 0;
    let answeredCount = 0;
    const missedQuestions = [];
    const unansweredQuestions = [];

    cards.forEach((card, index) => {
      const selected = card.querySelector("input[type='radio']:checked");
      const question = currentAttempt.questions[index];
      const options = Array.from(card.querySelectorAll(".quiz-option"));
      card.classList.toggle("unanswered", !selected);

      options.forEach((option, optionIndex) => {
        option.classList.remove("correct", "incorrect");
        if (optionIndex === question.orderedCorrectIndex) {
          option.classList.add("correct");
        }
      });

      if (selected) {
        answeredCount += 1;
        const selectedIndex = Number(selected.value);
        if (selectedIndex === question.orderedCorrectIndex) {
          correctCount += 1;
        } else {
          selected.closest(".quiz-option")?.classList.add("incorrect");
          missedQuestions.push(question);
        }
      } else {
        unansweredQuestions.push(question);
      }
    });

    const percent = Math.round((correctCount / currentAttempt.questions.length) * 100);
    const band = bundle.bands.find((item) => percent >= item.minScore) || bundle.bands[bundle.bands.length - 1];
    const resultTarget = document.getElementById("quizResult");

    if (resultTarget) {
      const unansweredCount = currentAttempt.questions.length - answeredCount;
      const topicBreakdown = buildQuizTopicBreakdown(currentAttempt.questions, cards);
      resultTarget.innerHTML = `
        <article class="quiz-result-card">
          <p class="eyebrow">${commonText().result}</p>
          <h3>${escapeHtml(band.label)}</h3>
          <p>${escapeHtml(band.description)}</p>
          <div class="quiz-result-grid">
            <article><span>${commonText().correctScore}</span><strong>${correctCount}/${currentAttempt.questions.length}</strong></article>
            <article><span>${commonText().ratio}</span><strong>${percent}%</strong></article>
            <article><span>${commonText().answered}</span><strong>${answeredCount}</strong></article>
            <article><span>${currentLanguage === "vi" ? "Chưa trả lời" : "Unanswered"}</span><strong>${unansweredCount}</strong></article>
            <article><span>${commonText().assessment}</span><strong>${escapeHtml(band.grade)}</strong></article>
          </div>
          <div class="quiz-review-actions">
            ${renderQuizReviewLinks(missedQuestions, currentLanguage === "vi" ? "Câu sai" : "Missed")}
            ${renderQuizReviewLinks(unansweredQuestions, currentLanguage === "vi" ? "Chưa trả lời" : "Unanswered")}
          </div>
          ${renderQuizTopicBreakdown(topicBreakdown)}
        </article>
      `;
      mountQuizTopicMeters(resultTarget);
      resultTarget.scrollIntoView({ behavior: "smooth", block: "start" });
      if (unansweredCount > 0) {
        document.querySelector(".quiz-question-card.unanswered")?.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    }
  };

  renderBundles();
  startAttempt(activeBundleId);
}

function buildQuizTopicBreakdown(questions, cards) {
  const topicMap = new Map();

  questions.forEach((question, index) => {
    const topic = question.topic || (currentLanguage === "vi" ? "Chưa phân loại" : "Uncategorized");
    const selected = cards[index]?.querySelector("input[type='radio']:checked");
    const selectedIndex = selected ? Number(selected.value) : null;
    const current = topicMap.get(topic) || {
      topic,
      total: 0,
      answered: 0,
      correct: 0,
      missed: 0,
      unanswered: 0
    };

    current.total += 1;
    if (selectedIndex === null) {
      current.unanswered += 1;
    } else {
      current.answered += 1;
      if (selectedIndex === question.orderedCorrectIndex) {
        current.correct += 1;
      } else {
        current.missed += 1;
      }
    }

    topicMap.set(topic, current);
  });

  return [...topicMap.values()].sort((a, b) => {
    const aRate = a.total ? a.correct / a.total : 0;
    const bRate = b.total ? b.correct / b.total : 0;
    return aRate - bRate || b.total - a.total || a.topic.localeCompare(b.topic, "vi");
  });
}

function renderQuizTopicBreakdown(items) {
  if (!items.length) {
    return "";
  }

  return `
    <section class="quiz-topic-breakdown" aria-label="${currentLanguage === "vi" ? "Kết quả theo chủ đề" : "Results by topic"}">
      <div class="quiz-topic-breakdown-head">
        <h4>${currentLanguage === "vi" ? "Cần ôn theo chủ đề" : "Topic review map"}</h4>
        <p>${currentLanguage === "vi" ? "Các chủ đề yếu hơn được đưa lên trước để bạn ôn lại đúng trọng tâm." : "Weaker topics are shown first so you can review the right areas next."}</p>
      </div>
      <div class="quiz-topic-breakdown-list">
        ${items
          .map((item) => {
            const percent = item.total ? Math.round((item.correct / item.total) * 100) : 0;
            const reviewUrl = getQuizTopicReviewUrl(item.topic);
            return `
              <article class="quiz-topic-score${percent < 60 ? " needs-review" : ""}">
                <div>
                  <strong>${escapeHtml(item.topic)}</strong>
                  <span>${item.correct}/${item.total} ${currentLanguage === "vi" ? "đúng" : "correct"} · ${item.unanswered} ${currentLanguage === "vi" ? "chưa trả lời" : "unanswered"}</span>
                </div>
                <div class="topic-score-meter" aria-label="${escapeHtml(item.topic)} ${percent}%">
                  <span data-topic-score-percent="${percent}"></span>
                </div>
                <em>${percent}%</em>
                <a class="topic-review-link" href="${escapeHtml(reviewUrl)}">
                  ${currentLanguage === "vi" ? "Ôn topic" : "Review topic"}
                </a>
              </article>
            `;
          })
          .join("")}
      </div>
    </section>
  `;
}

function getQuizTopicReviewUrl(topic) {
  const params = new URLSearchParams();
  params.set("search", topic);
  params.set("lang", currentLanguage);
  const activeTheme = document.documentElement.dataset.theme || currentTheme;
  if (activeTheme) {
    params.set("theme", activeTheme);
  }
  return `interview.html?${params.toString()}`;
}

function mountQuizTopicMeters(scope = document) {
  scope.querySelectorAll("[data-topic-score-percent]").forEach((meter) => {
    const percent = Number(meter.getAttribute("data-topic-score-percent") || 0);
    meter.style.width = `${Math.min(100, Math.max(0, percent))}%`;
  });
}

