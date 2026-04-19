package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.model.Note
import org.example.project.model.NoteColor

class NoteViewModel : ViewModel() {

    private val _notes = MutableStateFlow(dummyNotes())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()


    fun getNoteById(id: Int): Note? = _notes.value.find { it.id == id }

    fun addNote(title: String, content: String, color: NoteColor) {
        val newId = (_notes.value.maxOfOrNull { it.id } ?: 0) + 1
        _notes.value = _notes.value + Note(
            id      = newId,
            title   = title.trim(),
            content = content.trim(),
            color   = color
        )
    }

    fun updateNote(id: Int, title: String, content: String, color: NoteColor) {
        _notes.value = _notes.value.map { note ->
            if (note.id == id) note.copy(
                title   = title.trim(),
                content = content.trim(),
                color   = color
            ) else note
        }
    }

    fun deleteNote(id: Int) {
        _notes.value = _notes.value.filter { it.id != id }
    }

    fun toggleFavorite(id: Int) {
        _notes.value = _notes.value.map { note ->
            if (note.id == id) note.copy(isFavorite = !note.isFavorite) else note
        }
    }

    private fun dummyNotes() = listOf(
        Note(
            id         = 1,
            title      = "Jadwal Kuliah Senin",
            content    = "08:00 - Pemrograman Mobile (Lab Komputer 3).\n10:30 - Jaringan Komputer.\nJangan lupa bawa charger laptop dan kumpulkan tugas minggu lalu!",
            isFavorite = true,
            color      = NoteColor.PURPLE
        ),
        Note(
            id         = 2,
            title      = "Ide Kado Ulang Tahun",
            content    = "Cari sepatu lari atau hoodie warna hitam buat kado minggu depan. Cek promo di marketplace tanggal 25 nanti.",
            isFavorite = false,
            color      = NoteColor.TEAL
        ),
        Note(
            id         = 3,
            title      = "Playlist Gym Minggu Ini",
            content    = "1. Phonk High Bass\n2. Rock Classic 90s\n3. Podcast Pengembangan Diri.\nBiar semangat angkat beban!",
            isFavorite = true,
            color      = NoteColor.NAVY
        ),
        Note(
            id         = 4,
            title      = "Resep Nasi Goreng Simpel",
            content    = "Bawang merah, putih, cabai rawit, telur, kecap manis, dan sedikit margarin. Masak sampai harum buat sarapan besok.",
            isFavorite = false,
            color      = NoteColor.DEFAULT
        ),
        Note(
            id         = 5,
            title      = "Self-Reminder: Istirahat",
            content    = "Coding itu penting, tapi kesehatan lebih utama. Minum air putih 2 liter sehari dan jangan tidur lewat jam 12 malam.",
            isFavorite = true,
            color      = NoteColor.ROSE
        ),
    )
}
