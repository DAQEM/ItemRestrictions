package com.daqem.itemrestrictions.mixin.menu;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.IActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import com.daqem.knot.Knot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu {

    public MixinCraftingMenu(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("TAIL"), method = "slotChangedCraftingGrid")
    private static void slotChangedCraftingGrid(AbstractContainerMenu abstractContainerMenu, ServerLevel serverLevel, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipeHolder, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer instanceof ItemRestrictionsPlayer itemRestrictionsPlayer) {
                if (serverPlayer instanceof ArcPlayer arcPlayer) {
                    ItemStack itemStack = resultContainer.getItem(0);
                    RestrictionResult restrictionResult = itemRestrictionsPlayer.itemrestrictions$isRestricted(new ActionDataBuilder(arcPlayer, null)
                            .withData(IActionDataType.ITEM_STACK, itemStack)
                            .withData(IActionDataType.ITEM, itemStack.getItem())
                            .withData(IActionDataType.WORLD, serverLevel)
                            .build());
                    if (restrictionResult.isRestricted(RestrictionType.CRAFT)) {
                        resultContainer.setItem(0, ItemStack.EMPTY);
                        Knot.NETWORKING.sendToPlayer(serverPlayer, new ClientboundRestrictionPacket(RestrictionType.CRAFT));
                    } else {
                        Knot.NETWORKING.sendToPlayer(serverPlayer, new ClientboundRestrictionPacket(RestrictionType.NONE));
                    }
                }
            }
        }
    }
}
