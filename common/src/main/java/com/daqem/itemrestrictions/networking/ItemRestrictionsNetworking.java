package com.daqem.itemrestrictions.networking;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ItemRestrictionsNetworking {

    CustomPacketPayload.Type<ClientboundRestrictionPacket> CLIENTBOUND_RESTRICTION_TYPE = new CustomPacketPayload.Type<>(ItemRestrictions.getId("clientbound_restriction"));

    static void initClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CLIENTBOUND_RESTRICTION_TYPE, ClientboundRestrictionPacket.STREAM_CODEC, ClientboundRestrictionPacket::handleClientSide);
    }

    static void initServer() {
        NetworkManager.registerS2CPayloadType(CLIENTBOUND_RESTRICTION_TYPE, ClientboundRestrictionPacket.STREAM_CODEC);
    }

    static void init() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> ItemRestrictionsNetworking::initClient);
        EnvExecutor.runInEnv(Env.SERVER, () -> ItemRestrictionsNetworking::initServer);
    }
}
