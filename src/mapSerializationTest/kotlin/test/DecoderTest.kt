package test

import com.akuleshov7.kotlinx.serialization.map.MapSerialization
import com.akuleshov7.kotlinx.serialization.map.exceptions.MapDecodingException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Serializable
data class SerializationClass(
    val a: String = "default value, but provided",
    val b: NestedClass,
    val c: String
)

@Serializable
data class NestedClass(val d: String, val e: String, val f: InnerClass)

@Serializable
data class InnerClass(val g: Int, val h: String, val i: Double, val j: String)

class DecoderTest {
    @Test
    fun decodeMapSmallerThanTheClass() {
        // test example of decoding, you can put your parser here, but for simplicity let's avoid it
        // imagine that we would like to serialize the following structure (very similar to json, but very common):
        println("(a:1, b:(d:2, e:3, f:(g:114, h:5, i:6.0, j:7)), c:8)")
        assertFailsWith<MapDecodingException> {
            val nestedMap = mapOf("g" to 114, "h" to "5", "i" to 6.0, "j" to "7")
            val initialMap = mapOf("d" to "2", "e" to "3", "f" to nestedMap)
            val resMap = mapOf("a" to "1", "b" to initialMap)

            val obj = MapSerialization.decodeFromString<SerializationClass>(resMap)
            println("This is the object that was deserialized: $obj")
        }
    }

    @Test
    fun decodeMapSmallerThanTheClassButWithDefault() {
        // test example of decoding, you can put your parser here, but for simplicity let's avoid it
        // imagine that we would like to serialize the following structure (very similar to json, but very common):
        println("(b:(d:2, e:3, f:(g:114, h:5, i:6.0, j:7)), c:8)")
        val nestedMap = mapOf("g" to 114, "h" to "5", "i" to 6.0, "j" to "7")
        val initialMap = mapOf("d" to "2", "e" to "3", "f" to nestedMap)
        val resMap = mapOf("b" to initialMap, "c" to "1")
        val obj = MapSerialization.decodeFromString<SerializationClass>(resMap)
        println("This is the object that was deserialized: $obj")

        assertEquals(
            SerializationClass(b = NestedClass("2", "3", f = InnerClass(114, "5", 6.0, "7")), c = "1"),
            obj
        )
    }

    @Test
    fun decodeMapWithTheSameSizeAsClass() {
        // test example of decoding, you can put your parser here, but for simplicity let's avoid it
        // imagine that we would like to serialize the following structure (very similar to json, but very common):
        println("(a:1, b:(d:2, e:3, f:(g:114, h:5, i:6.0, j:7)), c:9)")

        val nestedMap = mapOf("g" to 114, "h" to "5", "i" to 6.0, "j" to "7")
        val initialMap = mapOf("e" to "3", "d" to "2", "f" to nestedMap)
        val resMap = mapOf("c" to "9", "a" to "1", "b" to initialMap)

        val obj = MapSerialization.decodeFromString<SerializationClass>(resMap)
        println("This is the object that was deserialized: $obj")

        assertEquals(
            SerializationClass(a = "1", b = NestedClass("2", "3", f = InnerClass(114, "5", 6.0, "7")), c = "9"),
            obj
        )
    }

    @Test
    fun decodeMapSmallerThanTheClassSmallExample() {
        assertFailsWith<MapDecodingException> {
            println("(g:114, h:5, j:7)")
            // missing required i field here!
            val nestedMap = mapOf("j" to "7", "h" to "5", "g" to 114)
            val obj = MapSerialization.decodeFromString<InnerClass>(nestedMap)
            println("This is the object that was deserialized: $obj")
        }
    }
}