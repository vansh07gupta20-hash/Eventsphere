
const forgotPasswordForm = document.getElementById("forgotPasswordForm");
const forgotMessage = document.getElementById("forgotMessage");
const resetButton = document.getElementById("resetButton");

if (forgotPasswordForm) {
    forgotPasswordForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("userEmail").value.trim();

        if (!email) {
            showMessage("Please enter your email address.", "error");
            return;
        }

        resetButton.disabled = true;
        resetButton.textContent = "Sending...";

        try {
            const response = await fetch(
                "http://localhost:8080/users/forgot-password",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        userEmail: email
                    })
                }
            );

            const data = await response.json().catch(() => ({}));

            if (response.ok) {
                showMessage(
                    data.message ||
                    "If an account exists for this email, reset instructions will be sent.",
                    "success"
                );

                forgotPasswordForm.reset();
            } else {
                showMessage(
                    data.message || "Unable to process your request. Please try again.",
                    "error"
                );
            }

        } catch (error) {
            console.error("Forgot password error:", error);

            showMessage(
                "Unable to connect to the server. Please try again later.",
                "error"
            );
        } finally {
            resetButton.disabled = false;
            resetButton.innerHTML = 'Send Reset Link <span>→</span>';
        }
    });
}

function showMessage(message, type) {
    if (!forgotMessage) return;

    forgotMessage.textContent = message;
    forgotMessage.className = `message ${type}`;
    forgotMessage.hidden = false;
}
