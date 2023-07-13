package com.daqem.itemrestrictions.data;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.registry.ArcRegistry;
import com.daqem.itemrestrictions.ItemRestrictions;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemRestriction {

    private ResourceLocation location;

    private final ItemStack icon;

    private final List<RestrictionType> restrictionTypes;
    private final List<ICondition> conditions;

    public ItemRestriction(ItemStack icon, List<RestrictionType> restrictionTypes, List<ICondition> conditions) {
        this.icon = icon;
        this.restrictionTypes = restrictionTypes;
        this.conditions = conditions;
    }

    public RestrictionResult isRestricted(ActionData actionData) {
        RestrictionResult result = new RestrictionResult();
        ItemStack itemStack = actionData.getData(ActionDataType.ITEM_STACK);
        if (itemStack != null) {
            for (ICondition condition : conditions) {
                boolean met = condition.isMet(actionData);
                if ((!condition.isInverted() && !met) || (condition.isInverted() && met)) {
                    result = new RestrictionResult(restrictionTypes);
                }
            }
        }
        return result;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public static class Serializer implements JsonDeserializer<ItemRestriction> {

        @Override
        public ItemRestriction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray restrictionTypesArray = GsonHelper.getAsJsonArray(jsonObject, "types");
            JsonArray conditionsArray = GsonHelper.getAsJsonArray(jsonObject, "conditions");

            List<RestrictionType> restrictionTypes = new ArrayList<>();
            List<ICondition> conditions = new ArrayList<>();

            JsonObject iconObject = GsonHelper.getAsJsonObject(jsonObject, "icon");
            Item item = GsonHelper.getAsItem(iconObject, "item");
            int count = GsonHelper.getAsInt(iconObject, "count", 1);
            ItemStack iconStack = new ItemStack(item);

            iconStack.setCount(count);

            if (iconObject.has("tag")) {
                String tagName = GsonHelper.getAsString(iconObject, "tag");

                try {
                    iconStack.setTag(TagParser.parseTag(tagName));
                } catch (CommandSyntaxException e) {
                    String errorMessage = String.format("Error parsing tag for PowerupInstance icon %s: %s", tagName, e.getMessage());
                    ItemRestrictions.LOGGER.error(errorMessage);
                }
            }

            restrictionTypesArray.forEach(jsonElement -> {
                String restrictionTypeString = jsonElement.getAsString();
                try {
                    RestrictionType restrictionType = RestrictionType.valueOf(restrictionTypeString.toUpperCase());
                    restrictionTypes.add(restrictionType);
                } catch (IllegalArgumentException e) {
                    ItemRestrictions.LOGGER.error("Could not deserialize restriction type {} because: {}", restrictionTypeString, e.getMessage());
                }
            });

            conditionsArray.forEach(jsonElement -> {
                ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(jsonElement.getAsJsonObject(), "type"));
                ArcRegistry.CONDITION_SERIALIZER.getOptional(type).ifPresent(serializer -> {
                    conditions.add(serializer.fromJson(new ResourceLocation(""), jsonElement.getAsJsonObject()));
                });
            });

            return new ItemRestriction(iconStack, restrictionTypes, conditions);
        }
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<ICondition> getConditions() {
        return conditions;
    }

    public List<RestrictionType> getRestrictionTypes() {
        return restrictionTypes;
    }
}
