# Why Authentication Uses Headers (Cookies / Bearer Tokens) Instead of Body or URL Params<br/>

## 0. Background — what HttpServletRequest and HttpServletResponse actually are<br/>

In Spring Boot, every incoming HTTP call is handled by an embedded server (Tomcat by default).<br/>
For each request that arrives, Tomcat creates two objects and hands them to Spring: an `HttpServletRequest`<br/>
and an `HttpServletResponse`. These two objects together represent the **entire** HTTP exchange —<br/>
nothing about the request or the response exists outside of them.<br/>

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

## 1. HTTP is "stateless" by nature — you need a mechanism to link requests together<br/>

Every HTTP request is independent. The server has no natural way of knowing that two separate requests came from the same person.<br/>
This is what "stateless" means: no built-in memory between calls.<br/>

To solve this, you need an identifier that travels **automatically** from one request to the next.<br/>
This is exactly the role of **headers** (whether it's the `Cookie` header or the `Authorization` header) — they were designed<br/>
in the HTTP spec specifically for this purpose: carrying metadata *about* the request.<br/>

The **body**, by contrast, is designed to carry the actual **business data**<br/>
of the request (email, password, form fields, JSON payloads, etc.) — not session metadata.<br/>
Mixing the two would blur the responsibility of each part of the request.<br/>

## 2. The body doesn't even work on every request type<br/>

A `GET` request (e.g. `GET /api/v1/posts`) has **no body**, by HTTP convention.<br/>
It's technically possible to attach one, but it's strongly discouraged and often silently stripped or ignored by servers,<br/>
proxies, and load balancers along the way.<br/>

If your token had to travel inside the body, you simply **couldn't authenticate any GET request** —<br/>
and since GET requests make up the majority of a typical API (fetching data, listing resources, etc.),<br/>
most of your API would become impossible to protect.<br/>

## 3. URL parameters are visible and logged everywhere<br/>

If you put a token in a URL parameter (`?token=xxx`), it creates several concrete leaks:<br/>

- It appears in **server logs** (Nginx, Apache, and most reverse proxies log full URLs by default).<br/>
- It appears in the **browser history**.<br/>
- It can leak via the `Referer` header if the user clicks an outbound link from your page — the full URL,<br/>
  token included, gets sent to the external site.<br/>
- It's visible on screen, in screenshots, and in shared/copy-pasted URLs.<br/>

This is a real, well-known security anti-pattern — never recommended for any kind of sensitive token or credential.<br/>

## 4. Cookies: automatic handling by the browser (a huge advantage)<br/>

As covered earlier, the browser manages sending the cookie **by itself**, on every request to the matching domain,<br/>
without your JavaScript code needing to do anything.<br/>

With a token in the body or URL params instead, **you (the frontend developer)** would have to:<br/>

- Manually retrieve the token from the login response,<br/>
- Store it somewhere (localStorage/sessionStorage),<br/>
- Manually attach it to every single request.<br/>

More code to write, and more room for mistakes (forgetting to attach it on one endpoint, for example).<br/>

## 5. Cookies: `HttpOnly` = protection against XSS<br/>

A cookie marked `HttpOnly` is **completely invisible to JavaScript** — even if malicious JS runs<br/>
on your page (an XSS vulnerability), that script cannot read the cookie to steal it.<br/>

A token stored in the body and then kept in `localStorage`, on the other hand, **is** accessible via JavaScript —<br/>
meaning any XSS vulnerability on your site would let an attacker steal the token directly.<br/>

## 6. So why does `Authorization: Bearer` exist at all, if cookies are "better"?<br/>

Because cookies also have a real limitation: they are **automatically scoped to a domain** by the browser.<br/>
This is convenient for a classic web app, but becomes a problem for:<br/>

- **Non-browser clients that don't behave like a browser** — most mobile apps and server-to-server calls don't have<br/>
  an automatic cookie jar the way a browser (or a tool like Postman, which *does* emulate one) does.<br/>
  Bearer tokens don't rely on any of that — the client just attaches the header itself, explicitly, every time.<br/>
- **Architectures where multiple domains/services** need to verify the same token independently (e.g. microservices) —<br/>
  the `Authorization` header is more universal and explicit, and doesn't depend on the browser's cookie/domain mechanism at all.<br/>

## Summary<br/>

- `HttpServletRequest`/`HttpServletResponse` are the raw, complete objects behind every request/response cycle —<br/>
  headers, body, cookies, status, all of it. Spring's shortcuts (`@RequestBody`, `ResponseEntity`) just wrap them.<br/>
- The **body/URL params** are meant to carry the **data of the request itself**, not credentials that need<br/>
  to automatically persist across many different requests — including `GET` requests that have no body at all.<br/>
- **Headers** (`Cookie` or `Authorization`) exist specifically in the HTTP spec to carry this kind<br/>
  of "metadata that accompanies every request."<br/>
- **Cookies** add two extra benefits on top of that: automatic browser handling,<br/>
  and `HttpOnly` protection against XSS.<br/>
- **Bearer tokens** trade away those two browser-specific conveniences in exchange for being<br/>
  usable by any kind of client, browser or not.<br/>