package io.github.mg138.player.menu

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.server.network.ServerPlayerEntity

import net.minecraft.util.ActionResult


fun interface PlayerActionCallback {
    fun onAction(player: ServerPlayerEntity, packet: PlayerActionC2SPacket): ActionResult

    companion object {
        val EVENT: Event<PlayerActionCallback> = EventFactory.createArrayBacked(
            PlayerActionCallback::class.java
        ) { listeners ->
            object : PlayerActionCallback {
                override fun onAction(player: ServerPlayerEntity, packet: PlayerActionC2SPacket): ActionResult {
                    for (listener in listeners) {
                        val result = listener.onAction(player, packet)

                        if (result != ActionResult.PASS) {
                            return result
                        }
                    }
                    return ActionResult.PASS
                }
            }
        }
    }
}