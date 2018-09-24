package com.presto.gallery

import com.google.gson.GsonBuilder
import com.presto.gallery.flickr.FlickrApi
import com.presto.gallery.flickr.FlickrPhoto
import com.presto.gallery.flickr.FlickrPhotoDeserializer
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class PhotoRepository(private val flickrApi: FlickrApi) {

    fun search(text: String, page: Int, count: Int): Single<SearchResult> {
        return flickrApi.search(text, page, count).map { it.result.toSearchResult() }
    }

    private fun FlickrApi.SearchResult.toSearchResult(): SearchResult {
        return SearchResult(page, pages, perPage, photos)
    }

    companion object {
        fun create(): PhotoRepository {
            val gson = GsonBuilder()
                    .registerTypeAdapter(FlickrPhoto::class.java, FlickrPhotoDeserializer())
                    .create()
            val okClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()

            return PhotoRepository(FlickrApi.create(gson, okClient))
        }
    }

    data class SearchResult(val currentPage: Int,
                            val totalPages: Int,
                            val perPage: Int,
                            val photos: List<Photo>)
}