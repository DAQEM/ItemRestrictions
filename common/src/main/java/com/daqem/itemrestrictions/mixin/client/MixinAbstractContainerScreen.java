package com.daqem.itemrestrictions.mixin.client;

import com.daqem.itemrestrictions.client.screen.ItemRestrictionsScreen;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen extends Screen implements ItemRestrictionsScreen {

    @Unique
    private RestrictionType itemrestrictions$restrictionType;

    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }

    @Override
    public void itemrestrictions$cantCraft(RestrictionType restrictionType) {
        this.itemrestrictions$restrictionType = restrictionType;
    }

    @Inject(at = @At("TAIL"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V")
    private void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (itemrestrictions$restrictionType != null && itemrestrictions$restrictionType != RestrictionType.NONE) {
            renderCantCraftMessage(poseStack, font, width, height, 166, itemrestrictions$restrictionType);
        }
    }
}
