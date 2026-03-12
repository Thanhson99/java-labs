document.addEventListener("DOMContentLoaded", () => {
    const copyButton = document.getElementById("copyCurlButton");
    const toggleButton = document.getElementById("toggleSnippet");
    const snippet = document.getElementById("curlSnippet");

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
});
