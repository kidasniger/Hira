package com.example.navigation

/**
 * Définition des destinations de l'architecture HIRA.
 * Dans cette étape 1, seul [Splash] est instancié et affiché.
 * Les autres écrans seront implémentés étape par étape selon le plan du projet :
 * - Onboarding (étapes 2, 3, 4 de la maquette)
 * - Connexion / Inscription (étapes 5, 6)
 * - Profil (étape 7)
 * - Permissions (étapes 8, 18)
 * - Discussions (étape 9)
 * - Contacts (étape 10)
 * - Conversation privée / Fichiers (étapes 11, 12, 13)
 * - Groupes (étapes 14, 15)
 * - Réglages (étape 17)
 */
sealed class HiraScreen(val route: String) {
    data object Splash : HiraScreen("splash")
    data object Onboarding : HiraScreen("onboarding")
    data object Login : HiraScreen("login")
    data object Register : HiraScreen("register")
    data object Profile : HiraScreen("profile")
    data object ContactPermission : HiraScreen("contact_permission")
    data object Chats : HiraScreen("chats")
    data object Contacts : HiraScreen("contacts")
    data object ChatDetail : HiraScreen("chat_detail")
    data object Groups : HiraScreen("groups")
    data object Settings : HiraScreen("settings")
    data object NotificationPermission : HiraScreen("notification_permission")
}
