package com.daqem.itemrestrictions.fabric;

import com.daqem.itemrestrictions.ItemRestrictionsExpectPlatform;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.itemrestrictions.fabric.data.ItemRestrictionManagerFabric;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ItemRestrictionsExpectPlatformImpl {
    /**
     * This is our actual method to {@link ItemRestrictionsExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static ItemRestrictionManager getItemRestrictionManager() {
        return new ItemRestrictionManagerFabric();
    }
}
