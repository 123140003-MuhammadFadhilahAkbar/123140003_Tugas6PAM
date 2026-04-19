package org.example.project.network

// ================================================================
// NewsApi.kt — API Layer untuk NewsAPI (Pertemuan 6)
//
// Endpoints yang dipakai:
//   GET /v2/top-headlines  → Berita terkini (by country)
//   GET /v2/everything     → Semua berita (by query/keyword)
//
// Dokumentasi: https://newsapi.org/docs/endpoints
// ================================================================

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.project.model.NewsResponse

class NewsApi(private val client: HttpClient) {

    private val baseUrl = NewsApiConfig.BASE_URL

    // ── GET top headlines (berita terkini) ───────────────────────
    // Dipakai untuk: tab utama News List
    suspend fun getTopHeadlines(
        country  : String = NewsApiConfig.DEFAULT_COUNTRY,
        pageSize : Int    = NewsApiConfig.DEFAULT_PAGE_SIZE,
        page     : Int    = 1
    ): NewsResponse {
        return client.get("$baseUrl/top-headlines") {
            parameter("country",  country)
            parameter("pageSize", pageSize)
            parameter("page",     page)
        }.body()
    }

    // ── GET berita berdasarkan keyword ───────────────────────────
    // Dipakai untuk: fitur Search
    suspend fun searchNews(
        query    : String,
        pageSize : Int = NewsApiConfig.DEFAULT_PAGE_SIZE,
        language : String = NewsApiConfig.DEFAULT_LANGUAGE
    ): NewsResponse {
        return client.get("$baseUrl/everything") {
            parameter("q",        query)
            parameter("pageSize", pageSize)
            parameter("language", language)
            parameter("sortBy",   "publishedAt")  // Terbaru dulu
        }.body()
    }

    // ── GET berita by kategori ───────────────────────────────────
    // Dipakai untuk: filter kategori
    // Kategori valid: business, entertainment, general,
    //                 health, science, sports, technology
    suspend fun getByCategory(
        category : String,
        country  : String = NewsApiConfig.DEFAULT_COUNTRY,
        pageSize : Int    = NewsApiConfig.DEFAULT_PAGE_SIZE
    ): NewsResponse {
        return client.get("$baseUrl/top-headlines") {
            parameter("category", category)
            parameter("country",  country)
            parameter("pageSize", pageSize)
        }.body()
    }
}
