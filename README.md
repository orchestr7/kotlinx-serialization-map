## kotlinx-serialization-map
Custom serialization library to convert from a kotlin map to a serializable class.
Supports all available Kotlin multiplatform targets:  

## How to use 
Now only decoding is supported. To deserialize you map use the following code:
```
import com.akuleshov7.kotlinx.serialization.map.MapSerialization

MapSerialization.decodeFromString<YourClassToDeserializeTo>(yourMapToDeserializeFrom)
```

And do not forget to mark `YourClassToDeserializeTo` with a `@Serializable` annotation.
