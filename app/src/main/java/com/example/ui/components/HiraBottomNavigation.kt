package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite

enum class HiraNavDestination {
    DISCUSSIONS,
    GROUPES,
    CONTACTS,
    REGLAGES
}

@Composable
fun HiraBottomNavigation(
    selected: HiraNavDestination,
    onDestinationClick: (HiraNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(HiraWhite)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HiraNavigationItem(
            destination = HiraNavDestination.DISCUSSIONS,
            label = "Discussions",
            icon = Icons.Outlined.ChatBubbleOutline,
            selected = selected == HiraNavDestination.DISCUSSIONS,
            onClick = { onDestinationClick(HiraNavDestination.DISCUSSIONS) }
        )
        HiraNavigationItem(
            destination = HiraNavDestination.GROUPES,
            label = "Groupes",
            icon = Icons.Outlined.Group,
            selected = selected == HiraNavDestination.GROUPES,
            onClick = { onDestinationClick(HiraNavDestination.GROUPES) }
        )
        HiraNavigationItem(
            destination = HiraNavDestination.CONTACTS,
            label = "Contacts",
            icon = Icons.Outlined.Contacts,
            selected = selected == HiraNavDestination.CONTACTS,
            onClick = { onDestinationClick(HiraNavDestination.CONTACTS) }
        )
        HiraNavigationItem(
            destination = HiraNavDestination.REGLAGES,
            label = "Réglages",
            icon = Icons.Outlined.Settings,
            selected = selected == HiraNavDestination.REGLAGES,
            onClick = { onDestinationClick(HiraNavDestination.REGLAGES) }
        )
    }
}

@Composable
private fun RowScope.HiraNavigationItem(
    destination: HiraNavDestination,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (selected) Color(0xFFE6EBFF) else Color.Transparent)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) HiraRoyalBlue else Color(0xFF6B7280),
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) HiraRoyalBlue else Color(0xFF6B7280),
            fontFamily = FontFamily.SansSerif,
            maxLines = 1
        )
    }
}
