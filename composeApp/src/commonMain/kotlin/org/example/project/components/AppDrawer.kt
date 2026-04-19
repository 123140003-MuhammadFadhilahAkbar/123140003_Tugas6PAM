package org.example.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DrawerItem(
    val icon    : ImageVector,
    val label   : String,
    val route   : String,
    val section : String = ""
)

@Composable
fun AppDrawerContent(
    currentRoute  : String?,
    isDarkMode    : Boolean,
    onToggleDark  : () -> Unit,
    onNavigate    : (String) -> Unit,
    onCloseDrawer : () -> Unit
) {
    val newsItems = listOf(
        DrawerItem(Icons.Default.Home,     "Berita",    "news_list"),
        DrawerItem(Icons.Default.Bookmark, "Tersimpan", "bookmarks"),
    )
    val noteItems = listOf(
        DrawerItem(Icons.Default.List,     "Catatan",   "note_list"),
        DrawerItem(Icons.Default.Favorite, "Favorit",   "favorites"),
        DrawerItem(Icons.Default.Person,   "Profil",    "profile"),
    )

    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
    ) {
        // ── Header ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A73E8), Color(0xFF6C63FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier         = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧑‍💻", fontSize = 32.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text       = "Muhammad Fadhilah Akbar",
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "123140003 · IF25 · ITERA",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Dark Mode Toggle ─────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = if (isDarkMode) Icons.Default.DarkMode
                                             else Icons.Default.LightMode,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text       = if (isDarkMode) "Mode Gelap" else "Mode Terang",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text     = if (isDarkMode) "Tampilan gelap aktif" else "Tampilan terang aktif",
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked         = isDarkMode,
                onCheckedChange = { onToggleDark() },
                modifier        = Modifier.scale(0.8f),
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = MaterialTheme.colorScheme.primary,
                    checkedTrackColor   = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

        // ── Seksi: News Reader ───────────────────────────────────
        Text(
            text       = "📰  NEWS READER",
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        newsItems.forEach { item ->
            DrawerNavItem(
                item         = item,
                currentRoute = currentRoute,
                onNavigate   = onNavigate,
                onClose      = onCloseDrawer
            )
        }

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(4.dp))

        // ── Seksi: Notes App ─────────────────────────────────────
        Text(
            text       = "📝  NOTES APP",
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        noteItems.forEach { item ->
            DrawerNavItem(
                item         = item,
                currentRoute = currentRoute,
                onNavigate   = onNavigate,
                onClose      = onCloseDrawer
            )
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // ── Footer ───────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Default.Info,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text     = "Tugas PAM",
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    item         : DrawerItem,
    currentRoute : String?,
    onNavigate   : (String) -> Unit,
    onClose      : () -> Unit
) {
    val selected = currentRoute == item.route
    NavigationDrawerItem(
        icon     = {
            Icon(
                imageVector        = item.icon,
                contentDescription = item.label,
                tint               = if (selected) MaterialTheme.colorScheme.primary
                                     else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        label    = {
            Text(
                text       = item.label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        selected = selected,
        onClick  = { onNavigate(item.route); onClose() },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors   = NavigationDrawerItemDefaults.colors(
            selectedContainerColor   = MaterialTheme.colorScheme.primaryContainer,
            selectedTextColor        = MaterialTheme.colorScheme.primary,
            selectedIconColor        = MaterialTheme.colorScheme.primary,
            unselectedContainerColor = Color.Transparent
        )
    )
}
