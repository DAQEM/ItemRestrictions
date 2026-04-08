package com.daqem.itemrestrictions.networking.clientbound;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.data.ItemRestriction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundUpdateItemRestrictionsPacket(List<ItemRestriction> itemRestrictions) implements CustomPacketPayload {

    public static final Type<@NotNull ClientboundUpdateItemRestrictionsPacket> TYPE = new Type<>(ItemRestrictions.API.getId("clientbound_update_item_restrictions_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateItemRestrictionsPacket> STREAM_CODEC = StreamCodec.composite(
            ItemRestriction.Serializer.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ClientboundUpdateItemRestrictionsPacket::itemRestrictions,
            ClientboundUpdateItemRestrictionsPacket::new
    );

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public List<ItemRestriction> getItemRestrictions() {
        return itemRestrictions;
    }
}
