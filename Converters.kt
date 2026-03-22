package com.aozora.knowledgevault.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagListConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTagList(tags: List<String>): String {
        return gson.toJson(tags)
    }
    
    @TypeConverter
    fun toTagList(tagsString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(tagsString, type) ?: emptyList()
    }
}
