document.addEventListener("DOMContentLoaded", () => {
    const flashes = document.querySelectorAll(".flash");
    flashes.forEach((flash) => {
        setTimeout(() => {
            flash.style.transition = "opacity 300ms ease";
            flash.style.opacity = "0";
        }, 2800);
    });

    const toggles = document.querySelectorAll("[data-toggle-target]");
    toggles.forEach((btn) => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-toggle-target");
            if (!targetId) {
                return;
            }

            const target = document.getElementById(targetId);
            if (!target) {
                return;
            }

            target.classList.toggle("open");
        });
    });

    const chatPage = document.querySelector(".item-page[data-item-id]");
    if (!chatPage) {
        return;
    }

    const chatModal = chatPage.querySelector("[data-chat-modal]");
    const openChatButton = chatPage.querySelector("[data-chat-open]");
    const closeChatButtons = chatPage.querySelectorAll("[data-chat-close]");
    const messagesContainer = chatPage.querySelector("[data-chat-messages]");
    const chatForm = chatPage.querySelector("[data-chat-form]");
    const chatInput = chatPage.querySelector("[data-chat-input]");
    const sendButton = chatPage.querySelector("[data-chat-send]");
    const itemId = chatPage.getAttribute("data-item-id");
    const chatBaseUrl = `/marketplace/${itemId}/chat`;
    let pollTimer = null;
    let activeThreadId = null;

    const stopPolling = () => {
        if (pollTimer) {
            window.clearInterval(pollTimer);
            pollTimer = null;
        }
    };

    const renderMessages = (messages) => {
        if (!messages.length) {
            messagesContainer.innerHTML = activeThreadId
                ? '<div class="chat-empty-state">No messages yet. Start the conversation.</div>'
                : '<div class="chat-empty-state">No chat thread exists yet. A buyer must message first.</div>';
            return;
        }

        messagesContainer.innerHTML = messages.map((message) => `
            <article class="chat-bubble ${message.mine ? 'mine' : ''}">
                <div class="chat-bubble-header">
                    <strong>${message.mine ? 'You' : message.senderName}</strong>
                    <span>${message.createdAt}</span>
                </div>
                <p class="chat-bubble-text"></p>
            </article>
        `).join("");

        messagesContainer.querySelectorAll(".chat-bubble-text").forEach((node, index) => {
            node.textContent = messages[index].content;
        });
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    };

    const loadChat = async () => {
        const response = await fetch(chatBaseUrl, {
            headers: {
                Accept: "application/json"
            }
        });

        if (!response.ok) {
            messagesContainer.innerHTML = '<div class="chat-empty-state">Unable to load chat right now.</div>';
            return;
        }

        const data = await response.json();
        activeThreadId = data.threadId ?? null;
        renderMessages(data.messages || []);
    };

    const openChat = async () => {
        if (!chatModal) {
            return;
        }

        chatModal.hidden = false;
        document.body.style.overflow = "hidden";

        await loadChat();

        stopPolling();
        pollTimer = window.setInterval(loadChat, 4000);
        chatInput?.focus();
    };

    const closeChat = () => {
        if (!chatModal) {
            return;
        }

        chatModal.hidden = true;
        document.body.style.overflow = "";
        stopPolling();
    };

    openChatButton?.addEventListener("click", openChat);
    closeChatButtons.forEach((button) => button.addEventListener("click", closeChat));

    chatInput?.addEventListener("keydown", (event) => {
        if (event.key === "Enter" && !event.shiftKey) {
            return;
        }
    });

    if (window.__CHAT_OPEN__) {
        openChat();
    }

    window.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !chatModal.hidden) {
            closeChat();
        }
    });
});
