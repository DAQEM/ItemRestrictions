package com.daqem.itemrestrictions.networking.clientbound;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.knot.api.codec.KnotStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundRestrictionPacket(RestrictionType restrictionType) implements CustomPacketPayload {

    public static final Type<@NotNull ClientboundRestrictionPacket> TYPE = new Type<>(ItemRestrictions.API.getId("clientbound_restriction_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRestrictionPacket> STREAM_CODEC = StreamCodec.composite(
            KnotStreamCodecs.enumCodec(RestrictionType.class),
            ClientboundRestrictionPacket::restrictionType,
            ClientboundRestrictionPacket::new
    );

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }
}
