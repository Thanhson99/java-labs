function setLoadingState(ids) {
  const message = currentLanguage === "vi" ? "Đang tải nội dung..." : "Loading content...";
  ids.forEach((id) => {
    const target = document.getElementById(id);
    if (target) {
      target.innerHTML = `<div class="loading-state" role="status">${message}</div>`;
    }
  });
}

function mountSkipLink() {
  if (document.querySelector(".skip-link")) {
    return;
  }

  const main = document.querySelector("main");
  if (!main) {
    return;
  }

  if (!main.id) {
    main.id = "mainContent";
  }

  const link = document.createElement("a");
  link.className = "skip-link";
  link.href = `#${main.id}`;
  link.textContent = currentLanguage === "vi" ? "Bỏ qua điều hướng" : "Skip to content";
  document.body.prepend(link);
}

function mountSiteBrand() {
  const nav = document.querySelector(".top-nav");
  if (!nav || nav.querySelector(".site-brand")) {
    return;
  }

  nav.setAttribute("aria-label", currentLanguage === "vi" ? "Điều hướng chính" : "Primary navigation");

  const brand = document.createElement("a");
  brand.className = "site-brand";
  brand.href = "index.html";
  brand.innerHTML = `
    <span class="site-brand-mark">JL</span>
    <span class="site-brand-copy">
      <span class="site-brand-text">Java Labs</span>
      <span class="site-brand-subtitle">${currentLanguage === "vi" ? "Nền tảng học Java" : "Java learning platform"}</span>
    </span>
  `;
  nav.prepend(brand);

  nav.querySelectorAll(".nav-link.active").forEach((link) => {
    link.setAttribute("aria-current", "page");
  });
}

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

function applyTheme(theme) {
  const normalizedTheme = theme === "dark" ? "dark" : "light";
  document.documentElement.dataset.theme = normalizedTheme;
  setMetaContent("theme-color", normalizedTheme === "dark" ? "#0F172A" : "#F89820");
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
    <button class="language-pill${currentLanguage === "vi" ? " active" : ""}" type="button" data-lang="vi" aria-label="Tiếng Việt">${copy().language.vi}</button>
    <button class="language-pill${currentLanguage === "en" ? " active" : ""}" type="button" data-lang="en" aria-label="English">${copy().language.en}</button>
    <button class="theme-toggle" type="button" data-theme-toggle aria-pressed="${currentTheme === "dark"}" aria-label="${currentLanguage === "vi" ? "Đổi giao diện sáng tối" : "Toggle color theme"}">
      <span class="theme-icon" aria-hidden="true"></span>
      <span class="theme-label">${currentTheme === "dark" ? "Dark" : "Light"}</span>
    </button>
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
      localStorage.setItem(LANGUAGE_STORAGE_KEY, nextLanguage);
      window.location.href = url.toString();
    });
  });

  switcher.querySelector("[data-theme-toggle]")?.addEventListener("click", () => {
    const activeTheme = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
    const nextTheme = activeTheme === "dark" ? "light" : "dark";
    localStorage.setItem(THEME_STORAGE_KEY, nextTheme);
    applyTheme(nextTheme);
    const toggle = switcher.querySelector("[data-theme-toggle]");
    const label = switcher.querySelector(".theme-label");
    if (toggle) {
      toggle.setAttribute("aria-pressed", String(nextTheme === "dark"));
    }
    if (label) {
      label.textContent = nextTheme === "dark" ? "Dark" : "Light";
    }
    const url = new URL(window.location.href);
    url.searchParams.set("theme", nextTheme);
    window.history.replaceState({}, "", url.toString());
    preserveLanguageLinks();
  });
}

function updateResultNote(anchor, text) {
  if (!anchor) {
    return;
  }

  const parent = anchor.parentElement;
  if (!parent) {
    return;
  }

  let note = parent.querySelector(".result-note");
  if (!note) {
    note = document.createElement("div");
    note.className = "result-note";
    note.setAttribute("aria-live", "polite");
    parent.insertBefore(note, anchor);
  }

  note.textContent = text;
}

function mountBackToTop() {
  if (document.getElementById("backToTop")) {
    return;
  }

  const button = document.createElement("button");
  button.id = "backToTop";
  button.className = "back-to-top";
  button.type = "button";
  button.textContent = "Top";
  button.setAttribute("aria-label", currentLanguage === "vi" ? "Ve dau trang" : "Back to top");
  button.addEventListener("click", () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  });
  document.body.appendChild(button);

  const updateVisibility = () => {
    button.classList.toggle("visible", window.scrollY > 480);
  };
  window.addEventListener("scroll", updateVisibility, { passive: true });
  updateVisibility();
}

function mountReadingProgress() {
  if (document.getElementById("readingProgress")) {
    return;
  }

  const progress = document.createElement("div");
  progress.id = "readingProgress";
  progress.className = "reading-progress";
  progress.innerHTML = "<span></span>";
  document.body.prepend(progress);

  const update = () => {
    const scrollable = document.documentElement.scrollHeight - window.innerHeight;
    const percent = scrollable > 0 ? (window.scrollY / scrollable) * 100 : 0;
    const bar = progress.querySelector("span");
    if (bar) {
      bar.style.width = `${Math.min(100, Math.max(0, percent))}%`;
    }
  };

  window.addEventListener("scroll", update, { passive: true });
  window.addEventListener("resize", update);
  update();
}

function mountCopyCodeButtons(scope = document) {
  scope.querySelectorAll(".code-sample").forEach((block) => {
    if (block.parentElement?.classList.contains("code-copy-wrap")) {
      return;
    }

    const wrapper = document.createElement("div");
    wrapper.className = "code-copy-wrap";
    block.parentNode?.insertBefore(wrapper, block);
    wrapper.appendChild(block);

    const button = document.createElement("button");
    button.className = "code-copy-button";
    button.type = "button";
    button.textContent = "Copy";
    button.addEventListener("click", async () => {
      const text = block.textContent || "";
      try {
        await navigator.clipboard.writeText(text);
        button.textContent = "Copied";
        setTimeout(() => {
          button.textContent = "Copy";
        }, 1200);
      } catch (_error) {
        button.textContent = "Copy failed";
        setTimeout(() => {
          button.textContent = "Copy";
        }, 1200);
      }
    });
    wrapper.appendChild(button);
  });
}

function mountSectionLinkButtons(scope = document) {
  scope.querySelectorAll(".topic-block[id], main > .panel[id]").forEach((section) => {
    if (section.querySelector("[data-copy-section-link]")) {
      return;
    }

    const header = section.querySelector(".topic-header, .section-heading");
    if (!header) {
      return;
    }

    const button = document.createElement("button");
    button.className = "section-link-button";
    button.type = "button";
    button.dataset.copySectionLink = "true";
    button.textContent = "#";
    button.setAttribute(
      "aria-label",
      currentLanguage === "vi" ? "Sao chép liên kết phần này" : "Copy link to this section"
    );
    button.addEventListener("click", async () => {
      const url = new URL(window.location.href);
      url.hash = section.id;
      try {
        await navigator.clipboard.writeText(url.toString());
        button.textContent = "OK";
      } catch (_error) {
        window.location.hash = section.id;
        button.textContent = "OK";
      }
      setTimeout(() => {
        button.textContent = "#";
      }, 1200);
    });

    header.appendChild(button);
  });
}

function mountActiveLinkFeedback(scope = document) {
  scope.querySelectorAll(".topic-link[href^='#'], .quiz-jump-list a[href^='#'], .quiz-review-group a[href^='#']").forEach((link) => {
    if (link.dataset.activeFeedback === "true") {
      return;
    }
    link.dataset.activeFeedback = "true";
    link.addEventListener("click", () => {
      const group = link.closest(".topic-nav, .quiz-jump-list, .quiz-review-group");
      group?.querySelectorAll(".active-jump").forEach((item) => item.classList.remove("active-jump"));
      link.classList.add("active-jump");
    });
  });
}

function mountSectionSpy(nav, sectionSelector) {
  if (!nav || !("IntersectionObserver" in window)) {
    return;
  }

  if (nav._sectionSpy) {
    nav._sectionSpy.disconnect();
  }

  const sections = Array.from(document.querySelectorAll(sectionSelector)).filter((section) => section.id);
  if (sections.length === 0) {
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      const visible = entries
        .filter((entry) => entry.isIntersecting)
        .sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
      if (!visible) {
        return;
      }

      const id = visible.target.id;
      nav.querySelectorAll(".active-jump").forEach((item) => item.classList.remove("active-jump"));
      const activeLink = nav.querySelector(`[href="#${CSS.escape(id)}"]`);
      activeLink?.classList.add("active-jump");
    },
    { rootMargin: "-20% 0px -65% 0px", threshold: [0.15, 0.3, 0.6] }
  );

  sections.forEach((section) => observer.observe(section));
  nav._sectionSpy = observer;
}

function scrollToElement(target, block = "start") {
  if (!target) {
    return;
  }

  const reducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)").matches;
  target.scrollIntoView({
    behavior: reducedMotion ? "auto" : "smooth",
    block,
    inline: "nearest"
  });
}

function mountRoadmapToc() {
  if (document.getElementById("roadmapToc")) {
    return;
  }

  const main = document.querySelector("main");
  const hero = document.querySelector(".hero");
  if (!main || !hero) {
    return;
  }

  const panels = Array.from(main.querySelectorAll(":scope > .panel"));
  if (panels.length === 0) {
    return;
  }

  const links = panels.map((panel, index) => {
    const heading = panel.querySelector(".section-heading h2");
    const eyebrow = panel.querySelector(".section-heading .eyebrow");
    const id = panel.id || `roadmap-section-${index + 1}`;
    panel.id = id;
    return `
      <a class="roadmap-toc-link" href="#${escapeHtml(id)}">
        <span>${String(index + 1).padStart(2, "0")}</span>
        <strong>${escapeHtml(heading?.textContent || `Section ${index + 1}`)}</strong>
        <small>${escapeHtml(eyebrow?.textContent || "")}</small>
      </a>
    `;
  });

  const toc = document.createElement("nav");
  toc.id = "roadmapToc";
  toc.className = "roadmap-toc";
  toc.setAttribute("aria-label", "Roadmap sections");
  toc.innerHTML = links.join("");
  hero.insertAdjacentElement("afterend", toc);
  mountActiveLinkFeedback(toc);
  mountSectionSpy(toc, "main > .panel");
}

function mountPageGuide(page) {
  if (!page || page === "roadmap" || document.getElementById("pageGuide")) {
    return;
  }

  const hero = document.querySelector(".hero");
  const main = document.querySelector("main");
  if (!hero || !main) {
    return;
  }

  const guides = getPageGuideItems(page);
  if (guides.length === 0) {
    return;
  }

  if (!main.id) {
    main.id = "mainContent";
  }

  const guide = document.createElement("section");
  guide.id = "pageGuide";
  guide.className = "page-guide";
  guide.setAttribute("aria-label", currentLanguage === "vi" ? "Huong dan thao tac nhanh" : "Quick page guide");
  guide.innerHTML = guides
    .map(
      (item, index) => `
        <a class="guide-card" href="${escapeHtml(item.href || `#${main.id}`)}">
          <span class="guide-index">${String(index + 1).padStart(2, "0")}</span>
          <span class="guide-body">
            <strong>${escapeHtml(item.title)}</strong>
            <small>${escapeHtml(item.body)}</small>
          </span>
        </a>
      `
    )
    .join("");

  hero.insertAdjacentElement("afterend", guide);
  preserveLanguageLinks();
  mountActiveLinkFeedback(guide);
}

function getPageGuideItems(page) {
  const text = {
    vi: {
      home: [
        ["Chọn lộ trình", "Bắt đầu với roadmap nếu bạn cần nhìn tổng thể.", "roadmap.html"],
        ["Ôn theo chủ đề", "Mở question bank để đọc toàn bộ nội dung theo topic.", "interview.html"],
        ["Tự đánh giá", "Làm trọn bộ quiz để kiểm tra mức độ hiện tại.", "quiz.html"]
      ],
      interview: [
        ["Tìm nhanh", "Gõ từ khóa, level hoặc loại câu hỏi để lọc đúng phần cần học.", "#questionSections"],
        ["Đọc theo topic", "Topic strip giữ đầy đủ chủ đề và số câu đang hiển thị.", "#topicNav"],
        ["Mở tất cả", "Dùng Expand all để đọc liên tục câu trả lời, ví dụ code và bài tập.", "#questionSections"]
      ],
      "interview-levels": [
        ["Chọn level", "Lọc theo level để ôn đúng mặt bằng phỏng vấn.", "#levelNav"],
        ["Kết hợp track", "Dùng track và question type để thu hẹp nội dung cần luyện.", "#levelSections"],
        ["Đọc đầy đủ", "Mỗi nhóm hiển thị toàn bộ câu hỏi phù hợp với bộ lọc.", "#levelSections"]
      ],
      quiz: [
        ["Chọn bộ đề", "Mỗi bundle hiển thị trọn bộ câu hỏi theo thứ tự rõ ràng.", "#quizBundles"],
        ["Theo dõi tiến độ", "Thanh progress và jump list cho biết câu nào đã trả lời.", "#quizWorkspace"],
        ["Xem kết quả", "Sau khi nộp bài, hệ thống giữ đáp án để bạn review ngay.", "#quizWorkspace"]
      ],
      roadmap: [
        ["Nhìn tổng thể", "Đọc mục bản đồ trước để biết cách dùng roadmap.", "#mainContent"],
        ["Đi theo giai đoạn", "Follow phase list để học theo thứ tự từ Core đến Senior.", "#roadmapPhases"],
        ["Gắn với thực hành", "Practice matrix nối từng kiến thức với việc cần làm trong repo.", "#practiceMatrix"]
      ]
    },
    en: {
      home: [
        ["Pick a path", "Start with the roadmap when you need the full direction.", "roadmap.html"],
        ["Study by topic", "Open the question bank to read every topic in order.", "interview.html"],
        ["Validate skills", "Take a complete quiz set to check your current level.", "quiz.html"]
      ],
      interview: [
        ["Find fast", "Use search, level, and type filters to reach the right content.", "#questionSections"],
        ["Read by topic", "The topic strip keeps every visible topic and count in one place.", "#topicNav"],
        ["Open all", "Use Expand all to read answers, code examples, and practice prompts continuously.", "#questionSections"]
      ],
      "interview-levels": [
        ["Choose a level", "Filter by level to review at the right interview baseline.", "#levelNav"],
        ["Combine filters", "Use track and question type to narrow the material.", "#levelSections"],
        ["Read complete sets", "Each group shows every question that matches your current filters.", "#levelSections"]
      ],
      quiz: [
        ["Choose a bundle", "Each bundle shows the full question set in a clear order.", "#quizBundles"],
        ["Track progress", "The progress bar and jump list show which questions are answered.", "#quizWorkspace"],
        ["Review result", "After submit, answers stay visible so you can review immediately.", "#quizWorkspace"]
      ],
      roadmap: [
        ["Scan the map", "Read the overview first to understand how to use the roadmap.", "#mainContent"],
        ["Follow phases", "Use the phase list to move from Core toward Senior topics.", "#roadmapPhases"],
        ["Tie to practice", "The practice matrix connects each knowledge area to repo work.", "#practiceMatrix"]
      ]
    }
  };

  return (text[currentLanguage][page] || []).map(([title, body, href]) => ({ title, body, href }));
}

function mountKeyboardShortcuts() {
  if (document.documentElement.dataset.keyboardShortcuts === "true") {
    return;
  }

  document.documentElement.dataset.keyboardShortcuts = "true";
  document.addEventListener("keydown", (event) => {
    const target = event.target;
    const isTyping = target instanceof HTMLInputElement ||
      target instanceof HTMLTextAreaElement ||
      target instanceof HTMLSelectElement;

    if (event.key === "/" && !isTyping) {
      const search = document.querySelector("input[type='search']");
      if (search) {
        event.preventDefault();
        search.focus();
      }
      return;
    }

    if (event.key === "Escape" && target instanceof HTMLInputElement && target.type === "search") {
      if (target.value) {
        target.value = "";
        target.dispatchEvent(new Event("input", { bubbles: true }));
      } else {
        target.blur();
      }
    }
  });
}

function mountClearFilters(toolbar, controls, callback) {
  if (!toolbar || toolbar.querySelector("[data-clear-filters]")) {
    return;
  }

  let actions = toolbar.querySelector(".toolbar-actions");
  if (!actions) {
    actions = document.createElement("div");
    actions.className = "toolbar-actions";
    toolbar.appendChild(actions);
  }

  const button = document.createElement("button");
  button.className = "btn btn-secondary clear-filters-button";
  button.type = "button";
  button.dataset.clearFilters = "true";
  button.textContent = currentLanguage === "vi" ? "Xóa bộ lọc" : "Clear filters";
  button.setAttribute("aria-label", currentLanguage === "vi" ? "Xóa tất cả bộ lọc" : "Clear all filters");
  button.addEventListener("click", () => {
    controls.forEach((control) => {
      if (control) {
        if (control.tagName === "SELECT" && !Array.from(control.options).some((option) => option.value === "")) {
          control.selectedIndex = 0;
        } else {
          control.value = "";
        }
      }
    });
    callback();
  });
  actions.appendChild(button);
}

function mountDetailControls(toolbar) {
  if (!toolbar || toolbar.querySelector("[data-details-open]")) {
    return;
  }

  let actions = toolbar.querySelector(".toolbar-actions");
  if (!actions) {
    actions = document.createElement("div");
    actions.className = "toolbar-actions";
    toolbar.appendChild(actions);
  }

  const expandButton = document.createElement("button");
  expandButton.className = "btn btn-secondary";
  expandButton.type = "button";
  expandButton.dataset.detailsOpen = "true";
  expandButton.textContent = currentLanguage === "vi" ? "Mở tất cả" : "Expand all";
  expandButton.setAttribute("aria-label", currentLanguage === "vi" ? "Mở tất cả nội dung chi tiết" : "Expand all details");
  expandButton.addEventListener("click", () => {
    document.querySelectorAll(".qa-detail").forEach((detail) => {
      detail.open = true;
    });
  });

  const collapseButton = document.createElement("button");
  collapseButton.className = "btn btn-secondary";
  collapseButton.type = "button";
  collapseButton.dataset.detailsClosed = "true";
  collapseButton.textContent = currentLanguage === "vi" ? "Thu gọn" : "Collapse";
  collapseButton.setAttribute("aria-label", currentLanguage === "vi" ? "Thu gọn tất cả nội dung chi tiết" : "Collapse all details");
  collapseButton.addEventListener("click", () => {
    document.querySelectorAll(".qa-detail").forEach((detail) => {
      detail.open = false;
    });
  });

  actions.append(expandButton, collapseButton);
}

function mountCodePracticeToggle(toolbar, isActive, callback) {
  if (!toolbar) {
    return;
  }

  let actions = toolbar.querySelector(".toolbar-actions");
  if (!actions) {
    actions = document.createElement("div");
    actions.className = "toolbar-actions";
    toolbar.appendChild(actions);
  }

  let button = actions.querySelector("[data-code-practice-toggle]");
  if (!button) {
    button = document.createElement("button");
    button.className = "btn btn-secondary code-practice-toggle";
    button.type = "button";
    button.dataset.codePracticeToggle = "true";
    button.addEventListener("click", () => {
      callback(button.getAttribute("aria-pressed") !== "true");
    });
    actions.prepend(button);
  }

  button.classList.toggle("active", Boolean(isActive));
  button.setAttribute("aria-pressed", String(Boolean(isActive)));
  button.textContent = isActive
    ? currentLanguage === "vi" ? "Đang lọc code" : "Code only"
    : currentLanguage === "vi" ? "Luyện code" : "Code practice";
  button.setAttribute(
    "aria-label",
    currentLanguage === "vi" ? "Chỉ hiển thị câu hỏi có code thực hành" : "Show only questions with practice code"
  );
}

function mountCatalogToggle(panel, label) {
  if (!panel || panel.querySelector("[data-catalog-toggle]")) {
    return;
  }

  const heading = panel.querySelector(".section-heading");
  if (!heading) {
    return;
  }

  const button = document.createElement("button");
  button.className = "btn btn-secondary catalog-toggle";
  button.type = "button";
  button.dataset.catalogToggle = "true";

  const update = () => {
    const collapsed = panel.classList.contains("catalog-collapsed");
    button.textContent = collapsed
      ? currentLanguage === "vi" ? `Hiện ${label}` : `Show ${label}`
      : currentLanguage === "vi" ? `Ẩn ${label}` : `Hide ${label}`;
    button.setAttribute("aria-expanded", String(!collapsed));
  };

  button.addEventListener("click", () => {
    panel.classList.toggle("catalog-collapsed");
    update();
  });

  heading.appendChild(button);
  update();
}

function mountReadingDensityToggle(toolbar, pageName) {
  if (!toolbar || toolbar.querySelector("[data-density-toggle]")) {
    return;
  }

  let actions = toolbar.querySelector(".toolbar-actions");
  if (!actions) {
    actions = document.createElement("div");
    actions.className = "toolbar-actions";
    toolbar.appendChild(actions);
  }

  const saved = readPageState(`${pageName}:density`);
  if (saved.mode === "compact") {
    document.body.classList.add("compact-reading");
  }

  const button = document.createElement("button");
  button.className = "btn btn-secondary density-toggle";
  button.type = "button";
  button.dataset.densityToggle = "true";

  const update = () => {
    const compact = document.body.classList.contains("compact-reading");
    button.textContent = compact
      ? currentLanguage === "vi" ? "Đọc thoáng" : "Comfort view"
      : currentLanguage === "vi" ? "Đọc gọn" : "Compact view";
    button.setAttribute("aria-pressed", String(compact));
  };

  button.addEventListener("click", () => {
    document.body.classList.toggle("compact-reading");
    writePageState(`${pageName}:density`, {
      mode: document.body.classList.contains("compact-reading") ? "compact" : "comfortable"
    });
    update();
  });

  actions.appendChild(button);
  update();
}

function updateFilterSummary(toolbar, entries) {
  if (!toolbar) {
    return;
  }

  let summary = toolbar.querySelector(".active-filter-summary");
  if (!summary) {
    summary = document.createElement("div");
    summary.className = "active-filter-summary";
    summary.setAttribute("aria-live", "polite");
  }
  toolbar.appendChild(summary);

  const activeEntries = entries.filter(([, value]) => String(value || "").trim());
  if (activeEntries.length === 0) {
    summary.innerHTML = "";
    return;
  }

  summary.innerHTML = activeEntries
    .map(([label, value]) => `
      <span class="filter-chip">
        <strong>${escapeHtml(label)}</strong>
        <em>${escapeHtml(value)}</em>
      </span>
    `)
    .join("");
}

function getSelectedOptionLabel(select) {
  if (!select) {
    return "";
  }

  return select.options[select.selectedIndex]?.textContent || select.value || "";
}

function debounce(callback, delay = 140) {
  let timeoutId = 0;
  return (...args) => {
    window.clearTimeout(timeoutId);
    timeoutId = window.setTimeout(() => callback(...args), delay);
  };
}

function stateKey(name) {
  return `${PAGE_STATE_PREFIX}:${currentLanguage}:${name}`;
}

function readPageState(name) {
  try {
    return JSON.parse(localStorage.getItem(stateKey(name)) || "{}");
  } catch (_error) {
    return {};
  }
}

function writePageState(name, value) {
  localStorage.setItem(stateKey(name), JSON.stringify(value));
}

function setPageTitleSuffix(baseTitle, suffix) {
  document.title = suffix ? `${baseTitle} - ${suffix}` : baseTitle;
}

function restoreControlValues(state, mapping) {
  Object.entries(mapping).forEach(([key, control]) => {
    if (control && typeof state[key] === "string") {
      control.value = state[key];
    }
  });
}

function readQueryState(keys) {
  const params = new URLSearchParams(window.location.search);
  return keys.reduce((state, key) => {
    const value = params.get(key);
    if (value !== null) {
      state[key] = value;
    }
    return state;
  }, {});
}

const writeQueryState = debounce((state, keys) => {
  const url = new URL(window.location.href);
  keys.forEach((key) => {
    const value = state[key] || "";
    if (value) {
      url.searchParams.set(key, value);
    } else {
      url.searchParams.delete(key);
    }
  });
  window.history.replaceState({}, "", url.toString());
}, 250);

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
      url.searchParams.set("theme", document.documentElement.dataset.theme || currentTheme);
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
  setText(".home-dashboard .section-heading h2", pageCopy.section);
  applyHomeEnhancementCopy();

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
    const action = target.querySelector(".card-action");
    if (action) {
      action.textContent = card.cta;
    }
  });
}

function applyHomeEnhancementCopy() {
  const text = currentLanguage === "vi"
    ? {
        overviewEyebrow: "Tổng quan",
        overviewTitle: "Không gian học Java backend có định hướng",
        pathsEyebrow: "Lộ trình học",
        pathsTitle: "Chọn bước tiếp theo hữu ích",
        previewEyebrow: "Nội dung mẫu",
        previewTitle: "Hôm nay bạn có thể học gì",
        metaDescription: "Nền tảng học Java từ JSON với phỏng vấn, quiz, roadmap và hỗ trợ tiếng Anh/tiếng Việt.",
        secondaryCta: "Xem roadmap",
        sideLabel: "Bắt đầu tại đây",
        footerTitle: "Tiếp tục học với lộ trình rõ ràng",
        footerCopy: "Dùng roadmap để định hướng, ngân hàng phỏng vấn để đào sâu, và quiz để kiểm tra nhanh.",
        footerNavLabel: "Điều hướng chân trang",
        footerLinks: ["Roadmap", "Interview Prep", "Quiz"],
        flow: [
          ["Chọn lộ trình", "Dùng roadmap khi bạn cần định hướng."],
          ["Học theo chủ đề", "Mở ngân hàng câu hỏi để lấp lỗ hổng kiến thức."],
          ["Kiểm tra lại", "Làm quiz khi cần kiểm tra nhanh trình độ."]
        ],
        stats: ["Chủ đề", "Câu hỏi phỏng vấn", "Giai đoạn roadmap", "Bộ đề quiz"],
        paths: [
          ["Xây nền tảng", "Bắt đầu với cú pháp, OOP, collections, exceptions, tests và kỹ năng đọc code.", "Theo roadmap"],
          ["Luyện trả lời phỏng vấn", "Đi từ định nghĩa ngắn sang giải thích thực tế, trade-off và ví dụ code.", "Luyện câu hỏi"],
          ["Kiểm tra bằng quiz", "Dùng bộ đề đầy đủ để tìm điểm yếu trước khi ôn sâu.", "Làm quiz"]
        ],
        preview: [
          ["Interview", "Đang tải ngân hàng câu hỏi...", "Chủ đề đầu tiên từ JSON sẽ hiển thị ở đây.", "Mở interview prep"],
          ["Roadmap", "Đang tải roadmap...", "Một mốc học tập từ JSON sẽ hiển thị ở đây.", "Xem roadmap"],
          ["Quiz", "Đang tải bộ đề quiz...", "Bộ đề đầu tiên từ JSON sẽ hiển thị ở đây.", "Mở quiz arena"]
        ]
      }
    : {
        overviewEyebrow: "Platform overview",
        overviewTitle: "A guided Java backend workspace",
        pathsEyebrow: "Learning paths",
        pathsTitle: "Choose the next useful step",
        previewEyebrow: "Content preview",
        previewTitle: "What you can study today",
        metaDescription: "A JSON-powered Java learning platform with interview prep, quizzes, roadmap guidance, and English/Vietnamese language support.",
        secondaryCta: "View roadmap",
        sideLabel: "Start here",
        footerTitle: "Keep learning with a clear path",
        footerCopy: "Use the roadmap for direction, the interview bank for depth, and quizzes for quick validation.",
        footerNavLabel: "Footer navigation",
        footerLinks: ["Roadmap", "Interview Prep", "Quiz"],
        flow: [
          ["Pick a path", "Use the roadmap when you need direction."],
          ["Study by topic", "Open the question bank to close knowledge gaps."],
          ["Validate", "Run quiz sets when you want a quick level check."]
        ],
        stats: ["Topics", "Interview questions", "Roadmap phases", "Quiz sets"],
        paths: [
          ["Build the foundation", "Start with syntax, OOP, collections, exceptions, tests, and code-reading habits.", "Follow the roadmap"],
          ["Practice interview answers", "Move from short definitions to practical explanations, trade-offs, and code examples.", "Practice questions"],
          ["Validate with quizzes", "Use complete quiz sets to find weak areas before deeper review.", "Take a quiz"]
        ],
        preview: [
          ["Interview", "Loading question bank...", "The first available topic from JSON will appear here.", "Open interview prep"],
          ["Roadmap", "Loading roadmap...", "A roadmap checkpoint from JSON will appear here.", "View roadmap"],
          ["Quiz", "Loading quiz sets...", "The first quiz bundle from JSON will appear here.", "Open quiz arena"]
        ]
      };

  if (currentLanguage === "vi") {
    Object.assign(text, {
      overviewEyebrow: "Tổng quan",
      overviewTitle: "Không gian học Java backend có định hướng",
      pathsEyebrow: "Lộ trình học",
      pathsTitle: "Chọn bước tiếp theo hữu ích",
      previewEyebrow: "Nội dung mẫu",
      previewTitle: "Hôm nay bạn có thể học gì",
      metaDescription: "Nền tảng học Java dùng dữ liệu JSON với phỏng vấn, quiz, roadmap và hỗ trợ tiếng Anh/tiếng Việt.",
      secondaryCta: "Xem roadmap",
      sideLabel: "Bắt đầu tại đây",
      footerTitle: "Tiếp tục học với lộ trình rõ ràng",
      footerCopy: "Dùng roadmap để định hướng, ngân hàng phỏng vấn để đào sâu, và quiz để kiểm tra nhanh.",
      footerNavLabel: "Điều hướng chân trang",
      footerLinks: ["Roadmap", "Interview Prep", "Quiz"],
      flow: [
        ["Chọn lộ trình", "Dùng roadmap khi bạn cần định hướng."],
        ["Học theo chủ đề", "Mở ngân hàng câu hỏi để lấp lỗ hổng kiến thức."],
        ["Kiểm tra lại", "Làm quiz khi cần kiểm tra nhanh trình độ."]
      ],
      stats: ["Chủ đề", "Câu hỏi phỏng vấn", "Giai đoạn roadmap", "Bộ đề quiz"],
      paths: [
        ["Xây nền tảng", "Bắt đầu với cú pháp, OOP, collections, exceptions, tests và kỹ năng đọc code.", "Theo roadmap"],
        ["Luyện trả lời phỏng vấn", "Đi từ định nghĩa ngắn sang giải thích thực tế, trade-off và ví dụ code.", "Luyện câu hỏi"],
        ["Kiểm tra bằng quiz", "Dùng bộ đề đầy đủ để tìm điểm yếu trước khi ôn sâu.", "Làm quiz"]
      ],
      preview: [
        ["Interview", "Đang tải ngân hàng câu hỏi...", "Chủ đề đầu tiên từ JSON sẽ hiển thị ở đây.", "Mở interview prep"],
        ["Roadmap", "Đang tải roadmap...", "Một mốc học tập từ JSON sẽ hiển thị ở đây.", "Xem roadmap"],
        ["Quiz", "Đang tải bộ đề quiz...", "Bộ đề đầu tiên từ JSON sẽ hiển thị ở đây.", "Mở quiz arena"]
      ]
    });
  }

  if (currentLanguage === "vi") {
    Object.assign(text, {
      overviewEyebrow: "T\u1ed5ng quan",
      overviewTitle: "Kh\u00f4ng gian h\u1ecdc Java backend c\u00f3 \u0111\u1ecbnh h\u01b0\u1edbng",
      pathsEyebrow: "L\u1ed9 tr\u00ecnh h\u1ecdc",
      pathsTitle: "Ch\u1ecdn b\u01b0\u1edbc ti\u1ebfp theo h\u1eefu \u00edch",
      previewEyebrow: "N\u1ed9i dung m\u1eabu",
      previewTitle: "H\u00f4m nay b\u1ea1n c\u00f3 th\u1ec3 h\u1ecdc g\u00ec",
      metaDescription: "N\u1ec1n t\u1ea3ng h\u1ecdc Java d\u00f9ng d\u1eef li\u1ec7u JSON v\u1edbi ph\u1ecfng v\u1ea5n, quiz, roadmap v\u00e0 h\u1ed7 tr\u1ee3 ti\u1ebfng Anh/ti\u1ebfng Vi\u1ec7t.",
      secondaryCta: "Xem roadmap",
      sideLabel: "B\u1eaft \u0111\u1ea7u t\u1ea1i \u0111\u00e2y",
      footerTitle: "Ti\u1ebfp t\u1ee5c h\u1ecdc v\u1edbi l\u1ed9 tr\u00ecnh r\u00f5 r\u00e0ng",
      footerCopy: "D\u00f9ng roadmap \u0111\u1ec3 \u0111\u1ecbnh h\u01b0\u1edbng, ng\u00e2n h\u00e0ng ph\u1ecfng v\u1ea5n \u0111\u1ec3 \u0111\u00e0o s\u00e2u, v\u00e0 quiz \u0111\u1ec3 ki\u1ec3m tra nhanh.",
      footerNavLabel: "\u0110i\u1ec1u h\u01b0\u1edbng ch\u00e2n trang",
      footerLinks: ["Roadmap", "Interview Prep", "Quiz"],
      flow: [
        ["Ch\u1ecdn l\u1ed9 tr\u00ecnh", "D\u00f9ng roadmap khi b\u1ea1n c\u1ea7n \u0111\u1ecbnh h\u01b0\u1edbng."],
        ["H\u1ecdc theo ch\u1ee7 \u0111\u1ec1", "M\u1edf ng\u00e2n h\u00e0ng c\u00e2u h\u1ecfi \u0111\u1ec3 l\u1ea5p l\u1ed7 h\u1ed5ng ki\u1ebfn th\u1ee9c."],
        ["Ki\u1ec3m tra l\u1ea1i", "L\u00e0m quiz khi c\u1ea7n ki\u1ec3m tra nhanh tr\u00ecnh \u0111\u1ed9."]
      ],
      stats: ["Ch\u1ee7 \u0111\u1ec1", "C\u00e2u h\u1ecfi ph\u1ecfng v\u1ea5n", "Giai \u0111o\u1ea1n roadmap", "B\u1ed9 \u0111\u1ec1 quiz"],
      paths: [
        ["X\u00e2y n\u1ec1n t\u1ea3ng", "B\u1eaft \u0111\u1ea7u v\u1edbi c\u00fa ph\u00e1p, OOP, collections, exceptions, tests v\u00e0 k\u1ef9 n\u0103ng \u0111\u1ecdc code.", "Theo roadmap"],
        ["Luy\u1ec7n tr\u1ea3 l\u1eddi ph\u1ecfng v\u1ea5n", "\u0110i t\u1eeb \u0111\u1ecbnh ngh\u0129a ng\u1eafn sang gi\u1ea3i th\u00edch th\u1ef1c t\u1ebf, trade-off v\u00e0 v\u00ed d\u1ee5 code.", "Luy\u1ec7n c\u00e2u h\u1ecfi"],
        ["Ki\u1ec3m tra b\u1eb1ng quiz", "D\u00f9ng b\u1ed9 \u0111\u1ec1 \u0111\u1ea7y \u0111\u1ee7 \u0111\u1ec3 t\u00ecm \u0111i\u1ec3m y\u1ebfu tr\u01b0\u1edbc khi \u00f4n s\u00e2u.", "L\u00e0m quiz"]
      ],
      preview: [
        ["Interview", "\u0110ang t\u1ea3i ng\u00e2n h\u00e0ng c\u00e2u h\u1ecfi...", "Ch\u1ee7 \u0111\u1ec1 \u0111\u1ea7u ti\u00ean t\u1eeb JSON s\u1ebd hi\u1ec3n th\u1ecb \u1edf \u0111\u00e2y.", "M\u1edf interview prep"],
        ["Roadmap", "\u0110ang t\u1ea3i roadmap...", "M\u1ed9t m\u1ed1c h\u1ecdc t\u1eadp t\u1eeb JSON s\u1ebd hi\u1ec3n th\u1ecb \u1edf \u0111\u00e2y.", "Xem roadmap"],
        ["Quiz", "\u0110ang t\u1ea3i b\u1ed9 \u0111\u1ec1 quiz...", "B\u1ed9 \u0111\u1ec1 \u0111\u1ea7u ti\u00ean t\u1eeb JSON s\u1ebd hi\u1ec3n th\u1ecb \u1edf \u0111\u00e2y.", "M\u1edf quiz arena"]
      ]
    });
  }

  setText(".home-overview .eyebrow", text.overviewEyebrow);
  setText("#overviewTitle", text.overviewTitle);
  setText(".home-paths .eyebrow", text.pathsEyebrow);
  setText("#pathsTitle", text.pathsTitle);
  setText(".home-preview .eyebrow", text.previewEyebrow);
  setText("#previewTitle", text.previewTitle);
  setMetaContent("description", text.metaDescription);
  setText(".hero-actions .btn-secondary", text.secondaryCta);
  setText(".home-focus-card .side-label", text.sideLabel);
  setText(".site-footer .eyebrow", copy().home.eyebrow);
  setText("#footerTitle", text.footerTitle);
  setText(".footer-copy", text.footerCopy);
  document.querySelector(".footer-links")?.setAttribute("aria-label", text.footerNavLabel);

  document.querySelectorAll(".home-focus-card .flow-list li").forEach((item, index) => {
    const flow = text.flow[index];
    if (!flow) {
      return;
    }
    setTextWithin(item, "strong", flow[0]);
    setTextWithin(item, "span", flow[1]);
  });

  document.querySelectorAll("#homeStats span").forEach((item, index) => {
    if (text.stats[index]) {
      item.textContent = text.stats[index];
    }
  });

  document.querySelectorAll(".learning-card").forEach((card, index) => {
    const path = text.paths[index];
    if (!path) {
      return;
    }
    setTextWithin(card, "h3", path[0]);
    setTextWithin(card, "p", path[1]);
    setTextWithin(card, ".card-action", path[2]);
  });

  document.querySelectorAll(".preview-card").forEach((card, index) => {
    const preview = text.preview[index];
    if (!preview) {
      return;
    }
    setTextWithin(card, ".meta-pill", preview[0]);
    setTextWithin(card, "h3", preview[1]);
    setTextWithin(card, "p", preview[2]);
    setTextWithin(card, ".btn", preview[3]);
  });

  document.querySelectorAll(".footer-links a").forEach((link, index) => {
    if (text.footerLinks[index]) {
      link.textContent = text.footerLinks[index];
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

function setMetaContent(name, value) {
  const target = Array.from(document.querySelectorAll("meta[name]"))
    .find((meta) => meta.getAttribute("name") === name);
  if (target) {
    target.setAttribute("content", value);
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

