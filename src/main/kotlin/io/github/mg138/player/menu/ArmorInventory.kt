package io.github.mg138.player.menu

import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

class ArmorInventory<T>(val clazz: Class<T>, val slot: Int) : SimpleInventory(1) {
    fun isOfType(stack: ItemStack): Boolean {
        if (clazz.isInstance(stack.item)) return true
        return false
    }
}