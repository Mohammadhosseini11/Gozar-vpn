package com.gozar.vpn.vpn

import android.content.Context
import android.os.ParcelFileDescriptor
import com.gozar.vpn.model.ServerProfile

/**
 * Adapter boundary for Xray/libv2ray.
 *
 * Implement this interface using a maintained Android binding to the chosen core.
 * The engine must:
 *  - translate VLESS/VMess links to supported core config
 *  - create/own the local TUN packet forwarding loop
 *  - protect the core's outbound sockets from VPN routing loops
 *  - stop threads and close descriptors deterministically
 *
 * No engine is bundled in this starter: connecting must remain disabled until
 * a real implementation is integrated and tested.
 */
interface CoreEngine {
    fun start(context: Context, profile: ServerProfile, tunFd: ParcelFileDescriptor): Result<Unit>
    fun stop()
}

class MissingCoreEngine : CoreEngine {
    override fun start(
        context: Context,
        profile: ServerProfile,
        tunFd: ParcelFileDescriptor
    ): Result<Unit> = Result.failure(
        IllegalStateException("موتور Xray/libv2ray هنوز به پروژه اضافه نشده است.")
    )

    override fun stop() = Unit
}
