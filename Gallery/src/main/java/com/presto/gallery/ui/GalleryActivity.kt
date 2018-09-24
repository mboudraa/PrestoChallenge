package com.presto.gallery.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.presto.gallery.BaseActivity
import com.presto.gallery.GlideApp
import com.presto.gallery.PhotoRepository
import com.presto.gallery.R
import kotlin.LazyThreadSafetyMode.NONE

class GalleryActivity : BaseActivity(R.layout.gallery) {

    private val searchView by lazy(NONE) { findViewById<SearchView>(R.id.gallery_searchview) }
    private val recyclerView by lazy(NONE) { findViewById<RecyclerView>(R.id.gallery_recyclerview) }
    private val galleryAdapter by lazy(NONE) { GalleryAdapter(this) }


    private val viewModel by lazyViewModel { GalleryViewModel(PhotoRepository.create()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.gallery_toolbar))
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.setQuery(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@GalleryActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val gridMargin = resources.getDimensionPixelOffset(R.dimen.grid_margin)
                    outRect.set(gridMargin, gridMargin, gridMargin, gridMargin)
                }
            })
            setRecyclerListener { viewHolder -> GlideApp.with(this).clear(viewHolder.itemView) }
            adapter = galleryAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1)) viewModel.nextPage()
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.photosLiveData.observe(this, Observer { viewState ->
            if (viewState.isLoading) Toast.makeText(this, "Loading", Toast.LENGTH_LONG).show()
            galleryAdapter.setData(viewState.photos, viewState.diffResult)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.photosLiveData.removeObservers(this)
    }

}

inline fun <reified VM : ViewModel> FragmentActivity.lazyViewModel(crossinline factory: () -> VM) = lazy(NONE) {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    }).get(VM::class.java)
}