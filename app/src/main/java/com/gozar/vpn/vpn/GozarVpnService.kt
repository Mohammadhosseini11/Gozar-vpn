package com.gozar.vpn.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import com.gozar.vpn.R

/**
 * Android VPN lifecycle scaffold. It deliberately does NOT establish a TUN
 * until a real CoreEngine implementation is wired in.
 */
class GozarVpnService : VpnService() {
    private var tun: ParcelFileDescriptor? = null
    private var engine: CoreEngine = MissingCoreEngine()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopTunnel()
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, notification("در انتظار موتور اتصال"))
                // Intentionally do not call Builder.establish() here.
                // Creating a TUN without packet forwarding would interrupt network traffic.
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    private fun stopTunnel() {
        runCatching { engine.stop() }
        runCatching { tun?.close() }
        tun = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onRevoke() {
        stopTunnel()
        super.onRevoke()
    }

    override fun onDestroy() {
        stopTunnel()
        super.onDestroy()
    }

    private fun notification(message: String): Notification {
        val channelId = "gozar_vpn_status"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "وضعیت Gozar VPN", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Gozar VPN")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setOngoing(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Gozar VPN")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setOngoing(true)
                .build()
        }
    }

    companion object {
        const val ACTION_START = "com.gozar.vpn.action.START"
        const val ACTION_STOP = "com.gozar.vpn.action.STOP"
        private const val NOTIFICATION_ID = 1001
    }
}
