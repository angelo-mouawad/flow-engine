# Flow Engine

A configurable workflow engine. You define a flow once, then trigger it over HTTP and it walks through its steps on its own: collecting request data, pausing while people approve it, and pushing notifications to whoever needs them in real time.

It was built as a **scrum team project at UCLL following an agile approach** under the team name GlacKIT.

## What it does

A **flow definition** is a template: a title, who is allowed to trigger it, and an ordered list of processes. There are three kinds of process.

| Process | What it does |
| --- | --- |
| `REQUEST` | Validates a payload against a request type and records a submission. Can require approval. |
| `APPROVAL` | Pauses the flow until the request it points at has been resolved. |
| `NOTIFICATION` | Pushes a message over WebSocket to a list of recipients. |

Triggering a definition creates a **flow instance**, which is one live run with its own status and step counter. The runner walks the processes in order until it either finishes (`SUCCESS`), hits an approval it has to wait on (`PENDING`), reaches an approval whose request was declined (`DECLINED`), or fails (`FAILURE`).

When an approver approves or declines from the UI, the submission is updated and the flow resumes from where it stopped. A declined request stops the flow at its approval step: the steps after it never run, and the flow is never reported as completed.

Four request types ship with it: absence, clock-in, task change, and meeting. Each has its own validation rules. Clock-in is the interesting one, since it auto approves anything inside office hours and only escalates the rest.

Definitions are checked when you save them, not when they run. An approval has to point at an earlier request that actually needs approval, and a notification needs someone to notify, either its own list or the approvers of the request it is linked to. A broken definition gets a `400` with the reason instead of failing halfway through a run.

There are two notification types. Pop-up notifications go out over WebSocket. Email notifications are rendered but only logged, since no mail server is wired up yet.

## Stack

**Backend** is Java 21 and Spring Boot 3.5, storing flow definitions, instances, and submissions in MongoDB. STOMP over SockJS handles notifications, and the API is documented with springdoc OpenAPI.

**Frontend** is Vue 3 with the Composition API, TypeScript, Vite, Pinia, and Tailwind 4. Vue Flow renders the process diagrams.

## Running it

The fastest route is Docker Compose, which brings up Mongo, the API, and the UI together.

From the root of the project run:

```bash
docker compose up --build
```

The UI lands on `http://localhost:3000` and the API on `http://localhost:8080`.

To run the two halves yourself, start a MongoDB on `localhost:27017` first, then:

```bash
cd backend
cp .env.example .env
mvn spring-boot:run
```

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

The UI is then on `http://localhost:5173`. In the `dev` profile the backend seeds three example flows on first start and triggers each of them once, so the dashboard has something to show. It only does this when the database is empty, so it will not wipe anything you have already created.

There is no login. The UI has a user switcher in the header with a few example users, and managers see the extra pages for definitions and approvals.

### Configuration

The backend reads these from the environment or a `.env` file, and every one has a working default:

| Variable | Default | Purpose |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017` | Mongo connection string |
| `MONGODB_DATABASE` | `flowEngine` | Database name |
| `SPRING_PROFILES_ACTIVE` | `dev` | Set to `prod` to skip the seed data |
| `SERVER_PORT` | `8080` | Port the API listens on |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma separated list of allowed origins |

The frontend needs one variable, `VITE_API_URL`, pointing at the backend.

Neither `.env` file is committed. Copy the matching `.env.example` and fill it in.

## API

Interactive docs are at `http://localhost:8080/swagger-ui.html` once the backend is up.

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/` | Health check |
| `GET` | `/flowDefinition` | List every definition |
| `GET` | `/flowDefinition/{id}` | Fetch one definition |
| `POST` | `/flowDefinition` | Create a definition |
| `PUT` | `/flowDefinition/{id}` | Update a definition |
| `DELETE` | `/flowDefinition/{id}` | Delete a definition |
| `GET` | `/flowInstance` | List every instance |
| `GET` | `/flowInstance/{id}` | Fetch one instance |
| `DELETE` | `/flowInstance/{id}` | Delete an instance |
| `POST` | `/trigger/{id}` | Start a flow from a definition |
| `GET` | `/request/pending` | Requests waiting on the caller |
| `GET` | `/request/pending/{id}` | One pending request |
| `PUT` | `/request/approve/{id}` | Approve a request |
| `PUT` | `/request/decline/{id}` | Decline a request |

The `/request` endpoints identify the caller through a `UserEmail` header.

Triggering a flow looks like this:

```bash
curl -X POST http://localhost:8080/trigger/{definitionId} \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Absence for Lais",
    "triggeredBy": "lais@glackit.be",
    "data": {
      "ABSENCE_REQUEST": {
        "Start_Date": "2025-11-10",
        "End_Date": "2025-11-19",
        "Reason": "sickness",
        "Submitted_By": "lais@glackit.be"
      }
    }
  }'
```

It responds with `201` and the new instance id and its status:

```json
{ "flowId": "6741f0c2a8b3e51d2c9a1f04", "status": "PENDING" }
```

If the flow stopped at an approval you get `PENDING`, and if it ran straight through you get `SUCCESS`. If the request data is invalid, you get a `400` with the reason and the instance is kept with status `FAILURE`.

Errors come back in a consistent shape, with a `400` for anything invalid and a `404` for anything missing:

```json
{ "status": 400, "error": "Start date cannot be after end date for absence request", "timestamp": "..." }
```

If you add a `callbackUrl` to the trigger body, the engine posts `{ "flowId": "...", "status": "..." }` back to it as the flow moves through `pending`, `completed`, `declined`, and `failed`. Callbacks are sent in the background with a short timeout. A callback that fails is logged and ignored, so an unreachable listener never breaks a run.

## Layout

```
.github/workflows/  CI for the backend and frontend
docker-compose.yml  Mongo, API and UI in one command
backend/
  src/main/java/be/ucll/
    config/       CORS, WebSocket and OpenAPI setup
    controller/   REST endpoints, DTOs and the global error handler
    exception/    Domain, service and not-found exceptions
    model/        Flow definitions, instances, processes and submissions
      strategies/ Request validation and notification rendering
    repository/   Mongo repositories and the dev seeder
    service/      Flow runner, approvals, requests and notifications
  src/test/java/  Unit tests
frontend/
  src/
    components/   Dashboard, flow, request, notification and layout components
    services/     Typed API client with shared error handling
    stores/       Pinia stores for user, theme and notifications
    utils/        Formatting and field lookup helpers
    views/        Routed pages
```

## Design notes

Request types and notification types are both **strategies** behind a factory, so adding a new one means writing a class and registering it, with nothing else to touch.

The flow runner is deliberately dumb about time. It runs synchronously until it hits something it cannot decide on its own, then stores its position and returns. Resuming is just picking the instance back up and carrying on, which keeps the whole thing restart safe without a scheduler.

Saving a decision and resuming its flow are two separate writes, so a crash in between could leave a flow waiting on an approval that is already decided. On startup the engine looks for those flows and resumes them.

Two people acting at the same moment cannot corrupt a flow. Instances and submissions carry a version, so a save based on a stale copy is rejected instead of silently overwriting newer data. A vote that loses that race is reapplied on the fresh copy. Resuming switches the flow from `PENDING` to `ACTIVE` in a single atomic update, so when two approvals land together only one of them continues the flow.

Approver votes are stored as a list of `{email, status}`. Emails are normalised (trimmed and lower cased) before every lookup, so callers can pass a plain email. Data written by older versions, which kept votes in a map keyed by the email with dots swapped for underscores and had no versions, is converted automatically at startup.

## Tests

```bash
cd backend && mvn test
```

The suite covers the domain model, request validation, approval arithmetic, the flow runner's branching, and the JSON contract with the frontend, using Mockito for the repositories.

```bash
cd frontend && npm run typecheck && npm run build
```

GitHub Actions runs both on every push that touches `backend/` or `frontend/`, and checks that both Docker images still build.

## License

Built for coursework at UCLL.