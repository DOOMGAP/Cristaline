# Architecture technique

## Vue d'ensemble
- `front`: application Angular standalone servie par Nginx.
- `api`: API Spring Boot avec profils `dev` et `prod`.
- `consumer-postgresql`: base PostgreSQL utilisee en `prod`.
- `kafka-0` + `redpanda-console`: broker Kafka et console d'observation.

## Profils d'execution
- `dev`: H2 en memoire, utile pour lancer rapidement l'API et les tests backend.
- `prod`: PostgreSQL + Kafka, demarrage via Docker Compose.
- `APP_SEED_ENABLED`: active ou desactive l'import initial FreeToGame au demarrage. Laisser `true` en usage normal, forcer `false` pour les e2e afin d'eviter une dependance reseau externe.

## Flux applicatifs importants
- Catalogue:
  - le front appelle `GET /games` et `GET /games/{id}` via `GamesApi`.
- Authentification:
  - le front ouvre une modale et stocke le JWT dans `localStorage`.
  - `AuthService` decode le token et expose `currentUser$`.
- Favoris et notes:
  - les appels authentifies partent avec le header `Authorization: Bearer <token>`.
- Import admin:
  - `POST /admin/import/freetogame` publie une demande d'import.
  - en profil `prod`, `ImportRequestConsumer` consomme le message Kafka puis declenche `ImportService`.

## Strategie de test
- Backend unitaire/slice:
  - `mvn test` dans `cristal/api`
  - couvre services, controleurs et couche JPA ciblee.
- Frontend unitaire:
  - `npm run test -- --watch=false --browsers=ChromeHeadless` dans `cristal/front`
  - couvre services HTTP et pages critiques.
- E2E:
  - `./scripts/run-e2e.sh` dans `cristal`
  - demarre la stack Docker avec `APP_SEED_ENABLED=false`, attend le front et l'API, puis execute Playwright.
