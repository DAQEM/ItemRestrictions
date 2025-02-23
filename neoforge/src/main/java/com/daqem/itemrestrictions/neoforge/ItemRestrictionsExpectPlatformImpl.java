package com.daqem.itemrestrictions.neoforge;

import com.daqem.itemrestrictions.ItemRestrictionsExpectPlatform;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.itemrestrictions.neoforge.data.ItemRestrictionManagerForge;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ItemRestrictionsExpectPlatformImpl {
    /**
     * This is our actual method to {@link ItemRestrictionsExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static ItemRestrictionManager getItemRestrictionManager() {
        return new ItemRestrictionManagerForge();
    }
}
