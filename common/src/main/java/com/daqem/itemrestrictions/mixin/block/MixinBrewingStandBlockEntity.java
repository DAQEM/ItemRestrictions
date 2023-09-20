package com.daqem.itemrestrictions.mixin.block;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.block.ItemRestrictionsBrewingStandBlockEntity;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsServerPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, ItemRestrictionsBrewingStandBlockEntity {

    @Shadow
    int brewTime;
    @Shadow
    private NonNullList<ItemStack> items;
    @Shadow
    private boolean[] lastPotionCount;

    @Unique
    @Nullable
    private UUID itemrestrictions$playerUUID;
    @Unique
    @Nullable
    private ServerPlayer itemrestrictions$player;

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z")
    private void stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (itemrestrictions$getPlayer() != serverPlayer) {
                itemrestrictions$setPlayer(serverPlayer);
                itemrestrictions$setPlayerUUID(serverPlayer.getUUID());
                saveWithFullMetadata();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V")
    private void saveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
        ServerPlayer serverPlayer = itemrestrictions$getPlayer();
        if (serverPlayer != null) {
            compoundTag.putString("ItemRestrictionsServerPlayer", serverPlayer.getUUID().toString());
        } else {
            UUID uuid = itemrestrictions$getPlayerUUID();
            if (uuid != null) {
                compoundTag.putString("ItemRestrictionsServerPlayer", uuid.toString());
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "load(Lnet/minecraft/nbt/CompoundTag;)V")
    private void load(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("ItemRestrictionsServerPlayer")) {
            itemrestrictions$setPlayerUUID(UUID.fromString(compoundTag.getString("ItemRestrictionsServerPlayer")));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;)V", cancellable = true)
    private static void doBrew(Level level, BlockPos blockPos, BlockState blockState, BrewingStandBlockEntity brewingStandBlockEntity, CallbackInfo ci) {
        if (brewingStandBlockEntity instanceof ItemRestrictionsBrewingStandBlockEntity block) {
            if (block.itemrestrictions$getPlayer() == null && block.itemrestrictions$getPlayerUUID() != null && level.getServer() != null) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(block.itemrestrictions$getPlayerUUID());
                block.itemrestrictions$setPlayer(player);

            }
            if (block.itemrestrictions$getPlayer() != null && !brewingStandBlockEntity.getItem(3).isEmpty()) {
                if (!brewingStandBlockEntity.getItem(0).isEmpty() || !brewingStandBlockEntity.getItem(1).isEmpty() || !brewingStandBlockEntity.getItem(2).isEmpty()) {
                    ItemStack ingredient = brewingStandBlockEntity.getItem(3);
                    for (int i = 0; i < 3; i++) {
                        ItemStack potion = brewingStandBlockEntity.getItem(i);
                        ItemStack mixedPotion = PotionBrewing.mix(ingredient, potion);

                        RestrictionResult result = new RestrictionResult();

                        if (block.itemrestrictions$getPlayer() instanceof ItemRestrictionsServerPlayer player) {
                            if (player instanceof ArcPlayer arcPlayer) {
                                result = player.itemrestrictions$isRestricted(
                                        new ActionDataBuilder(arcPlayer, null)
                                                .withData(ActionDataType.ITEM_STACK, mixedPotion)
                                                .build());
                            }
                        }

                        if (result.isRestricted(RestrictionType.BREW)) {
                            block.itemrestrictions$setBrewTime(0);
                            setChanged(level, blockPos, brewingStandBlockEntity.getBlockState());
                            ci.cancel();

                            boolean[] bls = block.itemrestrictions$getPotionBits();
                            if (!Arrays.equals(bls, block.itemrestrictions$getLastPotionCount())) {
                                block.itemrestrictions$setLastPotionCount(bls);
                                BlockState blockState2 = blockState;
                                if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
                                    return;
                                }

                                for (int j = 0; j < BrewingStandBlock.HAS_BOTTLE.length; ++j) {
                                    blockState2 = blockState2.setValue(BrewingStandBlock.HAS_BOTTLE[j], bls[j]);
                                }

                                level.setBlock(blockPos, blockState2, 2);
                            }
                            itemrestrictions$sendPacketCantCraft(RestrictionType.BREW, block);
                            return;
                        }
                    }
                } else if (brewingStandBlockEntity.getItem(0).isEmpty() && brewingStandBlockEntity.getItem(1).isEmpty() && brewingStandBlockEntity.getItem(2).isEmpty()) {
                    itemrestrictions$sendPacketCantCraft(RestrictionType.NONE, block);
                }
            } else if (block.itemrestrictions$getPlayer() != null && brewingStandBlockEntity.getItem(3).isEmpty()) {
                itemrestrictions$sendPacketCantCraft(RestrictionType.NONE, block);
            }
        }
    }

    @Unique
    private static void itemrestrictions$sendPacketCantCraft(RestrictionType type, ItemRestrictionsBrewingStandBlockEntity block) {
        if (block.itemrestrictions$getPlayer().containerMenu instanceof BrewingStandMenu menu) {
            if (menu.brewingStand.equals(block.itemrestrictions$getBrewingStandBlockEntity())) {
                new ClientboundRestrictionPacket(type).sendTo(block.itemrestrictions$getPlayer());
            }
        }
    }

    @Override
    @Nullable
    public ServerPlayer itemrestrictions$getPlayer() {
        return itemrestrictions$player;
    }

    @Override
    public void itemrestrictions$setPlayer(@Nullable ServerPlayer player) {
        this.itemrestrictions$player = player;
    }

    @Override
    @Nullable
    public UUID itemrestrictions$getPlayerUUID() {
        return itemrestrictions$playerUUID;
    }

    @Override
    public void itemrestrictions$setPlayerUUID(@Nullable UUID playerUUID) {
        this.itemrestrictions$playerUUID = playerUUID;
    }

    @Override
    public void itemrestrictions$setBrewTime(int i) {
        this.brewTime = i;
    }

    @Override
    public boolean[] itemrestrictions$getPotionBits() {
        boolean[] bls = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!this.items.get(i).isEmpty()) {
                bls[i] = true;
            }
        }

        return bls;
    }

    @Override
    public boolean[] itemrestrictions$getLastPotionCount() {
        return this.lastPotionCount;
    }

    @Override
    public void itemrestrictions$setLastPotionCount(boolean[] lastPotionCount) {
        this.lastPotionCount = lastPotionCount;
    }

    @SuppressWarnings("DataFlowIssue")
    public BrewingStandBlockEntity itemrestrictions$getBrewingStandBlockEntity() {
        return (BrewingStandBlockEntity) (Object) this;
    }
}
