# Conversion de Devise: EUR → MAD

## 📊 Résumé de la Conversion

**Taux de change appliqué**: 1 EUR = 11 MAD

---

## ✅ Modifications Effectuées

### 1. Base de Données MySQL
**Fichier**: `florist_db` (XAMPP)
**Action**: Mise à jour de tous les prix des fleurs

| Fleur | Ancien Prix (EUR) | Nouveau Prix (MAD) |
|-------|-------------------|-------------------|
| Rose Rouge | 3.50 € | 38.50 MAD |
| Tulipe Jaune | 2.80 € | 30.80 MAD |
| Orchidée Blanche | 15.00 € | 165.00 MAD |
| Lys Rose | 6.50 € | 71.50 MAD |
| Marguerite | 1.50 € | 16.50 MAD |
| Tournesol | 4.00 € | 44.00 MAD |

**Commande SQL exécutée**:
```sql
UPDATE flowers SET price = price * 11;
```

---

### 2. Interface Utilisateur (FXML)

#### FlowerManagement.fxml
- Colonne tableau: `Prix (€)` → `Prix (MAD)`
- Label formulaire: `Prix (€):` → `Prix (MAD):`

#### SaleForm.fxml
- Label prix unitaire: `0.00 €` → `0.00 MAD`
- Label total: `0.00 €` → `0.00 MAD`

---

### 3. Code Java (Contrôleurs)

#### SaleFormController.java
Mis à jour 7 occurrences de `€` → `MAD`:
- Affichage prix unitaire
- Calcul et affichage du total
- Messages de confirmation
- Réinitialisation des labels

**Exemple**:
```java
// AVANT
unitPriceLabel.setText(String.format("%.2f €", selectedFlower.getPrice()));

// APRÈS
unitPriceLabel.setText(String.format("%.2f MAD", selectedFlower.getPrice()));
```

---

## 🔄 Compilation

**Status**: ✅ BUILD SUCCESS
- 18 fichiers source compilés
- Aucune erreur
- Temps: 11.859 s

---

## 🎯 Prochaines Étapes

Pour voir les changements:
```powershell
mvn javafx:run
```

Toutes les interfaces afficheront maintenant les prix en **MAD** (Dirhams marocains).

---

## 📝 Notes Importantes

- Les prix en base de données ont été **définitivement modifiés**
- Si vous voulez revenir à EUR, divisez par 11: `UPDATE flowers SET price = price / 11;`
- Le taux de change (11) est fixe dans la base de données
- Pour un taux variable, il faudrait ajouter un paramètre de configuration

---

**Date de conversion**: 2025-12-18
**Taux appliqué**: 1 EUR = 11 MAD
