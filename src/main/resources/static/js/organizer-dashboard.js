document.addEventListener("DOMContentLoaded", () => {

    // ========================================
    // 1. GET HTML ELEMENTS
    // ========================================

    const organizerAvatar = document.getElementById("organizerAvatar");
    const headerOrganizerName = document.getElementById("headerOrganizerName");
    const welcomeOrganizerName = document.getElementById("welcomeOrganizerName");

    const totalEventsElement = document.getElementById("totalEvents");
    const upcomingEventsElement = document.getElementById("upcomingEvents");
    const totalRegistrationsElement = document.getElementById("totalRegistrations");

    const eventFormPanel = document.getElementById("eventFormPanel");
    const eventForm = document.getElementById("eventForm");
    const showEventFormButton = document.getElementById("showEventFormButton");

    const cancelEventButton =
        document.getElementById("cancelEventFormButton") ||
        document.getElementById("cancelEventButton");

    const formMessage = document.getElementById("formMessage");

    const eventGrid = document.getElementById("eventGrid");
    const eventsMessage = document.getElementById("eventsMessage");

    const registrationList = document.getElementById("registrationList");
    const registrationsMessage = document.getElementById("registrationsMessage");

    const logoutButton = document.getElementById("logoutButton");


    // ========================================
    // 2. CHECK ORGANIZER LOGIN
    // ========================================

    const token = sessionStorage.getItem("organizerAuthToken");

    const organizerName =
        sessionStorage.getItem("organizerName") || "Organizer";

    if (!token) {
        window.location.replace("/pages/organizer-login.html");
        return;
    }

    if (headerOrganizerName) {
        headerOrganizerName.textContent = organizerName;
    }

    if (welcomeOrganizerName) {
        welcomeOrganizerName.textContent = organizerName;
    }

    if (organizerAvatar) {
        organizerAvatar.textContent =
            organizerName.trim().charAt(0).toUpperCase() || "O";
    }


    // ========================================
    // 3. API ENDPOINTS
    // ========================================

    // Only this organizer's events
    const ORGANIZER_EVENTS_API = "/events/organizer";

    // Event creation endpoint
    const CREATE_EVENT_API = "/events";

    // Only registrations for this organizer's events
    const REGISTRATIONS_API = "/registrations/organizer";

    let events = [];
    let registrations = [];


    // ========================================
    // 4. COMMON API REQUEST
    // ========================================

    async function apiRequest(url, options = {}) {

        const headers = {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
            ...(options.headers || {})
        };

        const response = await fetch(url, {
            ...options,
            headers
        });

        const responseText = await response.text();

        let data = null;

        if (responseText) {
            try {
                data = JSON.parse(responseText);
            } catch (error) {
                data = responseText;
            }
        }

        if (!response.ok) {

            const message =
                typeof data === "object" && data !== null
                    ? data.message || data.error || data.detail
                    : data;

            if (response.status === 401 || response.status === 403) {
                throw new Error(
                    "Your session is invalid or expired. Please log in again."
                );
            }

            throw new Error(
                message || `Request failed (HTTP ${response.status})`
            );
        }

        return data;
    }


    // ========================================
    // 5. SHOW FORM MESSAGE
    // ========================================

    function showFormMessage(message, isError = false) {

        if (!formMessage) {
            console.error("HTML element #formMessage was not found.");
            return;
        }

        formMessage.textContent = message;
        formMessage.style.color = isError ? "#ffb4c1" : "#67e8f9";
    }


    // ========================================
    // 6. SHOW / HIDE EVENT FORM
    // ========================================

    function openEventForm() {

        if (eventFormPanel) {

            eventFormPanel.classList.add("visible");

            eventFormPanel.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
        }

        const firstInput = document.getElementById("eventTitle");

        if (firstInput) {
            setTimeout(() => firstInput.focus(), 300);
        }
    }


    function closeEventForm() {

        if (eventFormPanel) {
            eventFormPanel.classList.remove("visible");
        }

        if (eventForm) {
            eventForm.reset();
        }

        showFormMessage("");
    }


    if (showEventFormButton) {
        showEventFormButton.addEventListener("click", openEventForm);
    }


    document.querySelectorAll('a[href="#createEvent"]').forEach(link => {

        link.addEventListener("click", event => {

            event.preventDefault();
            openEventForm();

        });

    });


    if (cancelEventButton) {
        cancelEventButton.addEventListener("click", closeEventForm);
    }


    // ========================================
    // 7. EVENT PROPERTY HELPERS
    // ========================================

    function getEventTitle(event) {
        return event.eventTitle ?? event.name ?? "Untitled event";
    }


    function getEventDate(event) {
        return event.eventDateTime ?? event.date ?? "";
    }


    function getEventDescription(event) {
        return event.eventDescription ?? event.description ?? "";
    }


    function getEventLocation(event) {
        return event.eventLocation ?? event.location ?? "";
    }


    function getEventCapacity(event) {
        return event.usersCapacity ?? event.capacity ?? null;
    }


    // Only SCHEDULED and CANCELLED are supported statuses.
    function getEventStatus(event) {

        const status = String(
            event.eventStatus ?? event.status ?? "SCHEDULED"
        ).trim().toUpperCase();

        // Convert old status values if any old test records remain.
        if (status === "UPCOMING") {
            return "SCHEDULED";
        }

        return status;
    }


    function formatDate(dateTimeString) {

        if (!dateTimeString) {
            return "Date not specified";
        }

        const date = new Date(dateTimeString);

        if (Number.isNaN(date.getTime())) {
            return dateTimeString;
        }

        return date.toLocaleString("en-IN", {
            day: "numeric",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }


    // ========================================
    // 8. UPDATE DASHBOARD STATISTICS
    // ========================================

    function updateStatistics() {

        const scheduledEvents = events.filter(event =>
            getEventStatus(event) === "SCHEDULED"
        );

        const activeRegistrations = registrations.filter(
            registration =>
                String(registration.registrationStatus || "")
                    .toUpperCase() === "REGISTERED"
        );

        if (totalEventsElement) {
            totalEventsElement.textContent = events.length;
        }

        if (upcomingEventsElement) {
            upcomingEventsElement.textContent = scheduledEvents.length;
        }

        if (totalRegistrationsElement) {
            totalRegistrationsElement.textContent =
                activeRegistrations.length;
        }
    }


    // ========================================
    // 9. DISPLAY ORGANIZER'S EVENTS
    // ========================================

    function renderEvents() {

        if (!eventGrid) {
            return;
        }

        eventGrid.replaceChildren();

        if (events.length === 0) {

            const emptyState = document.createElement("div");

            emptyState.className = "empty-state";
            emptyState.textContent =
                "Your events will appear here. Create your first event to get started.";

            eventGrid.appendChild(emptyState);

            if (eventsMessage) {
                eventsMessage.textContent =
                    "You haven't created any events yet.";
            }

            updateStatistics();
            return;
        }

        if (eventsMessage) {
            eventsMessage.textContent =
                `Loaded ${events.length} of your event(s).`;
        }

        events.forEach(event => {

            const card = document.createElement("article");
            card.className = "event-card";

            const title = document.createElement("h3");
            title.textContent = getEventTitle(event);

            const description = document.createElement("p");
            description.textContent =
                getEventDescription(event) || "No description provided.";

            const date = document.createElement("p");
            date.textContent =
                `📅 ${formatDate(getEventDate(event))}`;

            const location = document.createElement("p");
            location.textContent =
                `📍 ${getEventLocation(event) || "Location not specified"}`;

            const capacity = document.createElement("p");
            const capacityValue = getEventCapacity(event);

            capacity.textContent =
                capacityValue != null
                    ? `Maximum participants: ${capacityValue}`
                    : "Participant limit not specified";

            const status = document.createElement("p");
            const eventStatus = getEventStatus(event);

            status.textContent = `Status: ${eventStatus}`;
            status.className = `event-status ${eventStatus.toLowerCase()}`;

            card.append(
                title,
                description,
                date,
                location,
                capacity,
                status
            );

            eventGrid.appendChild(card);

        });

        updateStatistics();
    }


    // ========================================
    // 10. LOAD ONLY LOGGED-IN ORGANIZER'S EVENTS
    // ========================================

    async function loadEvents() {

        if (eventsMessage) {
            eventsMessage.textContent = "Loading your events...";
        }

        try {

            const data = await apiRequest(ORGANIZER_EVENTS_API);

            events = Array.isArray(data)
                ? data
                : (data?.content ?? data?.events ?? []);

            renderEvents();

        } catch (error) {

            console.error("Error loading organizer events:", error);

            events = [];

            if (eventGrid) {
                eventGrid.replaceChildren();
            }

            if (eventsMessage) {
                eventsMessage.textContent =
                    `Unable to load your events: ${error.message}`;
            }

            updateStatistics();
        }
    }


    // ========================================
    // 11. CREATE EVENT
    // ========================================

    if (eventForm) {

        eventForm.addEventListener("submit", async event => {

            event.preventDefault();

            const eventTitle =
                document.getElementById("eventTitle")?.value.trim() ?? "";

            const eventDescription =
                document.getElementById("eventDescription")?.value.trim() ?? "";

            const eventDateTime =
                document.getElementById("eventDateTime")?.value ?? "";

            const eventLocation =
                document.getElementById("eventLocation")?.value.trim() ?? "";

            const capacityValue =
                document.getElementById("usersCapacity")?.value ?? "";

            const eventRegistrationStartDate =
                document.getElementById("eventRegistrationStartDate")?.value ?? "";

            const eventRegistrationEndDate =
                document.getElementById("eventRegistrationEndDate")?.value ?? "";


            // Validate required fields
            if (!eventTitle || !eventDateTime) {

                showFormMessage(
                    "Please enter Event Title and Event Date & Time.",
                    true
                );

                return;
            }


            // Validate event title
            if (eventTitle.length > 100) {

                showFormMessage(
                    "Event title must not exceed 100 characters.",
                    true
                );

                return;
            }


            // Validate event location
            if (eventLocation.length > 100) {

                showFormMessage(
                    "Event location must not exceed 100 characters.",
                    true
                );

                return;
            }


            // Validate event date
            const selectedEventDate = new Date(eventDateTime);

            if (
                Number.isNaN(selectedEventDate.getTime()) ||
                selectedEventDate <= new Date()
            ) {

                showFormMessage(
                    "Please select a valid future event date and time.",
                    true
                );

                return;
            }


            // Validate participant capacity
            let usersCapacity = null;

            if (capacityValue !== "") {

                usersCapacity = Number(capacityValue);

                if (!Number.isInteger(usersCapacity) || usersCapacity <= 0) {

                    showFormMessage(
                        "Capacity must be a whole number greater than zero.",
                        true
                    );

                    return;
                }
            }


            // Validate registration dates
            if (
                eventRegistrationStartDate &&
                eventRegistrationEndDate &&
                new Date(eventRegistrationEndDate) <=
                new Date(eventRegistrationStartDate)
            ) {

                showFormMessage(
                    "Registration end date must be after registration start date.",
                    true
                );

                return;
            }


            // Prepare event data
            const eventData = {

                eventTitle,

                eventDescription: eventDescription || null,

                eventDateTime,

                eventLocation: eventLocation || null,

                usersCapacity,

                eventRegistrationStartDate:
                    eventRegistrationStartDate || null,

                eventRegistrationEndDate:
                    eventRegistrationEndDate || null

            };


            const submitButton =
                eventForm.querySelector('button[type="submit"]');

            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = "Saving...";
            }

            showFormMessage("Saving event...");


            try {

                await apiRequest(CREATE_EVENT_API, {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(eventData)

                });


                showFormMessage("Event created successfully!");


                if (eventFormPanel) {
                    eventFormPanel.classList.remove("visible");
                }

                eventForm.reset();


                // Reload only this organizer's events
                await loadEvents();


                // Refresh this organizer's registrations
                await loadRegistrations();


                if (eventGrid) {

                    eventGrid.scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });

                }

            } catch (error) {

                console.error("Event creation error:", error);

                showFormMessage(
                    error.message || "Unable to save event.",
                    true
                );

            } finally {

                if (submitButton) {
                    submitButton.disabled = false;
                    submitButton.textContent = "Save Event";
                }

            }

        });

    }


    // ========================================
    // 12. LOAD ONLY THIS ORGANIZER'S REGISTRATIONS
    // ========================================

    async function loadRegistrations() {

        if (!registrationList) {
            return;
        }

        if (registrationsMessage) {
            registrationsMessage.textContent =
                "Loading your event registrations...";
        }

        try {

            const data = await apiRequest(REGISTRATIONS_API);

            registrations = Array.isArray(data)
                ? data
                : (data?.content ?? data?.registrations ?? []);

            renderRegistrations();

        } catch (error) {

            console.error("Registration loading error:", error);

            registrations = [];

            registrationList.replaceChildren();

            if (registrationsMessage) {
                registrationsMessage.textContent =
                    `Unable to load registrations: ${error.message}`;
            }

            updateStatistics();
        }
    }


    // ========================================
    // 13. DISPLAY ORGANIZER'S REGISTRATIONS
    // ========================================

    function renderRegistrations() {

        if (!registrationList) {
            return;
        }

        registrationList.replaceChildren();

        if (registrations.length === 0) {

            const emptyState = document.createElement("div");

            emptyState.className = "empty-state";
            emptyState.textContent =
                "No registrations found for your events.";

            registrationList.appendChild(emptyState);

            if (registrationsMessage) {
                registrationsMessage.textContent =
                    "No registrations available for your events yet.";
            }

            updateStatistics();
            return;
        }


        registrations.forEach(registration => {

            const item = document.createElement("div");
            item.className = "registration-item";

            const title = document.createElement("h4");
            title.textContent =
                registration.eventTitle || "Untitled event";

            const userName = document.createElement("p");
            userName.textContent =
                `Participant: ${registration.userName || "N/A"}`;

            const userEmail = document.createElement("p");
            userEmail.textContent =
                `Email: ${registration.userEmail || "N/A"}`;

            const registrationId = document.createElement("p");
            registrationId.textContent =
                `Registration ID: ${registration.registrationId ?? "N/A"}`;

            const registrationDate = document.createElement("p");
            registrationDate.textContent =
                `Registered on: ${
                    registration.registrationDate
                        ? formatDate(registration.registrationDate)
                        : "N/A"
                }`;

            const status = document.createElement("p");
            status.textContent =
                `Status: ${registration.registrationStatus || "N/A"}`;


            item.append(
                title,
                userName,
                userEmail,
                registrationId,
                registrationDate,
                status
            );

            registrationList.appendChild(item);

        });


        if (registrationsMessage) {

            const activeCount = registrations.filter(
                registration =>
                    String(registration.registrationStatus || "")
                        .toUpperCase() === "REGISTERED"
            ).length;

            registrationsMessage.textContent =
                `Loaded ${registrations.length} registration(s). ` +
                `${activeCount} active registration(s).`;
        }

        updateStatistics();
    }


    // ========================================
    // 14. LOGOUT
    // ========================================

    if (logoutButton) {

        logoutButton.addEventListener("click", () => {

            sessionStorage.removeItem("organizerAuthToken");
            sessionStorage.removeItem("organizerId");
            sessionStorage.removeItem("organizerName");
            sessionStorage.removeItem("organizerEmail");
            sessionStorage.removeItem("activeRole");
            sessionStorage.removeItem("organizerEvents");

            window.location.replace("/pages/organizer-login.html");

        });

    }


    // ========================================
    // 15. INITIALIZE DASHBOARD
    // ========================================

    loadEvents();
    loadRegistrations();

});