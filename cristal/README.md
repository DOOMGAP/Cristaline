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


Pour installer l'image: 

Telecharger les tar de la derniere release

```bash
docker load < cristaline-api.tar
docker run -p votre_port:votre_port cristaline-api:latest
```

Pour l'api, meme chose pour le front en remplacant api par front.
