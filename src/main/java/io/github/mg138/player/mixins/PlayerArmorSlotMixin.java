package io.github.mg138.player.mixins;

import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class PlayerArmorSlotMixin {

    @Inject(
            at = @At("HEAD"),
            method = "onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            cancellable = true
    )
    public void rpg_player_onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!((Object) this instanceof PlayerScreenHandler)) return;

        if (slotIndex >= 5 && slotIndex <= 8) {
            ci.cancel();
        }
    }

    @Inject(
            at= @At("HEAD"),
            method = "insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z",
            cancellable = true
    )
    public void rpg_player_insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof PlayerScreenHandler)) return;

        int s;

        if (fromLast) {
            s = endIndex;
        } else {
            s = startIndex;
        }

        if (s >= 5 && s <= 8) {
            cir.setReturnValue(false);
        }
    }
}
