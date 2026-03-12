document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("adminLoginForm");
    const loginButton = document.getElementById("adminLoginButton");
    const loadAllButton = document.getElementById("adminLoadAllButton");
    const tokenState = document.getElementById("adminTokenState");
    const adminOutput = document.getElementById("adminOutput");
    const adminSessionCount = document.getElementById("adminSessionCount");
    const adminPrimaryDb = document.getElementById("adminPrimaryDb");
    const adminAnalyticsDb = document.getElementById("adminAnalyticsDb");
    const adminConsumers = document.getElementById("adminConsumers");
    const adminOutboxPending = document.getElementById("adminOutboxPending");
    const adminOutboxPublished = document.getElementById("adminOutboxPublished");
    const adminOutboxDeadLetter = document.getElementById("adminOutboxDeadLetter");
    const outboxTableBody = document.getElementById("adminOutboxTableBody");

    let accessToken = "";

    const writeOutput = (title, payload) => {
        if (!adminOutput) {
            return;
        }

        const body = typeof payload === "string" ? payload : JSON.stringify(payload, null, 2);
        adminOutput.textContent = `${title}\n\n${body}`;
    };

    const setText = (element, value) => {
        if (element) {
            element.textContent = value;
        }
    };

    const escapeHtml = (value) => String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");

    const storeToken = (token) => {
        accessToken = token;
        try {
            window.sessionStorage.setItem("javaLabsAdminToken", token);
        } catch (error) {
            writeOutput("Token persistence warning", String(error));
        }
    };

    const restoreToken = () => {
        try {
            accessToken = window.sessionStorage.getItem("javaLabsAdminToken") || "";
        } catch (error) {
            accessToken = "";
        }

        setText(
            tokenState,
            accessToken ? "Admin token restored from session storage." : "No admin token loaded yet."
        );
        tokenState?.parentElement?.classList.toggle("token-ready", Boolean(accessToken));
    };

    const requireToken = () => {
        if (accessToken) {
            return true;
        }

        writeOutput("Missing token", "Login as admin first.");
        return false;
    };

    const renderOutboxRows = (rows) => {
        if (!outboxTableBody) {
            return;
        }

        if (!rows || rows.length === 0) {
            outboxTableBody.innerHTML = "<tr><td colspan=\"7\">No outbox rows yet.</td></tr>";
            return;
        }

        outboxTableBody.innerHTML = rows.map((row) => {
            const statusClass = String(row.status || "").toLowerCase().replaceAll("_", "-");
            const replayButton = row.status === "DEAD_LETTER"
                ? `<button class="table-action" type="button" data-replay-id="${row.id}">Replay</button>`
                : "<span class=\"helper-text\">No action</span>";
            return `
                <tr>
                    <td><strong>${escapeHtml(row.id)}</strong></td>
                    <td>${escapeHtml(row.aggregateType)}<br /><code>${escapeHtml(row.aggregateId)}</code></td>
                    <td><span class="status-badge ${escapeHtml(statusClass)}">${escapeHtml(row.status)}</span></td>
                    <td>${escapeHtml(row.attempts)}</td>
                    <td>${escapeHtml(row.availableAt || "n/a")}</td>
                    <td>${escapeHtml(row.lastError || "None")}</td>
                    <td>${replayButton}</td>
                </tr>
            `;
        }).join("");
    };

    const loadOverview = async () => {
        if (!requireToken()) {
            return;
        }

        const response = await fetch("/api/system/overview", {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(JSON.stringify(data));
        }

        setText(adminSessionCount, `${data.auth?.activeRefreshSessions ?? 0} active refresh sessions`);
        setText(
            adminPrimaryDb,
            `${data.primaryDatabase?.runtimeMaximumPoolSize ?? "--"} runtime max | ${data.primaryDatabase?.jdbcUrl ?? "n/a"}`
        );
        setText(
            adminAnalyticsDb,
            `${data.analyticsDatabase?.maximumPoolSize ?? "--"} max | events: ${data.analyticsDatabase?.eventCount ?? 0}`
        );
        setText(
            adminConsumers,
            JSON.stringify(data.messaging?.consumedCounts || {})
        );
        writeOutput("GET /api/system/overview", data);
    };

    const loadOutbox = async () => {
        if (!requireToken()) {
            return;
        }

        const response = await fetch("/api/system/outbox", {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(JSON.stringify(data));
        }

        setText(adminOutboxPending, `${data.summary?.pending ?? 0} pending rows`);
        setText(adminOutboxPublished, `${data.summary?.published ?? 0} published rows`);
        setText(adminOutboxDeadLetter, `${data.summary?.deadLetter ?? 0} dead-letter rows`);
        renderOutboxRows(data.rows);
        writeOutput("GET /api/system/outbox", data);
    };

    const replayOutboxRow = async (outboxId) => {
        if (!requireToken()) {
            return;
        }

        const response = await fetch(`/api/system/outbox/${outboxId}/replay`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(JSON.stringify(data));
        }

        writeOutput(`POST /api/system/outbox/${outboxId}/replay`, data);
        await loadOutbox();
    };

    const loginAsAdmin = async () => {
        const usernameInput = document.getElementById("adminUsername");
        const passwordInput = document.getElementById("adminPassword");
        const payload = {
            username: usernameInput?.value || "admin",
            password: passwordInput?.value || "admin123"
        };

        const response = await fetch("/api/auth/token", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
        const data = await response.json();
        if (!response.ok) {
            throw new Error(JSON.stringify(data));
        }

        storeToken(data.accessToken || "");
        setText(tokenState, accessToken ? "Admin token is ready in session storage." : "No token returned.");
        tokenState?.parentElement?.classList.toggle("token-ready", Boolean(accessToken));
        writeOutput("POST /api/auth/token", data);
    };

    const loadAll = async () => {
        try {
            await loadOverview();
            await loadOutbox();
        } catch (error) {
            writeOutput("Admin load failed", String(error));
        }
    };

    restoreToken();

    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            try {
                await loginAsAdmin();
            } catch (error) {
                writeOutput("Admin login failed", String(error));
            }
        });
    }

    if (loginButton) {
        loginButton.addEventListener("click", async () => {
            try {
                await loginAsAdmin();
            } catch (error) {
                writeOutput("Admin login failed", String(error));
            }
        });
    }

    if (loadAllButton) {
        loadAllButton.addEventListener("click", () => {
            loadAll();
        });
    }

    if (outboxTableBody) {
        outboxTableBody.addEventListener("click", async (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }

            const replayId = target.dataset.replayId;
            if (replayId) {
                try {
                    await replayOutboxRow(replayId);
                } catch (error) {
                    writeOutput(`Replay ${replayId} failed`, String(error));
                }
            }
        });
    }
});
