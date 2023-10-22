package com.daqem.itemrestrictions.mixin.block;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.data.RestrictionResult;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.level.block.ItemRestrictionsFurnaceBlockEntity;
import com.daqem.itemrestrictions.level.player.ItemRestrictionsServerPlayer;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible, ItemRestrictionsFurnaceBlockEntity {

    @Unique
    @Nullable
    private UUID itemrestrictions$playerUUID;

    @Unique
    @Nullable
    private ServerPlayer itemrestrictions$player;

    @Unique
    private boolean itemrestrictions$isRestricted = false;

    @Shadow
    int litTime;

    protected MixinAbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow
    public abstract @NotNull ItemStack getItem(int i);

    @Unique
    private RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> itemrestrictions$quickCheck;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<? extends AbstractCookingRecipe> recipeType, CallbackInfo ci) {
        this.itemrestrictions$quickCheck = RecipeManager.createCheck(recipeType);
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

    @Inject(at = @At(value = "HEAD"), method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)V", cancellable = true)
    private static void serverTickRecipe(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfo ci) {
        if (abstractFurnaceBlockEntity instanceof ItemRestrictionsFurnaceBlockEntity block) {
            if (block.itemrestrictions$getPlayer() == null && block.itemrestrictions$getPlayerUUID() != null && level.getServer() != null) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(block.itemrestrictions$getPlayerUUID());
                block.itemrestrictions$setPlayer(player);

            }
            if (block.itemrestrictions$getPlayer() != null && !abstractFurnaceBlockEntity.getItem(0).isEmpty()) {
                if (!abstractFurnaceBlockEntity.getItem(1).isEmpty()) {

                    Recipe<?> recipe = block.itemrestrictions$getRecipe();
                    if (recipe != null) {

                        RestrictionResult result = new RestrictionResult();

                        if (block.itemrestrictions$getPlayer() instanceof ItemRestrictionsServerPlayer player) {
                            if (player instanceof ArcPlayer arcPlayer && ((ServerPlayer) player).getServer() != null) {
                                result = player.itemrestrictions$isRestricted(
                                        new ActionDataBuilder(arcPlayer, null)
                                                .withData(ActionDataType.ITEM_STACK, recipe.getResultItem(((ServerPlayer) player).getServer().registryAccess()))
                                                .build());
                            }
                        }

                        if (result.isRestricted(RestrictionType.SMELT)) {
                            if (block.itemrestrictions$isLit()) {
                                block.itemrestrictions$setLitTime(block.itemrestrictions$getLitTime() - 1);
                            }
                            blockState = blockState.setValue(AbstractFurnaceBlock.LIT, false);
                            level.setBlock(blockPos, blockState, 3);
                            setChanged(level, blockPos, blockState);
                            ci.cancel();

                            itemrestrictions$sendPacketCantCraft(RestrictionType.SMELT, block);
                            block.itemrestrictions$setRestricted(true);
                        }
                    }
                } else {
                    if (block.itemrestrictions$isRestricted()) {
                        itemrestrictions$sendPacketCantCraft(RestrictionType.NONE, block);
                        block.itemrestrictions$setRestricted(false);
                    }
                }
            } else if (block.itemrestrictions$getPlayer() != null) {
                if (block.itemrestrictions$isRestricted()) {
                    itemrestrictions$sendPacketCantCraft(RestrictionType.NONE, block);
                    block.itemrestrictions$setRestricted(false);
                }
            }
        }
    }

    @Unique
    private static void itemrestrictions$sendPacketCantCraft(RestrictionType type, ItemRestrictionsFurnaceBlockEntity block) {
        if (block.itemrestrictions$getPlayer().containerMenu instanceof AbstractFurnaceMenu menu) {
            if (menu.container.equals(block.itemrestrictions$getAbstractFurnaceBlockEntity())) {
                new ClientboundRestrictionPacket(type).sendTo(block.itemrestrictions$getPlayer());
            }
        }
    }

    @Override
    public @Nullable ServerPlayer itemrestrictions$getPlayer() {
        return itemrestrictions$player;
    }

    @Override
    public void itemrestrictions$setPlayer(@Nullable ServerPlayer player) {
        this.itemrestrictions$player = player;
    }

    @Override
    public @Nullable UUID itemrestrictions$getPlayerUUID() {
        return itemrestrictions$playerUUID;
    }

    @Override
    public void itemrestrictions$setPlayerUUID(@Nullable UUID playerUUID) {
        this.itemrestrictions$playerUUID = playerUUID;
    }

    @Override
    public int itemrestrictions$getLitTime() {
        return litTime;
    }

    @Override
    public void itemrestrictions$setLitTime(int litTime) {
        this.litTime = litTime;
    }

    @Override
    public boolean itemrestrictions$isLit() {
        return itemrestrictions$getLitTime() > 0;
    }

    @Override
    @Nullable
    public RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> itemrestrictions$getQuickCheck() {
        return itemrestrictions$quickCheck;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public AbstractFurnaceBlockEntity itemrestrictions$getAbstractFurnaceBlockEntity() {
        return (AbstractFurnaceBlockEntity) (Object) this;
    }

    @Override
    @Nullable
    public Recipe<?> itemrestrictions$getRecipe() {
        if (getLevel() == null) return null;
        if (getItem(0).isEmpty()) return null;
        if (getItem(1).isEmpty()) return null;
        if (itemrestrictions$getQuickCheck() == null) return null;
        return Objects.requireNonNull(itemrestrictions$getQuickCheck()).getRecipeFor(this, getLevel()).orElse(null);
    }

    @Override
    public void itemrestrictions$setRestricted(boolean restricted) {
        this.itemrestrictions$isRestricted = restricted;
    }

    @Override
    public boolean itemrestrictions$isRestricted() {
        return itemrestrictions$isRestricted;
    }
}
