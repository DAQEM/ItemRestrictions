package com.daqem.itemrestrictions.networking;


import com.daqem.itemrestrictions.client.networking.ClientboundRestrictionPacketHandler;
import com.daqem.itemrestrictions.client.networking.ClientboundUpdateItemRestrictionsPacketHandler;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundUpdateItemRestrictionsPacket;
import com.daqem.knot.Knot;

public interface ItemRestrictionsNetworking {

    static void init() {
        Knot.NETWORKING.registerClientbound(ClientboundRestrictionPacket.TYPE, ClientboundRestrictionPacket.STREAM_CODEC, () -> ClientboundRestrictionPacketHandler::handle);
        Knot.NETWORKING.registerClientbound(ClientboundUpdateItemRestrictionsPacket.TYPE, ClientboundUpdateItemRestrictionsPacket.STREAM_CODEC, () -> ClientboundUpdateItemRestrictionsPacketHandler::handle);
    }
}
