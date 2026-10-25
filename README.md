# Happy David — Portfolio

A responsive, single-page portfolio for Happy David, a software engineering student and competitive programmer at Rwanda Coding Academy. The frontend is a typed React/Vite application. A small Spring Boot service accepts contact messages and hands them to a configured SMTP server. Portfolio content is static, typed frontend data; the first version has no database, login, analytics, or message storage.

## Run with Docker Compose

Requirements: Docker Engine/Desktop with the Compose plugin. From the project root:

```sh
cp .env.example .env
# Edit .env and set a real SMTP sender account and password.
docker compose up --build -d
```

Open [http://localhost:8080](http://localhost:8080). Nginx serves the production Vite build and proxies `/api/*` to the private `backend:8081` Compose service. The backend is not published as a host port. Inspect service health with `docker compose ps`, logs with `docker compose logs -f frontend backend`, and stop with `docker compose down`.

The frontend can be built and previewed without Docker from `frontend/` using `npm ci`, `npm run dev` or `npm run build`. To exercise contact submissions during local development, start the API separately from `backend/` with `mvn spring-boot:run`; Vite's development server does not proxy `/api` by default, so use Compose for the complete contact flow or add a local Vite proxy when developing that integration. The production frontend uses a relative `/api` URL and never embeds localhost or SMTP configuration.

## SMTP configuration

Copy `.env.example` to `.env`; Compose reads these variables only into the backend container:

| Variable | Purpose |
| --- | --- |
| `MAIL_HOST` | SMTP server host, for example `smtp.gmail.com` |
| `MAIL_PORT` | SMTP submission port; Gmail uses `587` with STARTTLS |
| `MAIL_USERNAME` | Authenticated sender account |
| `MAIL_PASSWORD` | SMTP password or provider app password; keep it private |
| `MAIL_FROM` | Sender address authorized by the SMTP provider; defaults to `MAIL_USERNAME` |
| `CONTACT_TO` | Portfolio recipient; defaults to `happydavid226@gmail.com` |

For Gmail, use an app password where required by the account. Never commit `.env` or put SMTP secrets in frontend variables, Docker build arguments, or image files. The endpoint reports acceptance by the configured mail server; SMTP acceptance does not guarantee inbox delivery. The form does not clear on failure and gives a neutral success/error message. Contact data is not persisted by this app.

## Architecture and safeguards

- `frontend/src/data/` holds typed profile, achievement, project, skill, education, and activity content, separate from layout components.
- `backend/.../contact/` separates request validation, rate limiting, email formatting, HTTP responses, and error handling.
- Contact submission is `POST /api/contact` with JSON fields `name`, `email`, `message`, and optional `website` honeypot. A request filter rate-limits before parsing/validation and caps the JSON body at 12 KiB; Bean Validation then checks required fields and lengths. A filled honeypot receives a generic accepted response without sending mail. Requests are limited to five per client address per 15 minutes in memory.
- The message is sent as plain text. The visitor address is `Reply-To`; the authenticated configured sender remains `From`. SMTP errors are logged without request contents or credentials and return a safe `503` response.
- The Nginx container overwrites `X-Real-IP`; only that trusted reverse proxy can reach the backend on the Compose network. Do not publish backend port 8081 directly. The in-memory limiter resets on restart and is intended for a small portfolio deployment.
- Nginx runs as an unprivileged user and sets a restrictive content security policy and basic browser security headers. `/actuator/health` is available only on the internal backend network for health checks; Nginx does not expose actuator routes publicly.
- No external font, analytics, or image service is required by the frontend. Browser contact requests use same-origin `/api` routing.

## Verify and test

Frontend production compilation:

```sh
cd frontend
npm ci
npm run build
```

Backend tests and package:

```sh
cd backend
mvn test
mvn package
```

The backend tests mock the mail sender; they do not send real messages. Compose builds both production images and waits for backend health before starting the frontend. To check proxy reachability after startup, open `http://localhost:8080/api/contact` in a client with a JSON POST (a GET should return `405`, confirming the proxy route reached the API). Real delivery requires valid SMTP settings and should be checked separately with a message you control.

## Content review before publication

Unconfirmed results and candidate items are intentionally held outside the public arrays in the corresponding `frontend/src/data` files. Review [CONTENT_REVIEW.md](CONTENT_REVIEW.md), confirm each item, then move only accurate items into the exported public arrays. Never add a certificate, standings, source repository, demo, or social URL until it is supplied and verified. A CV link is omitted until an actual CV file is added. Profile email is the supplied contact address.

## Public deployment

Compose runs the application locally; it does not publish it to the internet. Deploy the two-container Compose application to a host that supports Docker Compose, persistent service restarts, environment variables/secrets, and outbound SMTP (or a transactional SMTP provider). Put a TLS-enabled reverse proxy or managed HTTPS load balancer in front of port 8080, expose only ports 80/443 publicly, configure DNS and HTTPS certificates, and keep port 8081 private. Set SMTP variables through the host's secret manager, restrict outbound mail according to the provider, and monitor container health and logs. Build from a reviewed source revision, update base image tags deliberately, and test upgrades before replacing the running instance. No database or persistent volume is needed in this version.

## Troubleshooting

- **Frontend is healthy but contact fails:** check `docker compose logs backend`, verify SMTP host/port/sender credentials, and confirm the provider permits authenticated SMTP. The browser displays a safe failure and the form stays populated.
- **Frontend waits for backend:** inspect `docker compose ps` and backend health logs. SMTP is not contacted at startup, so SMTP credentials are unrelated to API health.
- **Rate limit during local checks:** wait 15 minutes or restart the backend. The limit is per address and held in process memory.
- **Port 8080 is occupied:** change the host side of the frontend mapping in `compose.yaml`, for example `8088:8080`, and use that port in the browser.
- **SMTP reports accepted but no inbox message arrives:** check the recipient's spam folder, provider delivery logs, and sender/domain policy; SMTP acceptance is not a delivery guarantee.
"# me" 
