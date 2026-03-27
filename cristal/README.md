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
docker-compose up --build
```

Dans Docker, l'API démarre automatiquement avec le profil `prod`.

## Ce que ça change

### Dev

- utilise H2
- utile pour lancer le backend rapidement en local
- pas besoin de PostgreSQL

### Prod

- utilise PostgreSQL au lieu de H2
- utilise Kafka
- démarre toute la stack avec Docker

## URLs utiles en prod Docker

- API : `http://localhost:8080`
- Front : `http://localhost:4200`
- PostgreSQL : `localhost:5432`
- pgAdmin : `http://localhost:81`
- Kafka Console : `http://localhost:8081`
