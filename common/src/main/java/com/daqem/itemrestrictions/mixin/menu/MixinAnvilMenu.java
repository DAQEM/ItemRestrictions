package com.daqem.itemrestrictions.mixin.menu;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsServerPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {

    public MixinAnvilMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Inject(at = @At("TAIL"), method = "createResult()V", cancellable = true)
    private void createResult(CallbackInfo ci) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer instanceof ItemRestrictionsServerPlayer itemRestrictionsPlayer) {
                if (serverPlayer instanceof ArcPlayer arcPlayer) {
                    if (!this.inputSlots.getItem(0).isEmpty() && !this.inputSlots.getItem(1).isEmpty() && !this.inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)) {
                        RestrictionResult restrictionResult = itemRestrictionsPlayer.itemrestrictions$isRestricted(new ActionDataBuilder(arcPlayer, null)
                                .withData(ActionDataType.ITEM_STACK, this.inputSlots.getItem(0))
                                .build());
                        if (restrictionResult.isRestricted(RestrictionType.REPAIR)) {
                            this.resultSlots.setItem(0, ItemStack.EMPTY);
                            new ClientboundRestrictionPacket(RestrictionType.REPAIR).sendTo(serverPlayer);
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }
}
