# 🔐 Simple API – OAuth2 Protection

## 📌 Overview

This project contains a simple Spring Boot API that was originally publicly accessible.
The goal of this task was to **secure all API endpoints using OAuth 2.0**, integrating with the provided Authorization Server.

The solution uses **OAuth2 Client Credentials flow**, **opaque access tokens**, and **token introspection**.

---

## ✅ Implemented

* Secured all API endpoints using **Spring Security OAuth2 Resource Server**
* Integrated the API with the provided **Authorization Server** via token introspection
* Enforced **scope-based authorization per endpoint**
* Added custom handling for authentication and authorization errors

---

## 🧭 Authorization Rules

Each endpoint requires a specific scope:

| Endpoint           | Required Scope |
| ------------------ | -------------- |
| `/health`          | `-`         |
| `/api/now`         | `time`         |
| `/api/random`      | `random`       |
| Others             | `api`          |

Scopes are validated based on the `scope` field returned by the token introspection endpoint.

---

## 🔄 OAuth Flow Used

* **Grant type:** `client_credentials`
* **Token type:** Opaque token
* **Token validation:** Token Introspection (`/token/introspect`)
* **Client credentials:**

  * `client_id`: `client1`
  * `client_secret`: `abcde12345`

---

## ▶️ How to run the project

Both the Authorization Server and the API can be started using Docker Compose.

From the root of the repository:

```bash
docker compose up
```

### Authorization Server

📍 Auth server will be available at:

```
http://localhost:8081
```

You can verify it via the discovery endpoint:

```
http://localhost:8081/.well-known/openid-configuration
```

---

### API

📍 The API will be available at:

```
http://localhost:8080
```

You can check the public health endpoint

```
http://localhost:8080/health
```

---

## 🔑 How to obtain an access token

Example using `curl`:

```bash
curl -X POST http://localhost:8081/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=client1" \
  -d "client_secret=abcde12345" \
  -d "scope=api time"
```

The response will include an opaque `access_token`.

---

## 🚀 How to call the API

Example request:

```bash
curl -X GET http://localhost:8080/api/now \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Expected responses

* ❌ **401 Unauthorized** → Missing or invalid token
* ⛔ **403 Forbidden** → Token does not contain the required scope
* ✅ **200 OK** → Authorized request

---

## 🔧 Security Configuration Summary

* OAuth2 Resource Server with opaque tokens
* Token introspection configured via `application.yml`
* Endpoint-level authorization using `requestMatchers`
* Fallback authorization rule using `anyRequest()` to avoid exposing new endpoints unintentionally

---

## 📝 Notes & Design Decisions

* The Authorization Server was **not modified**, except for minimal validation of the supported grant type
* Scopes are enforced explicitly at the API level