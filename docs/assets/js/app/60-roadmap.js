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
                ${branch.nodes.map((node) => renderRoadmapStudyLink(node, "tree-chip")).join("")}
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
                  ${phase.topics.map((topic) => `<li>${renderRoadmapStudyLink(topic, "phase-topic-link")}</li>`).join("")}
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
                ${step.focus.map((item) => renderRoadmapStudyLink(item, "tree-chip")).join("")}
              </div>
            </div>
          </article>
        `
      )
      .join("");
  }

  mountRoadmapToc();
  mountSectionLinkButtons(document.querySelector("main"));
}

function renderRoadmapStudyLink(label, className) {
  return `
    <a class="${escapeHtml(className)}" href="${escapeHtml(getRoadmapStudyUrl(label))}">
      ${escapeHtml(label)}
    </a>
  `;
}

function getRoadmapStudyUrl(keyword) {
  const params = new URLSearchParams();
  params.set("search", keyword);
  params.set("lang", currentLanguage);
  const activeTheme = document.documentElement.dataset.theme || currentTheme;
  if (activeTheme) {
    params.set("theme", activeTheme);
  }
  return `interview.html?${params.toString()}`;
}

