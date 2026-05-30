const LANGUAGE_STORAGE_KEY = "java-labs-language";
const THEME_STORAGE_KEY = "java-labs-theme";
const DEFAULT_LANGUAGE = "en";
const DEFAULT_THEME = "dark";
const currentLanguage = getCurrentLanguage();
const currentTheme = getCurrentTheme();
const jsonCache = new Map();
const PAGE_STATE_PREFIX = "java-labs-page-state";

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

function getCurrentLanguage() {
  const fromQuery = new URLSearchParams(window.location.search).get("lang");
  if (fromQuery === "vi" || fromQuery === "en") {
    localStorage.setItem(LANGUAGE_STORAGE_KEY, fromQuery);
    return fromQuery;
  }

  const storedLanguage = localStorage.getItem(LANGUAGE_STORAGE_KEY);
  if (storedLanguage === "vi" || storedLanguage === "en") {
    return storedLanguage;
  }

  return DEFAULT_LANGUAGE;
}

function getCurrentTheme() {
  const fromQuery = new URLSearchParams(window.location.search).get("theme");
  if (fromQuery === "light" || fromQuery === "dark") {
    localStorage.setItem(THEME_STORAGE_KEY, fromQuery);
    return fromQuery;
  }

  const storedTheme = localStorage.getItem(THEME_STORAGE_KEY);
  if (storedTheme === "light" || storedTheme === "dark") {
    return storedTheme;
  }

  return DEFAULT_THEME;
}

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
          body: "Làm trọn bộ câu hỏi theo cấp độ Fresher, Interview và Senior để tự đánh giá năng lực.",
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
      heading: "Thực chiến bằng bộ đề đầy đủ",
      intro:
        "Chọn một bộ đề và làm toàn bộ câu hỏi theo thứ tự rõ ràng. Kết quả giúp bạn nhìn lại mức năng lực từ cơ bản đến nâng cao.",
      statsLabel: "Quiz Stats",
      statBundle: "Bộ đề",
      statPool: "Ngân hàng câu",
      statDraw: "Cách hiển thị",
      statDrawValue: "Toàn bộ câu",
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
      drawCount: "Số câu",
      level: "Mức",
      suggestedTime: "Thời gian gợi ý",
      startBundle: "Bắt đầu bộ này",
      totalQuestions: "Tổng câu",
      submit: "Nộp bài và chấm điểm",
      retry: "Làm lại bộ này",
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
          body: "Take complete question sets across Fresher, Interview, and Senior levels to assess your skills.",
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
      heading: "Practice with complete exam sets",
      intro:
        "Choose a set and work through every question in a clear order. The result helps you review your level from basic to advanced.",
      statsLabel: "Quiz Stats",
      statBundle: "Bundles",
      statPool: "Question pool",
      statDraw: "Display mode",
      statDrawValue: "Full set",
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
      drawCount: "Questions shown",
      level: "Level",
      suggestedTime: "Suggested time",
      startBundle: "Start this bundle",
      totalQuestions: "Total questions",
      submit: "Submit and grade",
      retry: "Restart this set",
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

UI.vi = {
  ...UI.vi,
  home: {
    ...UI.vi.home,
    heading: "Học Java backend theo lộ trình rõ ràng",
    intro:
      "Đi từ Java Core đến Spring Boot, phỏng vấn, quiz và roadmap theo từng mốc học tập. Nội dung song ngữ được tổ chức để bạn luyện tập mỗi ngày.",
    cta: "Bắt đầu với roadmap",
    secondaryCta: "Luyện phỏng vấn",
    cards: [
      {
        title: "Ngân hàng câu hỏi Java và Spring",
        body: "Hệ thống câu hỏi và câu trả lời theo chủ đề, có lý thuyết, phân tích, ví dụ code và bài tập tự luyện.",
        cta: "Mở trang học"
      },
      {
        title: "Phỏng vấn theo cấp độ",
        body: "Câu hỏi phỏng vấn được nhóm từ cơ bản đến nâng cao, giúp bạn ôn luyện đúng level hiện tại.",
        cta: "Mở trang phỏng vấn"
      },
      {
        title: "Quiz đánh giá năng lực",
        body: "Làm trọn bộ câu hỏi theo cấp độ Fresher, Interview và Senior để tự kiểm tra điểm yếu.",
        cta: "Mở quiz"
      },
      {
        title: "Roadmap Java Backend",
        body: "Lộ trình từ Java Core đến Spring Boot, database, messaging, testing, vận hành và tư duy backend nâng cao.",
        cta: "Mở roadmap"
      }
    ]
  },
  interview: {
    ...UI.vi.interview,
    heading: "Luyện phỏng vấn Java theo chủ đề và cấp độ",
    intro:
      "Tìm câu hỏi, lọc theo độ khó, so sánh giải thích và xem ví dụ code mà vẫn giữ được ngữ cảnh học theo từng chủ đề.",
    statLevelsValue: "Cơ bản đến nâng cao",
    sidebarHeading: "Danh mục chủ đề",
    contentHeading: "Câu hỏi và câu trả lời",
    searchLabel: "Tìm kiếm",
    searchPlaceholder: "Ví dụ: transaction, JWT, Kafka...",
    levelLabel: "Mức độ",
    kindLabel: "Loại câu hỏi",
    sortLabel: "Sắp xếp"
  },
  interviewLevels: {
    ...UI.vi.interviewLevels,
    heading: "Câu hỏi phỏng vấn theo cấp độ",
    intro:
      "Ôn tập theo đúng mặt bằng phỏng vấn: bắt đầu từ nền tảng, lên trung cấp, nâng cao, rồi đến câu hỏi mẹo và tư duy hệ thống.",
    statTotal: "Tổng câu",
    statGroups: "Số level",
    statRange: "Phạm vi",
    statRangeValue: "Core đến Senior",
    sidebarHeading: "Nhóm cấp độ",
    contentHeading: "Bộ câu hỏi theo level",
    searchLabel: "Tìm kiếm",
    searchPlaceholder: "Ví dụ: JWT, transaction, Kafka...",
    kindLabel: "Loại câu hỏi",
    sortLabel: "Sắp xếp"
  },
  roadmap: {
    ...UI.vi.roadmap,
    heading: "Roadmap học Java Backend",
    intro:
      "Nhìn rõ nên bắt đầu từ đâu, học theo thứ tự nào, phần nào là nền tảng, phần nào là nâng cao và cách nối kiến thức thành một hệ thống backend hoàn chỉnh.",
    statPhase: "Giai đoạn",
    statTopic: "Chủ đề",
    statGoal: "Mục tiêu",
    statGoalValue: "Core đến Senior",
    sections: [
      ["Bản đồ", "Roadmap là gì?"],
      ["Khởi đầu", "Nếu mới bắt đầu thì học gì trước?"],
      ["Cây tri thức", "Knowledge Tree"],
      ["Công nghệ", "Học công nghệ nào, tích hợp ra sao?"],
      ["Tích hợp", "Dòng chảy tích hợp toàn hệ thống"],
      ["Lộ trình", "Học theo giai đoạn"],
      ["Thực chiến", "Học phần nào thì làm phần đó"],
      ["Bẫy học", "Những chỗ rất dễ học lệch hoặc hiểu hời hợt"],
      ["Hình dung", "Roadmap Visual"]
    ],
    introCards: [
      ["Tư duy đúng", "Roadmap không phải danh sách học thuộc. Nó là bản đồ để bạn biết học phần nào trước, phần nào sau và vì sao chúng liên kết với nhau."],
      ["Mục tiêu", "Đi từ biết cú pháp Java đến hiểu hệ thống backend: dữ liệu, auth, transaction, event, testing, observability và vận hành."],
      ["Cách dùng", "Chọn một giai đoạn, học theo thứ tự, đối chiếu code trong repo và quay lại bổ sung phần còn hổng thay vì nhảy lung tung."]
    ]
  },
  quiz: {
    ...UI.vi.quiz,
    heading: "Thực chiến bằng bộ đề đầy đủ",
    intro:
      "Chọn một bộ đề và làm toàn bộ câu hỏi theo thứ tự rõ ràng. Kết quả giúp bạn nhìn lại năng lực từ cơ bản đến nâng cao.",
    statBundle: "Bộ đề",
    statPool: "Ngân hàng câu",
    statDraw: "Cách hiển thị",
    statDrawValue: "Toàn bộ câu",
    sections: [
      ["Chọn đề", "Bộ đề theo cấp độ"],
      ["Làm bài", "Quiz Workspace"]
    ],
    empty: "Chọn một bộ đề để bắt đầu."
  },
  common: {
    ...UI.vi.common,
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
    interviewLevelSummary: "câu hỏi đã được tổng hợp ở level này.",
    learnFor: "Học để làm gì?",
    integratesWith: "Tích hợp với gì?",
    lookAt: "Trong repo nên nhìn đâu?",
    learnWhat: "Học gì?",
    doInRepo: "Làm gì trong repo?",
    outcomes: "Khi xong phải đạt",
    mistakes: "Dễ hiểu sai ở đâu?",
    fixLearning: "Nên sửa cách học thế nào?",
    chooseBundle: "Chọn một bộ đề để bắt đầu.",
    drawCount: "Số câu",
    level: "Mức",
    suggestedTime: "Thời gian gợi ý",
    startBundle: "Bắt đầu bộ này",
    totalQuestions: "Tổng câu",
    submit: "Nộp bài và chấm điểm",
    retry: "Làm lại bộ này",
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
};

UI.vi = {
  ...UI.vi,
  nav: ["Trang chủ", "Ôn phỏng vấn", "Cấp độ phỏng vấn", "Quiz", "Roadmap"],
  home: {
    ...UI.vi.home,
    title: "Java Labs Learning Sites",
    eyebrow: "Java Labs",
    heading: "Học Java backend theo lộ trình rõ ràng",
    intro:
      "Đi từ Java Core đến Spring Boot, phỏng vấn, quiz và roadmap theo từng mốc học tập. Nội dung song ngữ được tổ chức để bạn luyện tập mỗi ngày.",
    cta: "Bắt đầu với roadmap",
    secondaryCta: "Luyện phỏng vấn",
    section: "Learning Sites",
    cards: [
      {
        title: "Ngân hàng câu hỏi Java và Spring",
        body: "Hệ thống câu hỏi và câu trả lời theo chủ đề, có lý thuyết, phân tích, ví dụ code và bài tập tự luyện.",
        cta: "Mở trang học"
      },
      {
        title: "Phỏng vấn theo cấp độ",
        body: "Câu hỏi phỏng vấn được nhóm từ cơ bản đến nâng cao, giúp bạn ôn luyện đúng level hiện tại.",
        cta: "Mở trang phỏng vấn"
      },
      {
        title: "Quiz đánh giá năng lực",
        body: "Làm trọn bộ câu hỏi theo cấp độ Fresher, Interview và Senior để tự kiểm tra điểm yếu.",
        cta: "Mở quiz"
      },
      {
        title: "Roadmap Java Backend",
        body: "Lộ trình từ Java Core đến Spring Boot, database, messaging, testing, vận hành và tư duy backend nâng cao.",
        cta: "Mở roadmap"
      }
    ]
  },
  interview: {
    ...UI.vi.interview,
    title: "Java Interview Practice",
    eyebrow: "Java Labs / Luyện phỏng vấn",
    heading: "Luyện phỏng vấn Java theo chủ đề và cấp độ",
    intro:
      "Tìm câu hỏi, lọc theo độ khó, so sánh giải thích và xem ví dụ code mà vẫn giữ được ngữ cảnh học theo từng chủ đề.",
    statsLabel: "Thống kê ngân hàng",
    statTopics: "Chủ đề",
    statQuestions: "Câu hỏi",
    statLevels: "Cấp độ",
    statLevelsValue: "Cơ bản đến nâng cao",
    sidebarEyebrow: "Topics",
    sidebarHeading: "Danh mục chủ đề",
    contentEyebrow: "Nội dung",
    contentHeading: "Câu hỏi và câu trả lời",
    searchLabel: "Tìm kiếm",
    searchPlaceholder: "Ví dụ: transaction, JWT, Kafka...",
    levelLabel: "Mức độ",
    kindLabel: "Loại câu hỏi",
    sortLabel: "Sắp xếp"
  },
  interviewLevels: {
    ...UI.vi.interviewLevels,
    title: "Java Interview Levels",
    eyebrow: "Java Labs / Cấp độ phỏng vấn",
    heading: "Câu hỏi phỏng vấn theo cấp độ",
    intro:
      "Ôn tập theo đúng mặt bằng phỏng vấn: bắt đầu từ nền tảng, lên trung cấp, nâng cao, rồi đến câu hỏi mẹo và tư duy hệ thống.",
    statsLabel: "Thống kê phỏng vấn",
    statTotal: "Tổng câu",
    statGroups: "Số level",
    statRange: "Phạm vi",
    statRangeValue: "Core đến Senior",
    sidebarEyebrow: "Levels",
    sidebarHeading: "Nhóm cấp độ",
    contentEyebrow: "Interview Bank",
    contentHeading: "Bộ câu hỏi theo level",
    searchLabel: "Tìm kiếm",
    searchPlaceholder: "Ví dụ: JWT, transaction, Kafka...",
    trackLabel: "Track",
    kindLabel: "Loại câu hỏi",
    sortLabel: "Sắp xếp"
  },
  roadmap: {
    ...UI.vi.roadmap,
    title: "Java Backend Roadmap",
    eyebrow: "Java Labs / Roadmap",
    heading: "Roadmap học Java Backend",
    intro:
      "Nhìn rõ nên bắt đầu từ đâu, học theo thứ tự nào, phần nào là nền tảng, phần nào là nâng cao và cách nối kiến thức thành một hệ thống backend hoàn chỉnh.",
    statsLabel: "Thống kê roadmap",
    statPhase: "Giai đoạn",
    statTopic: "Chủ đề",
    statGoal: "Mục tiêu",
    statGoalValue: "Core đến Senior",
    sections: [
      ["Bản đồ", "Roadmap là gì?"],
      ["Khởi đầu", "Nếu mới bắt đầu thì học gì trước?"],
      ["Cây tri thức", "Knowledge Tree"],
      ["Công nghệ", "Học công nghệ nào, tích hợp ra sao?"],
      ["Tích hợp", "Dòng chảy tích hợp toàn hệ thống"],
      ["Lộ trình", "Học theo giai đoạn"],
      ["Thực chiến", "Học phần nào thì làm phần đó"],
      ["Bẫy học", "Những chỗ rất dễ học lệch hoặc hiểu hời hợt"],
      ["Hình dung", "Roadmap Visual"]
    ],
    introCards: [
      ["Tư duy đúng", "Roadmap không phải danh sách học thuộc. Nó là bản đồ để bạn biết học phần nào trước, phần nào sau và vì sao chúng liên kết với nhau."],
      ["Mục tiêu", "Đi từ biết cú pháp Java đến hiểu hệ thống backend: dữ liệu, auth, transaction, event, testing, observability và vận hành."],
      ["Cách dùng", "Chọn một giai đoạn, học theo thứ tự, đối chiếu code trong repo và quay lại bổ sung phần còn hổng thay vì nhảy lung tung."]
    ]
  },
  quiz: {
    ...UI.vi.quiz,
    title: "Java Backend Quiz Arena",
    eyebrow: "Java Labs / Quiz Arena",
    heading: "Thực chiến bằng bộ đề đầy đủ",
    intro:
      "Chọn một bộ đề và làm toàn bộ câu hỏi theo thứ tự rõ ràng. Kết quả giúp bạn nhìn lại năng lực từ cơ bản đến nâng cao.",
    statsLabel: "Thống kê quiz",
    statBundle: "Bộ đề",
    statPool: "Ngân hàng câu",
    statDraw: "Cách hiển thị",
    statDrawValue: "Toàn bộ câu",
    sections: [
      ["Chọn đề", "Bộ đề theo cấp độ"],
      ["Làm bài", "Quiz Workspace"]
    ],
    empty: "Chọn một bộ đề để bắt đầu."
  },
  common: {
    ...UI.vi.common,
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
    interviewLevelSummary: "câu hỏi đã được tổng hợp ở level này.",
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
    drawCount: "Số câu",
    level: "Mức",
    suggestedTime: "Thời gian gợi ý",
    startBundle: "Bắt đầu bộ này",
    totalQuestions: "Tổng câu",
    submit: "Nộp bài và chấm điểm",
    retry: "Làm lại bộ này",
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
};

