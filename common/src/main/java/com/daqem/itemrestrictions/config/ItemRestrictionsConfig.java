package com.daqem.itemrestrictions.config;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class ItemRestrictionsConfig {

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void init() {
    }

    public static final Supplier<Boolean> isDebug;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig(ItemRestrictions.MOD_ID, null, false);

        config.push("debug");
        isDebug = config.comment("if true, debug mode is enabled").define("is_debug", false);
        config.pop();

        config.build();
    }
}
