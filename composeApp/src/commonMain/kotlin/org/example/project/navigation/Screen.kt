package org.example.project.navigation

// ================================================================
// Screen.kt — Gabungan Tugas 5 + Tugas 6 PAM
// Semua routes terpusat di sini (type-safe, tidak ada magic string)
// ================================================================

sealed class Screen(val route: String) {

    // ── Tugas 6: News Reader ─────────────────────────────────────
    object NewsList      : Screen("news_list")
    object Bookmarks     : Screen("bookmarks")
    object ArticleDetail : Screen("article_detail/{articleId}") {
        fun createRoute(articleId: String) = "article_detail/$articleId"
    }

    // ── Tugas 5: Notes App ───────────────────────────────────────
    object NoteList  : Screen("note_list")
    object Favorites : Screen("favorites")
    object Profile   : Screen("profile")

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object AddNote : Screen("add_note")
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
}
