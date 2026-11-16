
"use strict";

const organizerRegisterForm =
    document.getElementById("organizerRegisterForm");

const registerMessage =
    document.getElementById("registerMessage");

const registerButton =
    document.getElementById("registerButton");

organizerRegisterForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    registerMessage.textContent = "";

    const organizerName =
        document.getElementById("organizerName").value.trim();

    const organizerEmail =
        document.getElementById("organizerEmail").value.trim();

    const organizerPhoneNumber =
        document.getElementById("organizerPhoneNumber").value.trim();

    const organizerPassword =
        document.getElementById("organizerPassword").value;

    if (!organizerName || !organizerEmail || !organizerPassword) {
        registerMessage.textContent = "Please fill in all required fields.";
        return;
    }

    if (!/^[6-9][0-9]{9}$/.test(organizerPhoneNumber)) {
        registerMessage.textContent =
            "Enter a valid 10-digit Indian mobile number.";
        return;
    }

    if (organizerPassword.length < 8) {
        registerMessage.textContent =
            "Password must contain at least 8 characters.";
        return;
    }

    const organizerData = {
        organizerName,
        organizerEmail,
        organizerPhoneNumber,
        organizerPassword
    };

    registerButton.disabled = true;
    registerButton.textContent = "Creating account...";

    try {
        const response = await fetch("/organizers", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(organizerData)
        });

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            if (response.status === 409) {
                throw new Error(
                    "This email is already registered. Please sign in."
                );
            }

            if (response.status === 400) {
                throw new Error(
                    "Please check your details and try again."
                );
            }

            throw new Error(
                result.message || result.error ||
                "Registration failed. Please try again."
            );
        }

        registerMessage.textContent =
            result.message ||
            "Registration successful. Please wait for Admin approval.";

        organizerRegisterForm.reset();

        // Allow time for the user to read the success message.
        registerButton.textContent = "Account Created";

        window.setTimeout(() => {
            window.location.href = "organizer-login.html";
        }, 2500);

    } catch (error) {
        registerMessage.textContent =
            error.message || "Unable to connect to the server.";

        registerButton.disabled = false;
        registerButton.textContent = "Create Organizer Account";
    }
});
