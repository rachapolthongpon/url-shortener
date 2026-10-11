# URL Shortener API

A simple API to make long URLs short (like Bitly).

## How to Run

Requirements: Java 21 and Docker.

**1. Start the database**

```bash
docker compose up -d
```

**2. Start the app**

```bash
./mvnw spring-boot:run
```

The app runs at `http://localhost:8080`.

**3. Run tests**

```bash
./mvnw test
```

## Example API Requests

For APIs that need login, send the token in the header: `Authorization: Bearer <token>`

### curl

**Register**

```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"password123"}'
```

**Login**

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"password123"}'
```

Response:

```json
{ "access_token": "eyJhbGciOi...", "token_type": "Bearer", "expires_in": 3600 }
```

Copy the `access_token` and use it in the next requests.

**Create a short URL** (login required)

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"original_url":"https://example.com/some/very/long/link"}'
```

Response:

```json
{ "short_url": "http://localhost:8080/r/aZ3kP9x" }
```

**See my short URLs** (login required)

```bash
curl http://localhost:8080/api/urls \
  -H "Authorization: Bearer <token>"
```

**Open a short URL** (no login needed)

```bash
curl -i http://localhost:8080/r/aZ3kP9x
```

The response is `302` with the original URL in the `Location` header.

**Delete a short URL** (login required)

```bash
curl -X DELETE http://localhost:8080/api/urls/1 \
  -H "Authorization: Bearer <token>"
```

### Postman

Import the collection in the `postman/` folder.

1. Run **Register** and **Login**.
2. Copy `access_token` and paste it into the collection variable `token`.
3. Run the other requests.
