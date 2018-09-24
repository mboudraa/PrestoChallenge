package com.presto.gallery.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.presto.gallery.R


class PhotoView @JvmOverloads constructor(context: Context,
                                          attrs: AttributeSet? = null,
                                          defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val imageView by lazy(LazyThreadSafetyMode.NONE) { findViewById<ImageView>(R.id.photoview_image) }
    private val metadataView by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.photoview_metadata) }

    val drawable: Drawable?
        get() = imageView.drawable

    fun reset() {
        setImageBitmap(null)
        metadataView.text = null
    }

    fun setMetaData(data: MetaData) {
        metadataView.text = "${data.width} x ${data.height} - ${data.size}KB - ${data.title}"
    }


    fun setImageDrawable(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    data class MetaData(val title: String, val size: Int, val width: Int, val height: Int)
}
