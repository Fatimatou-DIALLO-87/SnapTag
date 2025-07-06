# 🎨 SnapTag — Application JavaFX de Traitement d’Images

**SnapTag** est une application Java développée avec JavaFX, dédiée au traitement et à la transformation d’images. Elle permet à l’utilisateur d’appliquer différents filtres visuels, de sécuriser ses images, de gérer l’historique des modifications, et de stocker les métadonnées dans une base de données locale.

---

## 🎯 Objectif du projet

L’objectif principal est de concevoir une application graphique intuitive qui permet :

- Le **chargement, la visualisation et le traitement** d’images via une interface conviviale.
- L’application **de filtres personnalisés** pour modifier les couleurs, détecter les contours ou simuler des effets visuels.
- La **sauvegarde d’états intermédiaires** pour un historique de transformation.
- La **sécurisation des images** via des techniques de chiffrement.
- La **gestion et la persistance des métadonnées** (tags, filtres appliqués, etc.) dans une base de données embarquée (Apache Derby).

---

## ✨ Fonctionnalités principales

### 🔧 Traitements d’images

- Filtres intégrés :
  - 🔳 Niveaux de gris (GrayScaleFilter)
  - ☕ Effet sépia (SepiaFilter)
  - 🧠 Détection de contours (SobelFilter)
  - 🎨 Inversion des couleurs (SwapRGBFilter)

- Gestion de l’image :
  - 🧩 Historique des modifications
  - 🗂️ Restauration d’état précédent
  - 🔒 Chiffrement et déchiffrement (ImageSecurity)

 ## 🏗️ Architecture du projet

Le projet est organisé en plusieurs couches :
- **Interface utilisateur (JavaFX)** : fichiers `.fxml`, contrôleurs.
- **Traitement d’image** : filtres personnalisés (niveaux de gris, sépia…).
- **Persistance** : base de données Apache Derby embarquée pour stocker les métadonnées des images.
- **Sécurité** : module de chiffrement/déchiffrement des images.
- **Historique** : chaque transformation est sauvegardée pour permettre le suivi ou l’annulation.

## 📂 Arborescence du projet

Projet/ <br>
├── monDB/                            ← Répertoire contenant la base de données Apache Derby  <br>
├── src/ <br>
│   └── main/ <br>
│       ├── java/ <br>
│       │   ├── module-info.java      ← Module Java (Java 9+) <br>
│       │   └── com/example/projet/   ← Package principal Java <br>
│       │       ├── HelloApplication.java     ← Classe principale JavaFX <br>
│       │       ├── HelloController.java      ← Contrôleur FXML <br>
│       │       ├── Operation.java            ← Classe pour représenter une opération sur image <br>
│       │       ├── ImageSecurity.java        ← Gestion de la sécurité des images <br>
│       │       ├── ImageViewState.java       ← État de l’image affichée <br>
│       │       ├── DatabaseManager.java      ← Connexion à la base de données <br>
│       │       └── filter/                   ← Filtres d'image personnalisés <br>
│       │           ├── AbstractFilter.java   ← Classe abstraite pour les filtres <br>
│       │           ├── Filter.java           ← Interface de base des filtres <br>
│       │           ├── GrayScaleFilter.java  ← Filtre niveaux de gris <br>
│       │           ├── SepiaFilter.java      ← Filtre sépia <br>
│       │           ├── SobelFilter.java      ← Détection de contours (Sobel) <br>
│       │           └── SwapRGBFilter.java    ← Permutation des canaux RGB <br>
│       └── resources/ <br>
│           └── com/example/projet/ <br>
│               ├── logo/ <br>
│               │   └── logo.png              ← Logo de l'application <br>
│               ├── bg.jpg                    ← Image d’arrière-plan <br>
│               └── hello-view.fxml           ← Interface graphique JavaFX (FXML) <br>
├── pom.xml                           ← Configuration Maven <br>
├── mvnw / mvnw.cmd                   ← Scripts Maven Wrapper <br>
└── README.md                         ← Fichier de documentation <br>

🖼️ Aperçu


# ![Fatimatou](https://)


