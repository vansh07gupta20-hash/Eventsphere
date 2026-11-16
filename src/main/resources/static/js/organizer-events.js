document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("createEventForm");

    if (!form) {
        console.error("Create event form not found: #createEventForm");
        return;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const token = sessionStorage.getItem("organizerAuthToken");

        if (!token) {
            alert("Please log in as an organizer first.");
            window.location.href = "/pages/organizer-login.html";
            return;
        }

        const getValue = (id) =>
            document.getElementById(id)?.value.trim() ?? "";

        const payload = {
            eventTitle: getValue("eventTitle"),
            eventDescription: getValue("eventDescription"),
            eventDateTime: getValue("eventDateTime"),
            eventLocation: getValue("eventLocation"),
            usersCapacity: Number(getValue("usersCapacity")),
            eventRegistrationStartDate: getValue("eventRegistrationStartDate"),
            eventRegistrationEndDate: getValue("eventRegistrationEndDate")
        };

        if (
            !payload.eventTitle ||
            !payload.eventDateTime ||
            !payload.usersCapacity ||
            !payload.eventRegistrationStartDate ||
            !payload.eventRegistrationEndDate
        ) {
            alert("Please fill in all required event fields.");
            return;
        }

        if (
            new Date(payload.eventRegistrationEndDate) <=
            new Date(payload.eventRegistrationStartDate)
        ) {
            alert("Registration end date must be after start date.");
            return;
        }

        try {
            const response = await fetch("/events", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            const responseText = await response.text();

            if (!response.ok) {
                console.error(
                    "Event creation failed:",
                    response.status,
                    responseText
                );

                alert(
                    `Event creation failed (${response.status}): ` +
                    (responseText || "Please check the server logs.")
                );
                return;
            }

            alert("Event created successfully!");

            form.reset();

            // Refresh the events list if the dashboard provides this function.
            if (typeof window.loadOrganizerEvents === "function") {
                await window.loadOrganizerEvents();
            }
        } catch (error) {
            console.error("Error creating event:", error);
            alert("Could not connect to the server. Please try again.");
        }
    });
});