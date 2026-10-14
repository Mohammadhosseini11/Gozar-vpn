package com.gozar.vpn.data

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Basic HTTPS subscription downloader. Production hardening should add
 * strict response-size/time limits, redirect policy, and user-visible errors.
 */
object SubscriptionImporter {
    suspend fun download(urlText: String): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(urlText)
            require(url.protocol.equals("https", ignoreCase = true)) {
                "برای امنیت، لینک اشتراک باید HTTPS باشد."
            }
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 15_000
                requestMethod = "GET"
                setRequestProperty("Accept", "text/plain")
            }
            try {
                require(connection.responseCode in 200..299) {
                    "دریافت اشتراک ناموفق بود: HTTP ${connection.responseCode}"
                }
                val bytes = connection.inputStream.use { input ->
                    val output = java.io.ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= 1_500_000) { "حجم فایل اشتراک بیش از حد مجاز است." }
                        output.write(buffer, 0, count)
                    }
                    output.toByteArray()
                }
                val text = bytes.toString(Charset.forName("UTF-8")).trim()
                val decoded = runCatching {
                    Base64.decode(text, Base64.DEFAULT).toString(Charsets.UTF_8)
                }.getOrDefault(text)
                decoded.lineSequence()
                    .map { it.trim() }
                    .filter { it.startsWith("vless://") || it.startsWith("vmess://") }
                    .toList()
            } finally {
                connection.disconnect()
            }
        }
    }
}
