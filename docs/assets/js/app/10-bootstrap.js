document.addEventListener("DOMContentLoaded", async () => {
  const page = document.body.dataset.page;
  document.documentElement.lang = currentLanguage;
  applyTheme(currentTheme);
  mountSkipLink();
  mountSiteBrand();
  mountReadingProgress();
  mountBackToTop();
  mountKeyboardShortcuts();
  preserveLanguageLinks();
  mountLanguageSwitcher();
  applyStaticCopy(page);
  mountPageGuide(page);
  cleanVietnameseMojibakeInDom();

  try {
    if (page === "home") {
      setLoadingState(["homeStats"]);
      const [bank, roadmap, quizBank] = await Promise.all([
        loadJson(QUESTION_BANK_PATHS[currentLanguage], "question bank"),
        loadJson(ROADMAP_PATHS[currentLanguage], "roadmap"),
        loadJson(QUIZ_BANK_PATHS[currentLanguage], "quiz bank")
      ]);
      renderHome(normalizeQuestionBank(bank), roadmap, quizBank);
      cleanVietnameseMojibakeInDom();
      return;
    }

    if (page === "interview") {
      setLoadingState(["topicNav", "questionSections"]);
      const bank = normalizeQuestionBank(await loadJson(QUESTION_BANK_PATHS[currentLanguage], "question bank"));
      renderInterview(bank);
      cleanVietnameseMojibakeInDom();
      return;
    }

    if (page === "interview-levels") {
      setLoadingState(["levelNav", "levelSections"]);
      const bank = normalizeQuestionBank(await loadJson(QUESTION_BANK_PATHS[currentLanguage], "question bank"));
      renderInterviewLevels(bank);
      cleanVietnameseMojibakeInDom();
      return;
    }

    if (page === "roadmap") {
      setLoadingState([
        "roadmapBasics",
        "knowledgeTree",
        "technologyMap",
        "integrationFlow",
        "roadmapPhases",
        "practiceMatrix",
        "roadmapPitfalls",
        "roadmapVisual"
      ]);
      const roadmap = await loadJson(ROADMAP_PATHS[currentLanguage], "roadmap");
      renderRoadmap(roadmap);
      cleanVietnameseMojibakeInDom();
      return;
    }

    if (page === "quiz") {
      setLoadingState(["quizBundles", "quizWorkspace"]);
      const quizBank = await loadJson(QUIZ_BANK_PATHS[currentLanguage], "quiz bank");
      renderQuiz(quizBank);
      cleanVietnameseMojibakeInDom();
    }
  } catch (error) {
    renderError(error);
  }
});

