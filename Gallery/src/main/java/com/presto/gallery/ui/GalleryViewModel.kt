package com.presto.gallery.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.presto.gallery.Photo
import com.presto.gallery.PhotoRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class GalleryViewModel(private val photoRepository: PhotoRepository) : ViewModel() {

    val photosLiveData = MutableLiveData<ViewState>()

    private val pagesSubject = PublishSubject.create<ViewState>()
    private val querySubject = PublishSubject.create<String>()
    private val disposable = Observables.combineLatest(querySubject.startWith(""), pagesSubject.startWith(ViewState.default("")))
            .filter { (_, viewState) -> viewState.currentPage == 0 || viewState.currentPage < viewState.totalPages }
            .switchMap { (query, oldViewState) ->
                val isNewQuery = query != oldViewState.query
                val currentPage = if (isNewQuery) 0 else oldViewState.currentPage
                photoRepository.search(query, currentPage + 1, 30).toObservable()
                        .map<ViewState> { result ->
                            oldViewState.copy(
                                    query = query,
                                    currentPage = result.currentPage,
                                    totalPages = result.totalPages,
                                    photos = if (isNewQuery) result.photos else oldViewState.photos.toMutableList().apply { addAll(result.photos) },
                                    isLoading = false)
                        }
                        .onErrorResumeNext(Observable.empty<ViewState>())
                        .startWith(oldViewState.copy(query = query, isLoading = true))
                        .subscribeOn(Schedulers.io())
            }
            .scan { oldState, newState ->
                val diffResult = DiffUtil.calculateDiff(PhotosDiffUtilCallback(oldState.photos, newState.photos))
                newState.copy(diffResult = diffResult)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(photosLiveData::postValue)


    override fun onCleared() {
        disposable.dispose()
    }

    fun nextPage() {
        photosLiveData.value?.let(pagesSubject::onNext)
    }

    fun setQuery(query: String) {
        querySubject.onNext(query)
    }

    data class ViewState(val query: String,
                         val currentPage: Int,
                         val totalPages: Int,
                         val photos: List<Photo>,
                         val isLoading: Boolean,
                         val diffResult: DiffUtil.DiffResult?) {

        override fun toString(): String {
            return "ViewState(query=$query, currentPage=$currentPage, totalPages=$totalPages, photos=[${photos.size}], isLoading=$isLoading, diffResult=$diffResult)"
        }

        companion object {
            fun default(query: String) = ViewState(query, 0, 0, listOf(), false, null)
        }
    }

    class PhotosDiffUtilCallback(private val oldPhotos: List<Photo>, private val newPhotos: List<Photo>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPhotos[oldItemPosition].id == newPhotos[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldPhotos.size
        }

        override fun getNewListSize(): Int {
            return newPhotos.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPhotos[oldItemPosition] == newPhotos[newItemPosition]
        }

    }
}