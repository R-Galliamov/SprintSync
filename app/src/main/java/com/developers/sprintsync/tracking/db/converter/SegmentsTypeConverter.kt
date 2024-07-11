package com.developers.sprintsync.tracking.db.converter

import androidx.room.TypeConverter
import com.developers.sprintsync.tracking.model.track.Segment
import com.developers.sprintsync.tracking.model.track.Segments
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import org.json.JSONArray

class SegmentsTypeConverter {
    private val segmentAdapterFactory: RuntimeTypeAdapterFactory<Segment> =
        RuntimeTypeAdapterFactory
            .of(Segment::class.java, TYPE_FIELD)
            .registerSubtype(
                Segment.ActiveSegment::class.java,
                SegmentType.ACTIVE.lowercaseName,
            ).registerSubtype(
                Segment.InactiveSegment::class.java,
                SegmentType.INACTIVE.lowercaseName,
            )

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapterFactory(segmentAdapterFactory)
            .create()

    @TypeConverter
    fun fromSegmentList(segments: Segments): String {
        val jsonArray = JSONArray()
        for (segment in segments) {
            val segmentString = segmentToJson(segment)
            jsonArray.put(segmentString)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toSegmentList(segmentsString: String): Segments {
        val jsonArray = JSONArray(segmentsString)
        return (0 until jsonArray.length())
            .map { jsonArray.getString(it) }
            .map { gson.fromJson(it, Segment::class.java) }
    }

    private fun segmentToJson(segment: Segment): String {
        val jsonObject = gson.toJsonTree(segment).asJsonObject
        val type =
            when (segment) {
                is Segment.ActiveSegment -> SegmentType.ACTIVE.lowercaseName
                is Segment.InactiveSegment -> SegmentType.INACTIVE.lowercaseName
            }
        jsonObject.addProperty(TYPE_FIELD, type)
        return gson.toJson(jsonObject)
    }

    companion object {
        private const val TYPE_FIELD = "type"

        private enum class SegmentType {
            ACTIVE,
            INACTIVE,
            ;

            val lowercaseName: String get() = name.lowercase()
        }
    }
}
