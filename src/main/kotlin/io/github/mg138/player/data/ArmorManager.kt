package io.github.mg138.player.data

import io.github.mg138.player.database.profile.ProfileLoadCallback
import io.github.mg138.player.database.profile.ProfileLoader
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.screen.slot.Slot
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.bson.Document
import java.util.*

object ArmorManager : ProfileLoadCallback {
    const val RPG_ARMOR_KEY = "RPGArmor"
    const val HEAD_KEY =             "head"
    const val CHEST_KEY =           "chest"
    const val LEGS_KEY =            "legs"
    const val FEET_KEY =            "feet"
    const val NECKLACE_KEY =            "necklace"
    const val BRACELET_LEFT_KEY =       "bracelet_left"
    const val BRACELET_RIGHT_KEY =      "bracelet_right"
    const val RING_LEFT_KEY =       "ring_left"
    const val RING_RIGHT_KEY =      "ring_right"

    const val HEAD_SLOT = 11
    const val CHEST_SLOT = HEAD_SLOT + 9
    const val LEGS_SLOT = CHEST_SLOT + 9
    const val FEET_SLOT = LEGS_SLOT + 9
    const val NECKLACE_SLOT = HEAD_SLOT - 1
    const val BRACELET_LEFT_SLOT = CHEST_SLOT - 1
    const val BRACELET_RIGHT_SLOT = CHEST_SLOT + 1
    const val RING_LEFT_SLOT = LEGS_SLOT - 1
    const val RING_RIGHT_SLOT = LEGS_SLOT + 1

    fun getSlotKey(slot: Int): String {
        return when (slot) {
            HEAD_SLOT -> HEAD_KEY
            CHEST_SLOT -> CHEST_KEY
            LEGS_SLOT -> LEGS_KEY
            FEET_SLOT -> FEET_KEY
            NECKLACE_SLOT -> NECKLACE_KEY
            BRACELET_LEFT_SLOT -> BRACELET_LEFT_KEY
            BRACELET_RIGHT_SLOT -> BRACELET_RIGHT_KEY
            RING_LEFT_SLOT -> RING_LEFT_KEY
            RING_RIGHT_SLOT -> RING_RIGHT_KEY
            else -> ""
        }
    }

    val SLOTS = listOf(
        HEAD_SLOT,
        CHEST_SLOT,
        LEGS_SLOT,
        FEET_SLOT,
        NECKLACE_SLOT,
        BRACELET_LEFT_SLOT,
        BRACELET_RIGHT_SLOT,
        RING_LEFT_SLOT,
        RING_RIGHT_SLOT
    )

    data class Armor(
        var head: ItemStack,
        var chest: ItemStack,
        var legs: ItemStack,
        var feet: ItemStack,
        var necklace: ItemStack,
        var braceletLeft: ItemStack,
        var braceletRight: ItemStack,
        var ringLeft: ItemStack,
        var ringRight: ItemStack,
    ) {
        fun getFromSlot(slot: Int, default: ItemStack = ItemStack.EMPTY): ItemStack {
            return when (slot) {
                HEAD_SLOT -> this.head
                CHEST_SLOT -> this.chest
                LEGS_SLOT -> this.legs
                FEET_SLOT -> this.feet
                NECKLACE_SLOT -> this.necklace
                BRACELET_LEFT_SLOT -> this.braceletLeft
                BRACELET_RIGHT_SLOT -> this.braceletRight
                RING_LEFT_SLOT -> this.ringLeft
                RING_RIGHT_SLOT -> this.ringRight
                else -> default
            }
        }

        fun doAtSlot(slot: Int, callback: (ItemStack) -> Unit) {
            callback(getFromSlot(slot))
        }

        companion object {
            private fun NbtCompound.getItem(key: String): ItemStack {
                val item = this.getCompound(key)
                if (item.isEmpty) return ItemStack.EMPTY

                return ItemStack.fromNbt(item)
            }

            private fun ItemStack.write(): NbtCompound {
                return NbtCompound().also { this.writeNbt(it) }
            }

            fun readNbt(nbt: NbtCompound): Armor {
                val armor = nbt.getCompound(RPG_ARMOR_KEY)

                val head = armor.getItem(HEAD_KEY)
                val chest = armor.getItem(CHEST_KEY)
                val legs = armor.getItem(LEGS_KEY)
                val feet = armor.getItem(FEET_KEY)
                val necklace = armor.getItem(NECKLACE_KEY)
                val braceletLeft = armor.getItem(BRACELET_LEFT_KEY)
                val braceletRight = armor.getItem(BRACELET_RIGHT_KEY)
                val ringLeft = armor.getItem(RING_LEFT_KEY)
                val ringRight = armor.getItem(RING_RIGHT_KEY)

                return Armor(head, chest, legs, feet, necklace, braceletLeft, braceletRight, ringLeft, ringRight)
            }

            val EMPTY = Armor(
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
            )
        }

        fun writeNbt(nbt: NbtCompound) {
            val armor = NbtCompound()

            val head = this.head.write()
            val chest = this.chest.write()
            val legs = this.legs.write()
            val feet = this.feet.write()
            val necklace = this.necklace.write()
            val braceletLeft = this.braceletLeft.write()
            val braceletRight = this.braceletRight.write()
            val ringLeft = this.ringLeft.write()
            val ringRight = this.ringRight.write()

            armor.put(HEAD_KEY, head)
            armor.put(CHEST_KEY, chest)
            armor.put(LEGS_KEY, legs)
            armor.put(FEET_KEY, feet)
            armor.put(NECKLACE_KEY, necklace)
            armor.put(BRACELET_LEFT_KEY, braceletLeft)
            armor.put(BRACELET_RIGHT_KEY, braceletRight)
            armor.put(RING_LEFT_KEY, ringLeft)
            armor.put(RING_RIGHT_KEY, ringRight)

            nbt.put(RPG_ARMOR_KEY, armor)
        }
    }

    private val map: MutableMap<UUID, Armor> = mutableMapOf()

    override fun onLoad(data: Document, player: ServerPlayerEntity, server: MinecraftServer) {
        val armorData = StringNbtReader.parse(data.get(RPG_ARMOR_KEY, "{}"))

        map[player.uuid] = Armor.readNbt(armorData)
    }

    override fun onSave(data: Document, player: ServerPlayerEntity, server: MinecraftServer) {
        val uuid = player.uuid

        map[uuid]?.let {
            data.put(RPG_ARMOR_KEY, NbtCompound().apply {
                it.writeNbt(this)
            }.asString())
        }

        map.remove(uuid)
    }

    operator fun get(player: ServerPlayerEntity) = map.getOrPut(player.uuid) { Armor.EMPTY }

    operator fun set(player: ServerPlayerEntity, value: Armor) {
        map[player.uuid] = value
    }

    fun register() {
        ProfileLoader.registerCallback(this)
    }
}