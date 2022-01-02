package io.github.mg138.player.menu

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.GuiHelpers
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface
import eu.pb4.sgui.api.gui.SimpleGui
import io.github.mg138.player.data.ArmorManager
import io.github.mg138.player.data.ArmorManager.BRACELET_LEFT_SLOT
import io.github.mg138.player.data.ArmorManager.BRACELET_RIGHT_KEY
import io.github.mg138.player.data.ArmorManager.BRACELET_RIGHT_SLOT
import io.github.mg138.player.data.ArmorManager.CHEST_SLOT
import io.github.mg138.player.data.ArmorManager.FEET_SLOT
import io.github.mg138.player.data.ArmorManager.HEAD_SLOT
import io.github.mg138.player.data.ArmorManager.LEGS_SLOT
import io.github.mg138.player.data.ArmorManager.NECKLACE_SLOT
import io.github.mg138.player.data.ArmorManager.RING_LEFT_SLOT
import io.github.mg138.player.data.ArmorManager.RING_RIGHT_SLOT
import io.github.mg138.player.data.ArmorType
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class MenuGui(player: ServerPlayerEntity) :
    SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false) {
    private val head = ArmorInventory(ArmorType.Head::class.java, HEAD_SLOT)
    private val chest = ArmorInventory(ArmorType.Chest::class.java, CHEST_SLOT)
    private val legs = ArmorInventory(ArmorType.Legs::class.java, LEGS_SLOT)
    private val feet = ArmorInventory(ArmorType.Feet::class.java, FEET_SLOT)
    private val necklace = ArmorInventory(ArmorType.Necklace::class.java, NECKLACE_SLOT)
    private val braceletLeft = ArmorInventory(ArmorType.Bracelet::class.java, BRACELET_LEFT_SLOT)
    private val braceletRight = ArmorInventory(ArmorType.Bracelet::class.java, BRACELET_RIGHT_SLOT)
    private val ringLeft = ArmorInventory(ArmorType.Ring::class.java, RING_LEFT_SLOT)
    private val ringRight = ArmorInventory(ArmorType.Ring::class.java, RING_RIGHT_SLOT)

    private val inventories = listOf(
        head, chest, legs, feet, necklace, braceletLeft, braceletRight, ringLeft, ringRight
    )

    private val armor = ArmorManager[player]

    private fun name(slot: Int): Text {
        val key = ArmorManager.getSlotKey(slot)
        return TranslatableText("rpg_player.gui.name.$key")
    }

    private fun emptySlot() {
        if (screenHandler == null) return

        for (inventory in inventories) {
            val slot = inventory.slot
            val item = inventory.getStack(0)

            if (item.isEmpty) {
                GuiHelpers.sendSlotUpdate(
                    player, screenHandler.syncId, slot,
                    GuiElementBuilder()
                        .setItem(Items.WHITE_STAINED_GLASS_PANE)
                        .setName(name(slot))
                        .build()
                        .itemStack
                )
            }
        }
    }

    override fun onTick() {
        super.onTick()

        emptySlot()
    }

    override fun onClose() {
        super.onClose()

        inventories.forEach {
            val stack = it.getStack(0)

            if (it.isOfType(stack)) {
                armor.setFromSlot(it.slot, stack)
            }
        }
    }


    init {
        inventories.forEach {
            it.setStack(0, armor.getFromSlot(it.slot))
        }

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

        inventories.forEach {
            this.setSlotRedirect(it.slot, object : Slot(it, 0, 0, 0) {
                override fun canInsert(stack: ItemStack): Boolean {
                    return it.isOfType(stack)
                }
            })
        }
    }
}
