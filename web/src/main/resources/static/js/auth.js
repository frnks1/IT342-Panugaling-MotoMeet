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
        throw new Error(typeof data === "string" ? data : "Request failed.");
    }

    return data;
}

function wireLoginForm() {
    const form = document.getElementById("loginForm");
    if (!form) {
        return;
    }

    const msg = document.getElementById("loginMessage");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        setMessage(msg, "", false);

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        try {
            await postJson("/api/v1/auth/login", { email, password });
            setMessage(msg, "Login successful. Welcome back to MotoMeet.", true);
        } catch (error) {
            setMessage(msg, error.message || "Invalid credentials.", false);
        }
    });
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
            await postJson("/api/v1/auth/register", { firstname, lastname, email, password });
            setMessage(msg, "Registration successful. You can now sign in.", true);
            form.reset();
        } catch (error) {
            setMessage(msg, error.message || "Unable to register at the moment.", false);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    wireLoginForm();
    wireRegisterForm();
});
