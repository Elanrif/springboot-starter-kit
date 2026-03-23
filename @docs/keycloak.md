# Keycloak — Guide d'intégration complet

Ce guide couvre l'installation et la configuration de Keycloak pour le **Nextjs Starter Kit** avec le flow **ROPC (Resource Owner Password Credentials)** — l'utilisateur saisit email/password directement dans l'app Next.js, sans redirection vers Keycloak.

---

## Table des matières

1. [Démarrer Keycloak](#1-démarrer-keycloak)
2. [Créer le Realm](#2-créer-le-realm)
3. [Créer le Client (nextjs-app)](#3-créer-le-client-nextjs-app)
4. [Créer les Realm Roles](#4-créer-les-realm-roles)
5. [Configurer le rôle par défaut](#5-configurer-le-rôle-par-défaut)
6. [Ajouter des attributs custom (phoneNumber)](#6-ajouter-des-attributs-custom-phonenumber)
7. [Configurer les variables d'environnement](#7-configurer-les-variables-denvironnement)
8. [Architecture de l'intégration](#8-architecture-de-lintégration)
9. [Migration vers Authorization Code Flow](#9-migration-vers-authorization-code-flow)

---

## 1. Démarrer Keycloak

### Via Docker (recommandé)

```bash
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest \
  start-dev
```

Keycloak sera accessible sur `http://localhost:8080`.

Console d'administration : `http://localhost:8080/admin` → login `admin` / `admin`

---

## 2. Créer le Realm

Un **Realm** est un espace isolé qui regroupe utilisateurs, clients et rôles.

1. Connecte-toi à la console admin : `http://localhost:8080/admin`
2. Clique sur le dropdown **Keycloak** (en haut à gauche) → **Create realm**
3. Remplis :

| Champ      | Valeur            |
| ---------- | ----------------- |
| Realm name | `kickstart-realm` |
| Enabled    | ON                |

4. Clique **Create**

> Tu es maintenant dans le realm `kickstart-realm`. Toutes les étapes suivantes se font dans ce realm.

---

## 3. Créer le Client (nextjs-app)

Le **Client** représente ton application Next.js auprès de Keycloak.

### Step 1 — General settings

Sidebar → **Clients** → **Create client**

| Champ       | Valeur             |
| ----------- | ------------------ |
| Client type | OpenID Connect     |
| Client ID   | `kickstart-client` |
| Name        | Kickstart App      |

→ **Next**

### Step 2 — Capability config

| Option                   | Valeur                                          |
| ------------------------ | ----------------------------------------------- |
| Client authentication    | **ON** (client confidentiel → génère un secret) |
| Authorization            | OFF                                             |
| Standard flow            | ✓                                               |
| **Direct access grants** | **✓** (obligatoire pour ROPC)                   |
| Implicit flow            | ☐                                               |
| Service accounts roles   | ☐                                               |

→ **Next**

### Step 3 — Login settings

| Champ                           | Valeur                    |
| ------------------------------- | ------------------------- |
| Root URL                        | `http://localhost:3000`   |
| Home URL                        | `http://localhost:3000`   |
| Valid redirect URIs             | `http://localhost:3000/*` |
| Valid post logout redirect URIs | `http://localhost:3000/*` |
| Web origins                     | `http://localhost:3000`   |

→ **Save**

### Récupérer le Client Secret

**Clients → kickstart-client → onglet Credentials** → copier le **Client secret**

Tu en auras besoin dans les variables d'environnement (`KC_CLIENT_SECRET`).

---

## 4. Créer les Realm Roles

Les rôles `USER` et `ADMIN` sont utilisés pour déterminer les droits dans l'application.

Sidebar → **Realm roles** → **Create role**

### Créer USER

| Champ     | Valeur |
| --------- | ------ |
| Role name | `USER` |

→ **Save**

### Créer ADMIN

| Champ     | Valeur  |
| --------- | ------- |
| Role name | `ADMIN` |

→ **Save**

Tu dois voir dans la liste : `ADMIN`, `USER`, `default-roles-kickstart-realm`, `offline_access`, `uma_authorization`.

---

## 5. Configurer le rôle par défaut

Chaque nouvel utilisateur créé (via sign-up) recevra automatiquement le rôle `USER`.

1. Sidebar → **Realm settings**
2. Onglet **User registration**
3. Sous-onglet **Default roles**
4. Clique **Assign role**
5. Coche `USER`
6. Clique **Assign**

---

## 6. Ajouter des attributs custom (phoneNumber)

Par défaut, Keycloak retourne dans le token : `sub`, `email`, `given_name`, `family_name`. Pour inclure des champs custom comme `phoneNumber`, il faut configurer un **Mapper**.

### 6.1 — Ajouter le mapper sur le client

**Clients → kickstart-client → onglet Client scopes → kickstart-client-dedicated → Add mapper → By configuration → User Attribute**

| Champ                      | Valeur         |
| -------------------------- | -------------- |
| Name                       | `phoneNumber`  |
| User Attribute             | `phoneNumber`  |
| Token Claim Name           | `phone_number` |
| Claim JSON Type            | `String`       |
| Add to ID token            | **ON**         |
| Add to access token        | ON             |
| Add to userinfo            | ON             |
| Multivalued                | OFF            |
| Aggregate attribute values | OFF            |

→ **Save**

### 6.2 — Mettre à jour le service KC

Dans `src/lib/auth/keycloak/keycloak.service.ts`, ajoute `phone_number` aux claims :

```ts
type KcTokenClaims = {
  sub: string;
  email: string;
  given_name?: string;
  family_name?: string;
  phone_number?: string; // ← ajouter
  realm_access?: { roles: string[] };
};
```

Et dans `mapKcClaimsToUser` :

```ts
phoneNumber: claims.phone_number ?? "",
```

---

## 7. Configurer les variables d'environnement

### `env/.env.local`

```bash
# Keycloak
KC_URL=http://localhost:8080
KC_REALM=kickstart-realm
KC_CLIENT_ID=kickstart-client
KC_CLIENT_SECRET=<depuis Clients > kickstart-client > Credentials>
KC_ADMIN_CLIENT_ID=admin-cli
KC_ADMIN_USERNAME=admin
KC_ADMIN_PASSWORD=admin
```

### Copier vers `.env` actif

```bash
npm run env:local
```

> ⚠️ Next.js lit `.env` à la racine. Le fichier `env/.env.local` est le fichier source — `npm run env:local` le copie vers `.env`.

---

## 8. Architecture de l'intégration

```
Formulaire sign-in/sign-up
        │
        ▼
authClient (lib/auth/api/auth.client.ts)   ← façade inchangée
        │
        ▼
signInAction / signUpAction (lib/auth/actions/auth.ts)
        │
        ├── signIn  → NextAuth Credentials provider
        │                     │
        │                     ▼
        │              kcSignIn (keycloak.service.ts)
        │                     │
        │                     ▼
        │         POST /realms/{realm}/protocol/openid-connect/token
        │         grant_type=password, username, password, client_id, client_secret
        │                     │
        │                     ▼
        │         Keycloak retourne id_token (JWT)
        │         → decode claims → map vers User → stocké dans NextAuth JWT
        │
        └── signUp → kcSignUp (keycloak.service.ts)
                          │
                          ▼
                   1. GET admin token (master realm / admin-cli)
                   2. POST /admin/realms/{realm}/users
                      { email, firstName, lastName, credentials, attributes }
                   3. signIn("credentials") → crée la session NextAuth
```

### Fichiers modifiés vs inchangés

| Fichier                                     | Statut                            |
| ------------------------------------------- | --------------------------------- |
| `lib/auth/api/auth.ts`                      | ✅ Inchangé                       |
| `lib/auth/api/auth.client.ts`               | ✅ Inchangé                       |
| `components/features/auth/sign-in-form.tsx` | ✅ Inchangé                       |
| `components/features/auth/sign-up-form.tsx` | ✅ Inchangé                       |
| Layouts, pages                              | ✅ Inchangés                      |
| `lib/auth/keycloak/keycloak.config.ts`      | 🆕 Nouveau                        |
| `lib/auth/keycloak/keycloak.service.ts`     | 🆕 Nouveau                        |
| `lib/auth/next-auth/auth.ts`                | ✏️ Modifié (provider → KC)        |
| `lib/auth/next-auth/next-auth.service.ts`   | ✏️ Modifié (pas de fetch backend) |
| `lib/auth/actions/auth.ts`                  | ✏️ Modifié (signUp → kcSignUp)    |
| `lib/auth/models/auth.model.ts`             | ✏️ Modifié (Session étendu)       |

---

## 9. Migration vers Authorization Code Flow

Quand tu voudras migrer vers le flow standard (redirect vers la page login de Keycloak) :

### Avantages du Authorization Code Flow

- Pas de ROPC (deprecated dans OAuth 2.1)
- MFA, SSO, social login gérés par Keycloak automatiquement
- Plus sécurisé (le mot de passe ne transite jamais par Next.js)

### Ce qui change

**Seul `lib/auth/next-auth/auth.ts` change** — remplacer le provider `Credentials` par le provider `Keycloak` natif de NextAuth :

```ts
// Avant (ROPC)
import Credentials from "next-auth/providers/credentials";

Credentials({
  async authorize(credentials) {
    const result = await kcSignIn(credentials.email, credentials.password);
    ...
  }
})

// Après (Authorization Code Flow)
import Keycloak from "next-auth/providers/keycloak";

Keycloak({
  clientId: process.env.KC_CLIENT_ID!,
  clientSecret: process.env.KC_CLIENT_SECRET!,
  issuer: `${process.env.KC_URL}/realms/${process.env.KC_REALM}`,
})
```

**Tout le reste reste identique** : façade `auth.ts` / `auth.client.ts`, formulaires, layouts, pages.

### Côté Keycloak

Dans la Capability config du client :

- **Standard flow** : ✓ (déjà coché)
- **Direct access grants** : peut être décoché

---

## Backend Spring Boot (Resource Server)

Si tu as un backend sur le port `8081` qui valide les tokens KC :

```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/kickstart-realm
```

Spring récupère automatiquement les clés publiques KC via `/.well-known/openid-configuration` et valide chaque requête entrante. Aucune config supplémentaire dans le client Keycloak n'est nécessaire pour le backend.

Pour envoyer le token KC vers ton backend depuis Next.js, stocke l'`access_token` dans le JWT NextAuth :

```ts
// dans auth.ts — callback jwt
jwt({ token, account }) {
  if (account?.access_token) {
    token.kcAccessToken = account.access_token;
  }
  return token;
}
```

Puis dans les Server Actions / Services :

```ts
const session = await auth();
const kcToken = session?.kcAccessToken;
await fetch("http://localhost:8081/api/v1/products", {
  headers: { Authorization: `Bearer ${kcToken}` },
});
```
