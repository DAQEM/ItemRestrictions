package com.daqem.itemrestrictions.neoforge;

import com.daqem.itemrestrictions.client.ItemRestrictionsClient;
import com.daqem.itemrestrictions.neoforge.data.ItemRestrictionManagerForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class SideProxyForge {

    SideProxyForge() {
        IEventBus eventBus = NeoForge.EVENT_BUS;
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
