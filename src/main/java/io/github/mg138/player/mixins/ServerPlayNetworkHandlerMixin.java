package io.github.mg138.player.mixins;

import io.github.mg138.player.event.PlayerActionCallback;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Inject(
            at = @At("HEAD"),
            method = "onPlayerAction(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;)V",
            cancellable = true
    )
    public void rpg_player_onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;

        ActionResult result = PlayerActionCallback.Companion.getEVENT().invoker().onAction(handler.getPlayer(), packet);

        if (result != ActionResult.PASS) {
            ci.cancel();
        }
    }
}
