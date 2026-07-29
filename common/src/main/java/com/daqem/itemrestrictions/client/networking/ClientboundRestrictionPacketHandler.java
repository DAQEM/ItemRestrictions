package com.daqem.itemrestrictions.client.networking;

import com.daqem.itemrestrictions.client.screen.ItemRestrictionsScreen;
import com.daqem.itemrestrictions.networking.clientbound.ClientboundRestrictionPacket;
import com.daqem.knot.networking.ClientboundContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

public class ClientboundRestrictionPacketHandler {

    public static void handle(@NotNull ClientboundRestrictionPacket packet, ClientboundContext clientboundContext) {
        if (clientboundContext.player() instanceof LocalPlayer) {
            Screen currentScreen = Minecraft.getInstance().gui.screen();
            if (currentScreen instanceof ItemRestrictionsScreen itemRestrictionsScreen) {
                itemRestrictionsScreen.itemrestrictions$cantCraft(packet.getRestrictionType());
            }
        }
    }
}
