package com.daqem.itemrestrictions.networking;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface ItemRestrictionsNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(ItemRestrictions.MOD_ID);

    MessageType CLIENTBOUND_RESTRICTION = NETWORK.registerS2C("clientbound_restriction", ClientboundRestrictionPacket::new);

    static void init() {
    }
}
