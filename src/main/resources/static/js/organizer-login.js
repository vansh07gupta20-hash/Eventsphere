"use strict";

const form = document.getElementById("organizerLoginForm");
const messageBox = document.getElementById("message");
const loginButton = document.getElementById("loginButton");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    messageBox.hidden = true;
    loginButton.disabled = true;
    loginButton.textContent = "Signing in...";

    try {
        const response = await fetch("/organizer/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                organizerEmail: document.getElementById("email").value.trim(),
                organizerPassword: document.getElementById("password").value
            })
        });

        const text = await response.text();
        let data = {};
        try {
            data = text ? JSON.parse(text) : {};
        } catch {
            data = {};
        }

        if (!response.ok) {
            throw new Error(typeof data === "string"
                ? data
                : data.message || "Organizer login failed.");
        }

        if (!data.token) {
            throw new Error("The server did not return an authentication token.");
        }

        sessionStorage.removeItem("authToken");
        sessionStorage.removeItem("adminAuthToken");

        sessionStorage.setItem("organizerAuthToken", data.token);
        sessionStorage.setItem("organizerId", data.organizerId);
        sessionStorage.setItem("organizerName", data.organizerName);
        sessionStorage.setItem("organizerEmail", data.organizerEmail);
        sessionStorage.setItem("activeRole", "organizer");

        window.location.href = "/pages/organizer-dashboard.html";
    } catch (error) {
        messageBox.textContent = error.message || "Unable to connect to the server.";
        messageBox.className = "message error";
        messageBox.hidden = false;
    } finally {
        loginButton.disabled = false;
        loginButton.textContent = "Sign In";
    }
});