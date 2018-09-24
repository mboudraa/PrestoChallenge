package com.presto.gallery.flickr

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelCache
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import java.io.InputStream
import kotlin.math.max

class FlickrGlideUrlLoader private constructor(private val photoUrlBuilder: FlickrPhotoUrlBuilder,
                                               modelLoader: ModelLoader<GlideUrl, InputStream>,
                                               modelCache: ModelCache<FlickrPhoto, GlideUrl>) : BaseGlideUrlLoader<FlickrPhoto>(modelLoader, modelCache) {

    override fun getUrl(model: FlickrPhoto, width: Int, height: Int, options: Options?): String {
        return photoUrlBuilder.getPhotoUrl(model, width, height)
    }

    override fun getAlternateUrls(model: FlickrPhoto, width: Int, height: Int, options: Options?): MutableList<String> {
        return photoUrlBuilder.getAlternateUrls(model, width, height)
    }

    override fun handles(model: FlickrPhoto): Boolean = true

    companion object Factory : ModelLoaderFactory<FlickrPhoto, InputStream> {
        private val cache = ModelCache<FlickrPhoto, GlideUrl>()

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<FlickrPhoto, InputStream> {
            return FlickrGlideUrlLoader(
                    FlickrPhotoUrlBuilder(),
                    multiFactory.build(GlideUrl::class.java, InputStream::class.java),
                    cache)
        }

        override fun teardown() {
        }
    }
}


class FlickrPhotoUrlBuilder {

    fun getPhotoUrl(photo: FlickrPhoto, width: Int, height: Int): String {
        return getPhotoUrl(photo, getOptimalFlickrSize(width, height))
    }

    fun getAlternateUrls(photo: FlickrPhoto, width: Int, height: Int): MutableList<String> {
        return getLargestFlickrSizes(width, height).mapTo(mutableListOf()) { getPhotoUrl(photo, it) }
    }

    private fun getOptimalFlickrSize(width: Int, height: Int): Int {
        val size = max(width, height)
        return FLICKR_SIZE_SET.find { size <= it } ?: FLICKR_SIZE_SET[FLICKR_SIZE_SET.size - 1]
    }

    private fun getLargestFlickrSizes(width: Int, height: Int): List<Int> {
        val size = max(width, height)
        return FLICKR_SIZE_SET.filter { size > it }
    }

    private fun getPhotoUrl(photo: FlickrPhoto, flickrSize: Int): String {
        return String.format(FLICKR_PHOTO_URL, photo.farm, photo.server, photo.id, photo.secret, FLICKR_SIZE_MAP[flickrSize])
    }

    companion object {
        private const val FLICKR_PHOTO_URL = "http://farm%s.staticflickr.com/%s/%s_%s_%s.jpg"
        private val FLICKR_SIZE_MAP = linkedMapOf(
                75 to "s",
                100 to "t",
                150 to "q",
                240 to "m",
                320 to "n",
                640 to "z",
                1024 to "b"
        )
        private val FLICKR_SIZE_SET = FLICKR_SIZE_MAP.keys.toList()
    }
}
