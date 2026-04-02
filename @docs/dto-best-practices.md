# DTOs Best Practices - Summary, Response & DetailResponse

## Pourquoi plusieurs DTOs de réponse ?

Le principe fondamental : **retourner uniquement ce dont le client a besoin**.

```
Liste (GET /posts)     → Données légères pour afficher des cards
Détail (GET /posts/1)  → Données complètes pour afficher une page
```

---

## Les 3 types de DTOs de réponse

### 1. Summary - Le plus léger

**Usage** : Embedded dans d'autres DTOs (ex: author d'un post)

```java
public record Summary(
    Long id,
    String firstName,
    String lastName,
    String avatarUrl
) {}
```

**Quand l'utiliser ?**
- Afficher l'auteur d'un post/commentaire
- Afficher une liste d'utilisateurs dans une dropdown
- Partout où on a besoin d'identifier quelqu'un sans ses détails

---

### 2. Response - Standard pour les listes

**Usage** : `GET /posts`, `GET /comments`

```java
public record Response(
    Long id,
    String title,
    String imageUrl,
    String description,
    Long likes,
    UserDto.Summary author,    // Summary, pas Response complet
    int commentCount,          // Juste le COUNT, pas les commentaires
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

**Caractéristiques** :
- Utilise `Summary` pour les relations
- Utilise `commentCount` au lieu de `List<Comment>`
- Suffisant pour afficher une card/preview

---

### 3. DetailResponse - Complet pour le détail

**Usage** : `GET /posts/{id}`

```java
public record DetailResponse(
    Long id,
    String title,
    String imageUrl,
    String description,
    Long likes,
    UserDto.Summary author,
    List<CommentDto.Response> comments,  // Les vrais commentaires
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

**Caractéristiques** :
- Inclut les relations complètes (comments)
- Utilisé pour afficher une page de détail
- Plus lourd, mais c'est OK pour 1 seul item

---

## Tableau comparatif

| DTO | Poids | Relations | Usage |
|-----|-------|-----------|-------|
| `Summary` | Très léger | Aucune | Embedded (author, user mention) |
| `Response` | Léger | Summary + counts | Listes (`GET /resources`) |
| `DetailResponse` | Complet | Objets complets | Détail (`GET /resources/{id}`) |

---

## Exemple concret : User

```java
public final class UserDto {

    // Summary léger pour embedded dans d'autres DTOs (ex: author d'un Post/Comment)
    public record Summary(
        Long id,
        String firstName,
        String lastName,
        String avatarUrl,
        String email
    ) {}

    // Pour GET /users/{id}
    public record Response(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarUrl,
        UserRole role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}
```

### Comparaison JSON

**Summary (5 champs, ~150 bytes)** :
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "avatarUrl": "https://...",
  "email": "john@example.com"
}
```

**Response (10 champs, ~300 bytes)** - Détail complet :
```json
{
  "id": 1,
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+33612345678",
  "avatarUrl": "https://...",
  "role": "USER",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## Exemple concret : Post

```java
public final class PostDto {

    // Pour les listes
    public record Response(
        Long id,
        String title,
        String imageUrl,
        String description,
        Long likes,
        UserDto.Summary author,      // Léger
        int commentCount,            // Juste le nombre
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    // Pour le détail
    public record DetailResponse(
        Long id,
        String title,
        String imageUrl,
        String description,
        Long likes,
        UserDto.Summary author,
        List<CommentDto.Response> comments,  // Les vrais commentaires
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}
```

### Comparaison JSON

**Response pour liste (~200 bytes/post)** :
```json
{
  "id": 1,
  "title": "Mon article",
  "imageUrl": "https://...",
  "description": "Description...",
  "likes": 42,
  "author": {
    "id": 5,
    "firstName": "John",
    "lastName": "Doe",
    "avatarUrl": "https://..."
  },
  "commentCount": 15,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**DetailResponse (~2KB avec 10 commentaires)** :
```json
{
  "id": 1,
  "title": "Mon article",
  "imageUrl": "https://...",
  "description": "Description complète...",
  "likes": 42,
  "author": {
    "id": 5,
    "firstName": "John",
    "lastName": "Doe",
    "avatarUrl": "https://..."
  },
  "comments": [
    {
      "id": 1,
      "content": "Super article !",
      "author": { "id": 2, "firstName": "Jane", ... },
      "createdAt": "..."
    },
    // ... 9 autres commentaires
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## Le champ `commentCount`

### Pourquoi `commentCount` au lieu de `comments` ?

| Approche | Liste de 100 posts | Performance |
|----------|-------------------|-------------|
| `List<Comment> comments` | 100 posts x 10 comments = 1000 objets | Catastrophique |
| `int commentCount` | 100 posts x 1 int = 100 ints | Optimal |

### Comment le calculer ?

**Dans le Mapper** :
```java
@Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
PostDto.Response toResponse(Post post);
```

**Alternative avec @Formula (Hibernate)** :
```java
@Entity
public class Post {
    // ...

    @Formula("(SELECT COUNT(*) FROM comments c WHERE c.post_id = id)")
    private int commentCount;
}
```

---

## Mapping des DTOs

### UserMapper

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto.Summary toSummary(User user);    // Pour embedded
    UserDto.Response toResponse(User user);  // Pour détail
}
```

### PostMapper

```java
@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    PostDto.Response toResponse(Post post);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "comments", source = "comments")
    PostDto.DetailResponse toDetailResponse(Post post);
}
```

**Note** : MapStruct utilise automatiquement `UserMapper.toSummary()` pour mapper `author` vers `UserDto.Summary`.

---

## Architecture des endpoints

### Pattern recommandé

| Endpoint | Méthode Service | DTO retourné |
|----------|-----------------|--------------|
| `GET /posts` | `getPosts()` | `PostDto.Response` |
| `GET /posts/{id}` | `getPostById()` | `PostDto.DetailResponse` |
| `POST /posts` | `createPost()` | `PostDto.Response` |
| `PATCH /posts/{id}` | `updatePost()` | `PostDto.Response` |

### Service Layer

```java
@Service
public class PostService {

    // Liste - Response léger
    public PageResponse<PostDto.Response> getPosts(...) {
        return postRepository.findAll(specification, pageRequest)
            .map(postMapper::toResponse);
    }

    // Détail - DetailResponse complet
    public PostDto.DetailResponse getPostById(Long id) {
        Post post = postRepository.findByIdWithDetails(id)  // JOIN FETCH
            .orElseThrow(...);
        return postMapper.toDetailResponse(post);
    }
}
```

---

## Tableau récapitulatif par entité

### User

| DTO | Champs | Usage |
|-----|--------|-------|
| `Summary` | id, firstName, lastName, avatarUrl, email | Author de Post/Comment |
| `Response` | Tous les champs | `GET /users/{id}` |

### Post

| DTO | Champs | Usage |
|-----|--------|-------|
| `Response` | + author (Summary) + commentCount | `GET /posts` |
| `DetailResponse` | + author (Summary) + comments | `GET /posts/{id}` |

### Comment

| DTO | Champs | Usage |
|-----|--------|-------|
| `Summary` | id, content, author (Summary), createdAt | Embedded dans Post |
| `Response` | + postId, updatedAt | `GET /comments` |

---

## Avantages de cette architecture

### 1. Performance

```
100 posts avec Response     → ~20KB JSON
100 posts avec DetailResponse → ~200KB JSON (10x plus lourd)
```

### 2. Sécurité

```java
// Summary n'expose PAS les données sensibles
public record Summary(
    Long id,
    String firstName,      // OK
    String lastName,       // OK
    String avatarUrl       // OK
    // PAS d'email, phone, role, etc.
) {}
```

### 3. Flexibilité

```java
// Tu peux changer Response sans impacter DetailResponse
public record Response(...) {}        // Pour les listes
public record DetailResponse(...) {}  // Pour le détail
```

### 4. Clarté du code

```java
// Le type de retour indique clairement ce qu'on obtient
PostDto.Response       // Je sais que c'est léger
PostDto.DetailResponse // Je sais que c'est complet
```

---

## Parent vs Enfant : Summary vs ID

### Le principe fondamental

> **On AFFICHE ce qu'on possède (enfants), on RÉFÉRENCE ce qui nous possède (parent).**

| Direction | Ce qu'on met dans le DTO | Pourquoi |
|-----------|--------------------------|----------|
| **Parent → Enfant** | `Summary` ou `List<Response>` | On veut **afficher** les enfants |
| **Enfant → Parent** | `Long parentId` | On veut juste **référencer** le parent |

### Visualisation

```
┌─────────────────────────────────────────────────────┐
│                      POST (Parent)                  │
│  - id                                               │
│  - title                                            │
│  - author: UserDto.Summary  ← AFFICHER (qui a posté)│
│  - comments: List<Comment>  ← AFFICHER (les réponses)│
└─────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                  COMMENT (Enfant)                   │
│  - id                                               │
│  - content                                          │
│  - author: UserDto.Summary  ← AFFICHER (qui commente)│
│  - postId: Long             ← RÉFÉRENCER (lien retour)│
└─────────────────────────────────────────────────────┘
```

### Pourquoi cette logique ?

#### Le parent a besoin d'AFFICHER ses enfants

Quand tu es sur `/posts/1`, tu veux **voir** :
- Les infos de l'auteur (nom, avatar) → `Summary`
- Les commentaires avec leurs auteurs → `List<CommentDto.Response>`

#### L'enfant n'a PAS besoin des données du parent

Quand tu affiches un commentaire :
- Tu es **déjà** sur la page du post (tu connais ses infos)
- Ou tu veux juste un **lien** pour y aller → `postId` suffit

### Tableau récapitulatif

| Relation | Dans le DTO | Type | Raison |
|----------|-------------|------|--------|
| `Post → Author` | `author` | `UserDto.Summary` | Afficher qui a posté |
| `Post → Comments` | `comments` | `List<CommentDto.Response>` | Afficher les commentaires |
| `Comment → Author` | `author` | `UserDto.Summary` | Afficher qui a commenté |
| `Comment → Post` | `postId` | `Long` | Juste pour naviguer/référencer |

### Exemple JSON

#### Post (parent) - Affiche ses relations

```json
{
  "id": 1,
  "title": "Mon article",
  "author": {
    "id": 5,
    "firstName": "John",
    "avatarUrl": "..."
  },
  "comments": [
    {
      "id": 10,
      "content": "Super !",
      "postId": 1,
      "author": {
        "id": 8,
        "firstName": "Jane",
        "avatarUrl": "..."
      }
    }
  ]
}
```

#### Comment (enfant) - Référence son parent

```json
{
  "id": 10,
  "content": "Super !",
  "postId": 1,
  "author": {
    "id": 8,
    "firstName": "Jane",
    "avatarUrl": "..."
  }
}
```

→ Le commentaire a `postId: 1` (pas un objet Post complet) car :
- On est peut-être déjà sur la page du post
- On veut juste créer un lien "Voir le post"
- Pas besoin de dupliquer les infos du post

### Cas d'usage : Liste des commentaires d'un utilisateur

`GET /comments?authorId=8`

```json
[
  {
    "id": 10,
    "content": "Super !",
    "postId": 1,
    "author": { "id": 8, "firstName": "Jane", ... }
  },
  {
    "id": 15,
    "content": "Intéressant",
    "postId": 3,
    "author": { "id": 8, "firstName": "Jane", ... }
  }
]
```

→ Chaque commentaire a un `postId` différent pour permettre de naviguer vers le post correspondant.

### Résumé en une phrase

```
Post possède Comments   → Post AFFICHE List<Comment>
Comment appartient à Post → Comment RÉFÉRENCE postId
```

---

## Composition vs Association : Quand inclure une liste ?

### Le cas spécial de User

User n'est pas un "parent" classique. C'est une **entité de référence** (comme une table de lookup).

| Entité | Rôle | Type de relation |
|--------|------|------------------|
| `Post` | Parent de `Comment` | **Composition** - Un post contient ses commentaires |
| `User` | Référence/Auteur | **Association** - Un user est lié à des posts/comments |

### Pourquoi User n'a PAS de `List<Post>` ?

#### 1. Le contexte d'utilisation

| Action | Endpoint | Pourquoi |
|--------|----------|----------|
| Voir le profil | `GET /users/1` | Infos personnelles uniquement |
| Voir ses posts | `GET /posts?authorId=1` | Endpoint séparé, paginé |
| Voir ses comments | `GET /comments?authorId=1` | Endpoint séparé, paginé |

Tu ne veux **jamais** charger 500 posts d'un user en une seule requête.

#### 2. La cardinalité

| Relation | Cardinalité typique | Inclure dans DTO ? |
|----------|---------------------|-------------------|
| `Post → Comments` | 10-50 commentaires | Oui (gérable) |
| `User → Posts` | 10-10000 posts | Non (trop volumineux) |
| `User → Comments` | 10-50000 comments | Non (trop volumineux) |

#### 3. La navigation

```
Page Post (/posts/1)
└── Affiche les commentaires du post (10-50 max)
    └── Logique : on est sur le post, on veut voir SES commentaires

Page User (/users/1)
└── Affiche le profil
└── Pour voir ses posts : lien vers /posts?authorId=1
    └── Logique : c'est une AUTRE page, paginée
```

### Le vrai critère : Comment on consulte ?

La différence n'est pas "peut exister sans" mais plutôt **comment on consulte les données**.

#### POST + COMMENTS = Même page

```
┌─────────────────────────────┐
│  Mon Article                │
│  ─────────────────────────  │
│  Contenu du post...         │
│                             │
│  Commentaires:              │
│  ├── "Super !" - Jane       │
│  ├── "Merci" - Bob          │
│  └── "Cool" - Alice         │
└─────────────────────────────┘
→ On affiche TOUT sur une seule page
→ Donc on inclut dans le DTO
```

#### USER + POSTS = Pages séparées

```
┌─────────────────────────────┐      ┌─────────────────────────────┐
│  Profil de John             │      │  Posts de John (paginé)     │
│  ─────────────────────────  │  →   │  ─────────────────────────  │
│  Email: john@...            │      │  Page 1/50                  │
│  Avatar: ...                │      │  ├── Post 1                 │
│                             │      │  ├── Post 2                 │
│  [Voir ses posts]           │      │  └── Post 3                 │
└─────────────────────────────┘      └─────────────────────────────┘
→ On NE VEUT PAS 500 posts sur le profil
→ Donc endpoint séparé avec pagination
```

### Questions à se poser

| Question | Si OUI | Si NON |
|----------|--------|--------|
| On affiche sur la **même page** ? | Inclure dans DTO | Endpoint séparé |
| Quantité **limitée** (< 100) ? | Inclure dans DTO | Endpoint séparé |
| Toujours **consultés ensemble** ? | Inclure dans DTO | Endpoint séparé |

### Composition vs Association

```
COMPOSITION (inclure dans DTO) :
Post ◆─── Comment    "Un post EST COMPOSÉ de commentaires"
                      → On affiche ensemble (même page)
                      → Quantité limitée et prévisible
                      → Toujours consultés ensemble

ASSOCIATION (endpoint séparé) :
User ○─── Post       "Un user EST L'AUTEUR de posts"
                      → On consulte séparément (pages différentes)
                      → Quantité potentiellement illimitée
                      → Rarement consultés ensemble
```

### Tableau récapitulatif

| Relation | Type | Dans le DTO | Comment y accéder |
|----------|------|-------------|-------------------|
| `Post → Comments` | Composition | `List<Comment>` dans DetailResponse | Inclus dans le post |
| `User → Posts` | Association | Rien | `GET /posts?authorId=1` |
| `User → Comments` | Association | Rien | `GET /comments?authorId=1` |

### La règle finale

> **Inclure une liste dans un DTO si :**
> 1. On **consulte toujours ensemble** (même page)
> 2. La quantité est **prévisible et limitée** (< 100 items)
>
> **Endpoint séparé si :**
> 1. On **consulte séparément** (pages différentes)
> 2. La quantité est **potentiellement grande** (> 100 items)

---

## Règles d'or

1. **Liste** (`GET /resources`) → `Response` avec Summary + counts
2. **Détail** (`GET /resources/{id}`) → `DetailResponse` avec objets complets
3. **Relations embedded (enfants)** → `Summary` ou `List<Response>`
4. **Relations inverses (parent)** → Juste l'ID (`parentId`)
5. **Relations d'association (User → Posts)** → Endpoint séparé avec pagination
6. **Jamais d'entité dans le Controller** → Toujours des DTOs
7. **`commentCount`** plutôt que `List<Comment>` dans les listes
