package com.gozar.vpn.data

import android.content.Context
import com.gozar.vpn.model.ServerProfile
import org.json.JSONArray
import org.json.JSONObject

/**
 * Simple local persistence scaffold. For production, consider encrypted storage
 * if raw share links/credentials must be persisted.
 */
class ProfileStore(context: Context) {
    private val prefs = context.getSharedPreferences("gozar_profiles", Context.MODE_PRIVATE)

    fun save(profiles: List<ServerProfile>) {
        val array = JSONArray()
        profiles.forEach { p ->
            array.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("protocol", p.protocol.name)
                put("rawUri", p.rawUri)
                put("address", p.address ?: JSONObject.NULL)
                put("port", p.port ?: JSONObject.NULL)
            })
        }
        prefs.edit().putString("profiles", array.toString()).apply()
    }

    // Raw JSON parsing is kept small here; add migrations/versioning for production.
    fun loadRawJson(): String = prefs.getString("profiles", "[]") ?: "[]"
}
