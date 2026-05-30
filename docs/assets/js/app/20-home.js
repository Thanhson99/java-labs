function renderHome(bank, roadmap, quizBank) {
  const stats = document.getElementById("homeStats");
  const featuredTopicTitle = document.getElementById("featuredTopicTitle");
  const featuredTopicSummary = document.getElementById("featuredTopicSummary");
  const featuredRoadmapTitle = document.getElementById("featuredRoadmapTitle");
  const featuredRoadmapSummary = document.getElementById("featuredRoadmapSummary");
  const featuredQuizTitle = document.getElementById("featuredQuizTitle");
  const featuredQuizSummary = document.getElementById("featuredQuizSummary");

  const topics = Array.isArray(bank?.topics) ? bank.topics : [];
  const totalQuestions = topics.reduce((sum, topic) => sum + (topic.questions?.length || 0), 0);
  const phases = Array.isArray(roadmap?.phases) ? roadmap.phases : [];
  const bundles = Array.isArray(quizBank?.bundles) ? quizBank.bundles : [];

  if (stats) {
    const values = [
      [currentLanguage === "vi" ? "Chủ đề" : "Topics", topics.length],
      [currentLanguage === "vi" ? "Câu hỏi phỏng vấn" : "Interview questions", totalQuestions],
      [currentLanguage === "vi" ? "Giai đoạn roadmap" : "Roadmap phases", phases.length],
      [currentLanguage === "vi" ? "Bộ đề quiz" : "Quiz sets", bundles.length]
    ];

    stats.innerHTML = values
      .map(
        ([label, value]) => `
          <article>
            <span>${escapeHtml(label)}</span>
            <strong>${escapeHtml(value)}</strong>
          </article>
        `
      )
      .join("");
  }

  renderHomeContinuePanel({
    anchor: stats,
    totalQuestions,
    bundles
  });

  const featuredTopic = topics.find((topic) => topic.questions?.length > 0) || topics[0];
  if (featuredTopicTitle && featuredTopic) {
    featuredTopicTitle.textContent = normalizeTopicTitle(featuredTopic.id, featuredTopic.title);
  }
  if (featuredTopicSummary && featuredTopic) {
    featuredTopicSummary.textContent = normalizeTopicSummary(featuredTopic.id, featuredTopic.summary);
  }

  const featuredPhase = phases[0];
  if (featuredRoadmapTitle && featuredPhase) {
    featuredRoadmapTitle.textContent = featuredPhase.title || (currentLanguage === "vi" ? "Lộ trình học đầu tiên" : "First roadmap phase");
  }
  if (featuredRoadmapSummary && featuredPhase) {
    featuredRoadmapSummary.textContent =
      featuredPhase.summary ||
      featuredPhase.goal ||
      (currentLanguage === "vi"
        ? "Bắt đầu từ nền tảng Java Core trước khi đi sâu vào framework."
        : "Start from Java Core fundamentals before moving deeper into frameworks.");
  }

  const featuredBundle = bundles[0];
  if (featuredQuizTitle && featuredBundle) {
    featuredQuizTitle.textContent = featuredBundle.title;
  }
  if (featuredQuizSummary && featuredBundle) {
    featuredQuizSummary.textContent = `${featuredBundle.description} ${currentLanguage === "vi" ? "Số câu:" : "Questions:"} ${featuredBundle.questionIds?.length || 0}.`;
  }

  preserveLanguageLinks();
}

function renderHomeContinuePanel({ anchor, totalQuestions, bundles }) {
  if (!anchor?.parentElement) {
    return;
  }

  const existing = document.getElementById("homeContinuePanel");
  if (existing) {
    existing.remove();
  }

  const studiedIds = readHomeStudiedQuestionIds();
  const quizProgress = getHomeQuizProgress(bundles);
  const hasStudyProgress = studiedIds.length > 0;
  const hasQuizProgress = quizProgress.answered > 0;

  if (!hasStudyProgress && !hasQuizProgress) {
    return;
  }

  const panel = document.createElement("section");
  panel.id = "homeContinuePanel";
  panel.className = "home-continue-panel";
  panel.setAttribute("aria-label", currentLanguage === "vi" ? "Tiếp tục học" : "Continue learning");
  panel.innerHTML = `
    <div class="home-continue-copy">
      <p class="eyebrow">${currentLanguage === "vi" ? "Tiến độ của bạn" : "Your progress"}</p>
      <h3>${currentLanguage === "vi" ? "Tiếp tục từ nơi bạn dừng lại" : "Continue where you left off"}</h3>
      <p>${currentLanguage === "vi" ? "Trang chủ đang dùng tiến độ lưu trong trình duyệt để gợi ý bước tiếp theo." : "Home uses locally saved progress to suggest the next useful step."}</p>
    </div>
    <div class="home-continue-actions">
      ${
        hasQuizProgress
          ? `
            <a class="continue-card" href="${escapeHtml(getHomeUrl("quiz.html"))}">
              <span>${currentLanguage === "vi" ? "Quiz đang làm" : "Quiz in progress"}</span>
              <strong>${quizProgress.answered}/${quizProgress.total}</strong>
              <small>${escapeHtml(quizProgress.title)}</small>
            </a>
          `
          : ""
      }
      ${
        hasStudyProgress
          ? `
            <a class="continue-card" href="${escapeHtml(getHomeUrl("interview.html"))}">
              <span>${currentLanguage === "vi" ? "Câu đã học" : "Studied questions"}</span>
              <strong>${studiedIds.length}/${totalQuestions}</strong>
              <small>${currentLanguage === "vi" ? "Mở ngân hàng câu hỏi" : "Open the question bank"}</small>
            </a>
          `
          : ""
      }
    </div>
  `;

  anchor.insertAdjacentElement("afterend", panel);
}

function readHomeStudiedQuestionIds() {
  try {
    const value = JSON.parse(localStorage.getItem(stateKey(`study-progress-${currentLanguage}`)) || "[]");
    return Array.isArray(value) ? value : [];
  } catch (_error) {
    return [];
  }
}

function getHomeQuizProgress(bundles) {
  const savedAnswers = readPageState("quiz-answers");
  const bundleProgress = bundles
    .map((bundle) => {
      const answered = Object.keys(savedAnswers[bundle.id] || {}).length;
      return {
        title: bundle.title,
        total: bundle.questionIds?.length || 0,
        answered
      };
    })
    .filter((item) => item.answered > 0)
    .sort((a, b) => b.answered - a.answered)[0];

  return bundleProgress || {
    title: currentLanguage === "vi" ? "Chưa có bộ đề đang làm" : "No active quiz set",
    total: 0,
    answered: 0
  };
}

function getHomeUrl(path) {
  const params = new URLSearchParams();
  params.set("lang", currentLanguage);
  const activeTheme = document.documentElement.dataset.theme || currentTheme;
  if (activeTheme) {
    params.set("theme", activeTheme);
  }
  return `${path}?${params.toString()}`;
}

async function loadJson(path, label) {
  if (jsonCache.has(path)) {
    return jsonCache.get(path);
  }

  const response = await fetch(path);
  if (!response.ok) {
    throw new Error(`Failed to load ${label}: ${response.status}`);
  }

  const data = await response.json();
  jsonCache.set(path, data);
  return data;
}

