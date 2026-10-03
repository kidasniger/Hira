package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestionnaire de persistance pour l'onboarding HIRA.
 * Utilise SharedPreferences pour une exécution ultra-légère et sans latence sur tous types d'appareils.
 */
class OnboardingPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "hira_onboarding_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_CURRENT_ONBOARDING_STEP = "key_current_onboarding_step"
    }

    /**
     * Indique si l'utilisateur a parcouru l'intégralité des 3 écrans d'onboarding.
     * Reste à false tant que les 3 écrans ne sont pas validés.
     */
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Mémorise la validation définitive de l'onboarding.
     */
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    /**
     * Mémorise l'étape d'onboarding atteinte (ex: 1, 2, 3).
     */
    fun setOnboardingStep(step: Int) {
        prefs.edit().putInt(KEY_CURRENT_ONBOARDING_STEP, step).apply()
    }

    /**
     * Récupère la dernière étape d'onboarding enregistrée.
     */
    fun getOnboardingStep(): Int {
        return prefs.getInt(KEY_CURRENT_ONBOARDING_STEP, 1)
    }

    /**
     * Réinitialise l'état d'onboarding (utile pour les tests).
     */
    fun reset() {
        prefs.edit().clear().apply()
    }
}
