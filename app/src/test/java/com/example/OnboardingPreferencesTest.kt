package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.local.OnboardingPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests unitaires pour la persistance de l'onboarding HIRA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnboardingPreferencesTest {

    private lateinit var prefs: OnboardingPreferences

    @Before
    fun setUp() {
        prefs = OnboardingPreferences(ApplicationProvider.getApplicationContext())
        prefs.reset()
    }

    @Test
    fun `onboarding n est pas termine initialement`() {
        assertFalse(prefs.isOnboardingCompleted())
        assertEquals(1, prefs.getOnboardingStep())
    }

    @Test
    fun `apres etape 1 l onboarding reste non termine`() {
        prefs.setOnboardingStep(1)
        assertEquals(1, prefs.getOnboardingStep())
        // Règle stricte : l'onboarding reste inachevé après Accueil 1/3
        assertFalse(prefs.isOnboardingCompleted())
    }

    @Test
    fun `memorisation de la fin d onboarding uniquement lorsque demande`() {
        prefs.setOnboardingCompleted(true)
        assertTrue(prefs.isOnboardingCompleted())
    }
}
