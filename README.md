# Visualizer - Application de Prédiction du Trafic des Stations

## Aperçu
Visualizer est une application Android moderne qui aide les utilisateurs à surveiller et prédire le trafic dans les gares d'Île-de-France. L'application fournit une visualisation des données en temps réel, des prédictions de trafic et des notifications personnalisées pour aider les voyageurs à prendre des décisions éclairées concernant leurs déplacements.

## Fonctionnalités
- **Trafic des Stations en Temps Réel** : Visualisation du niveau d'affluence actuel dans les stations
- **Analyses Prédictives** : Consultation des prévisions de trafic tout au long de la journée
- **Carte Interactive** : Localisation des stations sur une interface OpenStreetMap avec des indicateurs de trafic en couleur
- **Stations à Proximité** : Recherche des stations proches de votre position
- **Comparaison de Stations** : Comparaison des tendances de trafic entre différentes stations
- **Alertes de Trafic** : Réception de notifications concernant les perturbations de service, les travaux de maintenance et autres événements
- **Fonction de Recherche** : Recherche facile des stations par nom ou par ligne

## Spécifications Techniques
- **SDK Minimum** : Android 24 (Android 7.0 Nougat)
- **SDK Cible** : Android 34
- **Architecture** : MVVM (Modèle-Vue-VueModèle)
- **Framework UI** : Jetpack Compose
- **Communication Backend** : Retrofit avec OkHttp
- **Intégration de Carte** : OSMDroid (OpenStreetMap)
- **Sérialisation** : Gson
- **Permissions Requises** :
    - Accès Internet
    - Accès à la localisation (précise et approximative)

## Écrans
- **Écran de Démarrage** : Introduction de l'application
- **Écran des Conditions** : Accord utilisateur
- **Écran Principal** : Page d'accueil avec recherche et carte
- **Écran de Prédiction** : Prédictions de trafic pour les stations sélectionnées
- **Écran de Comparaison** : Comparaison du trafic entre stations
- **Écran Paramètres** : Configuration de l'application
- **Écran Notifications** : Visualisation des notifications système
- **Écrans Légaux** : Conditions d'utilisation et politique de confidentialité

## Installation
1. Clonez le dépôt
2. Ouvrez le projet dans Android Studio
3. Assurez-vous d'avoir la bonne version de JDK (JDK 17)
4. Connectez-vous à votre appareil ou émulateur
5. Exécutez l'application

## Intégration API
L'application s'intègre à un service backend qui fournit :
- Informations sur les stations
- Données de trafic en temps réel
- Prédictions de trafic
- Notifications et alertes

Point de terminaison API par défaut : `http://10.0.2.2:22786/` (pour les tests sur émulateur)

## Structure du Projet
- **api** : Interfaces de service API et configuration du client
- **model** : Classes de données pour les stations, prédictions et notifications
- **repository** : Dépôts de données
- **ui/theme** : Composants UI Compose et écrans
- **viewmodel** : ViewModels pour chaque fonctionnalité majeure
- **location** : Services de localisation

## Contribution
Les contributions sont les bienvenues ! N'hésitez pas à soumettre une Pull Request.

## Licence
MIT License
Copyright (c) 2025 Visualizer
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

## Crédits
Développé par :
ATCHA Nadine
GARA Sarra
TIMIZAR Kamélia
KERKOURI ASMA
MANSEUR Ouiza
