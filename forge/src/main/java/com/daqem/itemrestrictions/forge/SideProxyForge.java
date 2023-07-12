package com.daqem.itemrestrictions.forge;

import com.daqem.itemrestrictions.client.ItemRestrictionsClient;
import com.daqem.itemrestrictions.forge.data.ItemRestrictionManagerForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class SideProxyForge {

    SideProxyForge() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(this::onAddReloadListeners);
    }

    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ItemRestrictionManagerForge());
    }

    public static class Server extends SideProxyForge {
        Server() {
        }
    }

    public static class Client extends SideProxyForge {
        Client() {
            ItemRestrictionsClient.init();
        }
    }
}
