# Handling User Deletion in Relationships (User ↔ Post)

When a `User` is deleted, related entities (`Post`, `Comment`,<br/>
`Order`...) still reference them via a foreign key (`author_id`).<br/>
Deleting the `User` naively causes a **foreign key constraint<br/>
violation**, because the database refuses to delete a row that<br/>
other rows still point to.

There are three common strategies to solve this, each with<br/>
different trade-offs.

---

## 1. Hard Delete + Cascade (`CascadeType.ALL`) — deletes everything

The `User` row is physically removed from the database, and<br/>
Hibernate cascades the deletion to every related `Post` as well.<br/>
Nothing is kept — this is a true hard delete on both sides.

```java
// User side — parent
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<Post> posts = new ArrayList<>();
```

```java
// Post side — child (plain FK, not nullable)
@ManyToOne
@JsonIgnore
@JoinColumn(name = "author_id", nullable = false)
private User author;
```

`CascadeType.ALL` includes `CascadeType.REMOVE`, which is an<br/>
**application-level** instruction: when `User` is deleted through<br/>
JPA, Hibernate also issues a `DELETE` for every related `Post`.<br/>
Both the `User` row and all of their `Post` rows disappear<br/>
permanently.

```java
@Transactional
public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
    // Hibernate also deletes every Post belonging to this user
}
```

**Pros**
- Simple, no extra configuration on the database side.
- Guarantees no orphaned or ownerless data ever exists.

**Cons**
- Content is lost forever — including content other users may<br/>
  still want to see (e.g. a post with comments from other people).
- Not reversible; no way to recover the deleted posts afterwards.

**Best for:** relationships where the child data has no meaning<br/>
without its parent, and should never outlive it (e.g. a `User`'s<br/>
private draft notes, session data, or personal-only records).

---

## 2. Hard Delete + `ON DELETE SET NULL` — keeps the posts

The `User` row is still physically removed (hard delete), but the<br/>
`Post` rows are **kept**, with `author_id` automatically set to<br/>
`null` by the database itself.

```java
// User side — parent, NO remove cascade
@OneToMany(mappedBy = "author")
@JsonIgnore
private List<Post> posts = new ArrayList<>();
```

```java
// Post side — child, nullable FK with a DB-level rule
@ManyToOne
@JsonIgnore
@JoinColumn(name = "author_id", nullable = true, foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL"
))
private User author;
```

This only works because the `User` side does **not** cascade<br/>
`REMOVE` to `posts`. Without that cascade, Hibernate never issues<br/>
a `DELETE` for the related `Post` rows on its own — the database's<br/>
`ON DELETE SET NULL` constraint is the only thing that runs,<br/>
setting `author_id` to `null` automatically.

```java
@Transactional
public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
    // author_id is set to null automatically by the DB;
    // the Post rows are kept
}
```

> If your code ever does `user.getPosts().add(newPost)` followed<br/>
> by `userRepository.save(user)` and expects the new `Post` to be<br/>
> persisted automatically through that collection, add<br/>
> `cascade = {CascadeType.PERSIST, CascadeType.MERGE}` — just<br/>
> never `REMOVE`, or this strategy breaks.

**Display fallback** (since `author` can now be `null`):

```java
String authorName = post.getAuthor() != null
        ? post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName()
        : "Deleted user";
```

**Pros**
- The `User` is truly gone from the database (real hard delete).
- Posts are preserved, no data loss on the content side.
- Handled entirely by the database — no manual cleanup logic needed.

**Cons**
- The original author's identity is lost forever. Every deleted<br/>
  user's post shows the same generic "Deleted user" label — there's<br/>
  no way to tell two different deleted authors apart.
- Any personal data tied to the `User` (email, name) is gone<br/>
  immediately — not suitable if you need to retain identity for<br/>
  legal, audit, or moderation reasons.
- Relies on the `User` side never declaring a `REMOVE` cascade —<br/>
  easy to accidentally break if that cascade is added later for<br/>
  an unrelated reason.

**Best for:** relationships where knowing *who exactly* the<br/>
deleted author was has no business value (e.g. social posts,<br/>
comments) and identity loss is acceptable.

---

## 3. Soft Delete (`@SQLDelete` + `@SQLRestriction`) — nothing is ever hard-deleted

Instead of deleting the `User` row, mark it as deleted and<br/>
anonymize its personal data. The row physically stays in the<br/>
database, so the foreign key is **never** broken — `author` is<br/>
never `null`, and no hard delete happens at all.

```java
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), status = 'DELETED', " +
        "email = CONCAT('deleted_', id, '_', email) WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime deletedAt;
    // ...
}
```

How it works:
- `@SQLDelete` intercepts every `userRepository.delete(user)`<br/>
  call and rewrites it into an `UPDATE` instead of a `DELETE`.<br/>
  The row is never physically removed — this is a soft delete,<br/>
  not a hard delete.
- `@SQLRestriction("deleted_at IS NULL")` is automatically<br/>
  appended to every JPA query on `User`, so deleted users<br/>
  become invisible to the application without actually being<br/>
  gone from the table.
- The email is rewritten (e.g. `deleted_42_jean@mail.com`)<br/>
  so the original email becomes available again — a new account<br/>
  can reuse `jean@mail.com` without violating the `unique`<br/>
  constraint.

**Pros**
- Foreign keys are never broken — `Post.author` always resolves<br/>
  to a real row, no null checks needed anywhere.
- Original identity (name, avatar, relationships) is preserved<br/>
  for internal/admin use, while personal data (email) is<br/>
  anonymized for privacy compliance.
- Emails (and any other unique fields you anonymize the same<br/>
  way) become reusable for new signups.
- Fully reversible — the row can technically be restored by<br/>
  clearing `deleted_at`.

**Cons**
- Deleted users still occupy a row forever — the table grows<br/>
  and never truly clears out (no real hard delete ever happens).
- Every unique/PII column that should be reusable after deletion<br/>
  (e.g. `phoneNumber`) must be explicitly anonymized in the<br/>
  `@SQLDelete` statement, or it will still block a new signup<br/>
  with the same value.

**Best for:** `User` in general, when you want deletion to be<br/>
reversible/auditable and never worry about foreign keys breaking<br/>
anywhere in the system — the most common approach in production<br/>
SaaS products.

---

## 4. Snapshot Columns (Hard Delete + `ON DELETE SET NULL` + snapshot)

Store a copy of the relevant author information directly on<br/>
`Post` at creation time, independent of the live `User` row.<br/>
Combine this with the hard-delete `ON DELETE SET NULL` setup<br/>
from strategy 2, so the FK is safely nulled but the original<br/>
identity is still available on each post individually.

```java
@Entity
public class Post extends AuditableEntity {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "author_id", nullable = true, foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL"
    ))
    private User author;

    // Frozen at creation time, never updated afterwards
    @Column(nullable = false, updatable = false)
    private String authorNameSnapshot;
}
```

```java
public Post toEntity(CreateRequest request, User author) {
    return Post.builder()
            .title(request.title())
            .author(author)
            .authorNameSnapshot(author.getFirstName() + " " + author.getLastName())
            .build();
}
```

**Display fallback**, now distinguishing each deleted author<br/>
individually:

```java
String authorName = post.getAuthor() != null
        ? post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName()
        : post.getAuthorNameSnapshot() + " (deleted account)";
```

**Pros**
- The `User` is truly gone from the database (real hard delete),<br/>
  yet original identity is preserved per-record — unlike strategy<br/>
  2 alone, two different deleted authors remain distinguishable<br/>
  ("Jean (deleted account)" vs "Marie (deleted account)").
- No need to keep the `User` row around like in soft delete —<br/>
  supports a genuine hard delete.
- Standard pattern for records that must stay legally/historically<br/>
  accurate regardless of what happens to the source entity later<br/>
  (e.g. `Order.customerNameSnapshot`, `shippingAddressSnapshot`<br/>
  at the time of purchase).

**Cons**
- Extra columns to maintain, filled once at creation and never<br/>
  updated — if the source data changes, the snapshot won't<br/>
  reflect it (this is intentional, but must be understood).
- Only captures the fields you explicitly snapshot; anything not<br/>
  copied is lost once the `User` row is gone.

**Best for:** records with legal, financial, or historical value<br/>
that must remain accurate exactly as they were at creation time<br/>
— most notably `Order`/`Commande`, where the customer's name,<br/>
email, and shipping address must stay frozen regardless of later<br/>
account changes or deletion.

---

## Summary

| Strategy | Deletion type | User row after | `author` field | Identity preserved | Typical use case |
|---|---|---|---|---|---|
| Cascade `ALL` | Hard delete | Deleted | Deleted with it | No (content gone) | Data with no meaning without its parent |
| `SET NULL` | Hard delete | Deleted | `null` | Lost (generic fallback only) | Social content (posts, comments) with no identity requirement |
| Soft delete | Never hard-deleted | Kept (anonymized) | Always resolves | Fully preserved (name, avatar, etc.) | `User` entity in general — safest default |
| Snapshot + `SET NULL` | Hard delete | Deleted | `null` | Preserved per-record via snapshot | Legally/financially significant records (`Order`) |