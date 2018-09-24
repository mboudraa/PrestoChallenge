package com.presto.gallery.flickr

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.presto.gallery.Photo
import java.lang.reflect.Type

data class FlickrPhoto(override val id: String,
                       val farm: String,
                       val owner: String,
                       val server: String,
                       val secret: String,
                       override val title: String) : Photo {
}

class FlickrPhotoDeserializer : JsonDeserializer<FlickrPhoto> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): FlickrPhoto {
        return json.asJsonObject.run {
            FlickrPhoto(
                    get("id").asString,
                    get("farm").asString,
                    get("owner").asString,
                    get("server").asString,
                    get("secret").asString,
                    get("title").asString)
        }

    }

}