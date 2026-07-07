# Projet Spring Boot MVC Parapharmacie

Application Spring Boot MVC pour vendre des produits de parapharmacie avec:

- catalogue produits;
- panier client;
- creation et suivi des commandes;
- administration des produits;
- gestion des statuts de commandes;
- inscription et connexion;
- authentification JWT pour les endpoints API.

## Stack

- Java 17
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Security
- JWT avec `jjwt`
- Spring Data JPA
- MySQL

## Configuration MySQL

Creer une base MySQL:

```sql
CREATE DATABASE parapharmacie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configuration par defaut dans `src/main/resources/application.properties`:

```text
DB_URL=jdbc:mysql://localhost:3306/parapharmacie_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=
```

Tu peux changer les identifiants avec des variables d'environnement:

```bash
set DB_USERNAME=root
set DB_PASSWORD=ton_mot_de_passe
```

## Lancer le projet

```bash
mvn spring-boot:run
```

Puis ouvrir:

```text
http://localhost:8080/products
```

## Compte admin de test

```text
Email: admin@para.test
Mot de passe: admin123
```

## Routes MVC principales

- `GET /products`: liste des produits
- `GET /products/{id}`: detail produit
- `GET /cart`: panier
- `POST /cart/add/{productId}`: ajouter au panier
- `GET /checkout`: page validation commande
- `POST /checkout`: creer une commande
- `GET /orders`: commandes du client
- `GET /admin/products`: gestion produits
- `GET /admin/orders`: gestion commandes

## Authentification JWT

Inscription API:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Client Test\",\"email\":\"client@test.com\",\"password\":\"secret123\"}"
```

Connexion API:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"client@test.com\",\"password\":\"secret123\"}"
```

Tester une route protegee:

```bash
curl http://localhost:8080/api/account/me \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

## Remarque securite

Change la valeur `app.jwt.secret` dans `src/main/resources/application.properties` avant une utilisation reelle.
