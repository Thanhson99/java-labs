document.addEventListener("DOMContentLoaded", () => {
    const storageKey = "javaLabsRoadmapProgress";
    const roadmapList = document.getElementById("roadmapList");
    const roadmapPercent = document.getElementById("roadmapPercent");
    const roadmapSummary = document.getElementById("roadmapSummary");
    const roadmapProgressBar = document.getElementById("roadmapProgressBar");
    const resetButton = document.getElementById("resetRoadmapButton");

    if (!roadmapList) {
        return;
    }

    const items = Array.from(roadmapList.querySelectorAll(".roadmap-item"));

    const readProgress = () => {
        try {
            return JSON.parse(window.localStorage.getItem(storageKey) || "{}");
        } catch (error) {
            return {};
        }
    };

    const writeProgress = (progress) => {
        window.localStorage.setItem(storageKey, JSON.stringify(progress));
    };

    const render = () => {
        const progress = readProgress();
        let completed = 0;

        items.forEach((item) => {
            const key = item.dataset.key;
            const checked = Boolean(key && progress[key]);
            const checkbox = item.querySelector("input[type='checkbox']");
            if (checkbox) {
                checkbox.checked = checked;
            }
            item.classList.toggle("completed", checked);
            if (checked) {
                completed += 1;
            }
        });

        const percent = items.length === 0 ? 0 : Math.round((completed / items.length) * 100);
        if (roadmapPercent) {
            roadmapPercent.textContent = `${percent}%`;
        }
        if (roadmapSummary) {
            roadmapSummary.textContent = `${completed} / ${items.length} topics complete`;
        }
        if (roadmapProgressBar) {
            roadmapProgressBar.style.width = `${percent}%`;
        }
    };

    items.forEach((item) => {
        const checkbox = item.querySelector("input[type='checkbox']");
        checkbox?.addEventListener("change", () => {
            const progress = readProgress();
            const key = item.dataset.key;
            if (!key) {
                return;
            }
            progress[key] = checkbox.checked;
            writeProgress(progress);
            render();
        });
    });

    resetButton?.addEventListener("click", () => {
        window.localStorage.removeItem(storageKey);
        render();
    });

    render();
});
