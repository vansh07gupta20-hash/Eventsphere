const loginForm = document.getElementById("loginForm");
const messageBox = document.getElementById("message");
const loginButton = document.getElementById("loginButton");

// Spring Boot backend URL
const API_BASE_URL = "http://localhost:8080";

// Show success or error messages
function showMessage(message, type = "error") {
    if (!messageBox) {
        alert(message);
        return;
    }

    messageBox.textContent = message;
    messageBox.className = `message ${type}`;
    messageBox.hidden = false;
}

// Handle user login
if (!loginForm) {
    console.error("Login form with id 'loginForm' was not found.");
} else {
    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const emailInput = document.getElementById("email");
        const passwordInput = document.getElementById("password");

        if (!emailInput || !passwordInput) {
            showMessage("Email or password input field is missing.");
            return;
        }

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {
            showMessage("Please enter your email and password.");
            return;
        }

        if (messageBox) {
            messageBox.hidden = true;
        }

        if (loginButton) {
            loginButton.disabled = true;
            loginButton.textContent = "Signing in...";
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json, text/plain"
                },
                body: JSON.stringify({
                    userEmail: email,
                    userPassword: password
                })
            });

            const responseText = await response.text();

            let data = {};

            try {
                data = responseText ? JSON.parse(responseText) : {};
            } catch {
                data = responseText;
            }

            // Handle login failure
            if (!response.ok) {
                let errorMessage =
                    "Login failed. Please check your credentials.";

                if (typeof data === "string" && data.trim()) {
                    errorMessage = data;
                } else if (data && data.message) {
                    errorMessage = data.message;
                }

                showMessage(errorMessage);
                console.error(
                    "Login failed:",
                    response.status,
                    data
                );

                return;
            }

            // Verify authentication token
            if (
                !data ||
                typeof data !== "object" ||
                !data.token
            ) {
                showMessage(
                    "The server response did not contain an authentication token."
                );

                console.error("Unexpected login response:", data);
                return;
            }

            // Store user session details
            sessionStorage.setItem("authToken", data.token);

            if (data.userId != null) {
                sessionStorage.setItem(
                    "userId",
                    String(data.userId)
                );
            }

            if (data.userName != null) {
                sessionStorage.setItem(
                    "userName",
                    data.userName
                );
            }

            if (data.userEmail != null) {
                sessionStorage.setItem(
                    "userEmail",
                    data.userEmail
                );
            }

            showMessage(
                "Login successful! Redirecting...",
                "success"
            );

            // Redirect to dashboard on Spring Boot port 8080
            window.location.href =
                `${API_BASE_URL}/pages/user-dashboard.html`;

        } catch (error) {
            console.error("Login request failed:", error);

            showMessage(
                "Cannot connect to the server. Make sure Spring Boot is running on port 8080."
            );

        } finally {
            if (loginButton) {
                loginButton.disabled = false;
                loginButton.textContent = "Sign In";
            }
        }
    });
}

// Handle forgot-password link
const forgotPasswordLink =
    document.getElementById("forgotPasswordLink");

if (forgotPasswordLink) {
    forgotPasswordLink.addEventListener("click", function (event) {
        event.preventDefault();

        window.location.href = "forgot-password.html";
    });
}
