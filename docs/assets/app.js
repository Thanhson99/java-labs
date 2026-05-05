const LANGUAGE_STORAGE_KEY = "java-labs-language";
const DEFAULT_LANGUAGE = "en";
const currentLanguage = getCurrentLanguage();

const QUESTION_BANK_PATHS = {
  vi: "data/content/question-bank.vi.json",
  en: "data/content/question-bank.en.json"
};

const ROADMAP_PATHS = {
  vi: "data/roadmap/backend-roadmap.vi.json",
  en: "data/roadmap/backend-roadmap.en.json"
};

const QUIZ_BANK_PATHS = {
  vi: "data/quizzes/quiz-bank.vi.json",
  en: "data/quizzes/quiz-bank.en.json"
};

const UI = {
  vi: {
    nav: ["Home", "Interview Prep", "Interview Levels", "Quiz", "Roadmap"],
    language: { vi: "VI", en: "EN" },
    home: {
      title: "Java Labs Learning Sites",
      eyebrow: "Java Labs",
      heading: "Static Learning Portal",
      intro:
        "Học Java Core, Spring Boot và kiến trúc backend trực tiếp trên GitHub Pages. Nội dung được load từ JSON để bạn học tập trực tiếp theo từng chủ đề từ cơ bản đến nâng cao.",
      cta: "Mở Interview Practice Site",
      section: "Learning Sites",
      cards: [
        {
          title: "Ngân hàng câu hỏi Java và Spring",
          body: "Hệ thống câu hỏi và câu trả lời theo chủ đề, có lý thuyết, phân tích, ví dụ code và bài tập tự luyện.",
          cta: "Mở site học tập"
        },
        {
          title: "Site phỏng vấn theo cấp độ",
          body: "Cổng tổng hợp câu hỏi phỏng vấn chia theo mức độ từ cơ bản đến nâng cao, có đáp án để ôn luyện theo level rõ ràng.",
          cta: "Mở site phỏng vấn"
        },
        {
          title: "Quiz thực chiến đánh giá năng lực",
          body: "Làm bộ đề trắc nghiệm ngẫu nhiên 100 câu theo cấp độ Fresher, Interview và Senior để tự đánh giá năng lực.",
          cta: "Mở quiz"
        },
        {
          title: "Roadmap học Java Backend",
          body: "Lộ trình từ Java Core đến Spring Boot, database, messaging, testing, vận hành và tư duy backend nâng cao.",
          cta: "Mở roadmap"
        }
      ]
    },
    interview: {
      title: "Java Interview Practice",
      eyebrow: "Java Labs / Interview Practice",
      heading: "Question Bank From JSON",
      intro:
        "Mục tiêu là có một portal học tập tinh gọn: danh sách chủ đề, câu hỏi, câu trả lời chuẩn, ví dụ code và bài tập tự luyện. Tất cả đều render từ file JSON để phù hợp GitHub Pages.",
      statsLabel: "Bank Stats",
      statTopics: "Topics",
      statQuestions: "Questions",
      statLevels: "Levels",
      statLevelsValue: "Cơ bản -> Nâng cao",
      sidebarEyebrow: "Topics",
      sidebarHeading: "Topic Catalog",
      contentEyebrow: "Content",
      contentHeading: "Questions And Answers",
      searchLabel: "Tìm kiếm",
      searchPlaceholder: "Ví dụ: transaction, JWT, Kafka...",
      levelLabel: "Mức độ",
      kindLabel: "Loại câu hỏi",
      sortLabel: "Sắp xếp"
    },
    interviewLevels: {
      title: "Java Interview Levels",
      eyebrow: "Java Labs / Interview Levels",
      heading: "Câu hỏi phỏng vấn theo cấp độ",
      intro:
        "Portal này gom câu hỏi phỏng vấn theo level để bạn ôn tập đúng mặt bằng: bắt đầu từ nền tảng, lên trung cấp, nâng cao và các câu hỏi mẹo hoặc tư duy hệ thống.",
      statsLabel: "Interview Stats",
      statTotal: "Tổng câu",
      statGroups: "Số level",
      statRange: "Phạm vi",
      statRangeValue: "Core -> Senior",
      sidebarEyebrow: "Levels",
      sidebarHeading: "Nhóm cấp độ",
      contentEyebrow: "Interview Bank",
      contentHeading: "Question Sets By Level",
      searchLabel: "Tìm kiếm",
      searchPlaceholder: "Ví dụ: JWT, transaction, Kafka...",
      trackLabel: "Track",
      kindLabel: "Loại câu hỏi",
      sortLabel: "Sắp xếp"
    },
    roadmap: {
      title: "Java Backend Roadmap",
      eyebrow: "Java Labs / Roadmap",
      heading: "Roadmap học Java Backend",
      intro:
        "Trang này giúp bạn nhìn bức tranh tổng thể: bắt đầu từ đâu, học theo thứ tự nào, phần nào nên làm trước, phần nào là nâng cao, và cách nối kiến thức thành một cây tư duy rõ ràng.",
      statsLabel: "Roadmap Stats",
      statPhase: "Giai đoạn",
      statTopic: "Chủ đề",
      statGoal: "Mục tiêu",
      statGoalValue: "Core -> Senior",
      sections: [
        ["Bản đồ", "Roadmap là gì?"],
        ["Khởi đầu", "Nếu mới bắt đầu thì học gì trước?"],
        ["Cây trí tuệ", "Knowledge Tree"],
        ["Công nghệ", "Học công nghệ nào, tích hợp ra sao?"],
        ["Tích hợp", "Dòng chảy tích hợp toàn hệ thống"],
        ["Lộ trình", "Học theo giai đoạn"],
        ["Thực chiến", "Học phần nào thì làm phần nào"],
        ["Bẫy học", "Những chỗ rất dễ học lệch hoặc hiểu hời hợt"],
        ["Hình dung", "Roadmap Visual"]
      ],
      introCards: [
        ["Tư duy đúng", "Roadmap không phải danh sách học thuộc. Nó là bản đồ để bạn biết học phần nào trước, phần nào sau, và vì sao chúng liên kết với nhau."],
        ["Mục tiêu", "Đi từ biết cú pháp Java đến hiểu hệ thống backend: dữ liệu, auth, transaction, event, testing, observability và vận hành."],
        ["Cách dùng", "Chọn một giai đoạn, học theo thứ tự, đối chiếu code trong repo và quay lại bổ sung phần còn hổng thay vì nhảy lung tung."]
      ]
    },
    quiz: {
      title: "Java Backend Quiz Arena",
      eyebrow: "Java Labs / Quiz Arena",
      heading: "Thực chiến bằng bộ đề ngẫu nhiên",
      intro:
        "Chọn một bộ đề có pool trên 100 câu, hệ thống sẽ rút ngẫu nhiên 100 câu để bạn làm bài và chấm theo mức năng lực từ cơ bản đến nâng cao.",
      statsLabel: "Quiz Stats",
      statBundle: "Bộ đề",
      statPool: "Ngân hàng câu",
      statDraw: "Mỗi lần làm",
      statDrawValue: "100 câu",
      sections: [
        ["Chọn đề", "Bộ đề theo cấp độ"],
        ["Làm bài", "Quiz Workspace"]
      ],
      empty: "Chọn một bộ đề để bắt đầu."
    },
    common: {
      all: "Tất cả",
      defaultSort: "Mặc định",
      levelAsc: "Độ khó tăng dần",
      levelDesc: "Độ khó giảm dần",
      titleAsc: "Theo tiêu đề A-Z",
      questionsDesc: "Nhiều câu trước",
      questionsAsc: "Ít câu trước",
      groupTitleAsc: "Tên level A-Z",
      noTopics: "Không có chủ đề nào khớp bộ lọc hiện tại.",
      noQuestions: "Không tìm thấy câu hỏi phù hợp. Hãy đổi từ khóa hoặc bộ lọc.",
      noLevels: "Không có level nào khớp bộ lọc hiện tại.",
      noLevelQuestions: "Không tìm thấy câu hỏi phỏng vấn phù hợp. Hãy đổi từ khóa hoặc bộ lọc.",
      interviewQuestionCount: "câu hỏi phỏng vấn",
      interviewLevelEyebrow: "Interview Level",
      interviewLevelSummary: "câu hỏi đã được tổng hợp ở mức này.",
      learnFor: "Học để làm gì?",
      integratesWith: "Tích hợp với gì?",
      lookAt: "Trong repo nên nhìn đâu?",
      learnWhat: "Học gì?",
      doInRepo: "Làm gì trong repo?",
      outcomes: "Khi xong phải đạt",
      mistakes: "Dễ hiểu sai ở đâu?",
      fixLearning: "Nên sửa cách học thế nào?",
      chooseBundle: "Chọn một bộ đề để bắt đầu.",
      pool: "Pool",
      drawCount: "Làm bài",
      level: "Mức",
      suggestedTime: "Thời gian gợi ý",
      startBundle: "Bắt đầu bộ này",
      totalQuestions: "Tổng câu",
      submit: "Nộp bài và chấm điểm",
      retry: "Rút đề ngẫu nhiên lại",
      result: "Kết quả",
      correctScore: "Điểm đúng",
      ratio: "Tỉ lệ",
      answered: "Đã trả lời",
      assessment: "Mức đánh giá",
      answer: "Trả lời",
      answerFallback: "Câu trả lời",
      explanation: "Giải thích dễ hiểu",
      apply: "Áp dụng / dễ sai ở đâu",
      practice: "Tự luyện",
      jsonError: "Không thể tải nội dung JSON."
    }
  },
  en: {
    nav: ["Home", "Interview Prep", "Interview Levels", "Quiz", "Roadmap"],
    language: { vi: "VI", en: "EN" },
    home: {
      title: "Java Labs Learning Sites",
      eyebrow: "Java Labs",
      heading: "Static Learning Portal",
      intro:
        "Study Java Core, Spring Boot, and backend architecture directly on GitHub Pages. Content is loaded from JSON so you can learn topic by topic from basic to advanced.",
      cta: "Open Interview Practice Site",
      section: "Learning Sites",
      cards: [
        {
          title: "Java and Spring question bank",
          body: "A topic-based system of questions and answers with theory, analysis, code examples, and self-practice prompts.",
          cta: "Open study site"
        },
        {
          title: "Interview site by level",
          body: "A curated interview portal grouped from basic to advanced, with answers to review by level.",
          cta: "Open interview site"
        },
        {
          title: "Hands-on skill quiz",
          body: "Take randomized 100-question sets across Fresher, Interview, and Senior levels to assess your skills.",
          cta: "Open quiz"
        },
        {
          title: "Java Backend roadmap",
          body: "A path from Java Core to Spring Boot, databases, messaging, testing, operations, and advanced backend thinking.",
          cta: "Open roadmap"
        }
      ]
    },
    interview: {
      title: "Java Interview Practice",
      eyebrow: "Java Labs / Interview Practice",
      heading: "Question Bank From JSON",
      intro:
        "The goal is a lean learning portal: topic list, questions, solid answers, code examples, and self-practice tasks. Everything is rendered from JSON so it works cleanly on GitHub Pages.",
      statsLabel: "Bank Stats",
      statTopics: "Topics",
      statQuestions: "Questions",
      statLevels: "Levels",
      statLevelsValue: "Basic -> Advanced",
      sidebarEyebrow: "Topics",
      sidebarHeading: "Topic Catalog",
      contentEyebrow: "Content",
      contentHeading: "Questions And Answers",
      searchLabel: "Search",
      searchPlaceholder: "Example: transaction, JWT, Kafka...",
      levelLabel: "Level",
      kindLabel: "Question type",
      sortLabel: "Sort"
    },
    interviewLevels: {
      title: "Java Interview Levels",
      eyebrow: "Java Labs / Interview Levels",
      heading: "Interview questions by level",
      intro:
        "This portal groups interview questions by level so you can review at the right baseline: start with fundamentals, move to intermediate and advanced, then tackle trick and system-thinking questions.",
      statsLabel: "Interview Stats",
      statTotal: "Total questions",
      statGroups: "Level groups",
      statRange: "Range",
      statRangeValue: "Core -> Senior",
      sidebarEyebrow: "Levels",
      sidebarHeading: "Level Groups",
      contentEyebrow: "Interview Bank",
      contentHeading: "Question Sets By Level",
      searchLabel: "Search",
      searchPlaceholder: "Example: JWT, transaction, Kafka...",
      trackLabel: "Track",
      kindLabel: "Question type",
      sortLabel: "Sort"
    },
    roadmap: {
      title: "Java Backend Roadmap",
      eyebrow: "Java Labs / Roadmap",
      heading: "Java Backend Roadmap",
      intro:
        "This page helps you see the full picture: where to start, what order to learn in, what should come first, what counts as advanced, and how to connect everything into a clear knowledge tree.",
      statsLabel: "Roadmap Stats",
      statPhase: "Phases",
      statTopic: "Topics",
      statGoal: "Goal",
      statGoalValue: "Core -> Senior",
      sections: [
        ["Map", "What is a roadmap?"],
        ["Starting Point", "What should you learn first if you are new?"],
        ["Knowledge Tree", "Knowledge Tree"],
        ["Technology", "What technologies should you learn and how do they integrate?"],
        ["Integration", "Full system integration flow"],
        ["Phases", "Learn by phase"],
        ["Hands-on", "What to build while learning each part"],
        ["Learning traps", "Places where it is easy to learn the wrong thing or stay shallow"],
        ["Visual", "Roadmap Visual"]
      ],
      introCards: [
        ["Think correctly", "A roadmap is not a list to memorize. It is a map that tells you what to study first, what comes later, and why the pieces connect."],
        ["Goal", "Move from knowing Java syntax to understanding a real backend system: data, auth, transactions, events, testing, observability, and operations."],
        ["How to use it", "Pick one phase, study in order, compare it with code in the repo, then come back to fill the gaps instead of jumping around."]
      ]
    },
    quiz: {
      title: "Java Backend Quiz Arena",
      eyebrow: "Java Labs / Quiz Arena",
      heading: "Practice with randomized exam sets",
      intro:
        "Choose a set with a pool of more than 100 questions. The system will draw 100 random questions and grade your level from basic to advanced.",
      statsLabel: "Quiz Stats",
      statBundle: "Bundles",
      statPool: "Question pool",
      statDraw: "Per attempt",
      statDrawValue: "100 questions",
      sections: [
        ["Choose a set", "Bundles by level"],
        ["Take the test", "Quiz Workspace"]
      ],
      empty: "Choose a quiz bundle to begin."
    },
    common: {
      all: "All",
      defaultSort: "Default",
      levelAsc: "Difficulty ascending",
      levelDesc: "Difficulty descending",
      titleAsc: "Title A-Z",
      questionsDesc: "Most questions first",
      questionsAsc: "Fewest questions first",
      groupTitleAsc: "Level name A-Z",
      noTopics: "No topics match the current filters.",
      noQuestions: "No matching questions found. Try another keyword or filter.",
      noLevels: "No levels match the current filters.",
      noLevelQuestions: "No matching interview questions found. Try another keyword or filter.",
      interviewQuestionCount: "interview questions",
      interviewLevelEyebrow: "Interview Level",
      interviewLevelSummary: "questions have been grouped at this level.",
      learnFor: "What should you learn it for?",
      integratesWith: "What does it integrate with?",
      lookAt: "Where should you look in this repo?",
      learnWhat: "What should you learn?",
      doInRepo: "What should you do in the repo?",
      outcomes: "What should you be able to do after this?",
      mistakes: "Where do people misunderstand this?",
      fixLearning: "How should you correct the way you learn it?",
      chooseBundle: "Choose a quiz bundle to begin.",
      pool: "Pool",
      drawCount: "Attempt",
      level: "Level",
      suggestedTime: "Suggested time",
      startBundle: "Start this bundle",
      totalQuestions: "Total questions",
      submit: "Submit and grade",
      retry: "Draw another random set",
      result: "Result",
      correctScore: "Correct",
      ratio: "Score",
      answered: "Answered",
      assessment: "Assessment",
      answer: "Answer",
      answerFallback: "Answer",
      explanation: "Plain-English explanation",
      apply: "Where to apply it / where people go wrong",
      practice: "Self-practice",
      jsonError: "Could not load JSON content."
    }
  }
};

document.addEventListener("DOMContentLoaded", async () => {
  const page = document.body.dataset.page;
  document.documentElement.lang = currentLanguage;
  preserveLanguageLinks();
  mountLanguageSwitcher();
  applyStaticCopy(page);

  try {
    if (page === "home") {
      return;
    }

    if (page === "interview") {
      const response = await fetch(QUESTION_BANK_PATHS[currentLanguage]);
      if (!response.ok) {
        throw new Error(`Failed to load question bank: ${response.status}`);
      }

      const bank = normalizeQuestionBank(await response.json());
      renderInterview(bank);
      return;
    }

    if (page === "interview-levels") {
      const response = await fetch(QUESTION_BANK_PATHS[currentLanguage]);
      if (!response.ok) {
        throw new Error(`Failed to load question bank: ${response.status}`);
      }

      const bank = normalizeQuestionBank(await response.json());
      renderInterviewLevels(bank);
      return;
    }

    if (page === "roadmap") {
      const response = await fetch(ROADMAP_PATHS[currentLanguage]);
      if (!response.ok) {
        throw new Error(`Failed to load roadmap: ${response.status}`);
      }

      const roadmap = await response.json();
      renderRoadmap(roadmap);
      return;
    }

    if (page === "quiz") {
      const response = await fetch(QUIZ_BANK_PATHS[currentLanguage]);
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

function getCurrentLanguage() {
  const fromQuery = new URLSearchParams(window.location.search).get("lang");
  if (fromQuery === "vi" || fromQuery === "en") {
    return fromQuery;
  }

  return DEFAULT_LANGUAGE;
}

function copy() {
  return UI[currentLanguage];
}

function commonText() {
  return copy().common;
}

function mountLanguageSwitcher() {
  const target = document.querySelector(".top-nav") || document.querySelector(".hero");
  if (!target || document.getElementById("languageSwitch")) {
    return;
  }

  const switcher = document.createElement("div");
  switcher.id = "languageSwitch";
  switcher.className = "language-switch";
  switcher.innerHTML = `
    <button class="language-pill${currentLanguage === "vi" ? " active" : ""}" type="button" data-lang="vi">${copy().language.vi}</button>
    <button class="language-pill${currentLanguage === "en" ? " active" : ""}" type="button" data-lang="en">${copy().language.en}</button>
  `;

  target.appendChild(switcher);
  switcher.querySelectorAll("[data-lang]").forEach((button) => {
    button.addEventListener("click", () => {
      const nextLanguage = button.getAttribute("data-lang");
      if (!nextLanguage || nextLanguage === currentLanguage) {
        return;
      }

      const url = new URL(window.location.href);
      url.searchParams.set("lang", nextLanguage);
      window.location.href = url.toString();
    });
  });
}

function preserveLanguageLinks() {
  const links = document.querySelectorAll("a[href]");
  links.forEach((link) => {
    const href = link.getAttribute("href");
    if (!href || href.startsWith("#") || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("mailto:")) {
      return;
    }

    try {
      const url = new URL(href, window.location.href);
      if (!url.pathname.endsWith(".html")) {
        return;
      }

      url.searchParams.set("lang", currentLanguage);
      link.setAttribute("href", `${url.pathname.split("/").pop()}${url.search}${url.hash}`);
    } catch (_error) {
      // Ignore malformed links and leave them untouched.
    }
  });
}

function applyStaticCopy(page) {
  applyNavCopy();

  if (page === "home") {
    applyHomeCopy();
    return;
  }

  if (page === "interview") {
    applyInterviewCopy();
    return;
  }

  if (page === "interview-levels") {
    applyInterviewLevelsCopy();
    return;
  }

  if (page === "roadmap") {
    applyRoadmapCopy();
    return;
  }

  if (page === "quiz") {
    applyQuizCopy();
  }
}

function applyNavCopy() {
  const links = document.querySelectorAll(".nav-link");
  if (links.length === 0) {
    return;
  }

  copy().nav.forEach((label, index) => {
    if (links[index]) {
      links[index].textContent = label;
    }
  });
}

function applyHomeCopy() {
  const pageCopy = copy().home;
  document.title = pageCopy.title;
  setText(".hero-content .eyebrow", pageCopy.eyebrow);
  setText(".hero-content h1", pageCopy.heading);
  setText(".hero-content .hero-copy", pageCopy.intro);
  setText(".hero-actions .btn", pageCopy.cta);
  setText("main .panel h2", pageCopy.section);

  const cards = document.querySelectorAll(".service-card");
  pageCopy.cards.forEach((card, index) => {
    const target = cards[index];
    if (!target) {
      return;
    }

    setTextWithin(target, "h3", card.title);
    const paragraphs = target.querySelectorAll("p");
    if (paragraphs[0]) {
      paragraphs[0].textContent = card.body;
    }
    if (paragraphs[1]) {
      const link = paragraphs[1].querySelector("a");
      if (link) {
        link.textContent = card.cta;
      }
    }
  });
}

function applyInterviewCopy() {
  const pageCopy = copy().interview;
  document.title = pageCopy.title;
  setText(".hero-content .eyebrow", pageCopy.eyebrow);
  setText(".hero-content h1", pageCopy.heading);
  setText(".hero-content .hero-copy", pageCopy.intro);
  setText(".side-label", pageCopy.statsLabel);
  setTextInList(".stat-list dt", [pageCopy.statTopics, pageCopy.statQuestions, pageCopy.statLevels]);
  setTextInList(".stat-list dd", [null, null, pageCopy.statLevelsValue]);
  setTextInList(".sidebar-panel .section-heading .eyebrow", [pageCopy.sidebarEyebrow]);
  setTextInList(".sidebar-panel .section-heading h2", [pageCopy.sidebarHeading]);
  setTextInList(".content-panel .section-heading .eyebrow", [pageCopy.contentEyebrow]);
  setTextInList(".content-panel .section-heading h2", [pageCopy.contentHeading]);
  setLabelAndPlaceholder("questionSearch", pageCopy.searchLabel, pageCopy.searchPlaceholder);
  setLabelAndSelect("levelFilter", pageCopy.levelLabel, commonText().all);
  setLabelAndSelect("kindFilter", pageCopy.kindLabel, commonText().all);
  setSortSelect("sortFilter", pageCopy.sortLabel, [
    commonText().defaultSort,
    commonText().levelAsc,
    commonText().levelDesc,
    commonText().titleAsc
  ]);
}

function applyInterviewLevelsCopy() {
  const pageCopy = copy().interviewLevels;
  document.title = pageCopy.title;
  setText(".hero-content .eyebrow", pageCopy.eyebrow);
  setText(".hero-content h1", pageCopy.heading);
  setText(".hero-content .hero-copy", pageCopy.intro);
  setText(".side-label", pageCopy.statsLabel);
  setTextInList(".stat-list dt", [pageCopy.statTotal, pageCopy.statGroups, pageCopy.statRange]);
  setTextInList(".stat-list dd", [null, null, pageCopy.statRangeValue]);
  setTextInList(".sidebar-panel .section-heading .eyebrow", [pageCopy.sidebarEyebrow]);
  setTextInList(".sidebar-panel .section-heading h2", [pageCopy.sidebarHeading]);
  setTextInList(".content-panel .section-heading .eyebrow", [pageCopy.contentEyebrow]);
  setTextInList(".content-panel .section-heading h2", [pageCopy.contentHeading]);
  setLabelAndPlaceholder("levelSearch", pageCopy.searchLabel, pageCopy.searchPlaceholder);
  setLabelAndSelect("levelTrackFilter", pageCopy.trackLabel, commonText().all);
  setLabelAndSelect("levelKindFilter", pageCopy.kindLabel, commonText().all);
  setSortSelect("levelSortFilter", pageCopy.sortLabel, [
    commonText().defaultSort,
    commonText().questionsDesc,
    commonText().questionsAsc,
    commonText().groupTitleAsc
  ]);
}

function applyRoadmapCopy() {
  const pageCopy = copy().roadmap;
  document.title = pageCopy.title;
  setText(".hero-content .eyebrow", pageCopy.eyebrow);
  setText(".hero-content h1", pageCopy.heading);
  setText(".hero-content .hero-copy", pageCopy.intro);
  setText(".side-label", pageCopy.statsLabel);
  setTextInList(".stat-list dt", [pageCopy.statPhase, pageCopy.statTopic, pageCopy.statGoal]);
  setTextInList(".stat-list dd", [null, null, pageCopy.statGoalValue]);
  setRoadmapSectionCopy(pageCopy.sections);
  setRoadmapIntroCards(pageCopy.introCards);
}

function applyQuizCopy() {
  const pageCopy = copy().quiz;
  document.title = pageCopy.title;
  setText(".hero-content .eyebrow", pageCopy.eyebrow);
  setText(".hero-content h1", pageCopy.heading);
  setText(".hero-content .hero-copy", pageCopy.intro);
  setText(".side-label", pageCopy.statsLabel);
  setTextInList(".stat-list dt", [pageCopy.statBundle, pageCopy.statPool, pageCopy.statDraw]);
  setTextInList(".stat-list dd", [null, null, pageCopy.statDrawValue]);
  setRoadmapSectionCopy(pageCopy.sections);
  const emptyState = document.querySelector("#quizWorkspace .empty-state");
  if (emptyState) {
    emptyState.textContent = pageCopy.empty;
  }
}

function setRoadmapSectionCopy(sections) {
  const headings = document.querySelectorAll("main .section-heading");
  sections.forEach((item, index) => {
    const target = headings[index];
    if (!target) {
      return;
    }

    setTextWithin(target, ".eyebrow", item[0]);
    setTextWithin(target, "h2", item[1]);
  });
}

function setRoadmapIntroCards(cards) {
  const blocks = document.querySelectorAll(".roadmap-intro-grid .info-card");
  cards.forEach((item, index) => {
    const target = blocks[index];
    if (!target) {
      return;
    }

    setTextWithin(target, "h3", item[0]);
    setTextWithin(target, "p", item[1]);
  });
}

function setLabelAndPlaceholder(inputId, label, placeholder) {
  const input = document.getElementById(inputId);
  if (!input) {
    return;
  }

  const field = input.closest(".toolbar-field");
  if (field) {
    const caption = field.querySelector("span");
    if (caption) {
      caption.textContent = label;
    }
  }

  input.placeholder = placeholder;
}

function setLabelAndSelect(selectId, label, firstOption) {
  const select = document.getElementById(selectId);
  if (!select) {
    return;
  }

  const field = select.closest(".toolbar-field");
  if (field) {
    const caption = field.querySelector("span");
    if (caption) {
      caption.textContent = label;
    }
  }

  if (select.options[0]) {
    select.options[0].textContent = firstOption;
  }
}

function setSortSelect(selectId, label, optionLabels) {
  const select = document.getElementById(selectId);
  if (!select) {
    return;
  }

  const field = select.closest(".toolbar-field");
  if (field) {
    const caption = field.querySelector("span");
    if (caption) {
      caption.textContent = label;
    }
  }

  optionLabels.forEach((text, index) => {
    if (select.options[index]) {
      select.options[index].textContent = text;
    }
  });
}

function setText(selector, value) {
  const target = document.querySelector(selector);
  if (target) {
    target.textContent = value;
  }
}

function setTextWithin(root, selector, value) {
  const target = root.querySelector(selector);
  if (target) {
    target.textContent = value;
  }
}

function setTextInList(selector, values) {
  const nodes = document.querySelectorAll(selector);
  values.forEach((value, index) => {
    if (value !== null && value !== undefined && nodes[index]) {
      nodes[index].textContent = value;
    }
  });
}

function normalizeQuestionBank(bank) {
  return {
    ...bank,
    siteTitle: normalizeDisplayText(bank.siteTitle || ""),
    topics: Array.isArray(bank.topics) ? bank.topics.map(normalizeTopic) : []
  };
}

function normalizeTopic(topic) {
  return {
    ...topic,
    track: normalizeDisplayText(topic.track || ""),
    title: normalizeTopicTitle(topic.id, topic.title || ""),
    summary: normalizeTopicSummary(topic.id, topic.summary || ""),
    questions: Array.isArray(topic.questions) ? topic.questions.map(normalizeQuestion) : []
  };
}

function normalizeQuestion(question) {
  return {
    ...question,
    level: normalizeDisplayText(question.level || question.leaboutl || ""),
    kind: normalizeDisplayText(question.kind || ""),
    question: normalizeDisplayText(question.question || ""),
    answer: Array.isArray(question.answer) ? question.answer.map((item) => normalizeDisplayText(item)) : [],
    answerShort: normalizeDisplayText(question.answerShort || ""),
    explanation: (Array.isArray(question.explanation) ? question.explanation : Array.isArray(question.expisnation) ? question.expisnation : [])
      .map((item) => normalizeDisplayText(item))
      .filter(Boolean),
    applyOrPitfalls: (Array.isArray(question.applyOrPitfalls) ? question.applyOrPitfalls : [])
      .map((item) => normalizeDisplayText(item))
      .filter((item) => item && !shouldHideNoisyEnglish(item)),
    practice: (Array.isArray(question.practice) ? question.practice : [])
      .map((item) => normalizeDisplayText(item))
      .filter((item) => item && !shouldHideNoisyEnglish(item))
  };
}

function normalizeTopicTitle(topicId, title) {
  if (currentLanguage !== "en") {
    return title;
  }

  const overrides = {
    "jaand-fundamentals": "Java fundamentals",
    "syntax-control-flow": "Syntax, variables, and control flow",
    "oop-design": "OOP, class design, and clean code",
    "collections-generics-streams": "Collections, generics, and streams",
    "strings-time-enums": "Strings, enums, and date-time basics",
    "exceptions-debugwhatng": "Exceptions, logging, and debugging mindset",
    "concurrency-async": "Concurrency, async flows, and callback-hell equivalents",
    "jvm-memory-performance": "JVM, memory, and performance",
    "jdbc-sql-database": "JDBC, SQL, and database connectivity",
    "testing-build-tools": "Testing, Maven, and real project habits",
    "spring-boot-web-data": "Spring Boot web, beans, and data access",
    "spring-security-auth": "Security, JWT, and session management",
    "spring-transactions-jpa": "JPA, transactions, Flyway, and durable data",
    "messawhatng-microservices": "Messaging, outbox, Kafka, RabbitMQ, and microservice thinking",
    "design-patterns-and-architecture": "Design patterns and architecture thinking",
    "http-rest-api": "HTTP, REST APIs, and backend communication",
    "obserandbility-reliability": "Observability, reliability, and operations",
    "file-io-networking": "Files, I/O, and external system integration",
    "interview-problem-solving": "Interview thinking, code analysis, and issue diagnosis",
    "adandnced-jaand-tricks": "Hard Java questions, tricks, and pitfalls",
    "adandnced-spring-and-data": "Advanced Spring Boot and data access",
    "senior-backend-mindset": "Advanced backend thinking and senior-style questions",
    "interview-expansion-bank": "Expanded interview question bank",
    "interview-famous-and-basic-bank": "Basic, famous, and trick interview questions"
  };

  return overrides[topicId] || normalizeDisplayText(title);
}

function normalizeTopicSummary(topicId, summary) {
  if (currentLanguage !== "en") {
    return summary;
  }

  const overrides = {
    "jaand-fundamentals": "Foundational questions about what Java is, how it runs, and why it remains dominant in backend systems.",
    "syntax-control-flow": "Core syntax and control-flow questions that make Java code easier to read and write correctly.",
    "oop-design": "Object-oriented design, responsibility boundaries, and clean-code habits in Java.",
    "collections-generics-streams": "Collection choices, generics, streams, and the trade-offs behind everyday data handling.",
    "strings-time-enums": "Small language features that look simple but often cause subtle bugs in real systems.",
    "exceptions-debugwhatng": "Error handling, stack traces, logging, and practical debugging habits for backend work.",
    "concurrency-async": "Threads, futures, shared state, and the Java version of async complexity.",
    "jvm-memory-performance": "JVM behavior, memory problems, and performance questions that appear in real production debugging.",
    "jdbc-sql-database": "Database connectivity, SQL execution, connection pools, and common backend persistence mistakes.",
    "testing-build-tools": "Testing layers, build tools, and habits that matter when code is maintained by real teams.",
    "spring-boot-web-data": "Spring Boot request flow, dependency injection, service boundaries, and data-access structure.",
    "spring-security-auth": "Authentication, authorization, JWT flows, refresh tokens, and common security mistakes.",
    "spring-transactions-jpa": "Transactions, JPA behavior, schema migration, and durable-data concerns.",
    "messawhatng-microservices": "Async integration, messaging guarantees, outbox patterns, and microservice trade-offs.",
    "design-patterns-and-architecture": "Patterns, trade-offs, and architecture thinking beyond syntax-level knowledge.",
    "http-rest-api": "HTTP semantics, API design, contracts, and backend communication behavior.",
    "obserandbility-reliability": "Logs, metrics, tracing, alerts, and thinking clearly about reliability in production.",
    "file-io-networking": "File handling, network I/O, and safe integration with external systems.",
    "interview-problem-solving": "How to think through unfamiliar code, explain trade-offs, and answer interview questions clearly.",
    "adandnced-jaand-tricks": "Hard Java questions, famous traps, and senior-level details that often show up in interviews.",
    "adandnced-spring-and-data": "Advanced Spring Boot, transactions, data consistency, and repository-level pitfalls.",
    "senior-backend-mindset": "Senior-style backend questions focused on risks, trade-offs, and system behavior.",
    "interview-expansion-bank": "Additional mixed interview questions that broaden the practice set across backend topics.",
    "interview-famous-and-basic-bank": "Basic but memorable questions, common traps, and interview classics."
  };

  return overrides[topicId] || normalizeDisplayText(summary);
}

function normalizeDisplayText(text) {
  if (!text || currentLanguage !== "en") {
    return text;
  }

  let value = String(text);
  const exactReplacements = [
    ["Apply it when you need read older code, explain thich cho person different or decide choose explain phap nao trong backend Java.", "Use this when you read older code, explain the idea to someone else, or choose between backend design options."],
    ["De wrong when only remember definition ma not noi no voi behavior runtime, data real and trade-off technical.", "People often go wrong when they memorize the definition but never connect it to runtime behavior, real data, and technical trade-offs."],
    ["Apply it when xu ly job nen, batch, xu ly song song or chia se data between nunderstand luong.", "Use this in background jobs, batch processing, parallel flows, or shared-state debugging."],
    ["De wrong when nghi code chay correct o local thi chac chan correct under tai high or when nunderstand thread together cham data.", "People often go wrong when code works locally but has never been tested under load or concurrent access."],
    ["Apply it when design hop dong API, choose status code, validation and retry strategy o client.", "Use this when you design API contracts, choose status codes, add validation, and plan client retry behavior."],
    ["De wrong when endpoint only chay duoc on happy path but not ro xu ly error, idempotency or backward compatibility.", "People often go wrong when an endpoint only handles the happy path and ignores errors, idempotency, or backward compatibility."],
    ["Apply it when tach xu ly bat dong bo, whatam coupling or need phuc hoi when downstream tam error.", "Use this when you split work asynchronously, reduce coupling, or need recovery after temporary downstream failures."],
    ["Tu mo ta isi flow: source -> bytecode -> class loader -> JVM -> JIT.", "Describe the flow yourself: source -> bytecode -> class loader -> JVM -> JIT."],
    ["Thu use `javac` and `javap -c` to inspect bytecode of a small class.", "Try `javac` and `javap -c` on a small class to inspect the generated bytecode."],
    ["Hay tu chay isi vi du or write one test very remember to check truc tiep behavior thay because only remember dap an.", "Run a tiny example yourself or write a focused test so you can verify the behavior directly instead of memorizing the answer."],
    ["Apply it when design dang nhap, authorization, refresh token, bao about endpoint and call service lien he thong.", "Use this when you design login flows, authorization, refresh tokens, protected endpoints, or cross-service calls."],
    ["Ap use strong nhat when review pull request, debug bug real or explain thich because sao one doan code look at correct ma andn error.", "This is especially useful in code review, real bug debugging, or when a piece of code looks correct but still fails."],
    ["Diem cot error is must phan biet between value noi use and reference object, because day is cho beginners Java very or nham.", "The key is to distinguish business value equality from object reference identity, because beginners confuse these very easily."],
    ["Khi read code real, or luon tu hoi doan nay dang compare value business logic or only dang check hai andriable co together tro into one object or not.", "When reading real code, ask whether the line compares business values or only checks whether two references point to the same object."]
  ];

  exactReplacements.forEach(([from, to]) => {
    if (value.includes(from)) {
      value = value.split(from).join(to);
    }
  });

  const regexReplacements = [
    [/\bJaand\b/g, "Java"],
    [/\bAdandnced\b/g, "Advanced"],
    [/\bcisss\b/g, "class"],
    [/\bMaaboutn\b/g, "Maven"],
    [/\bdeaboutlopment\b/g, "development"],
    [/\bdeaboutloper\b/g, "developer"],
    [/\bobserandbility\b/g, "observability"],
    [/\bmessawhatng\b/g, "messaging"],
    [/\beaboutnt-driaboutn\b/g, "event-driven"],
    [/\beaboutntual\b/g, "eventual"],
    [/\beaboutnt\b/g, "event"],
    [/\bseraboutr\b/g, "server"],
    [/\bprimitiabout\b/g, "primitive"],
    [/\bStackOaboutrflowError\b/g, "StackOverflowError"],
    [/\blogwhatng\b/g, "logging"],
    [/\bandrious\b/g, "various"],
    [/\bwhatiao\b/g, "solving"],
    [/\bwhatai\b/g, "solving"],
    [/\bbaisnces\b/g, "balances"],
    [/\bnunderstand luong\b/g, "multiple threads"],
    [/\bnunderstand thread\b/g, "multiple threads"],
    [/\bnunderstand object\b/g, "many objects"],
    [/\bnunderstand isn\b/g, "many times"],
    [/\bthoi whatan\b/g, "time"],
    [/\btrung whatan\b/g, "temporary"],
    [/\bbat andriable\b/g, "immutable"],
    [/\bpho andriable\b/g, "common"],
    [/\bquan ly phien\b/g, "session management"],
    [/\btruong hop nao\b/g, "use cases"],
    [/\bTuy nhien\b/g, "However"],
    [/\bVi\b/g, "Because"],
    [/\bNeu\b/g, "If"],
    [/\bKhi\b/g, "When"],
    [/\bNo\b/g, "It"],
    [/\bDay\b/g, "This"],
    [/\bChi\b/g, "Only"],
    [/\bHay\b/g, "Try"],
    [/\bThu\b/g, "Try"],
    [/\bkhong\b/g, "not"],
    [/\bco\b/g, "have"],
    [/\bnguoi\b/g, "person"],
    [/\bcau hoi\b/g, "question"],
    [/\btra loi\b/g, "answer"],
    [/\bgiai thich\b/g, "explain"],
    [/\bvi sao\b/g, "why"],
    [/\bdang\b/g, "is"],
    [/\bkhi nao\b/g, "when should"],
    [/\bneu\b/g, "if"],
    [/\bnen\b/g, "should"],
    [/\bma\b/g, "but"],
    [/\bde wrong when\b/gi, "People go wrong when"],
    [/\bDap an kieu nay quan trong\b/g, "This kind of answer matters"],
    [/\bMot dau understand cho thay\b/g, "What is a sign that"],
    [/\bMot question dang whata\b/g, "What is a very good question"],
    [/\bMot question 'to doi' when design class is it\?/g, "What is a good balancing question when designing a class?"],
    [/\bMot nguyen tac\b/g, "One principle"],
    [/\bMot meo\b/g, "One practical tip"],
    [/\bMot bug\b/g, "One common bug"],
    [/\bMot anti-pattern\b/g, "One classic anti-pattern"],
    [/\bMot error\b/g, "One common mistake"],
    [/\bMot habit\b/g, "One good habit"],
    [/\bMot varioush\b/g, "One practical way"],
    [/\bMot bai hoc\b/g, "One architecture lesson"],
    [/\bMot answer interview strong usually co question truc nao\?/g, "What structure does a strong interview answer usually have?"],
    [/\bdifferent nhau ra sao\?/g, "differ?"],
    [/\bco y nghia what\?/g, "mean?"],
    [/\bwhat do generics solve\?/gi, "What problem do generics solve?"],
    [/\blook at such as the nao\?/g, "look like in practice?"],
    [/\btrong repo nay\b/g, "in this repo"],
    [/\bgood hon\b/g, "better than"],
    [/\btranh\b/g, "avoid"],
    [/\bpho andriable\b/g, "common"],
    [/\bdoi eaboutnt contract\b/g, "change an event contract"],
    [/\bdoi schema\b/g, "change a schema"],
    [/\bcorrect varioush\b/g, "correctly"],
    [/\bcorrect-looking\b/g, "apparently correct"],
    [/\bnot must\b/g, "not every"],
    [/\bneed store y\b/g, "deserves attention"],
    [/\bwhatong thinking\b/g, "works like"],
    [/\bwhatong nhau\b/g, "similar"],
    [/\bunder tai high\b/g, "under high load"],
    [/\bwhatam coupling\b/g, "reduce coupling"],
    [/\bngoai luong\b/g, "outside the stream"],
    [/\bhappy path\b/g, "happy path"],
    [/\s+/g, " "]
  ];

  regexReplacements.forEach(([pattern, replacement]) => {
    value = value.replace(pattern, replacement);
  });

  return value.trim();
}

function shouldHideNoisyEnglish(text) {
  if (currentLanguage !== "en" || !text) {
    return false;
  }

  const suspicious = [
    "nunderstand",
    "andn",
    "whatan",
    "andriable",
    "whatam",
    "whata",
    "haabout",
    "whatau",
    "maaboutn",
    "saabout",
    "whatong",
    "andlidate",
    "aboutt",
    "lowhatn",
    "whatao",
    "cunderstand",
    "aaboutrage",
    "oaboutr",
    "aboutn",
    "rewhaton",
    "pluwhatn",
    "behaabout",
    "driaboutr"
  ];

  const lowered = text.toLowerCase();
  const hits = suspicious.reduce((sum, token) => sum + (lowered.includes(token) ? 1 : 0), 0);
  return hits >= 2;
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
    levelFilter.innerHTML = `<option value="">${commonText().all}</option>${levels
      .map((level) => `<option value="${escapeHtml(level)}">${escapeHtml(level)}</option>`)
      .join("")}`;
  }

  if (kindFilter) {
    kindFilter.innerHTML = `<option value="">${commonText().all}</option>${kinds
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
      : `<div class="empty-state">${commonText().noTopics}</div>`;

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
      : `<div class="empty-state">${commonText().noQuestions}</div>`;

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
    levelTrackFilter.innerHTML = `<option value="">${commonText().all}</option>${tracks
      .map((track) => `<option value="${escapeHtml(track)}">${escapeHtml(track)}</option>`)
      .join("")}`;
  }

  if (levelKindFilter) {
    levelKindFilter.innerHTML = `<option value="">${commonText().all}</option>${kinds
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
              <span class="topic-track">${commonText().all}</span>
              <span class="topic-link-body">
                <small>${filteredQuestions.length} ${commonText().interviewQuestionCount}</small>
              </span>
            </button>
          `,
          ...navGroups
            .map(
              (group) => `
                <button class="topic-link${activeLevel === group.level ? " active-filter" : ""}" type="button" data-level-filter="${escapeHtml(group.level)}">
                  <span class="topic-track">${escapeHtml(group.level)}</span>
                  <span class="topic-link-body">
                    <small>${group.count} ${commonText().interviewQuestionCount}</small>
                  </span>
                </button>
              `
            )
        ].join("")
      : `<div class="empty-state">${commonText().noLevels}</div>`;

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
                  <p class="eyebrow">${commonText().interviewLevelEyebrow}</p>
                  <h2>${escapeHtml(group.level)}</h2>
                  <p class="topic-summary">${group.questions.length} ${commonText().interviewLevelSummary}</p>
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
      : `<div class="empty-state">${commonText().noLevelQuestions}</div>`;

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
                <h4>${commonText().learnFor}</h4>
                <ul class="phase-list">
                  ${item.learnFor.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
                </ul>
              </article>
              <article class="technology-block">
                <h4>${commonText().integratesWith}</h4>
                <ul class="phase-list">
                  ${item.integratesWith.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
                </ul>
              </article>
              <article class="technology-block">
                <h4>${commonText().lookAt}</h4>
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
                <h4>${commonText().learnWhat}</h4>
                <ul class="phase-list">
                  ${phase.topics.map((topic) => `<li>${escapeHtml(topic)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>${commonText().doInRepo}</h4>
                <ul class="phase-list">
                  ${phase.actions.map((action) => `<li>${escapeHtml(action)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>${commonText().outcomes}</h4>
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
                <h4>${commonText().mistakes}</h4>
                <ul class="phase-list">
                  ${item.mistakes.map((mistake) => `<li>${escapeHtml(mistake)}</li>`).join("")}
                </ul>
              </article>
              <article class="phase-block">
                <h4>${commonText().fixLearning}</h4>
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
              <span>${commonText().pool}: ${bundle.questionIds.length} ${currentLanguage === "vi" ? "câu" : "questions"}</span>
              <span>${commonText().drawCount}: ${bundle.drawCount} ${currentLanguage === "vi" ? "câu" : "questions"}</span>
              <span>${commonText().level}: ${escapeHtml(bundle.level)}</span>
              <span>${commonText().suggestedTime}: ${escapeHtml(bundle.suggestedTime)}</span>
            </div>
            <p><button class="btn btn-primary" type="button" data-bundle-id="${escapeHtml(bundle.id)}">${commonText().startBundle}</button></p>
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
      quizWorkspace.innerHTML = `<div class="empty-state">${commonText().chooseBundle}</div>`;
      return;
    }

    const { bundle, questions } = currentAttempt;

    quizWorkspace.innerHTML = `
      <div class="quiz-summary-card">
        <p class="eyebrow">${escapeHtml(bundle.track)} / ${escapeHtml(bundle.title)}</p>
        <div class="quiz-summary-grid">
          <article><span>${commonText().totalQuestions}</span><strong>${questions.length}</strong></article>
          <article><span>${commonText().pool}</span><strong>${bundle.questionIds.length}</strong></article>
          <article><span>${commonText().suggestedTime}</span><strong>${escapeHtml(bundle.suggestedTime)}</strong></article>
          <article><span>${commonText().level}</span><strong>${escapeHtml(bundle.level)}</strong></article>
        </div>
        <div class="quiz-action-row">
          <button id="submitQuizButton" class="btn btn-primary" type="button">${commonText().submit}</button>
          <button id="retryQuizButton" class="btn btn-secondary" type="button">${commonText().retry}</button>
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
          <p class="eyebrow">${commonText().result}</p>
          <h3>${escapeHtml(band.label)}</h3>
          <p>${escapeHtml(band.description)}</p>
          <div class="quiz-result-grid">
            <article><span>${commonText().correctScore}</span><strong>${correctCount}/${currentAttempt.questions.length}</strong></article>
            <article><span>${commonText().ratio}</span><strong>${percent}%</strong></article>
            <article><span>${commonText().answered}</span><strong>${answeredCount}</strong></article>
            <article><span>${commonText().assessment}</span><strong>${escapeHtml(band.grade)}</strong></article>
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
  const answerTitle = normalizedQuestion.explanation.length > 0 ? commonText().answer : commonText().answerFallback;
  const explanationHtml = normalizedQuestion.explanation
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const applyHtml = normalizedQuestion.applyOrPitfalls
    .map((item) => `<li>${highlightText(item, keyword)}</li>`)
    .join("");
  const codeHtml = question.codeExample
    ? `<pre class="code-sample"><code>${highlightText(question.codeExample, keyword)}</code></pre>`
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
    <article class="question-card">
      <div class="question-meta">${metaPills}</div>
      <h3>${highlightText(question.question, keyword)}</h3>
      <div class="qa-grid">
        <section class="qa-block qa-block-primary">
          <h4>${answerTitle}</h4>
          <p>${highlightText(normalizedQuestion.answerShort, keyword)}</p>
        </section>
        <section class="qa-block">
          <h4>${commonText().explanation}</h4>
          <ul class="answer-list">${explanationHtml}</ul>
        </section>
        ${
          normalizedQuestion.applyOrPitfalls.length > 0
            ? `
              <section class="qa-block qa-block-warm">
                <h4>${commonText().apply}</h4>
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
      (currentLanguage === "vi"
        ? "Câu hỏi này cần được trả lời theo ngữ cảnh cụ thể của Java backend và tình huống thực tế."
        : "This question should be answered in the specific context of Java backend work and a real-world scenario."),
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
    target.innerHTML = `<div class="empty-state">${commonText().jsonError} ${escapeHtml(error.message)}</div>`;
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
      <p class="eyebrow">${escapeHtml(question.topic)} / ${currentLanguage === "vi" ? "Câu" : "Question"} ${question.order}</p>
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
