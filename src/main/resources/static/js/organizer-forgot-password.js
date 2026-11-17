
const forgotPasswordForm = document.getElementById("forgotPasswordForm");
const forgotMessage = document.getElementById("forgotMessage");
const forgotButton = document.getElementById("forgotButton");

function showMessage(message, type) {
    forgotMessage.textContent = message;
    forgotMessage.className = `message ${type}`;
    forgotMessage.hidden = false;
}

forgotPasswordForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const organizerEmail =
        document.getElementById("organizerEmail").value.trim();

    forgotButton.disabled = true;
    forgotButton.textContent = "Sending...";

    try {
        const response = await fetch(
            "http://localhost:8080/organizers/forgot-password",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    organizerEmail: organizerEmail
                })
            }
        );

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            showMessage(
                data.message || "If an account exists with this email, a reset link will be sent.",
                "success"
            );
            forgotPasswordForm.reset();
        } else {
            showMessage(
                data.message || "Unable to process your request.",
                "error"
            );
        }

    } catch (error) {
        console.error("Forgot password error:", error);
        showMessage("Unable to connect to the server.", "error");
    } finally {
        forgotButton.disabled = false;
        forgotButton.textContent = "Send Reset Link";
    }
});
