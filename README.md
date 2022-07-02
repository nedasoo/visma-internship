# Meetings application
A Spring Boot application to manage Visma's internal meetings. This is an internship project.

___
**The API consists of several endpoints:**

> GET /meetings

Returns a list of all meetings and their details. Possible query parameter filters:
- `description` - Filter meetings that match the description;
- `responsiblePerson` - Filter meetings by the responsible person's name;
- `category` - Filter meetings by their category; Possible values: *CodeMonkey,Hub,Short,TeamBuilding*;
- `type` - Filter meetings by their type; Possible values: *Live,InPerson*;
- `dateFrom` and `dateTo` - Filter meetings to match date criteria;
- `participantsFrom` and `participantsTo` - Filter meetings that have between `participantsFrom` and `participantsTo` attendees;

> POST /meetings

Create a new meeting. __Required__ request body parameters:
- `name` - Name for the meeting;
- `responsiblePerson` - Name of the responsible person for the meeting;
- `category` - Meeting's category. Possible values: *CodeMonkey,Hub,Short,TeamBuilding*;
- `type` - Meeting type. Possible values:  *Live,InPerson*;
- `startDate` - Starting date for the meeting. Use format `YYYY-MM-DD HH:mm:ss`.
- `endDate` - Ending date for the meeting. Use format `YYYY-MM-DD HH:mm:ss`.

Optional parameters:

- `description` -  A short description of the meeting.

> DELETE /meetings/{name}

Delete a meeting by it's `name`.

> POST /participants

Add a new participant (attendee) to the meeting. Required body parameters:
- `name` - Name of the person;
- `meeting` - Name of the meeting;
- `time` - Time at which the person is being added; Use format `YYYY-MM-DDTHH:MM:SS`, e.g. `2022-07-01T14:30:00`

>DELETE /meetings/{meeting}/participants/{name}

Remove a participant (attendee) from the meeting. Query parameters:
- `meeting` - name of the meeting;
- `name` - name of the person to remove;