
const resetPasswordForm = document.getElementById("resetPasswordForm");
const resetMessage = document.getElementById("resetMessage");
const resetPasswordButton = document.getElementById("resetPasswordButton");

// Read token from email link URL
const urlParams = new URLSearchParams(window.location.search);
const resetToken = urlParams.get("token");

function showResetMessage(message, type) {
    resetMessage.textContent = message;
    resetMessage.className = `message ${type}`;
    resetMessage.hidden = false;
}

if (!resetToken) {
    showResetMessage(
        "Invalid reset link. Please request a new password reset link.",
        "error"
    );

    resetPasswordButton.disabled = true;
}

resetPasswordForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (newPassword.length < 8 || newPassword.length > 72) {
        showResetMessage(
            "Password must be between 8 and 72 characters.",
            "error"
        );
        return;
    }

    if (newPassword !== confirmPassword) {
        showResetMessage("Passwords do not match.", "error");
        return;
    }

    resetPasswordButton.disabled = true;
    resetPasswordButton.textContent = "Resetting...";

    try {
        const response = await fetch(
            "http://localhost:8080/users/reset-password",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    resetToken: resetToken,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                })
            }
        );

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            showResetMessage(
                data.message || "Password reset successfully!",
                "success"
            );

            resetPasswordForm.reset();

            // Remove token from address bar after successful reset
            window.history.replaceState(
                {},
                document.title,
                window.location.pathname
            );

            setTimeout(() => {
                window.location.href = "login.html";
            }, 2500);

        } else {
            showResetMessage(
                data.message ||
                "Reset link is invalid or expired. Please request a new one.",
                "error"
            );
        }

    } catch (error) {
        console.error("Reset password error:", error);

        showResetMessage(
            "Unable to connect to the server. Please try again.",
            "error"
        );

    } finally {
        resetPasswordButton.disabled = false;
        resetPasswordButton.textContent = "Reset Password";
    }
});
