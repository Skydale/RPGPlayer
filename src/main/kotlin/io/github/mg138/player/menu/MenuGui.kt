package io.github.mg138.player.menu

import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import io.github.mg138.player.data.ArmorManager
import io.github.mg138.player.data.ArmorType
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class MenuGui(player: ServerPlayerEntity) :
    SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false) {
    companion object {
        val slots = ArmorManager.SLOTS
    }

    private val armor = ArmorManager[player]

    private fun click(slot: Int, itemStack: ItemStack) {
        when (slot) {
            ArmorManager.HEAD_SLOT -> {
                if (itemStack.item is ArmorType.Head) armor.head = itemStack
            }
            ArmorManager.CHEST_SLOT -> {
                if (itemStack.item is ArmorType.Chest) armor.chest = itemStack
            }
            ArmorManager.LEGS_SLOT -> {
                if (itemStack.item is ArmorType.Legs) armor.legs = itemStack
            }
            ArmorManager.FEET_SLOT -> {
                if (itemStack.item is ArmorType.Feet) armor.feet = itemStack
            }
            ArmorManager.NECKLACE_SLOT -> {
                if (itemStack.item is ArmorType.Necklace) armor.necklace = itemStack
            }
            ArmorManager.BRACELET_LEFT_SLOT -> {
                if (itemStack.item is ArmorType.Bracelet) armor.braceletLeft = itemStack
            }
            ArmorManager.BRACELET_RIGHT_SLOT -> {
                if (itemStack.item is ArmorType.Bracelet) armor.braceletRight = itemStack
            }
            ArmorManager.RING_LEFT_SLOT -> {
                if (itemStack.item is ArmorType.Ring) armor.ringLeft = itemStack
            }
            ArmorManager.RING_RIGHT_SLOT -> {
                if (itemStack.item is ArmorType.Ring) armor.ringRight = itemStack
            }
        }
    }

    fun name(slot: Int): Text {
        val key = ArmorManager.getSlotKey(slot)
        return TranslatableText("rpg_player.gui.name.$key")
    }

    fun settings() {
        val frame = GuiElementBuilder()
            .setItem(Items.BLACK_STAINED_GLASS_PANE)
            .setLore(listOf())
            .setName(LiteralText.EMPTY)
            .setCallback(GuiElement.EMPTY_CALLBACK)
            .hideFlags()
            .build()

        for (y in 0 until 6) {
            for (x in 0 until 9) {
                val index = (y * 9) + x
                this.setSlot(index, frame)
            }
        }

        for (i in slots.indices) {
            val slot = slots[i]
            val item = armor.getFromSlot(slot).let {
                if (it.isEmpty) {
                    Items.WHITE_STAINED_GLASS_PANE.defaultStack.apply {
                        setCustomName(name(slot))
                    }
                } else {
                    it
                }
            }

            this.setSlot(slot, GuiElementBuilder.from(item)
                .setCallback { index, type, action ->
                    val itemStack = player.currentScreenHandler.cursorStack
                    if (!itemStack.isEmpty) {
                        click(index, itemStack)
                    }
                }
            )
        }
    }
}
