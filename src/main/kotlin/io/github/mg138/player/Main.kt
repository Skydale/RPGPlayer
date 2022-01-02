package io.github.mg138.player

import eu.pb4.polymer.api.resourcepack.PolymerRPUtils
import io.github.mg138.player.data.ArmorManager
import io.github.mg138.player.event.RightClickArmor
import io.github.mg138.player.menu.PlayerOpenMenuEvent
import net.fabricmc.api.DedicatedServerModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("UNUSED")
object Main : DedicatedServerModInitializer {
    const val modId = "rpg_player"
    private val logger: Logger = LogManager.getLogger(modId)

    override fun onInitializeServer() {
        PolymerRPUtils.addAssetSource(modId)

        PlayerOpenMenuEvent.register()

        ArmorManager.register()

        RightClickArmor.register()

        logger.info("Registered RPG Player.")
    }
}