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

  const savedLevelState = {
    ...readPageState("interview-levels"),
    ...readQueryState(["search", "track", "kind", "sort", "activeLevel", "code"])
  };
  let codeOnly = savedLevelState.code === "1" || savedLevelState.code === true;
  restoreControlValues(savedLevelState, {
    search: levelSearch,
    track: levelTrackFilter,
    kind: levelKindFilter,
    sort: levelSortFilter
  });
  if (typeof savedLevelState.activeLevel === "string") {
    activeLevel = savedLevelState.activeLevel;
  }

  const renderByLevel = () => {
    const keyword = levelSearch?.value.trim().toLowerCase() || "";
    const selectedTrack = levelTrackFilter?.value || "";
    const selectedKind = levelKindFilter?.value || "";
    const selectedSort = levelSortFilter?.value || "default";
    writePageState("interview-levels", {
      search: levelSearch?.value || "",
      track: selectedTrack,
      kind: selectedKind,
      sort: selectedSort,
      activeLevel,
      code: codeOnly
    });
    writeQueryState({
      search: levelSearch?.value || "",
      track: selectedTrack,
      kind: selectedKind,
      sort: selectedSort === "default" ? "" : selectedSort,
      activeLevel,
      code: codeOnly ? "1" : ""
    }, ["search", "track", "kind", "sort", "activeLevel", "code"]);
    updateFilterSummary(document.querySelector(".question-toolbar"), [
      [copy().interviewLevels.searchLabel, levelSearch?.value || ""],
      [copy().interviewLevels.trackLabel, selectedTrack],
      [copy().interviewLevels.kindLabel, selectedKind],
      [copy().interviewLevels.sortLabel, selectedSort === "default" ? "" : getSelectedOptionLabel(levelSortFilter)],
      [currentLanguage === "vi" ? "Level đang chọn" : "Active level", activeLevel],
      [currentLanguage === "vi" ? "Code thực hành" : "Practice code", codeOnly ? currentLanguage === "vi" ? "Có code" : "Code examples only" : ""]
    ]);
    mountCodePracticeToggle(document.querySelector(".question-toolbar"), codeOnly, (nextValue) => {
      codeOnly = nextValue;
      activeLevel = "";
      renderByLevel();
    });

    const filteredQuestions = flatQuestions.filter((question) => {
      const haystack = getQuestionSearchText(question).toLowerCase();

      const matchesKeyword = !keyword || haystack.includes(keyword);
      const matchesTrack = !selectedTrack || question.track === selectedTrack;
      const matchesKind = !selectedKind || question.kind === selectedKind;
      const matchesCode = !codeOnly || Boolean(question.codeExample);

      return matchesKeyword && matchesTrack && matchesKind && matchesCode;
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
              <span class="topic-track">*</span>
              <span class="topic-link-body">
                <strong>${currentLanguage === "vi" ? "Tất cả level" : "All levels"}</strong>
                <small>${filteredQuestions.length} ${commonText().interviewQuestionCount}</small>
              </span>
            </button>
          `,
          ...navGroups
            .map(
              (group, index) => `
                <button class="topic-link${activeLevel === group.level ? " active-filter" : ""}" type="button" data-level-filter="${escapeHtml(group.level)}">
                  <span class="topic-track">${String(index + 1).padStart(2, "0")}</span>
                  <span class="topic-link-body">
                    <strong>${escapeHtml(group.level)}</strong>
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
            (group) => {
              const insights = summarizeQuestionGroup(group.questions, ["track", "kind", "topicTitle"]);
              return `
              <section class="topic-block" id="${escapeHtml(group.id)}">
                <div class="topic-header">
                  <div>
                    <p class="eyebrow">${commonText().interviewLevelEyebrow}</p>
                    <h2>${escapeHtml(group.level)}</h2>
                    <p class="topic-summary">${group.questions.length} ${commonText().interviewLevelSummary}</p>
                    <div class="topic-insights">
                      ${insights.map((item) => `<span>${escapeHtml(item)}</span>`).join("")}
                    </div>
                  </div>
                  <span class="topic-total">${group.questions.length} ${commonText().interviewQuestionCount}</span>
                </div>
                ${group.questions
                  .map((question, questionIndex) =>
                    renderQuestionCard(
                      question,
                      keyword,
                      [question.track, question.kind, question.topicTitle],
                      String(questionIndex + 1).padStart(2, "0")
                    )
                  )
                  .join("")}
              </section>
            `;
            }
          )
          .join("")
      : `<div class="empty-state">${commonText().noLevelQuestions}</div>`;

    if (levelTotalQuestions) {
      levelTotalQuestions.textContent = String(filteredQuestions.length);
      setPageTitleSuffix(copy().interviewLevels.title, `${filteredQuestions.length} questions`);
      updateResultNote(
        levelSections,
        currentLanguage === "vi"
          ? `Đang hiển thị ${filteredQuestions.length} câu hỏi trong ${sortedGroups.length} nhóm level.`
          : `Showing ${filteredQuestions.length} questions across ${sortedGroups.length} level groups.`
      );
    }
    mountCopyCodeButtons(levelSections);
    mountQuestionStudyActions(levelSections);
    mountSectionLinkButtons(levelSections);
  };

  const renderByLevelDebounced = debounce(renderByLevel);
  levelSearch?.addEventListener("input", renderByLevelDebounced);
  levelTrackFilter?.addEventListener("change", renderByLevel);
  levelKindFilter?.addEventListener("change", renderByLevel);
  levelSortFilter?.addEventListener("change", renderByLevel);
  mountClearFilters(
    document.querySelector(".question-toolbar"),
    [levelSearch, levelTrackFilter, levelKindFilter, levelSortFilter],
    () => {
      activeLevel = "";
      codeOnly = false;
      writePageState("interview-levels", {});
      writeQueryState({}, ["search", "track", "kind", "sort", "activeLevel", "code"]);
      renderByLevel();
    }
  );
  mountDetailControls(document.querySelector(".question-toolbar"));
  mountReadingDensityToggle(document.querySelector(".question-toolbar"), "interview-levels");
  mountCatalogToggle(document.querySelector(".sidebar-panel"), currentLanguage === "vi" ? "level" : "levels");

  renderByLevel();
}

