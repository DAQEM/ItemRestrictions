package com.daqem.itemrestrictions.networking.clientbound;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.client.screen.ItemRestrictionsScreen;
import com.daqem.itemrestrictions.data.RestrictionType;
import com.daqem.itemrestrictions.networking.ItemRestrictionsNetworking;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundRestrictionPacket extends BaseS2CMessage {

    private final RestrictionType restrictionType;

    public ClientboundRestrictionPacket(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public ClientboundRestrictionPacket(FriendlyByteBuf friendlyByteBuf) {
        this.restrictionType = friendlyByteBuf.readEnum(RestrictionType.class);
    }

    @Override
    public MessageType getType() {
        return ItemRestrictionsNetworking.CLIENTBOUND_RESTRICTION;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(restrictionType);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ItemRestrictions.LOGGER.error("Received restriction packet from server!");
        if (context.getPlayer() instanceof LocalPlayer) {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof ItemRestrictionsScreen itemRestrictionsScreen) {
                itemRestrictionsScreen.itemrestrictions$cantCraft(restrictionType);
            }
        }
    }
}
