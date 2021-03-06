package com.akuleshov7.kotlinx.serialization.map

import com.akuleshov7.kotlinx.serialization.map.decoders.MapDecoder
import com.akuleshov7.kotlinx.serialization.map.utils.Config
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.serializer

object MapSerialization {
    fun <T> decodeFromString(resMap: Map<*, *>, deserializer: DeserializationStrategy<T>, config: Config): T {
        val decoder = MapDecoder(resMap, config = config)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromString(resMap: Map<*, *>, config: Config = Config.default): T =
        decodeFromString(resMap, serializer(), config)
}
