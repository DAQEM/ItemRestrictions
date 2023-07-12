package com.daqem.itemrestrictions.fabric;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.fabric.data.ItemRestrictionManagerFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class ItemRestrictionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ItemRestrictions.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ItemRestrictionManagerFabric());
    }
}
