package com.daqem.itemrestrictions.data;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.ItemRestrictionsExpectPlatform;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ItemRestrictionManager extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeHierarchyAdapter(ItemRestriction.class, new ItemRestriction.Serializer())
            .create();

    private ImmutableMap<ResourceLocation, ItemRestriction> itemRestrictions = ImmutableMap.of();

    private static ItemRestrictionManager instance;

    public ItemRestrictionManager() {
        super(GSON, "itemrestrictions/restrictions");
        instance = this;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, ItemRestriction> tempItemRestrictions = new HashMap<>();

        map.forEach((location, jsonElement) -> {
            try {
                ItemRestriction itemRestriction = GSON.fromJson(jsonElement.getAsJsonObject(), ItemRestriction.class);
                itemRestriction.setLocation(location);
                tempItemRestrictions.put(location, itemRestriction);
            } catch (Exception e) {
                ItemRestrictions.LOGGER.error("Could not deserialize item restriction {} because: {}", location.toString(), e.getMessage());
            }
        });

        ItemRestrictions.LOGGER.info("Loaded {} item restrictions", tempItemRestrictions.size());
        this.itemRestrictions = ImmutableMap.copyOf(tempItemRestrictions);
    }

    public static ItemRestrictionManager getInstance() {
        return instance != null ? instance : ItemRestrictionsExpectPlatform.getItemRestrictionManager();
    }

    public List<ItemRestriction> getItemRestrictions() {
        return itemRestrictions.values().asList();
    }
}
