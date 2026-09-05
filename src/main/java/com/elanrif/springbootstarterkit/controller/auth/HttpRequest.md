# Why Authentication Uses Headers (Cookies / Bearer Tokens) Instead of Body or URL Params<br/>

## 1. Background — What `HttpServletRequest` and `HttpServletResponse` Actually Are<br/>

In Spring Boot, every incoming HTTP call is handled by an embedded server (Tomcat by default).<br/>
For each request that arrives, Tomcat creates two objects and hands them to Spring: an `HttpServletRequest`<br/>
and an `HttpServletResponse`. These two objects together represent the **entire HTTP exchange** —<br/>
nothing about the request or the response exists outside of them.<br/>

```mermaid
flowchart LR
  A[Client] -- "HttpServletRequest\n(method, headers, cookies, body)" --> B[Tomcat + Spring Boot]
  B -- "HttpServletResponse\n(status, headers, Set-Cookie, body)" --> A
```

<br/>

`HttpServletRequest` represents everything that came **in** from the client: the HTTP method (GET, POST...),<br/>
the URL and its query params, all the request headers (including the `Cookie` header), and the request body<br/>
(the raw JSON, form data, etc.). It is mostly used for **reading** — you pull information out of it.<br/>

`HttpServletResponse` represents everything that will go **out** back to the client: the status code (200, 404...),<br/>
the response headers (including `Set-Cookie`), and the response body. It is mostly used for **writing** —<br/>
you push information into it before it's sent.<br/>

In practice, Spring gives you higher-level tools that read/write these objects for you, so you rarely touch<br/>
them directly: `@RequestBody` reads the body out of `HttpServletRequest`, `@RequestParam`/`@PathVariable` read<br/>
query/path values out of it, and `ResponseEntity` writes the status/body into `HttpServletResponse`. You only<br/>
reach for the raw objects yourself when you need something these shortcuts don't cover — like manually creating<br/>
a session or setting a cookie, as seen in the `login` method earlier.<br/>

## 2. HTTP Is "Stateless" by Nature — You Need a Mechanism to Link Requests Together<br/>

Every HTTP request is independent. The server has no natural way of knowing that two separate requests came from the same person.<br/>
This is what "stateless" means: no built-in memory between calls.<br/>

```mermaid
sequenceDiagram
  participant C as Client
  participant S as Server

  C->>S: Request 1
  Note over S: No memory of anything before this
  S-->>C: Response 1

  C->>S: Request 2
  Note over S: Treated as a brand new, unrelated request
  S-->>C: Response 2
```

<br/>

To solve this, you need an identifier that travels **automatically** from one request to the next.<br/>
This is exactly the role of **headers** (whether it's the `Cookie` header or the `Authorization` header) — they were designed<br/>
in the HTTP spec specifically for this purpose: carrying metadata *about* the request.<br/>

The **body**, by contrast, is designed to carry the actual **business data**<br/>
of the request (email, password, form fields, JSON payloads, etc.) — not session metadata.<br/>
Mixing the two would blur the responsibility of each part of the request.<br/>

## 3. Cookies — Automatic Handling by the Browser<br/>

The browser manages sending the cookie **by itself**, on every request to the matching domain,<br/>
without your JavaScript code needing to do anything.<br/>

With a token in the body or URL params instead, **you (the frontend developer)** would have to:<br/>

* Manually retrieve the token from the login response,
* Store it somewhere (localStorage/sessionStorage),
* Manually attach it to every single request.

More code to write, and more room for mistakes (forgetting to attach it on one endpoint, for example).<br/>

### `HttpOnly` — Protection Against XSS<br/>

A cookie marked `HttpOnly` is **completely invisible to JavaScript** — even if malicious JS runs<br/>
on your page (an XSS vulnerability), that script cannot read the cookie to steal it.<br/>

A token stored in the body and then kept in `localStorage`, on the other hand, **is** accessible via JavaScript —<br/>
meaning any XSS vulnerability on your site would let an attacker steal the token directly.<br/>

```mermaid
flowchart TD
  subgraph Cookie["Cookie"]
    C1[Browser sends it automatically]
    C2[HttpOnly blocks JS access]
    C3[Tied to one domain]
  end
```

## 4. So Why Does `Authorization: Bearer` Exist at All?<br/>

Because cookies also have a real limitation: they are **automatically scoped to a domain** by the browser.<br/>
This is convenient for a classic web app, but becomes a problem for:<br/>

* **Non-browser clients that don't behave like a browser** — most mobile apps and server-to-server calls don't have<br/>
  an automatic cookie jar the way a browser (or a tool like Postman, which *does* emulate one) does.<br/>
  Bearer tokens don't rely on any of that — the client just attaches the header itself, explicitly, every time.<br/>
* **Architectures where multiple domains/services** need to verify the same token independently (e.g. microservices) —<br/>
  the `Authorization` header is more universal and explicit, and doesn't depend on the browser's cookie/domain mechanism at all.<br/>

```mermaid
flowchart TD
  subgraph Cookie["Cookie"]
    C1[Browser sends it automatically]
    C2[HttpOnly blocks JS access]
    C3[Tied to one domain]
  end

  subgraph Bearer["Authorization: Bearer"]
    B1[Client must attach it manually]
    B2[Readable by client code]
    B3[Works for any client, any domain]
  end
```

## 5. Why Can't Email + Password Alone Identify the Client on Every Request?<br/>

Email and password **do** identify the person — but only at the exact moment they're checked, during `login`.<br/>
Once that HTTP request ends, the server forgets everything, since HTTP is stateless (see section 2).<br/>

In theory you could resend email + password on every single request (this exists, it's called HTTP Basic Auth),<br/>
but it's a bad idea in practice, for three reasons:<br/>

* The password would travel over the network on **every** request, not just once, multiplying the risk of interception.
* Password hashing (bcrypt/argon2) is **intentionally slow** for security — recomputing it on every `GET` request<br/>
  would be a serious performance problem.
* The client would have to keep the raw password in memory at all times to keep resending it — a bad security practice.

The fix: verify identity **once**, then hand out a lightweight, disposable "ticket" (the session cookie, or a JWT)<br/>
that's cheap to check and easy to revoke, instead of repeating a costly, sensitive check on every request.<br/>

Think of it like badging into a company office, say Google: at reception, you show your ID card **once**,<br/>
and they hand you a visitor/employee badge. For the rest of the day, security only scans your badge at each door —<br/>
they don't ask for your ID card again every time you walk through a gate. The badge is weaker proof than the ID card,<br/>
but far cheaper to check, and it can be deactivated instantly at the end of the day without touching your actual ID.<br/>

```mermaid
sequenceDiagram
  participant You as You
  participant R as Reception
  participant D as Door sensor

  You->>R: Show ID card (once)
  R-->>You: Hand you a badge

  You->>D: Scan badge
  D-->>You: Access granted

  You->>D: Scan badge (later, another door)
  D-->>You: Access granted (no ID re-check)
```

<br/>

## 6. Important Nuance — Basic Auth *Does* Identify the Client, at a Cost<br/>

A common misunderstanding: Basic Auth doesn't fail to identify who's making the request — it actually resends<br/>
`email:password` (Base64-encoded) in the `Authorization` header on **every single request**, so the server<br/>
re-decodes and re-verifies the account each time. The client **is** identified, every time.<br/>

The real problem isn't identification — it's the **cost and risk** of how it's done: the password travels on the wire<br/>
constantly, and the expensive hash check runs on every call instead of once.<br/>

## 7. But What If Two Different Browsers Use the Exact Same Credentials?<br/>

Here's the important nuance: **this limitation applies equally to Basic Auth and to session cookies** — neither one<br/>
can tell two browsers apart *based on the account alone*, because it's the same account.<br/>

With Basic Auth, Client A and Client B would send the **exact same** `Authorization: Basic ...` value on every<br/>
request, since it's derived directly from the same email + password. The server has no way to know it's two<br/>
different browsers — it just sees "someone who knows this password," repeatedly.<br/>

```mermaid
flowchart TD
  A[Client A - same email/password] -->|Authorization: Basic xxxxx| S[Server]
  B[Client B - same email/password] -->|Authorization: Basic xxxxx| S
  S --> R[Server sees the identical header twice - cannot tell them apart]
```

<br/>

With sessions, the difference appears **after login**: even though both clients typed the same email/password,<br/>
each `login` call creates a brand-new, independently generated session ID. From that point on, the server is no<br/>
longer comparing "the account" — it's comparing "this exact session," which is unique per login.<br/>

```mermaid
flowchart TD
  A[Client A logs in] -->|Set-Cookie: JSESSIONID=AAA111| SA[Session AAA111]
  B[Client B logs in - same email/password] -->|Set-Cookie: JSESSIONID=BBB222| SB[Session BBB222]
  SA --> S[Session store]
  SB --> S
  S --> R[Server can now tell them apart - two distinct sessions]
```

<br/>

|                                | Basic Auth                                     | Session / Cookie                             |
| ------------------------------ | ---------------------------------------------- | -------------------------------------------- |
| What's sent on every request   | The **account** (email + password)             | A **ticket for this one login** (session ID) |
| Two logins, same account       | Identical value both times — indistinguishable | Two different IDs — distinguishable          |
| What the server actually knows | "Someone who knows this password"              | "Precisely this browser's open session"      |

<br/>

So the takeaway: identifying "the account" and identifying "this specific connected device" are two different<br/>
problems. Basic Auth only ever solves the first one. Sessions solve the second one too, simply because a new,<br/>
random ticket is minted at every login, regardless of whether the credentials behind it are shared.<br/>

## Summary<br/>

* `HttpServletRequest`/`HttpServletResponse` are the raw, complete objects behind every request/response cycle —<br/>
  headers, body, cookies, status, all of it. Spring's shortcuts (`@RequestBody`, `ResponseEntity`) just wrap them.
* The **body** is meant to carry the **data of the request itself**, not credentials that need to automatically persist<br/>
  across many different requests.
* **Headers** (`Cookie` or `Authorization`) provide a standard place to carry authentication-related metadata with requests.
* **Cookies** add two extra benefits on top of that: automatic browser handling,<br/>
  and `HttpOnly` protection against XSS.
* **Bearer tokens** trade away those two browser-specific conveniences in exchange for being<br/>
  usable by any kind of client, browser or not.
* Email/password only prove identity **once**, at login — sessions/tokens are what let that identity<br/>
  persist cheaply and safely across many separate, stateless requests.
* Basic Auth **does** identify the client on every request, just expensively and riskily — its real<br/>
  weakness is cost and exposure, not lack of identification.
* Neither Basic Auth nor sessions can distinguish two clients sharing the *same* credentials by account<br/>
  alone — sessions solve this only because each login mints its own independent, random ticket.
