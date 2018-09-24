package com.presto.gallery.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.Util
import com.presto.gallery.GlideApp
import com.presto.gallery.Photo
import com.presto.gallery.R

class GalleryAdapter(context: Context) : RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder>() {
    private val photos = arrayListOf<Photo>()
    private val glideRequest = GlideApp.with(context).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false) as PhotoView
        return PhotoViewHolder(imageView)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        glideRequest.load(photo)
                .placeholder(ColorDrawable(Color.LTGRAY))
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(PhotoViewBinder(photo, holder))
    }

    fun setData(photos: List<Photo>, diffResult: DiffUtil.DiffResult?) {
        this.photos.clear()
        this.photos.addAll(photos)
        diffResult?.dispatchUpdatesTo(this) ?: notifyDataSetChanged()
    }

    private inner class PhotoViewBinder(private val photo: Photo, private val holder: PhotoViewHolder) : CustomViewTarget<PhotoView, Bitmap>(holder.itemView as PhotoView), Transition.ViewAdapter {

        override fun getCurrentDrawable(): Drawable? {
            return (holder.itemView as PhotoView).drawable
        }

        override fun setDrawable(drawable: Drawable?) {
            (holder.itemView as PhotoView).setImageDrawable(drawable)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            setDrawable(errorDrawable)
        }

        override fun onResourceLoading(placeholder: Drawable?) {
            setDrawable(placeholder)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            (holder.itemView as PhotoView).reset()
            setDrawable(placeholder)
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val width = resource.width
            val height = resource.height
            val size = Util.getBitmapByteSize(resource) / 1024
            handler.post {
                view.setMetaData(PhotoView.MetaData(photo.title, size, width, height))
                view.setImageBitmap(resource)
            }
        }
    }


    class PhotoViewHolder(view: PhotoView) : RecyclerView.ViewHolder(view)
}