package com.developers.sprintsync.data.core

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory

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
        val jsonStrings: List<String> = items.map { itemToJson(it) }
        return gson.toJson(jsonStrings)
    }

    @TypeConverter
    fun toList(itemsString: String): List<T> {
        val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
        val jsonStrings: List<String> = gson.fromJson(itemsString, type)
        return jsonStrings.map { gson.fromJson(it, sealedClass) }
    }

    private fun createAdapterFactory(): RuntimeTypeAdapterFactory<T> {
        val factory = RuntimeTypeAdapterFactory.of(sealedClass, CLASS_TYPE_FIELD)

        val nestedClasses = sealedClass.declaredClasses

        nestedClasses.forEach { nestedClass ->
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
    }

    companion object {
        const val CLASS_TYPE_FIELD = "class_type"
    }
}