package com.daqem.itemrestrictions.client.networking;

import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundUpdateItemRestrictionsPacket;
import com.daqem.knot.networking.ClientboundContext;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateItemRestrictionsPacketHandler {

    public static void handle(@NotNull ClientboundUpdateItemRestrictionsPacket packet, ClientboundContext clientboundContext) {
        if (!Minecraft.getInstance().isLocalServer()) {
            ItemRestrictionManager.getInstance().setItemRestrictions(packet.getItemRestrictions());
        }
    }
}
