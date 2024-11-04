# API de Gestion de Centre de Formation

## Description

API REST complète pour la gestion d'un centre de formation, permettant la gestion des formations, apprenants, formateurs et classes. Développée avec Spring Boot et suivant les meilleures pratiques REST.

## Fonctionnalités

### Gestion des Apprenants

- Inscription et gestion des profils apprenants
- Assignation aux classes et formations
- Suivi du niveau et des formations
- Recherche avancée par nom/prénom
- Pagination des résultats

### Gestion des Formateurs

- Gestion complète des profils formateurs
- Attribution aux classes
- Gestion des spécialités
- Recherche par critères

### Gestion des Classes

- Création et configuration des classes
- Attribution des formateurs et apprenants
- Gestion des capacités
- Suivi des salles

### Gestion des Formations

- Planification des formations
- Gestion des inscriptions
- Suivi des statuts
- Configuration des prérequis et capacités

## Technologies Utilisées

- Java 8
- Spring Boot 2.x
- Spring Data JPA
- PostgreSQL / H2 Database
- Maven
- JUnit 5 & Mockito
- Swagger/OpenAPI
- Lombok
- JaCoCo pour la couverture de tests

## Prérequis

- JDK 8 ou supérieur
- Maven 3.6+
- PostgreSQL 12+ (pour le profil prod)
- Git

## Installation

1. Cloner le projet

```
git clone https://github.com/JavaAura/Labrihmi_Naoufal_S3_B2_Formation
```

```
cd Labrihmi_Naoufal_S3_B2_Formation
```

2. Configuration de la base de données

   Pour le profil dev (H2):

   ```
   spring.datasource.url=jdbc:h2:mem:formation_db
   spring.datasource.username=sa
   spring.datasource.password=
   ```

   Pour le profil prod (PostgreSQL):

   ```
    spring.datasource.url=jdbc:postgresql://localhost:5432/formation_db
    spring.datasource.username=votre_username
    spring.datasource.password=votre_password
   ```

3. Compiler et lancer
   bash
   mvn clean install
   mvn spring-boot:run

## Documentation API

La documentation Swagger est accessible à:

- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs (JSON)

### Endpoints Principaux

#### Apprenants

- POST /api/apprenants - Créer un apprenant
- GET /api/apprenants - Liste des apprenants
- GET /api/apprenants/{id} - Détails d'un apprenant
- PUT /api/apprenants/{id} - Modifier un apprenant
- DELETE /api/apprenants/{id} - Supprimer un apprenant

#### Classes

- POST /api/classes - Créer une classe
- GET /api/classes - Liste des classes
- PUT /api/classes/{id} - Modifier une classe
- POST /api/classes/{id}/apprenants/{apprenantId} - Assigner un apprenant

#### Formations

- POST /api/formations - Créer une formation
- GET /api/formations/available - Formations disponibles
- PUT /api/formations/{id}/status/{status} - Modifier le statut

## Tests

### Tests Unitaires

## Structure du Projet

    src/
    ├── main/
    │ ├── java/com/formation/
    │ │ ├── controllers/ # Contrôleurs REST
    │ │ ├── dto/ # Objets de transfert de données
    │ │ ├── models/ # Entités JPA
    │ │ ├── repositories/ # Repositories Spring Data
    │ │ ├── services/ # Logique métier
    │ │ └── validation/ # Validateurs
    │ └── resources/
    │ └── application.properties
    └── test/
    └── java/com/formation/
    ├── integration/ # Tests d'intégration
    └── unit/ # Tests unitaires

## Bonnes Pratiques Implémentées

- Architecture en couches
- DTO Pattern
- Repository Pattern
- Gestion des exceptions personnalisées
- Validation des données
- Logging avec SLF4J
- Tests unitaires et d'intégration
- Documentation API avec Swagger

## Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/nouvelle-feature`)
3. Commit les changements (`git commit -m 'Ajout nouvelle feature'`)
4. Push vers la branche (`git push origin feature/nouvelle-feature`)
5. Créer une Pull Request

## Gestion de Projet

- Git pour le contrôle de version
- JIRA pour la gestion de projet Scrum : [Lien JIRA](https://naoufallabrihmi.atlassian.net/jira/software/projects/FOR/boards/3)

## Diagramme UML

Le diagramme UML du projet est disponible dans le dossier `@Diagrammes` du projet.

## Auteur

- Naoufal LABRIHMI

## Licence

Ce projet est sous licence MIT.
