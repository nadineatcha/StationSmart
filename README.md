# StationSmart - Visualizer

## 📱 Présentation du Projet

**Nom du Projet** : Visualizer  
**Objectif** : Développer une application mobile Android permettant de visualiser les stations de transport en Île-de-France, afficher leurs détails (adresses, horaires, lignes), et poser les bases d’un futur système de prédiction du trafic.

**Données Sources** :  
- **Période d'analyse** : 2018-2022  
- **Origine** : Jeu de données de fréquentation des gares d'Île-de-France  
- **Méthodologie** : Traitement et modélisation via R dans le cadre du cours d'analyse de données

### 🧠 Contextualisation des Données
Les prédictions et modèles s'appuient sur une analyse des données de trafic couvrant :
- Les variations saisonnières
- Les tendances de fréquentation à long terme
- Les effets d’événements majeurs sur la mobilité

---

## 🛠️ Méthodologie de Développement

### Approche Agile
- Développement itératif
- Intégration continue
- Revues de code régulières
- Architecture modulaire

---

## 🧱 Architecture Technique

### Frontend
- **Framework** : Android Jetpack Compose
- **Langage** : Kotlin
- **Composants** :
  - Navigation
  - Gestion d’état avec Kotlin Flow
  - UI réactive

### Intégration Backend
- **API REST** : Retrofit
- **Analyse JSON** : Gson
- **Journalisation réseau** : OkHttp Interceptor

### Services de Localisation
- **Géolocalisation** : Google Play Services
- **Cartographie** : OpenStreetMap via `osmdroid`

---

## 🚧 Défis Techniques et Solutions

### 1. Gestion des Permissions de Localisation
- Demande de permissions à l'exécution
- Mécanisme de repli si refus
- Intégration complète dans `MainActivity`

### 2. Fiabilité des Données API
- Mécanisme de retry pour les appels réseau
- Données de secours en cas d'échec
- Gestion des erreurs dans les `ViewModels`

### 3. Gestion d'État
- `StateFlow` pour la réactivité
- `sealed class` pour représenter les états
- Logique centralisée dans les `ViewModels`

---

## 📈 Phases de Développement

| Phase | Détails |
|-------|---------|
| **Semaines 1-2** | Prototype initial, maquettes UI, définition de l’API |
| **Semaines 3-4** | Localisation, récupération des données, interfaces de base |
| **Semaines 5-6** | Notifications, détails station, amélioration UX |
| **Semaines 7-8** | Tests unitaires et intégration, optimisation, débogage |

---

## 📊 Métriques de Performance

- **Temps de réponse API** : < 500ms
- **Précision de la localisation** : Haute précision
- **Mise en cache** : Active pour limiter les appels
- **Optimisation mémoire** : Via `Kotlin Coroutines`

---

## 🔮 Feuille de Route

- 🔁 Algorithmes de prédiction plus avancés
- 🔍 Analyses enrichies des stations
- 🌐 Support multilingue
- 📴 Mode hors ligne

---

## 💡 Leçons Apprises

- Importance d’une **architecture modulaire**
- Gestion d’état complexe et fluide
- Stratégies robustes de gestion d’erreurs
- Équilibre entre **UX riche** et **performances**

---

## 🧰 Stack Technique

- Kotlin
- Jetpack Compose
- Retrofit
- Coroutines
- Google Play Services
- OkHttp
- OpenStreetMap (osmdroid)

---

## ✅ Recommandations d'Amélioration

1. Permissions de localisation plus granulaires
2. Mise en cache hors ligne avancée
3. Algorithmes de prédiction plus performants
4. Système de préférences utilisateur détaillé

---

## 👥 Équipe de Développement

| Nom | Rôle | Email | Contributions |
|-----|------|-------|---------------|
| **ATCHANadine** | Android & API | nadineatcha@edu.univ-eiffel.fr | Notification, prédiction, stations proches, géolocalisation, API |
| **TIMIZARKamélia** | Android & API | kameliatimizar@edu.univ-eiffel.fr | Accueil, carte, intégration interfaces, API |
| **Asma KERKOURI** | Frontend | asmakerkouri@edu.univ-eiffel.fr | Interface paramètres |
| **Sarah GARA** | Frontend | saragara@edu.univ-eiffel.fr | Interface comparaison |
| **Ouiza Manseur** | Frontend | ouizamaseur@edu.univ-eiffel.fr | Interface (à détailler selon ta partie exacte) |

---

## 🎓 Institution

- **Université Gustave Eiffel**  
- **Département** : Informatique et Ingénierie

---

## 🙏 Remerciements

- Nos camarades de classe pour leurs retours constructifs  
- La communauté open-source pour les bibliothèques et outils utilisés  
