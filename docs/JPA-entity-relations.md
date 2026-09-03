# Understanding JPA Relationships & Lombok `@Builder.Default`

This document explains the annotations used on `User` ↔ `Post` /
`Comment` / `Address` relationships, and why `@Builder.Default`
is required alongside `@Builder`.

---

## 1. `@OneToMany` — the inverse (non-owning) side

```java
@Builder.Default
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<Post> posts = new ArrayList<>();
```

### `mappedBy = "author"`

In a bidirectional relationship, only **one side** physically
owns the foreign key column in the database. The other side is
just a convenience for navigating the relationship in Java code.

- `mappedBy = "author"` tells JPA: *"the foreign key for this
  relationship is not here — go look at the `author` field on
  the `Post` entity instead."*
- The side with `mappedBy` is called the **inverse side**. It
  does not generate any column.
- The side referenced by `mappedBy` (here, `Post.author`) is
  called the **owning side**. It contains the actual
  `author_id` column.

### `LAZY` by default

`@OneToMany` is `LAZY` by default in JPA — the list of `Post`
is **not** fetched from the database until you explicitly call
`user.getPosts()`. This avoids loading potentially thousands of
rows just to fetch a `User`.

### `cascade = CascadeType.ALL`

Propagates persistence operations (`PERSIST`, `MERGE`,
`REMOVE`, `REFRESH`, `DETACH`) from `User` to its `Post`
collection. In practice here, it means: **deleting the `User`
also deletes all of their `Post` rows** — because `REMOVE` is
included in `ALL`.

### `orphanRemoval = true`

If a `Post` is removed from the `user.getPosts()` list (even
without deleting the `User` itself), JPA automatically deletes
that `Post` row from the database. This only makes sense when
the child (`Post`) has no meaningful existence without its
parent (`User`).

### `@JsonIgnore`

Prevents Jackson from serializing `posts` when a `User` is
converted to JSON. Without it, serializing a `User` would try
to serialize each `Post`, which in turn references its `author`
(`User` again) → infinite recursion → `StackOverflowError`.
DTOs are used instead to control exactly what gets exposed in
API responses.

---

## 2. Same pattern, different cascade behavior — `comments`

```java
// Deleting the User also deletes their Comments,
// without affecting the Posts they commented on.
@Builder.Default
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<Comment> comments = new ArrayList<>();
```

This is structurally identical to `posts`: `mappedBy = "author"`
means `Comment` owns the foreign key (`Comment.author`), and
`cascade = ALL` + `orphanRemoval = true` mean deleting a `User`
deletes all of their `Comment` rows.

The comment above the field is a **business rule clarification**,
not a technical difference: deleting a `User`'s comments does
**not** cascade further to the `Post` entities those comments
were attached to — cascading only flows in the direction the
`@OneToMany` is declared (`User → Comment`), never sideways to
an unrelated entity (`Comment → Post`).

---

## 3. `@ManyToOne` — the owning side

```java
// @ManyToOne is EAGER by default; forced to LAZY here to avoid
// loading the full User for every Post fetched.
// LAZY controls database fetching only, not JSON recursion.
// @JsonIgnore prevents recursive serialization with Jackson.
@ManyToOne(fetch = FetchType.LAZY)
@JsonIgnore
@JoinColumn(name = "author_id", nullable = true, foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL"
))
private User author;
```

This is the **owning side** of the relationship — the one that<br/>
actually has the foreign key column (`author_id`) in its table.

- **`@ManyToOne` is `EAGER` by default** (unlike `@OneToMany`,<br/>
  which is `LAZY` by default) — this is a common JPA gotcha, and<br/>
  it must always be overridden explicitly with<br/>
  `fetch = FetchType.LAZY`.
- **`@JoinColumn(name = "author_id")`** declares the actual<br/>
  foreign key column name in the `posts` table.
- **`nullable = true`** allows `author_id` to become `null`.
- **`foreignKeyDefinition = "... ON DELETE SET NULL"`**<br/>
  instructs the database itself to automatically set<br/>
  `author_id` to `null` when the referenced `User` row is<br/>
  deleted — instead of blocking the deletion with a constraint<br/>
  violation, or requiring the cascade to also delete the `Post`.
- **`@JsonIgnore`** here prevents the reverse recursion problem:<br/>
  serializing a `Post` would otherwise try to serialize its<br/>
  `author` (`User`), which in turn tries to serialize its<br/>
  `posts` list again → infinite loop.

### Why `FetchType.LAZY` matters here — the N+1 problem

Without `fetch = FetchType.LAZY`, every time a `Post` is loaded<br/>
(even a single one via `postRepository.findById()`), Hibernate<br/>
**immediately and automatically** loads the full `User` object<br/>
associated with it — even if `post.getAuthor()` is never called.

This becomes a real performance problem at scale. Imagine loading<br/>
3000 posts via `postRepository.findAll()`: with `EAGER` fetching,<br/>
Hibernate issues **3000 additional SQL queries**, one per post, to<br/>
fetch each author individually — this is known as the **N+1 query<br/>
problem**, a classic and costly JPA pitfall.

With `fetch = FetchType.LAZY`, `post.getAuthor()` returns a<br/>
**Hibernate proxy** instead of the real object — no SQL query is<br/>
sent to `users` until a method is actually called on that proxy<br/>
(e.g. `post.getAuthor().getFirstName()`). If a DTO mapping never<br/>
touches `author` at all, **zero extra queries** are executed.

> Note: `@OneToMany` (used for `posts` and `comments` on `User`)<br/>
> is already `LAZY` by default — no override is needed there. The<br/>
> gotcha only applies to `@ManyToOne` and `@OneToOne`, which<br/>
> default to `EAGER` in the JPA specification.

---

## 5. When a mapper touches a `LAZY` collection: `LazyInitializationException`

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(
          target = "numberOfAddresses",
          expression = "java(user.getAddresses() != null ? user.getAddresses().size() : 0)"
  )
  UserDto.Response toDto(User user);
}
```

This `expression` calls `user.getAddresses().size()` — and<br/>
`addresses` is a `LAZY` `@OneToMany` collection. Calling `.size()`<br/>
on a lazy collection forces Hibernate to go fetch it from the<br/>
database at that exact moment. This only works if a **Hibernate<br/>
session is still open** when the mapper runs.

### The problem: the session is usually already closed

In a typical Spring Boot flow, the repository call and the mapper<br/>
call happen in the **same service method**, so this looks fine at<br/>
first:

```java
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto.Response getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow();
    return userMapper.toDto(user); // accesses user.getAddresses() here
  }
}
```

But by default, Spring Data repository methods run in their own<br/>
short-lived transaction that **ends as soon as `findById` returns**.<br/>
Once that transaction commits, the Hibernate session closes and<br/>
`user` becomes a **detached** entity. If `addresses` was never<br/>
loaded before that point, calling `user.getAddresses().size()`<br/>
afterwards throws:

```
org.hibernate.LazyInitializationException:
failed to lazily initialize a collection of role:
com.example.User.addresses, could not initialize proxy - no Session
```

There are **two ways** to fix this, depending on where you choose<br/>
to act: in the **service** (by keeping the session open longer),<br/>
or in the **repository** (by explicitly loading the collection at<br/>
the initial query, never depending on the session at all).

---

### Option 1 — Keep the session open: `@Transactional` on the service

```java
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public UserDto.Response getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow();
    return userMapper.toDto(user); // OK: session still open here
  }
}
```

- By placing `@Transactional` on the **service** method (rather<br/>
  than only on the repository method), the transaction — and<br/>
  therefore the associated Hibernate session — stays open for<br/>
  **the entire execution of the method**, including during the<br/>
  call to `userMapper.toDto(user)`.
- As a result, when the mapper calls `user.getAddresses().size()`,<br/>
  Hibernate can still fire an extra SQL query on the fly to load<br/>
  the collection, because the session is still active.
- `readOnly = true` is a recommended optimization here: since the<br/>
  method only reads data (no `INSERT`, `UPDATE`, or `DELETE`),<br/>
  Hibernate can disable dirty checking and some other checks,<br/>
  slightly reducing overhead.
- **Downside**: this approach generates an **extra SQL query fired<br/>
  at an unpredictable point in time** (in the middle of mapping),<br/>
  which can reintroduce an N+1-style problem if several lazy<br/>
  collections are touched for multiple entities in a loop (e.g.<br/>
  when mapping a list of `User`).
- It's a simple, quick fix to put in place, but it shifts the<br/>
  problem rather than solving it at the source: the query stays<br/>
  implicit and the number of SQL queries executed isn't tightly<br/>
  controlled.

---

### Option 2 — Load explicitly in the repository: `@Query` (`JOIN FETCH`) or `@EntityGraph`

Rather than relying on an open session, you can ask Hibernate to<br/>
load the `addresses` collection **at the initial query**, in a<br/>
single SQL statement, independent of any transaction opened later.

#### 2.a — `@Query` with `JOIN FETCH`

```java
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.id = :id")
  Optional<User> findByIdWithAddresses(@Param("id") Long id);
}
```

- `JOIN FETCH` explicitly tells JPQL to load the `addresses`<br/>
  collection in the **same SQL query** as the `User` entity (via<br/>
  a database `JOIN`), instead of leaving it `LAZY` and firing a<br/>
  second query later.
- `LEFT JOIN FETCH` (rather than a plain `JOIN FETCH`) guarantees<br/>
  the `User` is returned **even if they have no addresses** — an<br/>
  `INNER JOIN` would exclude those users from the result.
- Once this method is used instead of `findById`,<br/>
  `user.getAddresses()` is **already initialized** by the time the<br/>
  mapper calls it, whether the session is still open or not: no<br/>
  `LazyInitializationException` is possible.

```java
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserDto.Response getUser(Long id) {
    User user = userRepository.findByIdWithAddresses(id).orElseThrow();
    return userMapper.toDto(user); // OK: addresses already loaded, no @Transactional needed
  }
}
```

#### 2.b — `@EntityGraph`

```java
public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = "addresses")
  Optional<User> findById(Long id);
}
```

- `@EntityGraph` achieves the same result as a `JOIN FETCH`<br/>
  (loading `addresses` in a single query), but in a<br/>
  **declarative** way, without hand-writing JPQL.
- `attributePaths = "addresses"` specifies which association(s)<br/>
  should be eagerly loaded for **this particular query only** —<br/>
  it does not change the default `LAZY` behavior defined on the<br/>
  `Address` entity itself, which avoids reintroducing a global<br/>
  `EAGER` problem like the one seen in section 3.
- Handy when you want to override the fetch behavior of an<br/>
  existing Spring Data method (like `findById` here) without<br/>
  writing a full `@Query`.
- For multiple associations, you can pass an array:<br/>
  `@EntityGraph(attributePaths = {"addresses", "posts"})`.

#### Comparing the two sub-options

| | `@Query` + `JOIN FETCH` | `@EntityGraph` |
|---|---|---|
| Control | Full (hand-written JPQL) | Declarative, less verbose |
| Readability for complex queries | More flexible (conditions, multiple joins) | Limited to association paths |
| Risk of error | Must remember `LEFT` to avoid excluding rows | Generates the `LEFT JOIN` automatically |
| Typical use case | Custom queries with filters | Overriding the fetch behavior of an existing derived method |

---

### Which option should you choose?

- **Option 1 (`@Transactional` on the service)** is fine for quick<br/>
  prototyping, or when the service method already touches several<br/>
  lazy associations unpredictably and you'd rather keep full<br/>
  flexibility to access the complete entity.
- **Option 2 (`@Query`/`@EntityGraph` in the repository)** is<br/>
  generally **preferable in production**: the number of SQL<br/>
  queries executed is predictable and controlled right at the<br/>
  repository layer, which avoids N+1-style surprises and makes<br/>
  the loading behavior explicit at the point where the data is<br/>
  requested, rather than hidden inside a transaction's lifetime.

The two approaches are actually **complementary**: you can safely<br/>
annotate the service method with `@Transactional(readOnly = true)`<br/>
as a safety net (to cover other unexpected lazy accesses), while<br/>
using `JOIN FETCH`/`@EntityGraph` for the associations you already<br/>
know you'll need — this minimizes the extra SQL queries fired on<br/>
the fly.