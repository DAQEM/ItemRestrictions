package com.daqem.itemrestrictions.fabric.data;

import com.daqem.itemrestrictions.ItemRestrictions;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ItemRestrictionManagerFabric extends ItemRestrictionManager implements IdentifiableResourceReloadListener {

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        super.apply(map, resourceManager, profilerFiller);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ItemRestrictions.getId("crafting_restrictions");
    }
}
