package com.example.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 8 — PERMISSION DES CONTACTS
 *
 * Reproduction du UI Kit V1 (360x800) :
 * - Fond blanc
 * - Illustration circulaire 120 dp, fond #EEF2FF
 * - Titre « Trouvez vos proches »
 * - Sous-titre « Retrouvez vos contacts qui utilisent déjà Hira »
 * - Boutons « Autoriser » et « Plus tard »
 *
 * Le bouton « Autoriser » déclenche la vraie permission Android READ_CONTACTS.
 */
@Composable
fun ContactsPermissionScreen(
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPermissionGranted()
        }
    }

    // Ne pas afficher inutilement cet écran si l'accès aux contacts est déjà accordé.
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            onPermissionGranted()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .testTag("contacts_permission_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF2FF))
                    .testTag("contacts_permission_illustration"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Contacts,
                    contentDescription = stringResource(
                        id = R.string.contacts_permission_icon_description
                    ),
                    tint = HiraRoyalBlue,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.contacts_permission_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("contacts_permission_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.contacts_permission_subtitle),
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .testTag("contacts_permission_subtitle")
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .height(52.dp)
                        .testTag("contacts_permission_allow"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HiraRoyalBlue,
                        contentColor = HiraWhite
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.contacts_permission_allow),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraWhite,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                androidx.compose.material3.TextButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .height(48.dp)
                        .testTag("contacts_permission_later")
                ) {
                    Text(
                        text = stringResource(id = R.string.contacts_permission_later),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}
