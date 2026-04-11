function setMessage(node, text, isSuccess) {
    node.textContent = text;
    node.className = "msg " + (isSuccess ? "success" : "error");
}

async function postJson(url, payload) {
    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    const contentType = response.headers.get("content-type") || "";
    let data;

    if (contentType.includes("application/json")) {
        data = await response.json();
    } else {
        data = await response.text();
    }

    if (!response.ok) {
        if (typeof data === "string") {
            throw new Error(data);
        }

        if (data.errors && Array.isArray(data.errors)) {
            throw new Error(data.errors.join(". "));
        }

        throw new Error(data.message || "Request failed.");
    }

    return data;
}

function wireLoginForm() {
    const form = document.getElementById("loginForm");
    if (!form) {
        return;
    }
}

function wireRegisterForm() {
    const form = document.getElementById("registerForm");
    if (!form) {
        return;
    }

    const msg = document.getElementById("registerMessage");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setMessage(msg, "", false);

        const firstname = document.getElementById("firstname").value.trim();
        const lastname = document.getElementById("lastname").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            setMessage(msg, "Passwords do not match.", false);
            return;
        }

        try {
            const result = await postJson("/api/v1/auth/register", { firstname, lastname, email, password });
            setMessage(msg, result.message || "Registration successful. You can now sign in.", true);
            form.reset();
            setTimeout(() => {
                window.location.href = "/login?registered=true";
            }, 900);
        } catch (error) {
            setMessage(msg, error.message || "Unable to register at the moment.", false);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    wireLoginForm();
    wireRegisterForm();
});
