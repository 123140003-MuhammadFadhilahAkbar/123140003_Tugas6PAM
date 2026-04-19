package org.example.project.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Note
import org.example.project.model.NoteColor
import org.example.project.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel   : NoteViewModel,
    onNoteClick : (Int) -> Unit,
    onAddClick  : () -> Unit,
    onMenuClick : () -> Unit = {}
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Column {
                        Text("Catatan Saya", fontWeight = FontWeight.Bold)
                        Text(
                            "${notes.size} catatan",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors  = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, "Cari", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary,
                shape          = CircleShape,
                modifier       = Modifier.size(58.dp)
            ) {
                Icon(Icons.Default.Add, "Tambah Catatan", modifier = Modifier.size(26.dp))
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            EmptyNoteState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note            = note,
                        onClick         = { onNoteClick(note.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(note.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun NoteCard(
    note            : Note,
    onClick         : () -> Unit,
    onFavoriteClick : () -> Unit
) {
    val cardBg = Color(note.color.hex)

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Text(
                    text       = note.title,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector        = if (note.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint               = if (note.isFavorite) Color(0xFFFF6B8A) else Color.White.copy(alpha = 0.6f),
                        modifier           = Modifier.size(19.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text       = note.content,
                color      = Color.White.copy(alpha = 0.8f),
                fontSize   = 13.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = formatNoteDate(note.timestamp),
                    color    = Color.White.copy(alpha = 0.55f),
                    fontSize = 10.sp
                )
                if (note.isFavorite) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFF6B8A).copy(alpha = 0.25f)
                    ) {
                        Text(
                            "⭐ Favorit",
                            color      = Color(0xFFFF6B8A),
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Medium,
                            modifier   = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNoteState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📭", fontSize = 60.sp)
            Spacer(Modifier.height(14.dp))
            Text("Belum ada catatan", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("Tekan + untuk membuat catatan baru",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

fun formatNoteDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id")).format(Date(timestamp))
