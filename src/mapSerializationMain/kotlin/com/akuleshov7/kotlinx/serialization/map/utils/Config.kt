package com.akuleshov7.kotlinx.serialization.map.utils

import kotlinx.serialization.Serializable

/**
 * @param isStrict: in case of 'true' value will raise an exception for unknown fields
 */
@Serializable
data class Config (
    val isStrict: Boolean
) {
    companion object  {
        val default: Config = Config(isStrict = false)
    }
}
