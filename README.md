## kotlinx-serialization-map
Custom serialization library to convert from a kotlin map to a serializable class.
Supports all available Kotlin multiplatform targets.

This library duplicates the logic that was added in [offical kotlinx library](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-properties/kotlinx-serialization-properties/kotlinx.serialization.properties/decode-from-map.html)
 and was created for educational purposes to show for decoding works in Kotlin. See the comments in `MapDecoder` to understand how deserialization works.

## How to use 
Now only decoding is supported. To deserialize you map use the following code:
```
import com.akuleshov7.kotlinx.serialization.map.MapSerialization

MapSerialization.decodeFromString<YourClassToDeserializeTo>(yourMapToDeserializeFrom)
```

And do not forget to mark `YourClassToDeserializeTo` with a `@Serializable` annotation.
