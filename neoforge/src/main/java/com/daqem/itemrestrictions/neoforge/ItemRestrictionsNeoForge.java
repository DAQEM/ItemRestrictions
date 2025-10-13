package com.daqem.itemrestrictions.neoforge;

import com.daqem.itemrestrictions.ItemRestrictions;
import net.neoforged.fml.common.Mod;

@Mod(ItemRestrictions.MOD_ID)
public class ItemRestrictionsNeoForge {
    public ItemRestrictionsNeoForge() {
        ItemRestrictions.init();
    }
}
