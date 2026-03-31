# Clonage du Projet

## Variables

Avant de commencer, définissez les variables suivantes:

```bash
BRANCH_NAME="<branch-name>"           # ex: kit/next-auth, develop, main
REPO_URL="<repository-url>"           # ex: https://github.com/Elanrif/springboot-starter-kit.git
FOLDER_NAME="<folder-name>"           # ex: springboot-social-feedback, my-project
```

## 1. Cloner le repository

```bash
git clone -b {BRANCH_NAME} --single-branch {REPO_URL} {FOLDER_NAME}
```

### Explication des paramètres:

- **`-b {BRANCH_NAME}`** : Clone la branche spécifique au lieu de la branche par défaut (main)
- **`--single-branch`** : Télécharge **uniquement** la branche spécifiée, au lieu de télécharger tout l'historique de toutes les branches
    - **Avantage:** Réduit considérablement la taille du téléchargement et accélère le clonage
    - **Avantage:** Économise l'espace disque (surtout utile pour les gros repositories)
    - **Utilité:** Idéal quand tu n'as besoin que d'une seule branche
- **`{REPO_URL}`** : URL du repository à cloner
- **`{FOLDER_NAME}`** : Nom du dossier local où sera cloné le projet

## 2. Vérifier les remotes

Une fois le clonage terminé, accédez au dossier et vérifiez les remotes configurés:

```bash
cd {FOLDER_NAME}
git remote -v
```

**Résultat attendu:**
```
origin  {REPO_URL} (fetch)
origin  {REPO_URL} (push)
```

## 3. ❌ Supprimer le remote `origin`

Si vous voulez détatacher ce projet de son repository source, supprimez le remote:

```bash
git remote remove origin
```

**Vérification:**
```bash
git remote -v
```

Maintenant la liste des remotes doit être vide.

## ⚠️ 3bis. Cherry-pick depuis un autre projet local (Optionnel)

Si tu veux importer des commits depuis un autre projet sur ta machine:

### Ajouter un remote local

```bash
git remote add local-project "{chemin-vers-l-autre-projet}"
```

**Exemple (Windows):**
```bash
git remote add local-project "D:\developer\Projects\autre-projet"
```

### Récupérer les commits

```bash
git fetch local-project
```

### Voir les commits disponibles

```bash
git log local-project/main --oneline
```

### Cherry-pick le commit

```bash
git cherry-pick {commit-hash}
```

**Exemple:**
```bash
git cherry-pick 0c4463d
```

### Supprimer le remote après

Une fois le cherry-pick fait, tu peux supprimer ce remote:

```bash
git remote remove local-project
```

**Vérification:**
```bash
git remote -v
```

## 4. Installer les dépendances

```bash
npm install
```

ou version courte:

```bash
npm i
```

Cela installera toutes les dépendances listées dans `package.json` dans le dossier `node_modules/`.

## Résumé complet des commandes

```bash
# 1. Cloner le repository avec une seule branche
git clone -b {BRANCH_NAME} --single-branch {REPO_URL} {FOLDER_NAME}

# 2. Accéder au dossier
cd {FOLDER_NAME}

# 3. Vérifier les remotes
git remote -v

# 4. Supprimer le remote origin (optionnel)
git remote remove origin

# 5. Installer les dépendances
npm install
```

## Exemple concret - Avec variables

```bash
BRANCH_NAME="kit/next-auth"
REPO_URL="https://github.com/Elanrif/springboot-starter-kit.git"
FOLDER_NAME="springboot-social-feedback"

git clone -b $BRANCH_NAME --single-branch $REPO_URL $FOLDER_NAME
cd $FOLDER_NAME
git remote -v
git remote remove origin
npm i
```

## Exemple concret - Directement dans les commandes

Si tu préfères utiliser les vraies valeurs directement (comme dans cet exemple):

```bash
git clone -b kit/next-auth --single-branch https://github.com/Elanrif/springboot-starter-kit.git springboot-social-feedback
cd springboot-social-feedback
git remote -v
git remote remove origin
npm i
```

**Tu peux modifier:**
- `kit/next-auth` → par n'importe quelle branche (replace par la branche réelle)
- `https://github.com/Elanrif/springboot-starter-kit.git` → par l'URL de ton repository
- `springboot-social-feedback` → le nom du dossier local où tu veux cloner

Voilà! Votre projet est maintenant configuré et prêt à être utilisé.
