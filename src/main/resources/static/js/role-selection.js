document.addEventListener("DOMContentLoaded", () => {
    const roleCards = document.querySelectorAll(".role-card");
    const loginButton = document.getElementById("loginButton");
    const registerButton = document.getElementById("registerButton");
    const roleMessage = document.getElementById("roleMessage");

    let selectedRole = null;

    // Disable actions until a role is selected
    loginButton.disabled = true;
    registerButton.disabled = true;

    roleCards.forEach((card) => {
        card.addEventListener("click", () => {
            selectedRole = card.dataset.role;

            // Highlight selected role
            roleCards.forEach((item) => {
                item.classList.remove("selected");
                item.setAttribute("aria-pressed", "false");
            });

            card.classList.add("selected");
            card.setAttribute("aria-pressed", "true");

            // Enable Login after selecting any role
            loginButton.disabled = false;

            // Admin can log in but cannot register
            if (selectedRole === "admin") {
                registerButton.disabled = true;
                roleMessage.textContent = "Admin login only.";
            } else {
                registerButton.disabled = false;
                roleMessage.textContent = "";
            }
        });
    });

    // Login navigation
    loginButton.addEventListener("click", () => {
        if (!selectedRole) return;

        const loginPages = {
            user: "login.html?role=user",
            organizer: "organizer-login.html",
            admin: "admin-login.html"
        };

        window.location.href = loginPages[selectedRole];
    });

    // Register navigation
    registerButton.addEventListener("click", () => {
        if (!selectedRole || selectedRole === "admin") return;

        const registerPages = {
            user: "register.html?role=user",
            organizer: "organizer-register.html"
        };

        window.location.href = registerPages[selectedRole];
    });
});