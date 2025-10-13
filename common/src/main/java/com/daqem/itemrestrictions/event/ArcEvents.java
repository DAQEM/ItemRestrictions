package com.daqem.itemrestrictions.event;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.IActionDataType;
import com.daqem.arc.api.event.*;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.player.ArcServerPlayer;
import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ArcEvents {

    public static void registerEvents() {
        ArcBlockEvent.BREAK_BLOCK.register((serverLevel, blockPos, blockState, serverPlayer, supplier) -> {
            if (serverPlayer instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && serverPlayer instanceof ArcServerPlayer arcServerPlayer) {
                if (blockState != null) {
                    RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                            new ActionDataBuilder(arcServerPlayer, null)
                                    .withData(IActionDataType.ITEM_STACK, blockState.getBlock().asItem().getDefaultInstance())
                                    .withData(IActionDataType.ITEM, blockState.getBlock().asItem())
                                    .withData(IActionDataType.BLOCK_STATE, blockState)
                                    .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                    .withData(IActionDataType.WORLD, serverLevel)
                                    .build());

                    if (result.isRestricted(RestrictionType.BREAK_BLOCK)) {
                        serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.BREAK_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                        return EventResult.INTERRUPT_FALSE;
                    }
                }


                ItemStack usedItemStack = serverPlayer.getMainHandItem();
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcServerPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, usedItemStack)
                                .withData(IActionDataType.ITEM, usedItemStack.getItem())
                                .withData(IActionDataType.BLOCK_STATE, blockState)
                                .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                .withData(IActionDataType.WORLD, serverLevel)
                                .build());

                if (result.isRestricted(RestrictionType.ITEM_BREAK_BLOCK)) {
                    serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.ITEM_BREAK_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);

        ArcBlockEvent.GET_DESTROY_SPEED.register((player, blockState, blockPos, itemStack, mutableFloat) -> {
            if (player instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && player instanceof ArcPlayer arcPlayer) {
                if (blockState != null) {
                    RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                            new ActionDataBuilder(arcPlayer, null)
                                    .withData(IActionDataType.ITEM_STACK, blockState.getBlock().asItem().getDefaultInstance())
                                    .withData(IActionDataType.ITEM, blockState.getBlock().asItem())
                                    .withData(IActionDataType.BLOCK_STATE, blockState)
                                    .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                    .withData(IActionDataType.WORLD, player.level())
                                    .build());

                    if (result.isRestricted(RestrictionType.BREAK_BLOCK)) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.BREAK_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                        }
                        mutableFloat.setValue(0.0F);
                        return EventResult.INTERRUPT_FALSE;
                    }
                }


                ItemStack usedItemStack = player.getMainHandItem();
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, usedItemStack)
                                .withData(IActionDataType.ITEM, usedItemStack.getItem())
                                .withData(IActionDataType.BLOCK_STATE, blockState)
                                .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                .withData(IActionDataType.WORLD, player.level())
                                .build());

                if (result.isRestricted(RestrictionType.ITEM_BREAK_BLOCK)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.ITEM_BREAK_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    }
                    mutableFloat.setValue(0.0F);
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        });

        ArcBlockEvent.PLACE_BLOCK.register((level, blockPos, blockState, entity) -> {
            if (entity instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && entity instanceof ArcPlayer arcPlayer) {
                if (blockState != null) {
                    RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                            new ActionDataBuilder(arcPlayer, null)
                                    .withData(IActionDataType.ITEM_STACK, blockState.getBlock().asItem().getDefaultInstance())
                                    .withData(IActionDataType.ITEM, blockState.getBlock().asItem())
                                    .withData(IActionDataType.BLOCK_STATE, blockState)
                                    .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                    .withData(IActionDataType.WORLD, level)
                                    .build());

                    if (result.isRestricted(RestrictionType.PLACE_BLOCK)) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.PLACE_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                        }
                        return EventResult.INTERRUPT_FALSE;
                    }
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);

        ArcEntityEvent.PLAYER_HURT_ENTITY.register((serverPlayer, livingEntity, damageSource, mutableFloat) -> {
            if (serverPlayer instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && serverPlayer instanceof ArcServerPlayer arcServerPlayer) {
                ItemStack usedItemStack = serverPlayer.getMainHandItem();
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcServerPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, usedItemStack)
                                .withData(IActionDataType.ITEM, usedItemStack.getItem())
                                .withData(IActionDataType.ENTITY, livingEntity)
                                .withData(IActionDataType.DAMAGE_SOURCE, damageSource)
                                .withData(IActionDataType.WORLD, serverPlayer.level())
                                .withData(IActionDataType.BLOCK_POSITION, serverPlayer.blockPosition())
                                .build());

                if (result.isRestricted(RestrictionType.HURT_ENTITY)) {
                    serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.HURT_ENTITY.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);

        ArcItemEvent.USE_ITEM.register((level, player, interactionHand, itemStack) -> {
            if (player instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && player instanceof ArcPlayer arcPlayer) {
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, itemStack)
                                .withData(IActionDataType.ITEM, itemStack.getItem())
                                .withData(IActionDataType.WORLD, level)
                                .withData(IActionDataType.BLOCK_POSITION, player.blockPosition())
                                .withData(IActionDataType.HAND, interactionHand)
                                .build());

                if (result.isRestricted(RestrictionType.USE_ITEM)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.USE_ITEM.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    }
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);

        ArcEntityEvent.INTERACT_WITH_ENTITY.register((player, entity, interactionHand) -> {
            if (player instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && player instanceof ArcPlayer arcPlayer) {
                ItemStack usedItemStack = player.getItemInHand(interactionHand);
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, usedItemStack)
                                .withData(IActionDataType.ITEM, usedItemStack.getItem())
                                .withData(IActionDataType.ENTITY, entity)
                                .withData(IActionDataType.WORLD, player.level())
                                .withData(IActionDataType.BLOCK_POSITION, player.blockPosition())
                                .withData(IActionDataType.HAND, interactionHand)
                                .build());

                if (result.isRestricted(RestrictionType.INTERACT_ENTITY)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.INTERACT_ENTITY.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    }
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);

        ArcBlockEvent.RIGHT_CLICK_BLOCK.register((itemStack, level, player, interactionHand, blockState, blockPos) -> {
            if (player instanceof ItemRestrictionsPlayer itemRestrictionsPlayer && player instanceof ArcPlayer arcPlayer) {
                RestrictionResult result = itemRestrictionsPlayer.itemrestrictions$isRestricted(
                        new ActionDataBuilder(arcPlayer, null)
                                .withData(IActionDataType.ITEM_STACK, itemStack)
                                .withData(IActionDataType.ITEM, itemStack.getItem())
                                .withData(IActionDataType.BLOCK_STATE, blockState)
                                .withData(IActionDataType.BLOCK_POSITION, blockPos)
                                .withData(IActionDataType.WORLD, level)
                                .withData(IActionDataType.HAND, interactionHand)
                                .build());

                if (result.isRestricted(RestrictionType.INTERACT_BLOCK)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(ItemRestrictions.translatable(RestrictionType.INTERACT_BLOCK.getTranslationKey()).withStyle(ChatFormatting.RED), true);
                    }
                    return EventResult.INTERRUPT_FALSE;
                }
            }
            return EventResult.PASS;
        }, EventPriority.HIGHEST);
    }
}
