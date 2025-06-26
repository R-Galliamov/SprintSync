package com.developers.sprintsync.data.core

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import org.json.JSONArray

abstract class SealedTypeConverter<T : Any>(
    private val sealedClass: Class<T>
) {

    private val adapterFactory: RuntimeTypeAdapterFactory<T> by lazy {
        createAdapterFactory()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapterFactory(adapterFactory)
            .create()
    }

    @TypeConverter
    fun fromList(items: List<T>): String {
        val jsonArray = JSONArray()
        for (item in items) {
            val itemString = itemToJson(item)
            jsonArray.put(itemString)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toList(itemsString: String): List<T> {
        val jsonArray = JSONArray(itemsString)
        return (0 until jsonArray.length())
            .map { jsonArray.getString(it) }
            .map { gson.fromJson(it, sealedClass) }
    }

    private fun createAdapterFactory(): RuntimeTypeAdapterFactory<T> {
        val factory = RuntimeTypeAdapterFactory.of(sealedClass, CLASS_TYPE_FIELD)

        val nestedClasses = sealedClass.declaredClasses

        for (nestedClass in nestedClasses) {
            @Suppress("UNCHECKED_CAST")
            val subtype = nestedClass as Class<out T>
            val typeName = getTypeNameFromClass(nestedClass)
            factory.registerSubtype(subtype, typeName)
        }

        return factory
    }

    private fun itemToJson(item: T): String {
        val jsonObject = gson.toJsonTree(item).asJsonObject
        val typeName = getTypeNameFromClass(item::class.java)
        jsonObject.addProperty(CLASS_TYPE_FIELD, typeName)
        return gson.toJson(jsonObject)
    }

    private fun getTypeNameFromClass(clazz: Class<*>): String {
        return clazz.simpleName
            .removeSuffix("Dto")
            .removeSuffix("Data")
            .removeSuffix("Model")
            .let { convertCamelCaseToSnakeCase(it) }
    }

    private fun convertCamelCaseToSnakeCase(input: String): String {
        return input.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }

    companion object {
        const val CLASS_TYPE_FIELD = "__class_type"
    }
}