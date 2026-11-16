"use strict";

const form = document.getElementById("adminLoginForm");
const messageBox = document.getElementById("message");
const loginButton = document.getElementById("loginButton");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    messageBox.hidden = true;
    loginButton.disabled = true;
    loginButton.textContent = "Signing in...";

    try {
        const response = await fetch("/admin/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                adminEmail: document.getElementById("email").value.trim(),
                adminPassword: document.getElementById("password").value
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
                : data.message || "Admin login failed.");
        }

        if (!data.token) {
            throw new Error("The server did not return an authentication token.");
        }

        sessionStorage.removeItem("authToken");
        sessionStorage.removeItem("organizerAuthToken");

        sessionStorage.setItem("adminAuthToken", data.token);
        sessionStorage.setItem("adminId", data.adminId);
        sessionStorage.setItem("adminName", data.adminName);
        sessionStorage.setItem("adminEmail", data.adminEmail);
        sessionStorage.setItem("activeRole", "admin");

        window.location.href = "admin-dashboard.html";
    } catch (error) {
        messageBox.textContent = error.message || "Unable to connect to the server.";
        messageBox.className = "message error";
        messageBox.hidden = false;
    } finally {
        loginButton.disabled = false;
        loginButton.textContent = "Sign In";
    }
});