document.addEventListener("DOMContentLoaded", () => {
    const copyButton = document.getElementById("copyCurlButton");
    const toggleButton = document.getElementById("toggleSnippet");
    const snippet = document.getElementById("curlSnippet");
    const helloForm = document.getElementById("helloForm");
    const loginForm = document.getElementById("loginForm");
    const loadOverviewButton = document.getElementById("loadOverviewButton");
    const apiOutput = document.getElementById("apiOutput");
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
                    writeOutput("POST /api/auth/token failed", data);
                    return;
                }

                accessToken = data.accessToken || "";
                writeOutput("POST /api/auth/token", data);
            } catch (error) {
                writeOutput("POST /api/auth/token failed", String(error));
            }
        });
    }

    if (loadOverviewButton) {
        loadOverviewButton.addEventListener("click", async () => {
            if (!accessToken) {
                writeOutput("Missing token", "Login first to load protected endpoints.");
                return;
            }

            try {
                const response = await fetch("/api/system/overview", {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });
                const data = await response.json();
                writeOutput("GET /api/system/overview", data);
            } catch (error) {
                writeOutput("GET /api/system/overview failed", String(error));
            }
        });
    }
});
