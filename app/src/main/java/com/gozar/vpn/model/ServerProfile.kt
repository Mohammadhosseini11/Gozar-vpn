package com.gozar.vpn.model

/**
 * Safe, display-oriented profile model. Do not log/share raw links:
 * they may contain credentials or UUIDs.
 */
data class ServerProfile(
    val id: String,
    val name: String,
    val protocol: Protocol,
    val rawUri: String,
    val address: String? = null,
    val port: Int? = null
)

enum class Protocol { VLESS, VMESS }

sealed interface ProfileParseResult {
    data class Success(val profile: ServerProfile) : ProfileParseResult
    data class Failure(val reason: String) : ProfileParseResult
}
