# Cristal

Sprint 1 livre un catalogue de jeux avec :

- une API Spring Boot + H2
- une liste de jeux avec recherche et filtres
- une page detail
- un ecran admin pour creer, modifier et supprimer des jeux

## Lancer le backend

```bash
cd api
mvn spring-boot:run
```

API : `http://localhost:8080`

## Lancer le frontend

```bash
cd front
npm install
npm start
```

Frontend : `http://localhost:4200`

## Docker

```bash
docker compose up --build
```
