package com.daqem.itemrestrictions.mixin.menu;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsServerPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu<CraftingContainer> {

    public MixinCraftingMenu(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("TAIL"), method = "slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;)V")
    private static void slotChangedCraftingGrid(AbstractContainerMenu abstractContainerMenu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer instanceof ItemRestrictionsServerPlayer itemRestrictionsPlayer) {
                if (serverPlayer instanceof ArcPlayer arcPlayer) {
                    ItemStack itemStack = resultContainer.getItem(0);
                    RestrictionResult restrictionResult = itemRestrictionsPlayer.itemrestrictions$isRestricted(new ActionDataBuilder(arcPlayer, null)
                            .withData(ActionDataType.ITEM_STACK, itemStack)
                            .build());
                    if (restrictionResult.isRestricted(RestrictionType.CRAFT)) {
                        resultContainer.setItem(0, ItemStack.EMPTY);
                        new ClientboundRestrictionPacket(RestrictionType.CRAFT).sendTo(serverPlayer);
                    } else {
                        new ClientboundRestrictionPacket(RestrictionType.NONE).sendTo(serverPlayer);
                    }
                }
            }
        }
    }
}
