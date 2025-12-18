# Florist Management System

Application Java de gestion de boutique de fleurs avec JavaFX et MySQL.

## 📋 Description

Système complet de gestion pour une boutique de fleurs incluant:
- 🌸 Gestion des fleurs (stock, prix, fraîcheur)
- 📦 Gestion des fournisseurs
- 💰 Enregistrement des ventes
- ⚠️ Système d'alertes (stock faible, expiration)
- 📊 Tableau de bord avec statistiques

## 🛠️ Technologies

- **Java 17**
- **JavaFX** - Interface graphique
- **MySQL** - Base de données
- **Maven** - Gestion de dépendances
- **JDBC** - Connexion base de données

## 📦 Prérequis

- Java JDK 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+ (ou XAMPP avec MySQL)
- Scene Builder (optionnel, pour éditer les FXML)

## ⚙️ Configuration

1. **Cloner le projet**
```bash
git clone <votre-url-github>
cd florist-management
```

2. **Configurer MySQL**
   - Démarrer MySQL (XAMPP ou service MySQL)
   - Créer la base de données:
```bash
mysql -u root -e "CREATE DATABASE florist_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root florist_db < src/main/resources/schema.sql
```

3. **Configuration de connexion**
   - Modifier `src/main/java/com/florist/dao/DatabaseConnection.java`
   - Ajuster USER et PASSWORD si nécessaire

## 🚀 Lancement

```bash
mvn clean compile
mvn javafx:run
```

Ou pour tester la connexion:
```bash
mvn compile exec:java -Dexec.mainClass="com.florist.TestConnection"
```

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/com/florist/
│   │   ├── MainApp.java           # Point d'entrée
│   │   ├── controller/            # Contrôleurs JavaFX
│   │   ├── dao/                   # Data Access Objects
│   │   ├── model/                 # Entités métier
│   │   └── service/               # Logique métier
│   └── resources/
│       ├── fxml/                  # Interfaces FXML
│       ├── css/                   # Styles CSS
│       └── schema.sql             # Schéma base de données
```

## 💰 Devise

Le système utilise le **Dirham marocain (MAD)**.
Taux de conversion appliqué: 1 EUR = 11 MAD

## 🔧 Fonctionnalités

### Gestion des Fleurs
- Ajout, modification, suppression
- Suivi du stock en temps réel
- Gestion de la fraîcheur
- Association aux fournisseurs

### Gestion des Ventes
- Enregistrement rapide des ventes
- Calcul automatique du total
- Historique complet
- Mise à jour automatique du stock

### Tableau de Bord
- Statistiques en temps réel
- Vue d'ensemble des ventes
- Alertes actives
- Graphiques de performance

### Système d'Alertes
- Stock faible (seuil: 20 unités)
- Fleurs expirées
- Notifications automatiques

## 📸 Captures d'écran

*(Ajoutez vos captures d'écran ici)*

## 👥 Auteur

Développé avec ❤️ par [Votre Nom]

## 📄 Licence

Ce projet est sous licence [MIT/Apache/etc.] - voir le fichier LICENSE pour plus de détails.

## 🤝 Contribution

Les contributions sont les bienvenues! N'hésitez pas à ouvrir une issue ou un pull request.
