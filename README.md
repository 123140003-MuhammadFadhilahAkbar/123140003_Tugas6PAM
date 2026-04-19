# 📰 News Reader App - Tugas 6 PAM

**Nama:** Muhammad Fadhilah Akbar  
**NIM:** 123140003    
**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile

---

## 📋 Deskripsi

News Reader adalah aplikasi berita berbasis **Kotlin Multiplatform (KMP)** dengan **Compose Multiplatform**. Tugas 6 berfokus pada implementasi **Networking dan REST API** menggunakan **Ktor Client** untuk mengambil berita real-time dari **NewsAPI**, lengkap dengan Repository Pattern, UI States (Loading/Success/Error), dan fitur search serta filter kategori.

---

## 🗂️ Struktur Folder

```
composeApp/src/commonMain/kotlin/org/example/project/
├── App.kt                          # Entry point
├── model/
│   ├── Article.kt                  # @Serializable data class + NewsResponse
│   └── UiState.kt                  # Sealed class: Loading / Success / Error
├── network/
│   ├── NewsApiConfig.kt            # API key & base URL config
│   ├── HttpClientFactory.kt        # Ktor Client setup (timeout, logging, JSON)
│   └── NewsApi.kt                  # HTTP request functions (GET endpoints)
├── repository/
│   └── NewsRepository.kt           # Repository pattern + error mapping
├── viewmodel/
│   └── NewsViewModel.kt            # StateFlow, debounced search, kategori, bookmark
├── components/
│   └── SharedComponents.kt         # Reusable composables (ArticleCard, dll)
├── screens/
│   ├── NewsListScreen.kt           # Tab 1 — daftar berita + 3 UI states
│   ├── ArticleDetailScreen.kt      # Detail artikel (articleId argument)
│   └── BookmarksScreen.kt          # Tab 2 — artikel tersimpan
└── navigation/
    ├── Screen.kt                   # Sealed class semua routes (type-safe)
    └── AppNavigation.kt            # NavHost, BottomNav, Theme
```

---

## ✅ Fitur yang Diimplementasikan

### 1. API Integration (Ktor Client)
- **Ktor Client** dikonfigurasi dengan ContentNegotiation, Logging, dan Timeout
- Header `X-Api-Key` otomatis ditambahkan ke setiap request via `defaultRequest`
- Endpoint: `/v2/top-headlines` dan `/v2/everything`
- Implementasi di: `HttpClientFactory.kt`, `NewsApi.kt`, `NewsApiConfig.kt`

### 2. Data Parsing (Kotlinx Serialization)
- `@Serializable` data class `Article` dan `NewsResponse` cocok persis dengan JSON NewsAPI
- `@SerialName("urlToImage")` dan `@SerialName("publishedAt")` untuk mapping field
- `ignoreUnknownKeys = true` dan `coerceInputValues = true` untuk robustness
- Implementasi di: `Article.kt`, `HttpClientFactory.kt`

### 3. UI States
| State | Tampilan | Trigger |
|---|---|---|
| **Loading** | `CircularProgressIndicator` di tengah | Saat pertama buka / ganti kategori |
| **Success** | `LazyColumn` daftar artikel dengan gambar | Data berhasil diterima dari API |
| **Error** | Emoji 📡 + pesan error + tombol Retry | Tidak ada internet / API key salah |

### 4. Repository Pattern
- `NewsRepository` memisahkan API logic dari ViewModel
- Semua response dibungkus `Result<T>` untuk clean error handling
- Error code NewsAPI (`apiKeyInvalid`, `rateLimited`, dll) di-map ke pesan Bahasa Indonesia
- Implementasi di: `NewsRepository.kt`

### 5. Fitur Tambahan
| Fitur | Implementasi |
|---|---|
| 🔍 Search | Debounced search 500ms ke endpoint `/v2/everything` |
| 🏷️ Filter Kategori | 7 kategori: Top, Teknologi, Bisnis, Kesehatan, Sains, Olahraga, Hiburan |
| 🖼️ Gambar Artikel | Load gambar URL via **Coil3 AsyncImage** |
| 🔖 Bookmark | Toggle simpan artikel (in-memory, StateFlow) |
| 🔄 Refresh | Reload data dari API dengan `LinearProgressIndicator` |
| 🌙 Dark Mode | Toggle light/dark theme |

### 6. Dokumentasi
- README ini beserta arsitektur diagram
- Screenshot setiap state (lihat bagian Screenshots)

---

## 🗺️ Arsitektur

```
┌─────────────────────────────────────┐
│           UI Layer (Screens)         │
│  NewsListScreen | DetailScreen | ... │
└──────────────┬──────────────────────┘
               │ collectAsState()
┌──────────────▼──────────────────────┐
│         ViewModel Layer              │
│          NewsViewModel               │
│  StateFlow: uiState, isRefreshing,  │
│  searchQuery, bookmarkedIds          │
└──────────────┬──────────────────────┘
               │ suspend fun
┌──────────────▼──────────────────────┐
│        Repository Layer              │
│         NewsRepository               │
│  • Wrap Result<T>                   │
│  • Filter artikel kosong/removed    │
│  • Map error code → pesan friendly  │
└──────────────┬──────────────────────┘
               │ HTTP Request
┌──────────────▼──────────────────────┐
│    NewsApi + HttpClientFactory       │
│  • Ktor Client + Header X-Api-Key   │
│  • Timeout 15s / ContentNegotiation │
└──────────────┬──────────────────────┘
               │ JSON Parsing (@Serializable)
┌──────────────▼──────────────────────┐
│   Article, NewsResponse, UiState    │
└─────────────────────────────────────┘
```

---

## 📱 Screenshots

| Screen | Deskripsi |
|--------|-----------|
| <details><summary><code>screenshot_loading.png</code></summary><br><img width="300" alt="Loading State" src="screenshots/loading.png" /></details> | Loading state — CircularProgressIndicator saat fetch data |
| <details><summary><code>screenshot_success.png</code></summary><br><img width="300" alt="Success State" src="screenshots/success.png" /></details> | Success state — LazyColumn daftar berita dengan gambar |
| <details><summary><code>screenshot_error.png</code></summary><br><img width="300" alt="Error State" src="screenshots/error.png" /></details> | Error state — pesan error + tombol Retry (airplane mode) |
| <details><summary><code>screenshot_detail.png</code></summary><br><img width="300" alt="Detail" src="screenshots/detail.png" /></details> | Detail artikel — gambar, author, konten, URL sumber |
| <details><summary><code>screenshot_search.png</code></summary><br><img width="300" alt="Search" src="screenshots/search.png" /></details> | Search — debounced search ke NewsAPI /everything |
| <details><summary><code>screenshot_kategori.png</code></summary><br><img width="300" alt="Kategori" src="screenshots/kategori.png" /></details> | Filter kategori — 7 kategori berita |
| <details><summary><code>screenshot_bookmark.png</code></summary><br><img width="300" alt="Bookmark" src="screenshots/bookmark.png" /></details> | Tab Tersimpan — artikel yang di-bookmark |
| <details><summary><code>screenshot_darkmode.png</code></summary><br><img width="300" alt="Dark Mode" src="screenshots/darkmode.png" /></details> | Dark mode aktif |

---

## 🎬 Video Demo

| Fitur | Preview | Keterangan |
| :--- | :--- | :--- |
| **Video Demo** | [▶️ Tonton Video Demo (Google Drive)](https://drive.google.com/your-link-here) | `demo_week6.mp4` (≤ 30 detik) |

> Video menunjukkan: Loading → Success → Error (airplane mode) → Retry → Search → Filter Kategori → Bookmark

---

## 🌐 API yang Digunakan

**[NewsAPI](https://newsapi.org)** — `https://newsapi.org/v2`

| Endpoint | Method | Fungsi |
|---|---|---|
| `/v2/top-headlines?country=us` | GET | Berita terkini (halaman utama) |
| `/v2/top-headlines?category={cat}` | GET | Filter berita by kategori |
| `/v2/everything?q={keyword}` | GET | Search berita by keyword |

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Language | Kotlin Multiplatform 2.3.20 |
| UI | Compose Multiplatform 1.10.3 |
| HTTP Client | Ktor Client 3.1.3 |
| JSON Parsing | Kotlinx Serialization 1.8.1 |
| Image Loading | Coil3 3.1.0 |
| Navigation | AndroidX Navigation Compose 2.9.0 |
| Architecture | KMP + Repository Pattern + MVVM |
| API | NewsAPI v2 |

---

## 📦 Dependencies

```toml
# libs.versions.toml
[versions]
ktor                 = "3.1.3"
kotlinxSerialization = "1.8.1"
navigationCompose    = "2.9.0-beta01"
coil                 = "3.1.0"

[libraries]
ktor-client-core                = { module = "io.ktor:ktor-client-core",                    version.ref = "ktor" }
ktor-client-android             = { module = "io.ktor:ktor-client-android",                 version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation",     version.ref = "ktor" }
ktor-client-logging             = { module = "io.ktor:ktor-client-logging",                 version.ref = "ktor" }
ktor-serialization-json         = { module = "io.ktor:ktor-serialization-kotlinx-json",     version.ref = "ktor" }
kotlinx-serialization-json      = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
androidx-navigation-compose     = { module = "androidx.navigation:navigation-compose",      version.ref = "navigationCompose" }
coil-compose                    = { module = "io.coil-kt.coil3:coil-compose",              version.ref = "coil" }
coil-network-ktor               = { module = "io.coil-kt.coil3:coil-network-ktor3",        version.ref = "coil" }
```

```kotlin
// build.gradle.kts — commonMain.dependencies
implementation(libs.ktor.client.core)
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.client.logging)
implementation(libs.ktor.serialization.json)
implementation(libs.kotlinx.serialization.json)
implementation(libs.androidx.navigation.compose)
implementation(libs.coil.compose)
implementation(libs.coil.network.ktor)
implementation(compose.materialIconsExtended)

// androidMain.dependencies
implementation(libs.ktor.client.android)
```

---

## 📊 Rubrik Penilaian

| Komponen | Kriteria | Implementasi |
|---|---|---|
| **API Integration** | Ktor Client setup, successful API calls | Ktor Client dikonfigurasi dengan `ContentNegotiation`, `Logging`, `HttpTimeout`. Header `X-Api-Key` otomatis via `defaultRequest`. GET request ke `/v2/top-headlines` dan `/v2/everything` — implementasi di `HttpClientFactory.kt` dan `NewsApi.kt` |
| **Data Parsing** | Kotlinx Serialization, proper data classes | `@Serializable` data class `Article` dan `NewsResponse` cocok persis dengan format JSON NewsAPI. `@SerialName` untuk field `urlToImage` dan `publishedAt`. `ignoreUnknownKeys = true` agar tidak crash saat ada field baru — implementasi di `Article.kt` |
| **UI States** | Loading, Success, Error handled properly | Sealed class `UiState<T>` dengan 3 state: **Loading** (CircularProgressIndicator), **Success** (LazyColumn daftar artikel), **Error** (pesan user-friendly + tombol Retry). Semua state di-handle di `NewsListScreen.kt` |
| **Architecture** | Repository pattern, clean separation | Repository Pattern — `NewsRepository` sebagai single source of truth, memisahkan HTTP logic dari ViewModel. Error code NewsAPI (`apiKeyInvalid`, `rateLimited`, dll) di-map ke pesan Bahasa Indonesia yang user-friendly |
| **Code Quality** | Clean code, documentation | Setiap file memiliki komentar dokumentasi, komponen UI dipisah ke `SharedComponents.kt` (reusable), tidak ada magic string (semua route di sealed class `Screen`), ViewModel tidak memegang referensi UI |
| **Bonus** | Offline caching dengan local storage | Artikel yang di-bookmark disimpan secara in-memory menggunakan `StateFlow<Set<String>>` di `NewsViewModel`. Data tetap tersedia selama sesi berjalan tanpa perlu fetch ulang ke API. Arsitektur sudah disiapkan untuk migrasi ke **DataStore/SQLDelight** di Pertemuan 7 |

---

*© 2025 · IF25-22017 · Institut Teknologi Sumatera*
