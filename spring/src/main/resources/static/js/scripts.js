document.addEventListener("DOMContentLoaded", () => {
    const copyButton = document.getElementById("copyCurlButton");
    const toggleButton = document.getElementById("toggleSnippet");
    const snippet = document.getElementById("curlSnippet");
    const helloForm = document.getElementById("helloForm");
    const loginForm = document.getElementById("loginForm");
    const loadOverviewButton = document.getElementById("loadOverviewButton");
    const loadOutboxButton = document.getElementById("loadOutboxButton");
    const apiOutput = document.getElementById("apiOutput");
    const tokenState = document.getElementById("tokenState");
    const dashboardAppName = document.getElementById("dashboardAppName");
    const dashboardArchitecture = document.getElementById("dashboardArchitecture");
    const dashboardPrimaryDb = document.getElementById("dashboardPrimaryDb");
    const dashboardPrimaryPool = document.getElementById("dashboardPrimaryPool");
    const dashboardAnalyticsDb = document.getElementById("dashboardAnalyticsDb");
    const dashboardAnalyticsMeta = document.getElementById("dashboardAnalyticsMeta");
    const dashboardRateLimit = document.getElementById("dashboardRateLimit");
    const dashboardRateWindow = document.getElementById("dashboardRateWindow");
    const dashboardMessaging = document.getElementById("dashboardMessaging");
    const dashboardTransportState = document.getElementById("dashboardTransportState");
    const dashboardHealthStatus = document.getElementById("dashboardHealthStatus");
    const dashboardHealthHint = document.getElementById("dashboardHealthHint");
    const dashboardAuthMetrics = document.getElementById("dashboardAuthMetrics");
    const dashboardAuthHint = document.getElementById("dashboardAuthHint");
    const dashboardRegistrationMetrics = document.getElementById("dashboardRegistrationMetrics");
    const dashboardRegistrationHint = document.getElementById("dashboardRegistrationHint");
    const dashboardOutbox = document.getElementById("dashboardOutbox");
    const dashboardOutboxHint = document.getElementById("dashboardOutboxHint");
    const dashboardAdminState = document.getElementById("dashboardAdminState");
    const dashboardAdminHint = document.getElementById("dashboardAdminHint");
    const dashboardLastRefresh = document.getElementById("dashboardLastRefresh");
    const outboxPendingValue = document.getElementById("outboxPendingValue");
    const outboxPublishedValue = document.getElementById("outboxPublishedValue");
    const outboxDeadLetterValue = document.getElementById("outboxDeadLetterValue");
    const outboxTableBody = document.getElementById("outboxTableBody");
    let accessToken = "";

    if (copyButton && snippet) {
        copyButton.addEventListener("click", async () => {
            const text = snippet.innerText.trim();
            try {
                await navigator.clipboard.writeText(text);
                copyButton.textContent = "Copied";
                copyButton.classList.add("copied");
                window.setTimeout(() => {
                    copyButton.textContent = "Copy Login Curl";
                    copyButton.classList.remove("copied");
                }, 1200);
            } catch (error) {
                copyButton.textContent = "Clipboard Blocked";
            }
        });
    }

    if (toggleButton && snippet) {
        toggleButton.addEventListener("click", () => {
            const hidden = snippet.hasAttribute("hidden");
            if (hidden) {
                snippet.removeAttribute("hidden");
                toggleButton.textContent = "Hide Snippet";
            } else {
                snippet.setAttribute("hidden", "hidden");
                toggleButton.textContent = "Show Snippet";
            }
        });
    }

    const writeOutput = (title, payload) => {
        if (!apiOutput) {
            return;
        }

        const body = typeof payload === "string"
            ? payload
            : JSON.stringify(payload, null, 2);
        apiOutput.textContent = `${title}\n\n${body}`;
    };

    const updateTokenState = (message, isReady) => {
        if (!tokenState) {
            return;
        }

        tokenState.textContent = message;
        tokenState.parentElement?.classList.toggle("token-ready", Boolean(isReady));
    };

    updateTokenState("No access token loaded yet.", false);

    const setText = (element, value) => {
        if (element) {
            element.textContent = value;
        }
    };

    const markRefreshTime = () => {
        const now = new Date();
        setText(dashboardLastRefresh, `Last refresh: ${now.toLocaleTimeString()}`);
    };

    const escapeHtml = (value) => String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");

    const requireToken = () => {
        if (accessToken) {
            return true;
        }

        writeOutput("Missing token", "Login first to load protected endpoints.");
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

    const renderOutboxSummary = (summary) => {
        setText(outboxPendingValue, `${summary?.pending ?? 0} rows ready or waiting`);
        setText(outboxPublishedValue, `${summary?.published ?? 0} rows already dispatched`);
        setText(outboxDeadLetterValue, `${summary?.deadLetter ?? 0} rows need replay or inspection`);
    };

    const loadOutbox = async () => {
        if (!requireToken()) {
            return;
        }

        try {
            const response = await fetch("/api/system/outbox", {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            const data = await response.json();
            if (!response.ok) {
                throw new Error(JSON.stringify(data));
            }

            renderOutboxSummary(data.summary);
            renderOutboxRows(data.rows);
            setText(dashboardAdminState, "Admin outbox loaded");
            setText(
                dashboardAdminHint,
                `Rows: ${(data.rows || []).length} | dead-letter: ${data.summary?.deadLetter ?? 0}`
            );
            writeOutput("GET /api/system/outbox", data);
        } catch (error) {
            writeOutput("GET /api/system/outbox failed", String(error));
        }
    };

    const replayOutboxRow = async (outboxId) => {
        if (!requireToken()) {
            return;
        }

        try {
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
            await loadDashboard();
        } catch (error) {
            writeOutput(`POST /api/system/outbox/${outboxId}/replay failed`, String(error));
        }
    };

    const loadDashboard = async () => {
        try {
            const response = await fetch("/api/system/dashboard");
            const data = await response.json();

            if (!response.ok) {
                throw new Error(JSON.stringify(data));
            }

            setText(dashboardAppName, data.application?.name || "spring");
            setText(
                dashboardArchitecture,
                `${data.application?.architecture || "No architecture summary."} | ${data.application?.securityMode || "No security summary."}`
            );
            setText(
                dashboardPrimaryDb,
                `${data.primaryDatabase?.engine || "Unknown"} primary database`
            );
            setText(
                dashboardPrimaryPool,
                `Pool size: ${data.primaryDatabase?.maximumPoolSize ?? "--"}`
            );
            setText(
                dashboardAnalyticsDb,
                `${data.analyticsDatabase?.engine || "Unknown"} analytics database`
            );
            setText(
                dashboardAnalyticsMeta,
                `Pool size: ${data.analyticsDatabase?.maximumPoolSize ?? "--"} | events: ${data.analyticsDatabase?.eventCount ?? "--"}`
            );
            setText(
                dashboardRateLimit,
                `${data.registrationRateLimit?.maxRequests ?? "--"} requests`
            );
            setText(
                dashboardRateWindow,
                `Window: ${data.registrationRateLimit?.windowMillis ?? "--"} ms`
            );

            const activeTransports = data.messaging?.activeTransports || [];
            setText(
                dashboardMessaging,
                activeTransports.length > 0 ? activeTransports.join(" + ") : "Messaging disabled"
            );
            setText(
                dashboardTransportState,
                `Kafka: ${Boolean(data.messaging?.kafkaEnabled)} | RabbitMQ: ${Boolean(data.messaging?.rabbitmqEnabled)}`
            );

            const auth = data.observability?.businessMetrics?.auth || {};
            setText(
                dashboardAuthMetrics,
                `${auth.tokensIssued ?? 0} tokens | ${auth.refreshSuccess ?? 0} refresh`
            );
            setText(
                dashboardAuthHint,
                `logout: ${auth.logoutSuccess ?? 0} | logout-all: ${auth.logoutAllSuccess ?? 0}`
            );

            const registration = data.observability?.businessMetrics?.registration || {};
            setText(
                dashboardRegistrationMetrics,
                `${registration.success ?? 0} success | ${registration.rateLimited ?? 0} rate-limited`
            );
            setText(
                dashboardRegistrationHint,
                `failure: ${registration.failure ?? 0} | avg: ${Number(registration.averageDurationMs ?? 0).toFixed(2)} ms`
            );

            const outbox = data.outbox || {};
            setText(
                dashboardOutbox,
                `${outbox.pending ?? 0} pending | ${outbox.published ?? 0} published`
            );
            setText(
                dashboardOutboxHint,
                `dead-letter: ${outbox.deadLetter ?? 0} | ${data.messaging?.deadLetterPaths?.join(" | ") || "No DLQ paths"}`
            );
            markRefreshTime();
        } catch (error) {
            setText(dashboardAppName, "Dashboard unavailable");
            setText(dashboardArchitecture, String(error));
        }
    };

    const loadHealth = async () => {
        try {
            const response = await fetch("/actuator/health");
            const data = await response.json();

            setText(dashboardHealthStatus, data.status || "UNKNOWN");
            const details = data.components
                ? Object.keys(data.components).join(", ")
                : "No component details";
            setText(dashboardHealthHint, `Components: ${details}`);
        } catch (error) {
            setText(dashboardHealthStatus, "Unavailable");
            setText(dashboardHealthHint, String(error));
        }
    };

    loadDashboard();
    loadHealth();
    window.setInterval(() => {
        loadDashboard();
        loadHealth();
    }, 10000);

    if (helloForm) {
        helloForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(helloForm);
            const name = formData.get("helloName") || "Spring";

            try {
                const response = await fetch(`/hello?name=${encodeURIComponent(name)}`);
                const text = await response.text();
                writeOutput("GET /hello", text);
            } catch (error) {
                writeOutput("GET /hello failed", String(error));
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(loginForm);
            const payload = {
                username: formData.get("username"),
                password: formData.get("password")
            };

            try {
                const response = await fetch("/api/auth/token", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(payload)
                });
                const data = await response.json();
                if (!response.ok) {
                    updateTokenState("Login failed. No valid access token is stored.", false);
                    writeOutput("POST /api/auth/token failed", data);
                    return;
                }

                accessToken = data.accessToken || "";
                updateTokenState(
                    accessToken
                        ? "Access token loaded in memory. Protected requests are ready."
                        : "Login finished but no access token was returned.",
                    Boolean(accessToken)
                );
                setText(dashboardAdminState, "Token loaded");
                setText(dashboardAdminHint, "Use the overview button to load admin-only runtime detail.");
                writeOutput("POST /api/auth/token", data);
            } catch (error) {
                updateTokenState("Login failed. No valid access token is stored.", false);
                writeOutput("POST /api/auth/token failed", String(error));
            }
        });
    }

    if (loadOverviewButton) {
        loadOverviewButton.addEventListener("click", async () => {
            if (!requireToken()) {
                return;
            }

            try {
                const response = await fetch("/api/system/overview", {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });
                const data = await response.json();
                if (response.ok) {
                    setText(dashboardAdminState, "Admin overview loaded");
                    setText(
                        dashboardAdminHint,
                        `Consumers tracked: ${JSON.stringify(data.messaging?.consumedCounts || {})}`
                    );
                }
                writeOutput("GET /api/system/overview", data);
            } catch (error) {
                writeOutput("GET /api/system/overview failed", String(error));
            }
        });
    }

    if (loadOutboxButton) {
        loadOutboxButton.addEventListener("click", () => {
            loadOutbox();
        });
    }

    if (outboxTableBody) {
        outboxTableBody.addEventListener("click", (event) => {
            const target = event.target;
            if (!(target instanceof HTMLElement)) {
                return;
            }

            const replayId = target.dataset.replayId;
            if (replayId) {
                replayOutboxRow(replayId);
            }
        });
    }
});
