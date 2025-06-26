import com.developers.sprintsync.data.core.SealedTypeConverter
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.JsonParser
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

// Test sealed class and subtypes
sealed class TestSealed {
 data class TypeOneDto(val value: String, val count: Int) : TestSealed()
 data class TypeTwoData(val flag: Boolean, val items: List<String>) : TestSealed()
 data class TypeThreeModel(val id: Long) : TestSealed()
 data class SimpleType(val name: String) : TestSealed()
}

// Concrete implementation for testing
class TestSealedTypeConverter : SealedTypeConverter<TestSealed>(TestSealed::class.java)

class SealedTypeConverterTest {

 private lateinit var converter: TestSealedTypeConverter

 @Before
 fun setup() {
  converter = TestSealedTypeConverter()
 }

 @Test
 fun `fromList converts empty list to empty JSON array`() {
  val emptyList = emptyList<TestSealed>()

  val result = converter.fromList(emptyList)

  assertThat(result).isEqualTo("[]")
 }

 @Test
 fun `fromList converts single item to JSON array with one element`() {
  val item = TestSealed.TypeOneDto("test", 42)
  val list = listOf(item)

  val result = converter.fromList(list)
  val jsonArray = JsonParser.parseString(result).asJsonArray

  assertThat(jsonArray.size()).isEqualTo(1)

  val jsonString = jsonArray.get(0).toString()
  val jsonObject = JsonParser.parseString(jsonString).asJsonObject

  assertThat(jsonObject.has("__class_type")).isTrue()
  assertThat(jsonObject.get("__class_type").asString).isEqualTo("type_one")
  assertThat(jsonObject.get("value").asString).isEqualTo("test")
  assertThat(jsonObject.get("count").asInt).isEqualTo(42)
 }

 @Test
 fun `fromList converts multiple items with different types`() {
  val items = listOf(
   TestSealed.TypeOneDto("test", 42),
   TestSealed.TypeTwoData(true, listOf("a", "b")),
   TestSealed.TypeThreeModel(123L)
  )

  val result = converter.fromList(items)
  val jsonArray = JsonParser.parseString(result).asJsonArray

  assertThat(jsonArray.size()).isEqualTo(3)

  // Verify each item has correct type annotation
  val firstItem = jsonArray.get(0).asJsonObject
  assertThat(firstItem.get("__class_type").asString).isEqualTo("type_one")

  val secondItem = jsonArray.get(1).asJsonObject
  assertThat(secondItem.get("__class_type").asString).isEqualTo("type_two")

  val thirdItem = jsonArray.get(2).asJsonObject
  assertThat(thirdItem.get("__class_type").asString).isEqualTo("type_three")
 }

 @Test
 fun `toList converts empty JSON array to empty list`() {
  val emptyJsonArray = "[]"

  val result = converter.toList(emptyJsonArray)

  assertThat(result).isEmpty()
 }

 @Test
 fun `toList converts single JSON item back to sealed class instance`() {
  val originalItem = TestSealed.TypeOneDto("test", 42)
  val jsonString = converter.fromList(listOf(originalItem))

  val result = converter.toList(jsonString)

  assertThat(result).hasSize(1)
  assertThat(result[0]).isEqualTo(originalItem)
 }

 @Test
 fun `toList converts multiple JSON items with different types`() {
  val originalItems = listOf(
   TestSealed.TypeOneDto("test", 42),
   TestSealed.TypeTwoData(true, listOf("a", "b")),
   TestSealed.TypeThreeModel(123L)
  )
  val jsonString = converter.fromList(originalItems)

  val result = converter.toList(jsonString)

  assertThat(result).hasSize(3)
  assertThat(result[0]).isEqualTo(originalItems[0])
  assertThat(result[1]).isEqualTo(originalItems[1])
  assertThat(result[2]).isEqualTo(originalItems[2])
 }

 @Test
 fun `round trip conversion preserves data integrity`() {
  val originalItems = listOf(
   TestSealed.TypeOneDto("hello world", 999),
   TestSealed.TypeTwoData(false, listOf("x", "y", "z")),
   TestSealed.SimpleType("simple"),
   TestSealed.TypeThreeModel(Long.MAX_VALUE)
  )

  val jsonString = converter.fromList(originalItems)
  val restoredItems = converter.toList(jsonString)

  assertThat(restoredItems).isEqualTo(originalItems)
 }

 @Test
 fun `getTypeNameFromClass removes Dto suffix and converts to snake_case`() {
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("getTypeNameFromClass", Class::class.java)
  method.isAccessible = true

  val result = method.invoke(converter, TestSealed.TypeOneDto::class.java) as String

  assertThat(result).isEqualTo("type_one")
 }

 @Test
 fun `getTypeNameFromClass removes Data suffix and converts to snake_case`() {
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("getTypeNameFromClass", Class::class.java)
  method.isAccessible = true

  val result = method.invoke(converter, TestSealed.TypeTwoData::class.java) as String

  assertThat(result).isEqualTo("type_two")
 }

 @Test
 fun `getTypeNameFromClass removes Model suffix and converts to snake_case`() {
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("getTypeNameFromClass", Class::class.java)
  method.isAccessible = true

  val result = method.invoke(converter, TestSealed.TypeThreeModel::class.java) as String

  assertThat(result).isEqualTo("type_three")
 }

 @Test
 fun `getTypeNameFromClass handles class without suffix`() {
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("getTypeNameFromClass", Class::class.java)
  method.isAccessible = true

  val result = method.invoke(converter, TestSealed.SimpleType::class.java) as String

  assertThat(result).isEqualTo("simple_type")
 }

 @Test
 fun `convertCamelCaseToSnakeCase converts correctly`() {
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("convertCamelCaseToSnakeCase", String::class.java)
  method.isAccessible = true

  val testCases = mapOf(
   "CamelCase" to "camel_case",
   "XMLHttpRequest" to "x_m_l_http_request",
   "SimpleType" to "simple_type",
   "A" to "a",
   "AB" to "a_b",
   "lowercase" to "lowercase",
   "MixedCASEString" to "mixed_c_a_s_e_string"
  )

  testCases.forEach { (input, expected) ->
   val result = method.invoke(converter, input) as String
   assertThat(result).isEqualTo(expected)
  }
 }

 @Test(expected = Exception::class)
 fun `toList throws exception for malformed JSON`() {
  val malformedJson = "not a json array"

  converter.toList(malformedJson)
 }

 @Test(expected = Exception::class)
 fun `toList throws exception for JSON array with invalid item structure`() {
  val invalidJson = """["not a json object"]"""

  converter.toList(invalidJson)
 }

 @Test
 fun `fromList handles complex nested data structures`() {
  val complexItem = TestSealed.TypeTwoData(
   flag = true,
   items = listOf("item1", "item2", "item3")
  )
  val list = listOf(complexItem)

  val result = converter.fromList(list)
  val restored = converter.toList(result)

  assertEquals(1, restored.size)
  assertEquals(complexItem, restored[0])
 }

 @Test
 fun `converter creates correct type adapter factory`() {
  val converter = TestSealedTypeConverter()
  val factoryField = converter.javaClass.getDeclaredField("adapterFactory")
  factoryField.isAccessible = true
  val factory = factoryField.get(converter)

  assertNotNull(factory)
 }

 @Test
 fun `gson instance is created with proper configuration`() {
  val converter = TestSealedTypeConverter()
  val gsonField = converter.javaClass.getDeclaredField("gson")
  gsonField.isAccessible = true
  val gson = gsonField.get(converter) as Gson

  assertNotNull(gson)
  // Verify gson can serialize/deserialize our test types
  val testItem = TestSealed.SimpleType("test")
  val json = gson.toJson(testItem)
  assertNotNull(json)
 }

 @Test
 fun `CLASS_TYPE_FIELD constant has correct value`() {
  assertEquals("__class_type", SealedTypeConverter.CLASS_TYPE_FIELD)
 }

 @Test
 fun `itemToJson adds type field to JSON object`() {
  val item = TestSealed.SimpleType("test")
  val converter = TestSealedTypeConverter()
  val method = converter.javaClass.getDeclaredMethod("itemToJson", Any::class.java)
  method.isAccessible = true

  val result = method.invoke(converter, item) as String
  val jsonObject = JsonParser.parseString(result).asJsonObject

  assertTrue(jsonObject.has("__class_type"))
  assertEquals("simple_type", jsonObject.get("__class_type").asString)
  assertTrue(jsonObject.has("name"))
  assertEquals("test", jsonObject.get("name").asString)
 }
}