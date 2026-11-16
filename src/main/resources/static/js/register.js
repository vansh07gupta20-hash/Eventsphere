"use strict";

const registerForm = document.getElementById("registerForm");
const registerButton = document.getElementById("registerButton");
const registerMessage = document.getElementById("registerMessage");

// Spring Boot backend URL
const API_BASE_URL = "http://localhost:8080";

function showRegisterMessage(message, type = "error") {
    if (!registerMessage) {
        alert(message);
        return;
    }

    registerMessage.textContent = message;
    registerMessage.className = `message ${type}`;
    registerMessage.hidden = false;
}

if (!registerForm) {
    console.error("Registration form with id 'registerForm' was not found.");
} else {
    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        if (!registerForm.reportValidity()) {
            return;
        }

        const userNameInput = document.getElementById("userName");
        const userEmailInput = document.getElementById("userEmail");
        const userPhoneInput = document.getElementById("userPhoneNumber");
        const userPasswordInput = document.getElementById("userPassword");
        const confirmPasswordInput = document.getElementById("confirmPassword");

        if (
            !userNameInput ||
            !userEmailInput ||
            !userPhoneInput ||
            !userPasswordInput ||
            !confirmPasswordInput
        ) {
            showRegisterMessage("A required registration field is missing.");
            return;
        }

        const userName = userNameInput.value.trim();
        const userEmail = userEmailInput.value.trim();
        const userPhoneNumber = userPhoneInput.value.trim();
        const userPassword = userPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (!userName) {
            showRegisterMessage("Please enter your full name.");
            return;
        }

        if (!userEmail) {
            showRegisterMessage("Please enter your email address.");
            return;
        }

        if (userPassword.length < 8) {
            showRegisterMessage(
                "Password must contain at least 8 characters."
            );
            return;
        }

        if (userPassword !== confirmPassword) {
            showRegisterMessage("Passwords do not match.");
            return;
        }

        const mobilePattern = /^[6-9][0-9]{9}$/;

        if (!mobilePattern.test(userPhoneNumber)) {
            showRegisterMessage(
                "Enter a valid 10-digit Indian mobile number."
            );
            return;
        }

        if (registerButton) {
            registerButton.disabled = true;
            registerButton.textContent = "Creating Account...";
        }

        if (registerMessage) {
            registerMessage.hidden = true;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/users`, {
method: "POST",
    headers: {
    "Content-Type": "application/json",
        "Accept": "application/json, text/plain"
},
body: JSON.stringify({
    userName: userName,
    userEmail: userEmail,
    userPassword: userPassword,
    userPhoneNumber: userPhoneNumber
})
});

const responseText = await response.text();

let data = responseText;

try {
    data = responseText ? JSON.parse(responseText) : {};
} catch {
    // Keep plain-text error responses readable.
}

if (response.ok) {
    showRegisterMessage(
        "Account created successfully! Redirecting to login...",
        "success"
    );

    registerForm.reset();

    setTimeout(() => {
        window.location.href = "http://localhost:8080/pages/login.html?role=user";
    }, 1200);

    return;
}

let message =
    `Could not create account (HTTP ${response.status}).`;

if (response.status === 409) {
    message =
        "This email may already be registered. Please try logging in.";
} else if (typeof data === "string" && data.trim()) {
    message = data;
} else if (data && typeof data.message === "string") {
    message = data.message;
} else if (data && typeof data.detail === "string") {
    message = data.detail;
}

showRegisterMessage(message);
console.error(
    "Registration failed:",
    response.status,
    data
);

} catch (error) {
    console.error("Registration request failed:", error);

    showRegisterMessage(
        "Unable to connect to Spring Boot. Check that the server is running on port 8080."
    );

} finally {
    if (registerButton) {
        registerButton.disabled = false;
        registerButton.textContent = "Create Account";
    }
}
});
}