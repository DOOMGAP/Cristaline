# Cristal

## Run

### Dev

```bash
cd api
mvn spring-boot:run
```

Le profil `dev` est le profil par défaut.

### Prod

```bash
cd /home/nadir95400/fullstack/cristal
docker compose up --build
```

Dans Docker, l'API démarre automatiquement avec le profil `prod`.
Les variables sensibles et les ports sont lus depuis `.env`.
L'import initial FreeToGame au démarrage peut être désactivé avec `APP_SEED_ENABLED=false`, utile pour les tests e2e reproductibles.

### Arrêt et redémarrage propre

```bash
cd /home/nadir95400/fullstack/cristal
docker compose down -v --remove-orphans
docker compose up --build
```

## Ce que ça change

### Dev

- utilise H2
- utile pour lancer le backend rapidement en local
- pas besoin de PostgreSQL

### Prod

- utilise PostgreSQL au lieu de H2
- utilise Kafka
- démarre toute la stack avec Docker
- synchronise les jeux FreeToGame à chaque démarrage de l'API via `api_id`
- met à jour les jeux importés existants et supprime ceux qui ne sont plus présents dans FreeToGame
- conserve les jeux ajoutés manuellement avec `api_id = null`

## URLs utiles en prod Docker

- API : `http://localhost:8080`
- Front : `http://localhost:4200`
- PostgreSQL : `localhost:5432`
- pgAdmin : `http://localhost:81`
- Kafka Console : `http://localhost:8081`

## Notes d'exécution

### Logs Kafka au démarrage

Des messages comme `CoordinatorLoadInProgressException`, `Request joining group due to: rebalance failed` ou `(Re-)joining group` peuvent apparaître juste après le démarrage de Kafka et de l'API.

Ce comportement est normal tant que le consumer finit par rejoindre le groupe, par exemple avec des lignes comme :

- `Successfully joined group`
- `Successfully synced group`
- `partitions assigned`

### Front Angular en 404 sur `/games`

Si l'application front marche sur `/` mais renvoie un 404 Nginx sur une route comme `/games`, `/favorites` ou `/admin/games`, le problème vient du fallback SPA côté Nginx.

Le conteneur front doit rediriger les routes applicatives vers `index.html`. Après modification de la configuration Nginx, reconstruire l'image :

```bash
docker compose up --build front
```

Pour installer l'image : 

-Telecharger les tar de la derniere release

```bash
docker load < cristaline-api.tar
docker run -p 8080:8080 cristaline-api:latest
```
Pour l'api, meme chose pour le front en remplacant api par front.

## Tests

### Backend

```bash
cd api
mvn test
```

### Frontend unitaire

```bash
cd front
npm ci --legacy-peer-deps
npm run test -- --watch=false --browsers=ChromeHeadless
```

### E2E Playwright sur la stack Docker

Avant le premier lancement, installer les navigateurs Playwright :

```bash
cd front
npm run e2e:install
```

```bash
cd /home/nadir95400/fullstack/cristal
./scripts/run-e2e.sh
```

Le script:
- démarre Docker Compose avec `APP_SEED_ENABLED=false`
- attend que le front et l'API répondent
- exécute les scénarios Playwright contre la vraie stack

## Documentation technique

Une vue d'ensemble de l'architecture, des profils et de la stratégie de test est disponible dans [../docs/architecture-technique.md](../docs/architecture-technique.md).
