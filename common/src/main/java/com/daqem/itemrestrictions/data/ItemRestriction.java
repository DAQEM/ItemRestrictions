package com.daqem.itemrestrictions.data;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.data.type.ActionDataType;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.data.serializer.ArcSerializer;
import com.daqem.arc.registry.ArcRegistry;
import com.daqem.itemrestrictions.ItemRestrictions;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
        ItemStack itemStack = actionData.getData(ActionDataType.ITEM_STACK);

        if (itemStack == null) {
            return new RestrictionResult();
        }

        boolean allConditionsMet = this.conditions.stream()
                .allMatch(condition ->
                        (condition.isMet(actionData) && !condition.isInverted()) ||
                                (!condition.isMet(actionData) && condition.isInverted())
                );

        if (allConditionsMet) {
            return new RestrictionResult(this.restrictionTypes);
        } else {
            return new RestrictionResult();
        }
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public static class Serializer implements JsonDeserializer<ItemRestriction>, ArcSerializer {

        @Override
        public ItemRestriction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray restrictionTypesArray = GsonHelper.getAsJsonArray(jsonObject, "types");
            JsonArray conditionsArray = GsonHelper.getAsJsonArray(jsonObject, "conditions");

            List<RestrictionType> restrictionTypes = new ArrayList<>();
            List<ICondition> conditions = new ArrayList<>();

            ItemStack iconStack = ItemStack.EMPTY;
            if (jsonObject.has("icon")) {
                iconStack = getItemStack(jsonObject.getAsJsonObject("icon"));
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
                ResourceLocation conditionTypeLocation = ResourceLocation.parse(GsonHelper.getAsString(jsonElement.getAsJsonObject(), "type"));
                ArcRegistry.CONDITION.getOptional(conditionTypeLocation).ifPresent(conditionType -> {
                    conditions.add(conditionType.getSerializer().fromJson(ResourceLocation.parse(""), jsonElement.getAsJsonObject()));
                });
            });

            return new ItemRestriction(iconStack, restrictionTypes, conditions);
        }
    }

    @SuppressWarnings("unused")
    public ResourceLocation getLocation() {
        return location;
    }

    @SuppressWarnings("unused")
    public ItemStack getIcon() {
        return icon;
    }

    @SuppressWarnings("unused")
    public List<ICondition> getConditions() {
        return conditions;
    }

    @SuppressWarnings("unused")
    public List<RestrictionType> getRestrictionTypes() {
        return restrictionTypes;
    }
}
