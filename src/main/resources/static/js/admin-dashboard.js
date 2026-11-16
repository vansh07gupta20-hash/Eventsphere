"use strict";

// ---------- CONFIGURATION ----------

const API_BASE = "";

const token = sessionStorage.getItem("adminAuthToken");
const adminId = sessionStorage.getItem("adminId");

const messageBox = document.getElementById("message");

const pageTitles = {
    overview: "Dashboard Overview",
    users: "User Management",
    organizers: "Organizer Management",
    events: "Event Management"
};


// ---------- AUTHENTICATION ----------

if (!token || !adminId) {
    window.location.href = "admin-login.html";
    throw new Error("Admin authentication required.");
}

const adminNameElement = document.getElementById("adminName");
const adminEmailElement = document.getElementById("adminEmail");

if (adminNameElement) {
    adminNameElement.textContent =
        sessionStorage.getItem("adminName") || "Admin";
}

if (adminEmailElement) {
    adminEmailElement.textContent =
        sessionStorage.getItem("adminEmail") || "Administrator";
}


// ---------- LOGOUT ----------

const logoutButton = document.getElementById("logoutButton");

if (logoutButton) {
    logoutButton.addEventListener("click", () => {

        sessionStorage.removeItem("adminAuthToken");
        sessionStorage.removeItem("adminId");
        sessionStorage.removeItem("adminName");
        sessionStorage.removeItem("adminEmail");
        sessionStorage.removeItem("activeRole");

        window.location.href = "admin-login.html";
    });
}


// ---------- COMMON API FUNCTION ----------

async function apiRequest(endpoint, options = {}) {

    const headers = {
        Authorization: `Bearer ${token}`,

        ...(options.body !== undefined
            ? { "Content-Type": "application/json" }
            : {}),

        ...(options.headers || {})
    };

    const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
    });

    const responseText = await response.text();

    let data = null;

    if (responseText) {
        try {
            data = JSON.parse(responseText);
        } catch {
            data = responseText;
        }
    }

    if (response.status === 401) {
        throw new Error(
            data?.message || "Your session has expired. Please log in again."
        );
    }

    if (response.status === 403) {
        throw new Error(
            data?.message || "You are not authorized to perform this action."
        );
    }

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : data?.message || `Request failed: ${response.status}`
        );
    }

    return data;
}


// ---------- MESSAGE ----------

function showMessage(message, type = "success") {

    if (!messageBox) return;

    messageBox.textContent = message;
    messageBox.className = `message ${type}`;

    window.clearTimeout(showMessage.timeout);

    showMessage.timeout = window.setTimeout(() => {
        messageBox.className = "message hidden";
    }, 3500);
}


// ---------- HTML ESCAPING ----------

function escapeHTML(value) {

    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}


// ---------- STATUS CLASS ----------

function getStatusClass(status) {

    const normalized = String(status || "").trim().toUpperCase();

    if (
        normalized === "ACTIVE" ||
        normalized === "REGISTERED" ||
        normalized === "SCHEDULED"
    ) {
        return "active";
    }

    if (
        normalized === "BLOCKED" ||
        normalized === "INACTIVE" ||
        normalized === "CANCELLED"
    ) {
        return "blocked";
    }

    return "";
}


// ---------- TABLE ERROR ----------

function showTableError(elementId, colspan, error) {

    const table = document.getElementById(elementId);

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="${colspan}">
                ${escapeHTML(error.message)}
            </td>
        </tr>
    `;
}


// ---------- NAVIGATION ----------

function navigateToSection(sectionName) {

    if (!pageTitles[sectionName]) return;

    const targetSection = document.getElementById(sectionName);

    if (!targetSection) return;

    // Remove active class from all navigation buttons
    document.querySelectorAll(".nav-link").forEach(item => {
        item.classList.remove("active");
    });

    // Remove active class from all content sections
    document.querySelectorAll(".content-section").forEach(section => {
        section.classList.remove("active");
    });

    // Activate the corresponding sidebar navigation button
    const activeNavButton = document.querySelector(
        `.nav-link[data-section="${sectionName}"]`
    );

    if (activeNavButton) {
        activeNavButton.classList.add("active");
    }

    // Activate the selected section
    targetSection.classList.add("active");

    // Update page title
    const pageTitle = document.getElementById("pageTitle");

    if (pageTitle) {
        pageTitle.textContent = pageTitles[sectionName];
    }

    // Load the relevant data
    if (sectionName === "overview") loadOverview();
    if (sectionName === "users") loadUsers();
    if (sectionName === "organizers") loadOrganizers();
    if (sectionName === "events") loadEvents();
}


// Sidebar navigation
document.querySelectorAll(".nav-link").forEach(button => {

    button.addEventListener("click", () => {

        const sectionName = button.dataset.section;

        navigateToSection(sectionName);
    });

});


// Welcome banner quick-action buttons
document.querySelectorAll("[data-section-link]").forEach(button => {

    button.addEventListener("click", () => {

        const sectionName = button.dataset.sectionLink;

        navigateToSection(sectionName);
    });

});

// ==================================================
// DASHBOARD OVERVIEW
// ==================================================

async function loadOverview() {

    try {

        const [users, organizers, events] = await Promise.all([
            apiRequest("/users"),
            apiRequest("/organizers"),
            apiRequest("/events")
        ]);

        const totalUsers = document.getElementById("totalUsers");
        const totalOrganizers = document.getElementById("totalOrganizers");
        const totalEvents = document.getElementById("totalEvents");

        if (totalUsers) {
            totalUsers.textContent =
                Array.isArray(users) ? users.length : "—";
        }

        if (totalOrganizers) {
            totalOrganizers.textContent =
                Array.isArray(organizers) ? organizers.length : "—";
        }

        if (totalEvents) {
            totalEvents.textContent =
                Array.isArray(events) ? events.length : "—";
        }

    } catch (error) {

        showMessage(
            "Could not load dashboard statistics: " + error.message,
            "error"
        );
    }
}


// ==================================================
// USERS
// ==================================================

async function loadUsers() {

    const table = document.getElementById("usersTable");

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="6">Loading users...</td>
        </tr>
    `;

    try {

        const users = await apiRequest("/users");

        if (!Array.isArray(users) || users.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="6">No users found.</td>
                </tr>
            `;

            return;
        }

        table.innerHTML = users.map(user => {

            const status = user.userAccountStatus || "UNKNOWN";

            const isActive =
                String(status).toUpperCase() === "ACTIVE";

            return `
                <tr>
                    <td>${escapeHTML(user.userId)}</td>
                    <td>${escapeHTML(user.userName)}</td>
                    <td>${escapeHTML(user.userEmail)}</td>
                    <td>${escapeHTML(user.userPhoneNumber)}</td>

                    <td>
                        <span class="status ${getStatusClass(status)}">
                            ${escapeHTML(status)}
                        </span>
                    </td>

                    <td>
                        <button
                            class="action-button ${isActive ? "block" : "unblock"}"
                            data-user-id="${escapeHTML(user.userId)}"
                            data-next-status="${isActive ? "BLOCKED" : "ACTIVE"}">

                            ${isActive ? "Block" : "Activate"}

                        </button>
                    </td>
                </tr>
            `;

        }).join("");

    } catch (error) {

        showTableError("usersTable", 6, error);
    }
}


// ---------- UPDATE USER STATUS ----------

const usersTable = document.getElementById("usersTable");

if (usersTable) {

    usersTable.addEventListener("click", async event => {

        const button = event.target.closest("[data-user-id]");

        if (!button) return;

        const userId = button.dataset.userId;
        const nextStatus = button.dataset.nextStatus;

        if (!confirm(`Change this user's status to ${nextStatus}?`)) {
            return;
        }

        button.disabled = true;

        try {

            await apiRequest(
                `/users/${encodeURIComponent(userId)}/account-status`,
                {
                    method: "PUT",
                    body: JSON.stringify({
                        status: nextStatus
                    })
                }
            );

            showMessage("User account status updated successfully.");

            await loadUsers();
            await loadOverview();

        } catch (error) {

            showMessage(
                "Could not update user: " + error.message,
                "error"
            );

            button.disabled = false;
        }
    });
}


// ==================================================
// ORGANIZERS
// ==================================================

async function loadOrganizers() {

    const table = document.getElementById("organizersTable");

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="5">Loading organizers...</td>
        </tr>
    `;

    try {

        const organizers = await apiRequest("/organizers");

        if (!Array.isArray(organizers) || organizers.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="5">No organizers found.</td>
                </tr>
            `;

            return;
        }

        table.innerHTML = organizers.map(organizer => {

            const status =
                organizer.organizerAccountStatus || "UNKNOWN";

            const isBlocked =
                String(status).toUpperCase() === "BLOCKED";

            const action = isBlocked ? "unblock" : "block";

            return `
                <tr>
                    <td>${escapeHTML(organizer.organizerId)}</td>
                    <td>${escapeHTML(organizer.organizerName)}</td>
                    <td>${escapeHTML(organizer.organizerEmail)}</td>

                    <td>
                        <span class="status ${getStatusClass(status)}">
                            ${escapeHTML(status)}
                        </span>
                    </td>

                    <td>
                        <button
                            class="action-button ${isBlocked ? "unblock" : "block"}"
                            data-organizer-id="${escapeHTML(organizer.organizerId)}"
                            data-action="${action}">

                            ${isBlocked ? "Unblock" : "Block"}

                        </button>
                    </td>
                </tr>
            `;

        }).join("");

    } catch (error) {

        showTableError("organizersTable", 5, error);
    }
}


// ---------- BLOCK / UNBLOCK ORGANIZER ----------

const organizersTable = document.getElementById("organizersTable");

if (organizersTable) {

    organizersTable.addEventListener("click", async event => {

        const button = event.target.closest("[data-organizer-id]");

        if (!button) return;

        const organizerId = button.dataset.organizerId;
        const action = button.dataset.action;

        if (!confirm(`Are you sure you want to ${action} this organizer?`)) {
            return;
        }

        button.disabled = true;

        try {

            await apiRequest(
                `/organizers/${encodeURIComponent(organizerId)}/${action}`,
                {
                    method: "PATCH"
                }
            );

            const message = action === "block"
                ? "Organizer blocked successfully."
                : "Organizer unblocked successfully.";

            showMessage(message);

            await loadOrganizers();
            await loadOverview();

        } catch (error) {

            showMessage(
                "Organizer update failed: " + error.message,
                "error"
            );

            button.disabled = false;
        }
    });
}


// ==================================================
// EVENTS
// ==================================================

async function loadEvents() {

    const table = document.getElementById("eventsTable");

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="6">Loading events...</td>
        </tr>
    `;

    try {

        const events = await apiRequest("/events");

        if (!Array.isArray(events) || events.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="6">No events found.</td>
                </tr>
            `;

            return;
        }

        table.innerHTML = events.map(event => {

            const status = String(
                event.eventStatus ?? "UNKNOWN"
            ).trim().toUpperCase();

            return `
                <tr>
                    <td>${escapeHTML(event.eventId)}</td>
                    <td>${escapeHTML(event.eventTitle)}</td>
                    <td>${escapeHTML(event.eventLocation)}</td>
                    <td>${escapeHTML(event.eventDateTime)}</td>
                    <td>${escapeHTML(event.usersCapacity)}</td>

                    <td>
                        <span class="status ${getStatusClass(status)}">
                            ${escapeHTML(status)}
                        </span>
                    </td>
                </tr>
            `;

        }).join("");

    } catch (error) {

        showTableError("eventsTable", 6, error);
    }
}


// ==================================================
// REFRESH BUTTONS
// ==================================================

const refreshUsers = document.getElementById("refreshUsers");

if (refreshUsers) {
    refreshUsers.addEventListener("click", loadUsers);
}

const refreshOrganizers = document.getElementById("refreshOrganizers");

if (refreshOrganizers) {
    refreshOrganizers.addEventListener("click", loadOrganizers);
}

const refreshEvents = document.getElementById("refreshEvents");

if (refreshEvents) {
    refreshEvents.addEventListener("click", loadEvents);
}


// ==================================================
// INITIAL LOAD
// ==================================================

loadOverview();