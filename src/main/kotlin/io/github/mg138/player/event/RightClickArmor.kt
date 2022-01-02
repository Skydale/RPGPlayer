package io.github.mg138.player.event

import eu.pb4.polymer.mixin.client.item.packet.ClickSlotC2SPacketMixin
import io.github.mg138.player.data.ArmorManager
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.InventoryChangedListener
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

object RightClickArmor {
    private fun onUseItem(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.mainHandStack

        if (player is ServerPlayerEntity) {
            if (ArmorManager[player].tryPut(itemStack)) {
                itemStack.decrement(1)

                return TypedActionResult(ActionResult.CONSUME_PARTIAL, itemStack)
            }
        }
        return TypedActionResult(ActionResult.FAIL, itemStack)
    }

    fun register() {
        UseItemCallback.EVENT.register(this::onUseItem)
    }
}