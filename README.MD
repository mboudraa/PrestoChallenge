# Gallery Challenge
## Problem to solve
Develop a small Image Gallery app based on Flickr search API.
The project must be organized in a way that it has to be scalable to accept more sources in the future.
The gallery must display for each image: the size, the dimensions and the title.

## Solution
### Adding a source to fetch Photos
Each photo source relies on 3 components

- A Glide Component responsible for fetching photos from a dedicated source. For Flickr it's `FlickrGlideUrlLoader`
- A API to actually search photos based on a text query. For Flickr it's `FlickrApi`
- An implementation of 

```kotlin
interface Photo {
    val id: String
    Val title: String
}
```

For Flickr it's `FlickrPhoto`

### Combining all the sources together
Searching a photo goes through `PhotoRepository` is responsible to combine all the sources api together and return a `Single<SearchResult>`

This API is paginated.


```kotlin
class PhotoRepository(...) {

    fun search(text: String, page: Int, count: Int): Single<SearchResult> 
    
    data class SearchResult(val currentPage: Int,
                            val totalPages: Int,
                            val perPage: Int,
                            val photos: List<Photo>)
}
```

### UI
UI relies on 3 components

- The **ViewState**: Responsible for holding the state of the view to render
- The **ViewModel**: Responsible for querying the `PhotoRepository` and transforming the data into a `ViewState`
- The **GalleryActivity**: Responsible for observing the `ViewState` from the `ViewModel` and render the view.


## What I would improve

- User Dagger for decency injection
- Better way to manage the state so I can save it in a database to provide offline features
- CustomView for the Gallery to avoid having all the code in the Activity
- Write Unit Tests
