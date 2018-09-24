package com.presto.gallery.flickr

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

interface FlickrApi {

    @GET("services/rest/?api_key=949e98778755d1982f537d56236bbb42&method=flickr.photos.search&format=json&nojsoncallback=1")
    fun search(@Query("text") text: String,
               @Query("page") page: Int,
               @Query("per_page") count: Int): Single<SearchResponse>


    data class SearchResult(val page: Int,
                            val pages: Int,
                            @SerializedName("perpage") val perPage: Int,
                            @SerializedName("photo") val photos: List<FlickrPhoto>)

    data class SearchResponse(@SerializedName("photos") val result: SearchResult)


    companion object {
        fun create(gson: Gson, okHttpClient: OkHttpClient): FlickrApi {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.flickr.com/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            return retrofit.create(FlickrApi::class.java)
        }
    }
}

