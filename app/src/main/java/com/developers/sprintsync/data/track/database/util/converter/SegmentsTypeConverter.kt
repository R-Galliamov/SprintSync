package com.developers.sprintsync.data.track.database.util.converter

import androidx.room.TypeConverter
import com.developers.sprintsync.domain.track.model.Segment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SegmentsTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromSegmentList(segments: List<Segment>?): String? {
        if (segments == null) {
            return null
        }
        return gson.toJson(segments)
    }

    @TypeConverter
    fun toSegmentList(segmentsString: String?): List<Segment>? {
        if (segmentsString == null) {
            return null
        }
        val listType = object : TypeToken<List<Segment>>() {}.type
        return gson.fromJson(segmentsString, listType)
    }
}

