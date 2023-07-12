package com.daqem.itemrestrictions.forge;

import com.daqem.itemrestrictions.ItemRestrictions;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ItemRestrictions.MOD_ID)
public class ItemRestrictionsForge {
    public ItemRestrictionsForge() {
        EventBuses.registerModEventBus(ItemRestrictions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ItemRestrictions.init();

        DistExecutor.safeRunForDist(
                () -> SideProxyForge.Client::new,
                () -> SideProxyForge.Server::new
        );
    }
}
