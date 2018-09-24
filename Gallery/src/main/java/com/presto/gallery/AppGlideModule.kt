package com.presto.gallery

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.presto.gallery.flickr.FlickrGlideUrlLoader
import com.presto.gallery.flickr.FlickrPhoto
import java.io.InputStream

@GlideModule
class AppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCalculator = MemorySizeCalculator.Builder(context)
                .setBitmapPoolScreens(3f)
                .setMemoryCacheScreens(3f)
                .build();
        builder
                .setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig())
                .setDiskCache(InternalCacheDiskCacheFactory(context, 1024 * 1024 * 100))
                .setBitmapPool(LruBitmapPool(memoryCalculator.bitmapPoolSize.toLong()))
                .setMemoryCache(LruResourceCache(memoryCalculator.memoryCacheSize.toLong()))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(FlickrPhoto::class.java, InputStream::class.java, FlickrGlideUrlLoader.Factory)
    }

    override fun isManifestParsingEnabled(): Boolean = false
}