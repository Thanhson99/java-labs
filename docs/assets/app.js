const QUESTION_BANK_PATH = "data/content/question-bank.json";
const ROADMAP_PATH = "data/roadmap/backend-roadmap.json";
const QUIZ_BANK_PATH = "data/quizzes/quiz-bank.json";

document.addEventListener("DOMContentLoaded", async () => {
  const page = document.body.dataset.page;

  try {
    if (page === "home") {
      return;
    }

    if (page === "interview") {
      const response = await fetch(QUESTION_BANK_PATH);
      if (!response.ok) {
        throw new Error(`Failed to load question bank: ${response.status}`);
      }

      const bank = await response.json();
      renderInterview(bank);
      return;
    }

    if (page === "interview-levels") {
      const response = await fetch(QUESTION_BANK_PATH);
      if (!response.ok) {
        throw new Error(`Failed to load question bank: ${response.status}`);
      }

      const bank = await response.json();
      renderInterviewLevels(bank);
      return;
    }

    if (page === "roadmap") {
      const response = await fetch(ROADMAP_PATH);
      if (!response.ok) {
        throw new Error(`Failed to load roadmap: ${response.status}`);
      }

      const roadmap = await response.json();
      renderRoadmap(roadmap);
      return;
    }

    if (page === "quiz") {
      const response = await fetch(QUIZ_BANK_PATH);
      if (!response.ok) {
        throw new Error(`Failed to load quiz bank: ${response.status}`);
      }

      const quizBank = await response.json();
      renderQuiz(quizBank);
    }
  } catch (error) {
    renderError(error);
  }
});

function renderHome(bank) {
  return bank;
}

function renderInterview(bank) {
  const topicNav = document.getElementById("topicNav");
  const questionSections = document.getElementById("questionSections");
  const topicCount = document.getElementById("topicCount");
  const questionCount = document.getElementById("questionCount");
  const questionSearch = document.getElementById("questionSearch");
  const levelFilter = document.getElementById("levelFilter");
  const kindFilter = document.getElementById("kindFilter");
  const sortFilter = document.getElementById("sortFilter");

  if (!topicNav || !questionSections) {
    return;
  }

  const totalQuestions = bank.topics.reduce((sum, topic) => sum + topic.questions.length, 0);
  const levels = [...new Set(bank.topics.flatMap((topic) => topic.questions.map((question) => question.level)))];
  const kinds = [...new Set(bank.topics.flatMap((topic) => topic.questions.map((question) => question.kind)))];

  if (topicCount) {
    topicCount.textContent = String(bank.topics.length);
  }

  if (questionCount) {
    questionCount.textContent = String(totalQuestions);
  }

  if (levelFilter) {
    levelFilter.innerHTML = `<option value="">Tất cả</option>${levels
      .map((level) => `<option value="${escapeHtml(level)}">${escapeHtml(level)}</option>`)
      .join("")}`;
  }

  if (kindFilter) {
    kindFilter.innerHTML = `<option value="">Tất cả</option>${kinds
      .map((kind) => `<option value="${escapeHtml(kind)}">${escapeHtml(kind)}</option>`)
      .join("")}`;
  }

  const renderFiltered = () => {
    const keyword = questionSearch?.value.trim().toLowerCase() || "";
    const selectedLevel = levelFilter?.value || "";
    const selectedKind = kindFilter?.value || "";
    const selectedSort = sortFilter?.value || "default";

    const filteredTopics = bank.topics
      .map((topic) => ({
        ...topic,
        questions: topic.questions.filter((question) => {
          const haystack = getQuestionSearchText({
            ...question,
            topicTitle: topic.title,
            topicSummary: topic.summary
          }).toLowerCase();

          const matchesKeyword = !keyword || haystack.includes(keyword);
          const matchesLevel = !selectedLevel || question.level === selectedLevel;
          const matchesKind = !selectedKind || question.kind === selectedKind;

          return matchesKeyword && matchesLevel && matchesKind;
        }),
        originalIndex: bank.topics.findIndex((item) => item.id === topic.id)
      }))
      .map((topic) => ({
        ...topic,
        questions: sortQuestions(topic.questions, selectedSort)
      }))
      .filter((topic) => topic.questions.length > 0);

    const sortedTopics = selectedSort === "title-asc"
      ? [...filteredTopics].sort((a, b) => a.title.localeCompare(b.title, "vi"))
      : filteredTopics.sort((a, b) => a.originalIndex - b.originalIndex);

    topicNav.innerHTML = sortedTopics.length
      ? sortedTopics
          .map(
            (topic) => `
              <a class="topic-link" href="#${escapeHtml(topic.id)}">
                <span class="topic-track">${escapeHtml(compactTrackLabel(topic.track))}</span>
                <span class="topic-link-body">
                  <strong title="${escapeHtml(topic.title)}">${highlightText(topic.title, keyword)}</strong>
                  <small title="${escapeHtml(topic.summary)}">${highlightText(topic.summary, keyword)}</small>
                </span>
              </a>
            `
          )
          .join("")
      : `<div class="empty-state">Không có chủ đề nào khớp bộ lọc hiện tại.</div>`;

    questionSections.innerHTML = sortedTopics.length
      ? sortedTopics
          .map(
            (topic) => `
              <section class="topic-block" id="${escapeHtml(topic.id)}">
                <div class="topic-header">
                  <p class="eyebrow">${escapeHtml(topic.track)}</p>
                  <h2>${highlightText(topic.title, keyword)}</h2>
                  <p class="topic-summary">${highlightText(topic.summary, keyword)}</p>
                </div>
                ${topic.questions.map((question) => renderQuestionCard(question, keyword)).join("")}
              </section>
            `
          )
          .join("")
      : `<div class="empty-state">Không tìm thấy câu hỏi phù hợp. Hãy đổi từ khóa hoặc bộ lọc.</div>`;

    if (questionCount) {
      questionCount.textContent = String(
        sortedTopics.reduce((sum, topic) => sum + topic.questions.length, 0)
      );
    }
  };

  questionSearch?.addEventListener("input", renderFiltered);
  levelFilter?.addEventListener("change", renderFiltered);
  kindFilter?.addEventListener("change", renderFiltered);
  sortFilter?.addEventListener("change", renderFiltered);

  renderFiltered();
}

function renderInterviewLevels(bank) {
  const levelNav = document.getElementById("levelNav");
  const levelSections = document.getElementById("levelSections");
  const levelTotalQuestions = document.getElementById("levelTotalQuestions");
  const levelGroupCount = document.getElementById("levelGroupCount");
  const levelSearch = document.getElementById("levelSearch");
  const levelTrackFilter = document.getElementById("levelTrackFilter");
  const levelKindFilter = document.getElementById("levelKindFilter");
  const levelSortFilter = document.getElementById("levelSortFilter");

  if (!levelNav || !levelSections) {
    return;
  }

  const flatQuestions = bank.topics.flatMap((topic) =>
    topic.questions.map((question) => ({
      ...question,
      topicId: topic.id,
      topicTitle: topic.title,
      topicSummary: topic.summary,
      track: topic.track
    }))
  );

  const totalQuestions = flatQuestions.length;
  const tracks = [...new Set(flatQuestions.map((question) => question.track))];
  const kinds = [...new Set(flatQuestions.map((question) => question.kind))];
  const levels = [...new Set(flatQuestions.map((question) => question.level))];
  let activeLevel = "";

  if (levelTotalQuestions) {
    levelTotalQuestions.textContent = String(totalQuestions);
  }

  if (levelGroupCount) {
    levelGroupCount.textContent = String(levels.length);
  }

  if (levelTrackFilter) {
    levelTrackFilter.innerHTML = `<option value="">Tất cả</option>${tracks
      .map((track) => `<option value="${escapeHtml(track)}">${escapeHtml(track)}</option>`)
      .join("")}`;
  }

  if (levelKindFilter) {
    levelKindFilter.innerHTML = `<option value="">Tất cả</option>${kinds
      .map((kind) => `<option value="${escapeHtml(kind)}">${escapeHtml(kind)}</option>`)
      .join("")}`;
  }

  const renderByLevel = () => {
    const keyword = levelSearch?.value.trim().toLowerCase() || "";
    const selectedTrack = levelTrackFilter?.value || "";
    const selectedKind = levelKindFilter?.value || "";
    const selectedSort = levelSortFilter?.value || "default";

    const filteredQuestions = flatQuestions.filter((question) => {
      const haystack = getQuestionSearchText(question).toLowerCase();

      const matchesKeyword = !keyword || haystack.includes(keyword);
      const matchesTrack = !selectedTrack || question.track === selectedTrack;
      const matchesKind = !selectedKind || question.kind === selectedKind;

      return matchesKeyword && matchesTrack && matchesKind;
    });

    const grouped = levels
      .map((level, index) => ({
        id: `level-${index + 1}`,
        level,
        questions: filteredQuestions.filter((question) => question.level === level)
      }))
      .filter((group) => group.questions.length > 0)
      .filter((group) => !activeLevel || group.level === activeLevel);

    const sortedGroups = [...grouped];
    if (selectedSort === "questions-desc") {
      sortedGroups.sort((a, b) => b.questions.length - a.questions.length);
    } else if (selectedSort === "questions-asc") {
      sortedGroups.sort((a, b) => a.questions.length - b.questions.length);
    } else if (selectedSort === "title-asc") {
      sortedGroups.sort((a, b) => a.level.localeCompare(b.level, "vi"));
    }

    const navGroups = levels
      .map((level, index) => ({
        id: `level-${index + 1}`,
        level,
        count: filteredQuestions.filter((question) => question.level === level).length
      }))
      .filter((group) => group.count > 0);

    levelNav.innerHTML = navGroups.length
      ? [
          `
            <button class="topic-link${activeLevel === "" ? " active-filter" : ""}" type="button" data-level-filter="">
              <span class="topic-track">Tất cả</span>
              <span class="topic-link-body">
                <small>${filteredQuestions.length} câu hỏi phỏng vấn</small>
              </span>
            </button>
          `,
          ...navGroups
            .map(
              (group) => `
                <button class="topic-link${activeLevel === group.level ? " active-filter" : ""}" type="button" data-level-filter="${escapeHtml(group.level)}">
                  <span class="topic-track">${escapeHtml(group.level)}</span>
                  <span class="topic-link-body">
                    <small>${group.count} câu hỏi phỏng vấn</small>
                  </span>
                </button>
              `
            )
        ].join("")
      : `<div class="empty-state">Không có level nào khớp bộ lọc hiện tại.</div>`;

    levelNav.querySelectorAll("[data-level-filter]").forEach((button) => {
      button.addEventListener("click", () => {
        activeLevel = button.getAttribute("data-level-filter") || "";
        renderByLevel();
      });
    });

    levelSections.innerHTML = sortedGroups.length
      ? sortedGroups
          .map(
            (group) => `
              <section class="topic-block" id="${escapeHtml(group.id)}">
                <div class="topic-header">
                  <p class="eyebrow">Interview Level</p>
                  <h2>${escapeHtml(group.level)}</h2>
                  <p class="topic-summary">${group.questions.length} câu hỏi đã được tổng hợp ở mức này.</p>
                </div>
                ${group.questions
                  .map(
                    (question) => `
                      ${renderQuestionCard(question, keyword, [
                        question.track,
                        question.kind,
                        question.topicTitle
                      ])}
                    `
                  )
                  .join("")}
              </section>
            `
          )
          .join("")
      : `<div class="empty-state">Không tìm thấy câu hỏi phỏng vấn phù hợp. Hãy đổi từ khóa hoặc bộ lọc.</div>`;

    if (levelTotalQuestions) {
      levelTotalQuestions.textContent = String(filteredQuestions.length);
    }
  };

  levelSearch?.addEventListener("input", renderByLevel);
  levelTrackFilter?.addEventListener("change", renderByLevel);
  levelKindFilter?.addEventListener("change", renderByLevel);
  levelSortFilter?.addEventListener("change", renderByLevel);

  renderByLevel();
}

function renderRoadmap(roadmap) {
  const phaseCount = document.getElementById("roadmapPhaseCount");
  const topicCount = document.getElementById("roadmapTopicCount");
  const roadmapBasics = document.getElementById("roadmapBasics");
  const knowledgeTree = document.getElementById("knowledgeTree");
  const technologyMap = document.getElementById("technologyMap");
  const integrationFlow = document.getElementById("integrationFlow");
  const roadmapPhases = document.getElementById("roadmapPhases");
  const practiceMatrix = document.getElementById("practiceMatrix");
  const roadmapPitfalls = document.getElementById("roadmapPitfalls");
  const roadmapVisual = document.getElementById("roadmapVisual");

  if (phaseCount) {
    phaseCount.textContent = String(roadmap.phases.length);
  }

  if (topicCount) {
    topicCount.textContent = String(
      roadmap.phases.reduce((sum, phase) => sum + phase.topics.length, 0)
    );
  }

  if (roadmapBasics) {
    roadmapBasics.innerHTML = roadmap.basicsChecklist
      .map(
        (item) => `
          <article class="basic-card">
            <div class="basic-card-head">
              <span class="basic-step">${escapeHtml(item.step)}</span>
              <h3>${escapeHtml(item.title)}</h3>
            </div>
            <p>${escapeHtml(item.summary)}</p>
            <ul class="phase-list">
              ${item.checks.map((check) => `<li>${escapeHtml(check)}</li>`).join("")}
            </ul>
          </article>
        `
      )
      .join("");
  }

  if (knowledgeTree) {
    knowledgeTree.innerHTML = roadmap.tree
      .map(
        (branch) => `
          <article class="tree-branch">
            <div class="tree-icon">${escapeHtml(branch.icon)}</div>
            <div class="tree-body">
              <h3>${escapeHtml(branch.title)}</h3>
              <p>${escapeHtml(branch.summary)}</p>
              <div class="tree-chip-list">
                ${branch.nodes.map((node) => `<span class="tree-chip">${escapeHtml(node)}</span>`).join("")}
              </div>
            </div>
          </article>
        `
      )
      .join("");
  }

  if (technologyMap) {
    technologyMap.innerHTML = roadmap.technologyMap
      .map(
        (item) => `
          <article class="technology-card">
            <div class="technology-head">
              <div>
                <p class="eyebrow">${escapeHtml(item.layer)}</p>
                <h3>${escapeHtml(item.name)}</h3>
              </div>
              <span class="phase-badge">${escapeHtml(item.level)}</span>
            </div>
            <p class="technology-summary">${escapeHtml(item.purpose)}</p>
            <div class="technology-grid">
              <article class="technology-block">
                <h4>Học để làm gì?</h4>
                <ul class="phase-list">
                  ${item.learnFor.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
                </ul>
              </article>
              <article class="technology-block">
                <h4>Tích hợp với gì?</h4>
                <ul class="phase-list">
                  ${item.integratesWith.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
                </ul>
              </article>
              <article class="technology-block">
                <h4>Trong repo nên nhìn đâu?</h4>
                <ul class="phase-list">
                  ${item.lookAt.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
                </ul>
              </article>
            </div>
          </article>
        `
      )
      .join("");
  }

  if (integrationFlow) {
    integrationFlow.innerHTML = roadmap.integrationFlow
      .map(
        (item, index) => `
          <article class="integration-card">
            <div class="integration-index">${index + 1}</div>
            <div class="integration-body">
              <p class="eyebrow">${escapeHtml(item.stage)}</p>
              <h3>${escapeHtml(item.title)}</h3>
              <p>${escapeHtml(item.summary)}</p>
              <ul class="phase-list">
                ${item.links.map((link) => `<li>${escapeHtml(link)}</li>`).join("")}
              </ul>
            </div>
          </article>
        `
      )
      .join("");
  }

  if (roadmapPhases) {
    roadmapPhases.innerHTML = roadmap.phases
      .map(
        (phase) => `
          <section class="phase-card">
            <div class="phase-head">
              <div>
                <p class="eyebrow">${escapeHtml(phase.stage)}</p>
                <h3>${escapeHtml(phase.title)}</h3>
              </div>
              <span class="phase-badge">${escapeHtml(phase.duration)}</span>
            </div>
            <p class="phase-summary">${escapeHtml(phase.summary)}</p>
            <div class="phase-subgrid">
              <article class="phase-block">
                <h4>Học gì?</h4>
                <ul class="phase-list">
                  ${phase.topics.map((topic) => `<li>${escapeHtml(topic)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>Làm gì trong repo?</h4>
                <ul class="phase-list">
                  ${phase.actions.map((action) => `<li>${escapeHtml(action)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>Khi xong phải đạt</h4>
                <ul class="phase-list">
                  ${phase.outcomes.map((outcome) => `<li>${escapeHtml(outcome)}</li>`).join("")}
                </ul>
              </article>
            </div>
          </section>
        `
      )
      .join("");
  }

  if (roadmapPitfalls) {
    roadmapPitfalls.innerHTML = roadmap.pitfalls
      .map(
        (item) => `
          <article class="pitfall-card">
            <h3>${escapeHtml(item.title)}</h3>
            <p>${escapeHtml(item.why)}</p>
            <div class="phase-subgrid">
              <article class="phase-block">
                <h4>Dễ hiểu sai ở đâu?</h4>
                <ul class="phase-list">
                  ${item.mistakes.map((mistake) => `<li>${escapeHtml(mistake)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>Nên sửa cách học thế nào?</h4>
                <ul class="phase-list">
                  ${item.fix.map((step) => `<li>${escapeHtml(step)}</li>`).join("")}
                </ul>
              </article>
            </div>
          </article>
        `
      )
      .join("");
  }

  if (practiceMatrix) {
    practiceMatrix.innerHTML = roadmap.practiceMap
      .map(
        (item) => `
          <article class="practice-card">
            <h3>${escapeHtml(item.when)}</h3>
            <p>${escapeHtml(item.focus)}</p>
            <ul class="phase-list">
              ${item.doNow.map((step) => `<li>${escapeHtml(step)}</li>`).join("")}
            </ul>
          </article>
        `
      )
      .join("");
  }

  if (roadmapVisual) {
    roadmapVisual.innerHTML = roadmap.visualSteps
      .map(
        (step, index) => `
          <article class="visual-step-card">
            <div class="visual-step-index">${index + 1}</div>
            <div class="visual-step-body">
              <p class="eyebrow">${escapeHtml(step.level)}</p>
              <h3>${escapeHtml(step.title)}</h3>
              <p>${escapeHtml(step.short)}</p>
              <div class="tree-chip-list">
                ${step.focus.map((item) => `<span class="tree-chip">${escapeHtml(item)}</span>`).join("")}
              </div>
            </div>
          </article>
        `
      )
      .join("");
  }
}

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

  let activeBundleId = quizBank.bundles[0]?.id || "";
  let currentAttempt = null;

  const startAttempt = (bundleId) => {
    const bundle = quizBank.bundles.find((item) => item.id === bundleId);
    if (!bundle) {
      return;
    }

    activeBundleId = bundleId;
    const poolQuestions = bundle.questionIds
      .map((id) => questionMap.get(id))
      .filter(Boolean);
    const selectedQuestions = shuffleArray(poolQuestions).slice(0, bundle.drawCount);

    currentAttempt = {
      bundle,
      questions: selectedQuestions.map((question, index) => {
        const shuffledOptions = shuffleOptions(question.options, question.correctIndex);
        return {
          ...question,
          order: index + 1,
          shuffledOptions: shuffledOptions.options,
          shuffledCorrectIndex: shuffledOptions.correctIndex
        };
      })
    };

    renderBundles();
    renderQuizWorkspace();
  };

  const renderBundles = () => {
    quizBundles.innerHTML = quizBank.bundles
      .map(
        (bundle) => `
          <article class="quiz-bundle-card${bundle.id === activeBundleId ? " active" : ""}">
            <p class="eyebrow">${escapeHtml(bundle.track)}</p>
            <h3>${escapeHtml(bundle.title)}</h3>
            <p>${escapeHtml(bundle.description)}</p>
            <div class="quiz-bundle-meta">
              <span>Pool: ${bundle.questionIds.length} câu</span>
              <span>Làm bài: ${bundle.drawCount} câu</span>
              <span>Mức: ${escapeHtml(bundle.level)}</span>
              <span>Thời gian gợi ý: ${escapeHtml(bundle.suggestedTime)}</span>
            </div>
            <p><button class="btn btn-primary" type="button" data-bundle-id="${escapeHtml(bundle.id)}">Bắt đầu bộ này</button></p>
          </article>
        `
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
      quizWorkspace.innerHTML = `<div class="empty-state">Chọn một bộ đề để bắt đầu.</div>`;
      return;
    }

    const { bundle, questions } = currentAttempt;

    quizWorkspace.innerHTML = `
      <div class="quiz-summary-card">
        <p class="eyebrow">${escapeHtml(bundle.track)} / ${escapeHtml(bundle.title)}</p>
        <div class="quiz-summary-grid">
          <article><span>Tổng câu</span><strong>${questions.length}</strong></article>
          <article><span>Pool đề</span><strong>${bundle.questionIds.length}</strong></article>
          <article><span>Thời gian gợi ý</span><strong>${escapeHtml(bundle.suggestedTime)}</strong></article>
          <article><span>Cấp độ</span><strong>${escapeHtml(bundle.level)}</strong></article>
        </div>
        <div class="quiz-action-row">
          <button id="submitQuizButton" class="btn btn-primary" type="button">Nộp bài và chấm điểm</button>
          <button id="retryQuizButton" class="btn btn-secondary" type="button">Rút đề ngẫu nhiên lại</button>
        </div>
      </div>
      <div id="quizResult"></div>
      <div class="quiz-actions">
        ${questions.map((question) => renderQuizQuestionCard(question)).join("")}
      </div>
    `;

    document.getElementById("submitQuizButton")?.addEventListener("click", () => {
      submitQuizAttempt(bundle);
    });

    document.getElementById("retryQuizButton")?.addEventListener("click", () => {
      startAttempt(bundle.id);
    });
  };

  const submitQuizAttempt = (bundle) => {
    if (!currentAttempt) {
      return;
    }

    const cards = Array.from(document.querySelectorAll(".quiz-question-card"));
    let correctCount = 0;
    let answeredCount = 0;

    cards.forEach((card, index) => {
      const selected = card.querySelector("input[type='radio']:checked");
      const question = currentAttempt.questions[index];
      const options = Array.from(card.querySelectorAll(".quiz-option"));

      options.forEach((option, optionIndex) => {
        option.classList.remove("correct", "incorrect");
        if (optionIndex === question.shuffledCorrectIndex) {
          option.classList.add("correct");
        }
      });

      if (selected) {
        answeredCount += 1;
        const selectedIndex = Number(selected.value);
        if (selectedIndex === question.shuffledCorrectIndex) {
          correctCount += 1;
        } else {
          selected.closest(".quiz-option")?.classList.add("incorrect");
        }
      }
    });

    const percent = Math.round((correctCount / currentAttempt.questions.length) * 100);
    const band = bundle.bands.find((item) => percent >= item.minScore) || bundle.bands[bundle.bands.length - 1];
    const resultTarget = document.getElementById("quizResult");

    if (resultTarget) {
      resultTarget.innerHTML = `
        <article class="quiz-result-card">
          <p class="eyebrow">Kết quả</p>
          <h3>${escapeHtml(band.label)}</h3>
          <p>${escapeHtml(band.description)}</p>
          <div class="quiz-result-grid">
            <article><span>Điểm đúng</span><strong>${correctCount}/${currentAttempt.questions.length}</strong></article>
            <article><span>Tỉ lệ</span><strong>${percent}%</strong></article>
            <article><span>Đã trả lời</span><strong>${answeredCount}</strong></article>
            <article><span>Mức đánh giá</span><strong>${escapeHtml(band.grade)}</strong></article>
          </div>
        </article>
      `;
      resultTarget.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  };

  renderBundles();
  startAttempt(activeBundleId);
}

function renderQuestionCard(question, keyword = "", extraMeta = []) {
  const normalizedQuestion = normalizeQuestionDetails(question);
  const metaPills = [question.level, question.kind, ...extraMeta]
    .filter(Boolean)
    .map((item) => `<span class="meta-pill">${escapeHtml(item)}</span>`)
    .join("");
  const answerTitle = normalizedQuestion.explanation.length > 0 ? "Trả lời" : "Câu trả lời";
  const explanationHtml = normalizedQuestion.explanation
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const applyHtml = normalizedQuestion.applyOrPitfalls
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const codeHtml = question.codeExample
    ? `<pre class="code-sample"><code>${highlightText(question.codeExample, keyword)}</code></pre>`
    : "";
  const practiceHtml = question.practice
    ? `
      <div class="practice-block">
        <h4>Tự luyện</h4>
        <ul>
          ${question.practice.map((item) => `<li>${highlightText(item, keyword)}</li>`).join("")}
        </ul>
      </div>
    `
    : "";

  return `
    <article class="question-card">
      <div class="question-meta">${metaPills}</div>
      <h3>${highlightText(question.question, keyword)}</h3>
      <div class="qa-grid">
        <section class="qa-block qa-block-primary">
          <h4>${answerTitle}</h4>
          <p>${highlightText(normalizedQuestion.answerShort, keyword)}</p>
        </section>
        <section class="qa-block">
          <h4>Giải thích dễ hiểu</h4>
          <ul class="answer-list">${explanationHtml}</ul>
        </section>
        ${
          normalizedQuestion.applyOrPitfalls.length > 0
            ? `
              <section class="qa-block qa-block-warm">
                <h4>Áp dụng / dễ sai ở đâu</h4>
                <ul class="answer-list">${applyHtml}</ul>
              </section>
            `
            : ""
        }
      </div>
      ${codeHtml}
      ${practiceHtml}
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
    ...(question.practice || [])
  ]
    .filter(Boolean)
    .join(" ");
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

  return {
    answerShort:
      question.answerShort ||
      answer[0] ||
      "Câu hỏi này cần được trả lời theo ngữ cảnh cụ thể của Java backend và tình huống thực tế.",
    explanation:
      explanation.length > 0
        ? explanation
        : answer.length > 1
          ? answer.slice(1)
          : ["Hãy đọc thêm code, test và flow chạy thực tế để hiểu rõ bản chất của câu hỏi này."],
    applyOrPitfalls:
      filteredApplyHints.length > 0
        ? filteredApplyHints
        : []
  };
}

function renderError(error) {
  const targets = [
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
    target.innerHTML = `<div class="empty-state">Không thể tải nội dung JSON. ${escapeHtml(error.message)}</div>`;
  });
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
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
    "Nâng cao": 3
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

function shuffleArray(items) {
  const copied = [...items];
  for (let index = copied.length - 1; index > 0; index -= 1) {
    const swapIndex = Math.floor(Math.random() * (index + 1));
    [copied[index], copied[swapIndex]] = [copied[swapIndex], copied[index]];
  }
  return copied;
}

function shuffleOptions(options, correctIndex) {
  const items = options.map((label, index) => ({
    label,
    isCorrect: index === correctIndex
  }));
  const shuffled = shuffleArray(items);
  return {
    options: shuffled.map((item) => item.label),
    correctIndex: shuffled.findIndex((item) => item.isCorrect)
  };
}

function renderQuizQuestionCard(question) {
  return `
    <article class="quiz-question-card" data-question-id="${escapeHtml(question.id)}">
      <p class="eyebrow">${escapeHtml(question.topic)} / Câu ${question.order}</p>
      <h3>${escapeHtml(question.prompt)}</h3>
      <div class="quiz-options">
        ${question.shuffledOptions
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
