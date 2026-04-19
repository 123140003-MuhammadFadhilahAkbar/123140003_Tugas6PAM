package org.example.project.model

// ================================================================
// Article.kt — Data Model NewsAPI (Pertemuan 6)
//
// API: https://newsapi.org/v2/top-headlines
// Endpoint: GET /v2/top-headlines?country=id&apiKey=YOUR_KEY
//
// Format JSON dari NewsAPI:
// {
//   "status": "ok",
//   "totalResults": 38,
//   "articles": [
//     {
//       "source": { "id": "cnn", "name": "CNN" },
//       "author": "John Doe",
//       "title": "Judul berita...",
//       "description": "Deskripsi singkat...",
//       "url": "https://...",
//       "urlToImage": "https://...jpg",
//       "publishedAt": "2024-01-15T10:30:00Z",
//       "content": "Isi lengkap berita..."
//     }
//   ]
// }
// ================================================================

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Response wrapper dari NewsAPI ────────────────────────────────
@Serializable
data class NewsResponse(
    val status       : String           = "",
    val totalResults : Int              = 0,
    val articles     : List<Article>    = emptyList(),
    // NewsAPI mengembalikan "message" saat error
    val message      : String?          = null,
    val code         : String?          = null
)

// ── Source object ────────────────────────────────────────────────
@Serializable
data class Source(
    val id   : String? = null,
    val name : String  = "Unknown"
)

// ── Article data class ───────────────────────────────────────────
@Serializable
data class Article(
    val source      : Source  = Source(),
    val author      : String? = null,
    val title       : String  = "",
    val description : String? = null,
    val url         : String  = "",
    @SerialName("urlToImage")
    val urlToImage  : String? = null,
    @SerialName("publishedAt")
    val publishedAt : String  = "",
    val content     : String? = null
) {
    // ID unik dari URL (NewsAPI tidak punya integer ID)
    val id: String
        get() = url.hashCode().toString()

    // Judul yang aman (tidak null)
    val safeTitle: String
        get() = title.ifBlank { "Tanpa Judul" }

    // Deskripsi yang aman
    val safeDescription: String
        get() = description ?: "Tidak ada deskripsi tersedia."

    // Nama sumber berita
    val sourceName: String
        get() = source.name.ifBlank { "Unknown Source" }

    // Format tanggal: "2024-01-15T10:30:00Z" → "15 Jan 2024"
    val formattedDate: String
        get() {
            return try {
                // Parse: 2024-01-15T10:30:00Z
                val parts = publishedAt.split("T")[0].split("-")
                val months = listOf(
                    "", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                    "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                )
                val month = parts[1].toIntOrNull() ?: 0
                "${parts[2]} ${months.getOrElse(month) { "?" }} ${parts[0]}"
            } catch (e: Exception) {
                publishedAt.take(10)
            }
        }

    // Estimasi waktu baca dari content
    val readTime: String
        get() {
            val words   = (content ?: description ?: "").split(" ").size
            val minutes = maxOf(1, words / 200)
            return "$minutes menit baca"
        }

    // Preview untuk list (150 karakter)
    val preview: String
        get() = safeDescription.let {
            if (it.length > 150) it.take(150) + "..." else it
        }

    // Kategori berdasarkan source name (simulasi)
    val category: String
        get() = when {
            sourceName.contains("tech", ignoreCase = true)     -> "Teknologi"
            sourceName.contains("sport", ignoreCase = true)    -> "Olahraga"
            sourceName.contains("business", ignoreCase = true) -> "Bisnis"
            sourceName.contains("health", ignoreCase = true)   -> "Kesehatan"
            sourceName.contains("science", ignoreCase = true)  -> "Sains"
            else -> "Umum"
        }

    // Warna avatar fallback (saat gambar tidak ada)
    val avatarColor: Long
        get() = when (category) {
            "Teknologi" -> 0xFF1565C0L
            "Olahraga"  -> 0xFF2E7D32L
            "Bisnis"    -> 0xFFE65100L
            "Kesehatan" -> 0xFFAD1457L
            "Sains"     -> 0xFF6A1B9AL
            else        -> 0xFF00897BL
        }
}
