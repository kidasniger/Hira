package com.example.ui.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class PhoneContact(
    val id: Long,
    val name: String,
    val initials: String
)

@Composable
fun ContactsScreen(
    onDiscussionsClick: () -> Unit = {},
    onInviteClick: () -> Unit = {},
    onContactClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var contacts by remember { mutableStateOf<List<PhoneContact>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLoading = true
            contacts = withContext(Dispatchers.IO) {
                loadPhoneContacts(context)
            }
            isLoading = false
        }
    }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }

    val groupedContacts = remember(filteredContacts) {
        filteredContacts
            .groupBy { contact -> contact.name.firstOrNull()?.uppercaseChar() ?: '#' }
            .toSortedMap()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("contacts_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("contacts_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.contacts_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onInviteClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("contacts_invite_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = stringResource(R.string.contacts_invite_description),
                        tint = HiraRoyalBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.contacts_invite),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraRoyalBlue,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HiraBorder)
            )

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(56.dp)
                    .testTag("contacts_search"),
                placeholder = {
                    Text(
                        text = stringResource(R.string.contacts_search_hint),
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.contacts_search_description),
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F4F6),
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    disabledContainerColor = Color(0xFFF3F4F6),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.contacts_loading),
                        fontSize = 14.sp,
                        color = HiraGrayDark
                    )
                }
            } else if (groupedContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.contacts_empty),
                        fontSize = 14.sp,
                        color = HiraGrayDark,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("contacts_list")
                ) {
                    groupedContacts.forEach { (letter, entries) ->
                        item(key = "header_$letter") {
                            Text(
                                text = letter.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HiraGrayDark,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        items(
                            items = entries,
                            key = { it.id }
                        ) { contact ->
                            ContactRow(
                                contact = contact,
                                onClick = { onContactClick(contact.name) }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HiraWhite)
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = onInviteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("contacts_invite_friend_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = HiraRoyalBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.contacts_invite_friend),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraRoyalBlue
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(HiraWhite)
                    .testTag("contacts_bottom_navigation"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationLabel(
                    text = stringResource(R.string.discussions_nav_discussions),
                    active = false,
                    onClick = onDiscussionsClick
                )
                BottomNavigationLabel(
                    text = stringResource(R.string.discussions_nav_groups),
                    active = false,
                    onClick = {}
                )
                BottomNavigationLabel(
                    text = stringResource(R.string.discussions_nav_contacts),
                    active = true,
                    onClick = {}
                )
                BottomNavigationLabel(
                    text = stringResource(R.string.discussions_nav_settings),
                    active = false,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: PhoneContact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .testTag("contact_row_" + contact.id),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6EBFF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initials,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = HiraRoyalBlue,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = contact.name,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111111),
            fontFamily = FontFamily.SansSerif,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RowScope.BottomNavigationLabel(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .weight(1f)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) HiraRoyalBlue else Color.Transparent)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (active) HiraRoyalBlue else Color(0xFF9CA3AF),
            fontFamily = FontFamily.SansSerif
        )
    }
}

private fun loadPhoneContacts(context: Context): List<PhoneContact> {
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    )

    val contacts = LinkedHashMap<Long, PhoneContact>()

    context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            if (idColumn < 0 || nameColumn < 0) continue

            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)?.trim().orEmpty()
            if (id <= 0L || name.isBlank()) continue

            contacts.putIfAbsent(
                id,
                PhoneContact(
                    id = id,
                    name = name,
                    initials = initialsFor(name)
                )
            )
        }
    }

    return contacts.values.sortedBy { it.name.lowercase() }
}

private fun initialsFor(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> (parts.first().first().toString() + parts.last().first()).uppercase()
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "?"
    }
}
