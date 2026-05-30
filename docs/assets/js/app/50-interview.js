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
  const initialTopicId = normalizeTopicId(decodeURIComponent(window.location.hash.replace("#", "")));
  let activeTopicId = bank.topics.some((topic) => topic.id === initialTopicId)
    ? initialTopicId
    : bank.topics[0]?.id || "";

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

  const interviewState = {
    ...readPageState("interview"),
    ...readQueryState(["search", "level", "kind", "sort", "code"])
  };
  let codeOnly = interviewState.code === "1" || interviewState.code === true;
  restoreControlValues(interviewState, {
    search: questionSearch,
    level: levelFilter,
    kind: kindFilter,
    sort: sortFilter
  });

  let lastTopicNavKey = "";

  const syncTopicNavActiveState = () => {
    topicNav.querySelectorAll("[data-topic-id]").forEach((link) => {
      const topicId = link.getAttribute("data-topic-id") || "";
      link.classList.toggle("active-jump", topicId === activeTopicId);
      if (topicId === activeTopicId) {
        link.setAttribute("aria-current", "true");
      } else {
        link.removeAttribute("aria-current");
      }
    });
  };

  const renderFiltered = () => {
    const keyword = questionSearch?.value.trim().toLowerCase() || "";
    const selectedLevel = levelFilter?.value || "";
    const selectedKind = kindFilter?.value || "";
    const selectedSort = sortFilter?.value || "default";
    writePageState("interview", {
      search: questionSearch?.value || "",
      level: selectedLevel,
      kind: selectedKind,
      sort: selectedSort,
      code: codeOnly
    });
    writeQueryState({
      search: questionSearch?.value || "",
      level: selectedLevel,
      kind: selectedKind,
      sort: selectedSort === "default" ? "" : selectedSort,
      code: codeOnly ? "1" : ""
    }, ["search", "level", "kind", "sort", "code"]);
    updateFilterSummary(document.querySelector(".question-toolbar"), [
      [copy().interview.searchLabel, questionSearch?.value || ""],
      [copy().interview.levelLabel, selectedLevel],
      [copy().interview.kindLabel, selectedKind],
      [copy().interview.sortLabel, selectedSort === "default" ? "" : getSelectedOptionLabel(sortFilter)],
      [currentLanguage === "vi" ? "Code thực hành" : "Practice code", codeOnly ? currentLanguage === "vi" ? "Có code" : "Code examples only" : ""]
    ]);
    mountCodePracticeToggle(document.querySelector(".question-toolbar"), codeOnly, (nextValue) => {
      codeOnly = nextValue;
      activeTopicId = "";
      renderFiltered();
    });

    const filteredTopics = bank.topics
      .map((topic, originalIndex) => ({
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
          const matchesCode = !codeOnly || Boolean(question.codeExample);

          return matchesKeyword && matchesLevel && matchesKind && matchesCode;
        }),
        originalIndex
      }))
      .map((topic) => ({
        ...topic,
        questions: sortQuestions(topic.questions, selectedSort)
      }))
      .filter((topic) => topic.questions.length > 0);

    const sortedTopics = selectedSort === "title-asc"
      ? [...filteredTopics].sort((a, b) => a.title.localeCompare(b.title, "vi"))
      : filteredTopics.sort((a, b) => a.originalIndex - b.originalIndex);

    if (activeTopicId && !sortedTopics.some((topic) => topic.id === activeTopicId)) {
      activeTopicId = "";
    }

    const visibleTopics = activeTopicId
      ? sortedTopics.filter((topic) => topic.id === activeTopicId)
      : sortedTopics;

    topicNav.dataset.topicMode = activeTopicId ? "single" : "all";
    const topicNavKey = JSON.stringify([
      keyword,
      selectedLevel,
      selectedKind,
      selectedSort,
      sortedTopics.map((topic) => [topic.id, topic.track, topic.title, topic.summary, topic.questions.length])
    ]);

    if (sortedTopics.length && topicNavKey !== lastTopicNavKey) {
      const allTopicsLabel = currentLanguage === "vi" ? "T\u1ea5t c\u1ea3 ch\u1ee7 \u0111\u1ec1" : "All topics";
      const visibleQuestionLabel = commonText().interviewQuestionCount;
      const totalVisibleQuestions = sortedTopics.reduce((sum, topic) => sum + topic.questions.length, 0);
      topicNav.innerHTML = [
        `
          <a class="topic-link${activeTopicId === "" ? " active-jump" : ""}" href="#questionSections" data-topic-id="">
            <span class="topic-track">${commonText().all}</span>
            <span class="topic-link-body">
              <strong>${allTopicsLabel}</strong>
              <span class="topic-count">${totalVisibleQuestions} ${visibleQuestionLabel}</span>
            </span>
          </a>
        `,
        ...sortedTopics.map(
          (topic) => `
            <a class="topic-link${activeTopicId === topic.id ? " active-jump" : ""}" href="#${escapeHtml(topic.id)}" data-topic-id="${escapeHtml(topic.id)}">
              <span class="topic-track">${escapeHtml(compactTrackLabel(topic.track))}</span>
              <span class="topic-link-body">
                <strong title="${escapeHtml(topic.title)}">${highlightText(topic.title, keyword)}</strong>
                <small title="${escapeHtml(topic.summary)}">${highlightText(topic.summary, keyword)}</small>
                <span class="topic-count">${topic.questions.length} ${visibleQuestionLabel}</span>
              </span>
            </a>
          `
        )
      ].join("");
      lastTopicNavKey = topicNavKey;
    } else if (!sortedTopics.length) {
      topicNav.innerHTML = `<div class="empty-state">${commonText().noTopics}</div>`;
      lastTopicNavKey = topicNavKey;
    }

    syncTopicNavActiveState();

    questionSections.innerHTML = visibleTopics.length
      ? visibleTopics
          .map(
            (topic, topicIndex) => {
              const insights = summarizeQuestionGroup(topic.questions, ["level", "kind"]);
              return `
              <section class="topic-block" id="${escapeHtml(topic.id)}">
                <div class="topic-header">
                  <div>
                    <p class="eyebrow">${escapeHtml(topic.track)}</p>
                    <h2>${highlightText(topic.title, keyword)}</h2>
                    <p class="topic-summary">${highlightText(topic.summary, keyword)}</p>
                    <div class="topic-insights">
                      ${insights.map((item) => `<span>${escapeHtml(item)}</span>`).join("")}
                    </div>
                  </div>
                  <span class="topic-total">${topic.questions.length} ${commonText().interviewQuestionCount}</span>
                </div>
                ${topic.questions
                  .map((question, questionIndex) =>
                    renderQuestionCard(question, keyword, [], `${topicIndex + 1}.${questionIndex + 1}`)
                  )
                  .join("")}
              </section>
            `;
            }
          )
          .join("")
      : `<div class="empty-state">${commonText().noQuestions}</div>`;

    if (questionCount) {
      const visibleQuestions = visibleTopics.reduce((sum, topic) => sum + topic.questions.length, 0);
      questionCount.textContent = String(visibleQuestions);
      setPageTitleSuffix(copy().interview.title, `${visibleQuestions} questions`);
      updateResultNote(
        questionSections,
        currentLanguage === "vi"
          ? `Đang hiển thị ${visibleQuestions} câu hỏi trong ${visibleTopics.length} chủ đề.`
          : `Showing ${visibleQuestions} questions across ${visibleTopics.length} topics.`
      );
      if (visibleTopics.length === 1) {
        const activeTopic = visibleTopics[0];
        updateResultNote(
          questionSections,
          currentLanguage === "vi"
            ? `Dang xem topic: ${activeTopic.title}. Hien thi ${visibleQuestions} cau hoi. Chon "Tat ca chu de" de xem toan bo.`
            : `Viewing topic: ${activeTopic.title}. Showing ${visibleQuestions} questions. Choose "All topics" to see everything.`
        );
      }
      if (currentLanguage === "vi") {
        const activeTopic = visibleTopics.length === 1 ? visibleTopics[0] : null;
        updateResultNote(
          questionSections,
          activeTopic
            ? `\u0110ang xem topic: ${activeTopic.title}. Hi\u1ec3n th\u1ecb ${visibleQuestions} c\u00e2u h\u1ecfi. Ch\u1ecdn "T\u1ea5t c\u1ea3 ch\u1ee7 \u0111\u1ec1" \u0111\u1ec3 xem to\u00e0n b\u1ed9.`
            : `\u0110ang hi\u1ec3n th\u1ecb ${visibleQuestions} c\u00e2u h\u1ecfi trong ${visibleTopics.length} ch\u1ee7 \u0111\u1ec1.`
        );
      }
    }
    mountCopyCodeButtons(questionSections);
    mountQuestionStudyActions(questionSections);
    mountSectionLinkButtons(questionSections);
  };

  topicNav.addEventListener("click", (event) => {
    const link = event.target.closest("[data-topic-id]");
    if (!link || !topicNav.contains(link)) {
      return;
    }

    event.preventDefault();
    const nextTopicId = link.getAttribute("data-topic-id") || "";
    if (activeTopicId === nextTopicId) {
      scrollToElement(nextTopicId ? document.getElementById(nextTopicId) : questionSections);
      return;
    }

    activeTopicId = nextTopicId;
    history.replaceState(null, "", activeTopicId ? `#${activeTopicId}` : window.location.pathname + window.location.search);
    renderFiltered();
    requestAnimationFrame(() => {
      scrollToElement(activeTopicId ? document.getElementById(activeTopicId) : questionSections);
    });
  });

  const renderFilteredDebounced = debounce(renderFiltered);
  questionSearch?.addEventListener("input", () => {
    activeTopicId = "";
    renderFilteredDebounced();
  });
  levelFilter?.addEventListener("change", renderFiltered);
  kindFilter?.addEventListener("change", renderFiltered);
  sortFilter?.addEventListener("change", renderFiltered);
  mountClearFilters(
    document.querySelector(".question-toolbar"),
    [questionSearch, levelFilter, kindFilter, sortFilter],
    () => {
      activeTopicId = "";
      codeOnly = false;
      writePageState("interview", {});
      writeQueryState({}, ["search", "level", "kind", "sort", "code"]);
      renderFiltered();
    }
  );
  mountDetailControls(document.querySelector(".question-toolbar"));
  mountReadingDensityToggle(document.querySelector(".question-toolbar"), "interview");
  mountCatalogToggle(document.querySelector(".sidebar-panel"), currentLanguage === "vi" ? "chủ đề" : "topics");

  renderFiltered();
}

