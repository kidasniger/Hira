package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.ui.profile.ProfileSetupScreen
import com.example.ui.theme.HiraTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileSetupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileSetupScreen_rendersElementsCorrectly() {
        composeTestRule.setContent {
            HiraTheme {
                ProfileSetupScreen(onFinish = { _, _ -> })
            }
        }

        composeTestRule.onNodeWithTag("profile_setup_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_setup_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_avatar_container").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_avatar_edit_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_username_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_display_name_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_finish_button").assertIsDisplayed()
    }

    @Test
    fun profileSetupScreen_validationErrorsOnEmptySubmit() {
        var finishCalled = false
        composeTestRule.setContent {
            HiraTheme {
                ProfileSetupScreen(onFinish = { _, _ -> finishCalled = true })
            }
        }

        composeTestRule.onNodeWithTag("profile_finish_button").performClick()

        assertFalse(finishCalled)
        composeTestRule.onNodeWithTag("profile_username_error", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("profile_display_name_error", useUnmergedTree = true).assertExists()
    }

    @Test
    fun profileSetupScreen_successfulSubmitNormalizesUsername() {
        var submittedUser = ""
        var submittedName = ""
        composeTestRule.setContent {
            HiraTheme {
                ProfileSetupScreen(
                    onFinish = { u, d ->
                        submittedUser = u
                        submittedName = d
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("profile_username_input").performTextInput("amina")
        composeTestRule.onNodeWithTag("profile_display_name_input").performTextInput("Amina Diallo")
        composeTestRule.onNodeWithTag("profile_finish_button").performClick()

        assertEquals("@amina", submittedUser)
        assertEquals("Amina Diallo", submittedName)
    }

    @Test
    fun profileSetupScreen_preservesLeadingAtWithoutDuplication() {
        var submittedUser = ""
        var submittedName = ""
        composeTestRule.setContent {
            HiraTheme {
                ProfileSetupScreen(
                    onFinish = { u, d ->
                        submittedUser = u
                        submittedName = d
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("profile_username_input").performTextInput("@amina")
        composeTestRule.onNodeWithTag("profile_display_name_input").performTextInput("Amina Diallo")
        composeTestRule.onNodeWithTag("profile_finish_button").performClick()

        assertEquals("@amina", submittedUser)
        assertEquals("Amina Diallo", submittedName)
    }
}
