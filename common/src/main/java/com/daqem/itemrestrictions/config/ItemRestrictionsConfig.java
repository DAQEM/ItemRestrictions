package com.daqem.itemrestrictions.config;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.knot.api.platform.Platform;
import com.daqem.yamlconfig.api.config.ConfigExtension;
import com.daqem.yamlconfig.api.config.ConfigType;
import com.daqem.yamlconfig.api.config.IConfigBuilder;
import com.daqem.yamlconfig.api.config.entry.IConfigEntry;
import com.daqem.yamlconfig.impl.config.ConfigBuilder;

import java.util.List;

public class ItemRestrictionsConfig {

    public static final IConfigEntry<List<String>> excludedRestrictions;

    static {
        IConfigBuilder builder = new ConfigBuilder(
                ItemRestrictions.MOD_ID,
                "item-restrictions-common",
                ConfigExtension.YAML,
                ConfigType.COMMON,
                Platform.INFO.getConfigFolder().resolve(ItemRestrictions.MOD_ID)
        );

        builder.push("restrictions");
        excludedRestrictions = builder.defineStringList("excluded_restrictions", List.of())
                .withComments("A list of restriction IDs to exclude from the game. Example: ['<namespace>:<restriction_id>']");
        builder.pop();

        builder.build();
    }

    public static void init() {
    }
}
