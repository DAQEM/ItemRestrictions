package com.daqem.itemrestrictions.mixin.menu;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.IActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {

    @Unique
    private ServerPlayer itemrestrictions$player;

    protected MixinEnchantmentMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("HEAD"), method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", cancellable = true)
    private void clickMenuButton(Player player, int level, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer instanceof ItemRestrictionsPlayer itemRestrictionsPlayer) {
                if (serverPlayer instanceof ArcPlayer arcPlayer) {
                    this.itemrestrictions$player = serverPlayer;
                    ItemStack stack = getSlot(0).getItem();
                    RestrictionResult craftingResult = itemRestrictionsPlayer.itemrestrictions$isRestricted(new ActionDataBuilder(arcPlayer, null)
                            .withData(IActionDataType.ITEM_STACK, stack)
                            .withData(IActionDataType.ITEM, stack.getItem())
                            .withData(IActionDataType.WORLD, serverPlayer.level())
                            .build());
                    if (craftingResult.isRestricted(RestrictionType.ENCHANT)) {
                        NetworkManager.sendToPlayer(serverPlayer, new ClientboundRestrictionPacket(RestrictionType.ENCHANT));
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "slotsChanged(Lnet/minecraft/world/Container;)V")
    private void slotsChanged(Container container, CallbackInfo ci) {
        if (itemrestrictions$player != null) {
            NetworkManager.sendToPlayer(this.itemrestrictions$player, new ClientboundRestrictionPacket(RestrictionType.NONE));
        }
    }
}
