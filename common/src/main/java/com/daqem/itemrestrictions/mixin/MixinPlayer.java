package com.daqem.itemrestrictions.mixin;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

import com.daqem.arc.data.ActionData;
import com.daqem.itemrestrictions.data.ItemRestriction;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsPlayer;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class MixinPlayer implements ItemRestrictionsPlayer {

    @Override
    public RestrictionResult itemrestrictions$isRestricted(ActionData actionData) {
        List<ItemRestriction> itemRestrictions = ItemRestrictionManager.getInstance().getItemRestrictions();
        Optional<RestrictionResult> optionalRestrictionResult = itemRestrictions.stream()
                .filter(itemRestriction -> {
                    if ((Object) this instanceof LocalPlayer) {
                        return itemRestriction.isClientSide();
                    }
                    return true;
                })
                .map(itemRestriction -> itemRestriction.isRestricted(actionData))
                .reduce((restrictionResult1, restrictionResult2) -> {
                    restrictionResult2.merge(restrictionResult1);
                    return restrictionResult2;
                });
        return optionalRestrictionResult.orElse(new RestrictionResult());
    }
}
