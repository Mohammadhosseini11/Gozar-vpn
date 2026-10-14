package com.gozar.vpn.data

import android.net.Uri
import android.util.Base64
import com.gozar.vpn.model.ProfileParseResult
import com.gozar.vpn.model.Protocol
import com.gozar.vpn.model.ServerProfile
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Minimal import parser for common share links.
 * This is NOT a complete protocol/config validator. The selected engine must
 * validate the resulting profile before connecting.
 */
object ProfileParser {
    fun parse(input: String): ProfileParseResult {
        val value = input.trim()
        if (value.startsWith("vless://", ignoreCase = true)) return parseVless(value)
        if (value.startsWith("vmess://", ignoreCase = true)) return parseVmess(value)
        return ProfileParseResult.Failure("فرمت پشتیبانی نمی‌شود؛ لینک VLESS یا VMess وارد کنید.")
    }

    private fun parseVless(value: String): ProfileParseResult = runCatching {
        val uri = Uri.parse(value)
        val host = uri.host ?: error("آدرس سرور در لینک پیدا نشد.")
        val port = uri.port
        require(port in 1..65535) { "پورت سرور معتبر نیست." }
        val fragment = uri.fragment?.takeIf { it.isNotBlank() } ?: host
        ProfileParseResult.Success(
            ServerProfile(
                id = UUID.randomUUID().toString(),
                name = fragment,
                protocol = Protocol.VLESS,
                rawUri = value,
                address = host,
                port = port
            )
        )
    }.getOrElse { ProfileParseResult.Failure(it.message ?: "لینک VLESS معتبر نیست.") }

    private fun parseVmess(value: String): ProfileParseResult = runCatching {
        val encoded = value.removePrefix("vmess://").trim()
        require(encoded.isNotBlank()) { "محتوای لینک VMess خالی است." }
        val decoded = Base64.decode(encoded, Base64.DEFAULT)
            .toString(StandardCharsets.UTF_8)
        val json = JSONObject(decoded)
        val host = json.optString("add").takeIf { it.isNotBlank() }
        val port = json.optString("port").toIntOrNull()
        require(host != null) { "آدرس سرور در کانفیگ VMess وجود ندارد." }
        require(port != null && port in 1..65535) { "پورت VMess معتبر نیست." }
        val name = json.optString("ps").takeIf { it.isNotBlank() } ?: host
        ProfileParseResult.Success(
            ServerProfile(
                id = UUID.randomUUID().toString(),
                name = name,
                protocol = Protocol.VMESS,
                rawUri = value,
                address = host,
                port = port
            )
        )
    }.getOrElse { ProfileParseResult.Failure(it.message ?: "لینک VMess معتبر نیست.") }
}
