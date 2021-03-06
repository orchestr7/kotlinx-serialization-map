package com.akuleshov7.kotlinx.serialization.map.decoders

import com.akuleshov7.kotlinx.serialization.map.exceptions.MapDecodingException
import com.akuleshov7.kotlinx.serialization.map.utils.Config
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class MapDecoder(
    val map: Map<*, *>,
    var elementsCount: Int = 0,
    val config: Config = Config.default
) : AbstractDecoder() {
    private var elementIndex = 0

    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun decodeValue(): Any {
        val keyAtTheCurrentIndex = map.keys.elementAt(elementIndex - 1)
        return map[keyAtTheCurrentIndex]!!
    }

    /**
     * this method should be overridden to map between the FIELDS in your class and the VALUE from
     * the input that you would like to inject into it
     */
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == map.size) return CompositeDecoder.DECODE_DONE

        val fieldName = map.keys.elementAt(elementIndex).toString()

        // index of the field from the class where we should inject our value
        val fieldWhereValueShouldBeInjected =
            descriptor.getElementIndex(map.keys.elementAt(elementIndex).toString())

        if (fieldWhereValueShouldBeInjected == CompositeDecoder.UNKNOWN_NAME) {
            if (config.isStrict) {
                throw MapDecodingException(
                    "Unknown property <$fieldName>." +
                            " To ignore unknown properties use 'isStrict = false' in the Config for the MapDecoder"
                )
            }
        }
        elementIndex++

        return fieldWhereValueShouldBeInjected
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        decodeInt().also {
            elementsCount = it
        }

    // used to trigger the processing for structures (including nested)
    // when we use this method we go throw nested (non-primitive) structures IN THE CLASS
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        // corner case at the beginning of the decoding
        if (elementIndex == 0) {
            validateDecodedInput(descriptor, map)
            return MapDecoder(map, descriptor.elementsCount)
        } else {
            // need to decrement element index, as unfortunately it was incremented in the iteration of `decodeElementIndex`
            return when (val innerMap = map.values.elementAt(elementIndex - 1)) {
                is Map<*, *> -> {
                    validateDecodedInput(descriptor, innerMap)
                    MapDecoder(innerMap, descriptor.elementsCount)
                }
                else -> throw MapDecodingException("Incorrect format of nested data provided." +
                        " Expected map, but received: <$innerMap>")
            }
        }
    }

    override fun decodeNotNullMark(): Boolean = decodeString().toUpperCase() != "NULL"

    // we should validate that all keys (required fields) from the class are not missing in the input
    // this function will check that all required fields are provided
    private fun validateDecodedInput(descriptor: SerialDescriptor, map: Map<*, *>) {
        val missingKeysInInput = descriptor.elementNames.toSet() - map.keys.toSet()
        missingKeysInInput.forEach {
            val elementIndex = descriptor.getElementIndex(it.toString())
            if (!descriptor.isElementOptional(elementIndex)) {
                throw MapDecodingException(
                    "Invalid number of arguments provided for deserialization. Missing required field " +
                            "<${descriptor.getElementName(elementIndex)}> in the input"
                )
            }
        }
    }
}
