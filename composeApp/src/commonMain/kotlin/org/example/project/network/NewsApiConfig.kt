package org.example.project.network

// ================================================================
// NewsApiConfig.kt — Konfigurasi API Key NewsAPI
//
// PENTING: Ganti API_KEY dengan key kamu dari newsapi.org
//
// Cara mendapatkan API key GRATIS:
//   1. Buka https://newsapi.org/register
//   2. Daftar dengan email
//   3. Copy API key dari dashboard
//   4. Paste di bawah (ganti "MASUKKAN_API_KEY_KAMU_DI_SINI")
//
// Free plan: 100 request/hari, hanya endpoint /everything & /top-headlines
// ================================================================

object NewsApiConfig {

    // ⚠️ GANTI INI DENGAN API KEY KAMU ⚠️
    const val API_KEY = "609e7659de0e485a93a98494df99efe0"

    const val BASE_URL = "https://newsapi.org/v2"

    // Parameter default
    const val DEFAULT_COUNTRY  = "us"       // us = berita Amerika (paling banyak)
    const val DEFAULT_LANGUAGE = "en"       // en = Bahasa Inggris
    const val DEFAULT_PAGE_SIZE = 20        // 20 artikel per halaman
}
