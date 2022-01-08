package io.github.mg138.player.menu

import io.github.mg138.player.event.PlayerActionCallback
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult

object PlayerOpenMenuEvent {
    private fun onAction(player: ServerPlayerEntity, packet: PlayerActionC2SPacket): ActionResult {
        if (packet.action == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            if (player.isSneaking) {
                val gui = MenuGui(player)
                gui.open()
                return ActionResult.CONSUME
            }
        }
        return ActionResult.PASS
    }

    fun register() {
        PlayerActionCallback.EVENT.register(this::onAction)
    }
}