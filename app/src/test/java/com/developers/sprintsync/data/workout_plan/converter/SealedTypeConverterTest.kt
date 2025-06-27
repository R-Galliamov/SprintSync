package com.developers.sprintsync.data.workout_plan.converter

import com.developers.sprintsync.data.core.SealedTypeConverter
import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

sealed class TestSealed {
    data class TypeOne(val value: String, val count: Int) : TestSealed()
    data class TypeTwo(val flag: Boolean, val items: List<String>) : TestSealed()
    data class TypeThree(val id: Long) : TestSealed()
}

class TestSealedTypeConverter : SealedTypeConverter<TestSealed>(TestSealed::class.java)

class SealedTypeConverterTest {

    private lateinit var converter: TestSealedTypeConverter

    @BeforeEach
    fun setUp() {
        converter = TestSealedTypeConverter()
    }

    @Test
    fun `fromList empty produces json array string and round-trips`() {
        val json = converter.fromList(emptyList())
        assertThat(json).isEqualTo("[]")
        val restored = converter.toList(json)
        assertThat(restored).isEmpty()
    }

    @Test
    fun `fromList and toList with single object - data and type marker`() {
        val item = TestSealed.TypeOne("abc", 5)
        val json = converter.fromList(listOf(item))
        val arr = Gson().fromJson(json, Array<Any>::class.java)
        assertThat(arr).hasSize(1)

        // Deserialize and verify fields
        val restored = converter.toList(json)
        assertThat(restored).containsExactly(item)

        // Check type marker
        val asObj = Gson().fromJson(arr[0].toString(), Map::class.java)
        assertThat(asObj["class_type"]).isEqualTo("TypeOne")
    }

    @Test
    fun `round-trip for all subclasses - preserves type and value`() {
        val list = listOf(
            TestSealed.TypeOne("x", 1),
            TestSealed.TypeTwo(true, listOf("a", "b")),
            TestSealed.TypeThree(9999L)
        )
        val json = converter.fromList(list)
        val restored = converter.toList(json)
        assertThat(restored).isEqualTo(list)
    }

    @Test
    fun `handles nested lists correctly`() {
        val nested = TestSealed.TypeTwo(false, listOf("one", "two", "three"))
        val json = converter.fromList(listOf(nested))
        val restored = converter.toList(json)
        assertThat(restored).containsExactly(nested)
    }

    @Test
    fun `toList with empty json array returns empty list`() {
        val restored = converter.toList("[]")
        assertThat(restored).isEmpty()
    }

    @Test
    fun `throws on malformed json array`() {
        val result = kotlin.runCatching { converter.toList("not a json array") }
        assertThat(result.exceptionOrNull()).isNotNull
    }

    @Test
    fun `throws on non-string json elements`() {
        val badJson = """[123, 456]"""
        val result = kotlin.runCatching { converter.toList(badJson) }
        assertThat(result.exceptionOrNull()).isNotNull
    }

    @Test
    fun `getTypeNameFromClass returns correct class name`() {
        val method = converter.javaClass.superclass.getDeclaredMethod("getTypeNameFromClass", Class::class.java)
        method.isAccessible = true
        assertThat(method.invoke(converter, TestSealed.TypeOne::class.java)).isEqualTo("TypeOne")
        assertThat(method.invoke(converter, TestSealed.TypeTwo::class.java)).isEqualTo("TypeTwo")
        assertThat(method.invoke(converter, TestSealed.TypeThree::class.java)).isEqualTo("TypeThree")
    }
}
