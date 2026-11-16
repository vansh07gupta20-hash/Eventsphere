const API_BASE = "";

const token = sessionStorage.getItem("authToken");
const userName = sessionStorage.getItem("userName") || "Explorer";

const eventGrid = document.getElementById("eventGrid");
const registrationList = document.getElementById("registrationList");
const eventsMessage = document.getElementById("eventsMessage");
const registrationsMessage = document.getElementById("registrationsMessage");
const searchInput = document.getElementById("eventSearch");
const categoryFilter = document.getElementById("categoryFilter");

let allEvents = [];
let myRegistrations = [];

// ---------------- USER DETAILS ----------------

document.getElementById("welcomeName").textContent = userName;
document.getElementById("headerUserName").textContent = userName;

document.getElementById("userAvatar").textContent =
    userName.trim().charAt(0).toUpperCase() || "U";

// ---------------- API FUNCTIONS ----------------

function authHeaders() {
    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };
}

async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            ...authHeaders(),
            ...(options.headers || {})
        }
    });

    if (response.status === 401 || response.status === 403) {
        throw new Error(
            "Your session may have expired, or you don't have permission."
        );
    }

    if (!response.ok) {
        throw new Error(`Request failed (${response.status}).`);
    }

    if (response.status === 204) return null;

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function unwrapList(data) {
    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.content)) return data.content;
    if (Array.isArray(data?.data)) return data.data;
    if (Array.isArray(data?.events)) return data.events;
    if (Array.isArray(data?.registrations)) return data.registrations;

    return [];
}

// ---------------- EVENT DETAILS ----------------

function getEventId(event) {
    return event.eventId ?? event.id ?? event.event_id;
}

function getEventName(event) {
    return event.eventTitle
        ?? event.eventName
        ?? event.name
        ?? event.title
        ?? "Untitled Event";
}

function getEventCategory(event) {
    return event.eventCategory
        ?? event.category
        ?? event.eventType
        ?? "Event";
}

function getEventLocation(event) {
    return event.eventLocation
        ?? event.location
        ?? event.venue
        ?? "Location to be announced";
}

function formatDate(value) {
    if (!value) return "Not announced";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return String(value);
    }

    return date.toLocaleString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
        hour: "numeric",
        minute: "2-digit"
    });
}

function getEventDate(event) {
    return formatDate(
        event.eventDateTime
        ?? event.eventDate
        ?? event.date
        ?? event.startDate
    );
}

function safeText(value) {
    return String(value ?? "");
}

// ---------------- HTML ELEMENT HELPERS ----------------

function createElement(tag, className, text) {
    const element = document.createElement(tag);

    if (className) {
        element.className = className;
    }

    if (text !== undefined) {
        element.textContent = text;
    }

    return element;
}

function showEmpty(container, message) {
    const item = createElement("div", "empty-state", message);
    container.replaceChildren(item);
}

// ---------------- CATEGORY FILTER ----------------

function populateCategories(events) {
    const categories = [
        ...new Set(events.map(getEventCategory).filter(Boolean))
    ];

    categoryFilter.replaceChildren(
        new Option("All Categories", "all"),
        ...categories.map(category => new Option(category, category))
    );
}

// ---------------- RENDER EVENTS ----------------

function renderEvents() {
    const query = searchInput.value.trim().toLowerCase();
    const category = categoryFilter.value;

    const filtered = allEvents.filter(event => {
        const searchable = [
            getEventName(event),
            getEventCategory(event),
            getEventLocation(event),
            event.eventDescription ?? event.description ?? ""
        ].join(" ").toLowerCase();

        return searchable.includes(query) &&
            (category === "all" || getEventCategory(event) === category);
    });

    document.getElementById("availableCount").textContent =
        allEvents.length;

    eventGrid.replaceChildren();

    if (!filtered.length) {
        showEmpty(
            eventGrid,
            allEvents.length
                ? "No events match your search. Try another keyword or category."
                : "No events are available right now."
        );
        return;
    }

    filtered.forEach((event, index) => {
        const card = createElement("article", "event-card");

        const cover = createElement("div", "event-cover");
        cover.setAttribute("aria-hidden", "true");

        cover.append(
            document.createTextNode(["✦", "♫", "✧", "◈"][index % 4])
        );

        const categoryTag = createElement(
            "span",
            "event-category",
            getEventCategory(event)
        );

        cover.append(categoryTag);

        const body = createElement("div", "event-body");

        // Event title
        body.append(
            createElement("h3", "", getEventName(event))
        );

        // Event date and time
        body.append(
            createElement(
                "div",
                "event-meta",
                `◷ Event Date: ${getEventDate(event)}`
            )
        );

        // Event location
        body.append(
            createElement(
                "div",
                "event-meta",
                `⌖ Location: ${getEventLocation(event)}`
            )
        );

        // Registration opening date
        body.append(
            createElement(
                "div",
                "event-meta",
                `Registration Starts: ${formatDate(
    event.eventRegistrationStartDate
)}`
            )
        );

        // Registration closing date
        body.append(
            createElement(
                "div",
                "event-meta",
                `Registration Ends: ${formatDate(
    event.eventRegistrationEndDate
)}`
            )
        );

        // Event capacity
        body.append(
            createElement(
                "div",
                "event-meta",
                `Capacity: ${event.usersCapacity ?? "Not specified"}`
            )
        );

        // Event description
        const description =
            event.eventDescription ?? event.description;

        if (description) {
            body.append(
                createElement(
                    "p",
                    "event-description",
                    safeText(description).slice(0, 150)
                )
            );
        }

        // Register button
        const footer = createElement("div", "event-footer");

        footer.append(
            createElement("span", "event-date", "You're invited")
        );

        const button = createElement(
            "button",
            "register-button",
            "Register →"
        );

        button.type = "button";

        button.addEventListener("click", () => {
            registerForEvent(event, button);
        });

        footer.append(button);
        body.append(footer);

        card.append(cover, body);
        eventGrid.append(card);
    });
}

// ---------------- LOAD EVENTS ----------------

async function loadEvents() {
    eventsMessage.textContent = "Loading events...";

    try {
        const data = await apiRequest("/events");

        allEvents = unwrapList(data);

        populateCategories(allEvents);
        renderEvents();

        eventsMessage.textContent = allEvents.length
            ? `${allEvents.length} event(s) found.`
            : "No events have been published yet.";

    } catch (error) {
        eventsMessage.textContent =
            `Couldn't load events: ${error.message} Check that GET /events is available.`;

showEmpty(eventGrid, "Events could not be loaded.");
}
}

// ---------------- REGISTRATIONS ----------------

function getRegistrationEventName(registration) {
    return registration.eventTitle
        ?? registration.eventName
        ?? registration.event?.eventTitle
        ?? registration.name
        ?? "Registered Event";
}

function renderRegistrations() {
    document.getElementById("registrationCount").textContent =
        myRegistrations.length;

    registrationList.replaceChildren();

    if (!myRegistrations.length) {
        showEmpty(
            registrationList,
            "You haven't registered for any events yet. Explore events above to get started."
        );
        return;
    }

    myRegistrations.forEach(registration => {
        const item = createElement("article", "registration-item");
        const details = createElement("div");

        details.append(
            createElement(
                "h3",
                "",
                getRegistrationEventName(registration)
            )
        );

        const eventDate =
            registration.eventDateTime
            ?? registration.event?.eventDateTime
            ?? registration.eventDate
            ?? registration.event?.eventDate;

        details.append(
            createElement(
                "p",
                "",
                eventDate
                    ? `Event Date: ${formatDate(eventDate)}`
                    : "Your event registration"
            )
        );

        item.append(details);

        item.append(
            createElement(
                "span",
                "registration-status",
                registration.registrationStatus
                ?? registration.status
                ?? "Registered"
            )
        );

        registrationList.append(item);
    });
}

async function loadRegistrations() {
    registrationsMessage.textContent = "Loading registrations...";

    try {
        const data = await apiRequest("/registrations/my");

        myRegistrations = unwrapList(data);

        renderRegistrations();

        registrationsMessage.textContent = myRegistrations.length
            ? "Your registrations are up to date."
            : "No registrations yet.";

    } catch (error) {
        registrationsMessage.textContent =
            `Couldn't load registrations: ${error.message} Check GET /registrations/my.`;

        showEmpty(
            registrationList,
            "Your registrations could not be loaded."
        );
    }
}

// ---------------- REGISTER FOR EVENT ----------------

async function registerForEvent(event, button) {
    const eventId = getEventId(event);

    if (eventId == null) {
        alert("Event ID nahi mila. GET /events ka response check karo.");
        return;
    }

    button.disabled = true;
    button.textContent = "Registering...";

    try {
        await apiRequest(`/registrations/events/${eventId}`, {
            method: "POST"
        });

        button.textContent = "Registered ✓";
        button.disabled = true;

        await loadRegistrations();

        alert("Event registration successful!");

    } catch (error) {
        alert(`Registration failed: ${error.message}`);

        button.disabled = false;
        button.textContent = "Register →";
    }
}

// ---------------- SEARCH AND FILTER ----------------

searchInput.addEventListener("input", renderEvents);

categoryFilter.addEventListener("change", renderEvents);

// ---------------- LOGOUT ----------------

document.getElementById("logoutButton").addEventListener("click", () => {
    sessionStorage.removeItem("authToken");
    sessionStorage.removeItem("userId");
    sessionStorage.removeItem("userName");
    sessionStorage.removeItem("userEmail");

    window.location.href = "login.html?role=user";
});

// ---------------- INITIALIZE DASHBOARD ----------------

if (!token) {
    window.location.href = "login.html?role=user";
} else {
    loadEvents();
    loadRegistrations();
}