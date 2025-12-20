package com.daqem.itemrestrictions.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.daqem.arc.api.action.data.IActionDataType;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.IConditionSerializer;
import com.daqem.arc.data.ActionData;
import com.daqem.arc.data.serializer.ArcSerializer;
import com.daqem.arc.registry.ArcRegistry;
import com.daqem.itemrestrictions.ItemRestrictions;
import com.google.gson.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class ItemRestriction {

    private final Identifier location;
    private final ItemStack icon;
    private final List<RestrictionType> restrictionTypes;
    private final List<ICondition> conditions;
    private final boolean clientSide;

    public ItemRestriction(Identifier location, ItemStack icon, List<RestrictionType> restrictionTypes, List<ICondition> conditions, boolean clientSide) {
        this.location = location;
        this.icon = icon;
        this.restrictionTypes = restrictionTypes;
        this.conditions = conditions;
        this.clientSide = clientSide;
    }

    public RestrictionResult isRestricted(ActionData actionData) {
        ItemStack itemStack = actionData.getData(IActionDataType.ITEM_STACK);

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

    public static class Serializer implements JsonDeserializer<ItemRestriction>, ArcSerializer {

        @Override
        public ItemRestriction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Identifier location = getIdentifier(jsonObject, "location");
            JsonArray restrictionTypesArray = GsonHelper.getAsJsonArray(jsonObject, "types");
            JsonArray conditionsArray = GsonHelper.getAsJsonArray(jsonObject, "conditions");
            boolean clientSide = GsonHelper.getAsBoolean(jsonObject, "client_side", true);

            List<RestrictionType> restrictionTypes = new ArrayList<>();
            List<ICondition> conditions = new ArrayList<>();
            ItemStack iconStack = getItemStack(jsonObject, "icon", ItemStack.EMPTY);

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
                Identifier conditionTypeLocation = Identifier.parse(GsonHelper.getAsString(jsonElement.getAsJsonObject(), "type"));
                ArcRegistry.CONDITION.getOptional(conditionTypeLocation).ifPresent(conditionType -> {
                    conditions.add(conditionType.getSerializer().fromJson(Identifier.parse(""), jsonElement.getAsJsonObject()));
                });
            });

            return new ItemRestriction(location, iconStack, restrictionTypes, conditions, clientSide);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, ItemRestriction itemRestriction) {
            buf.writeIdentifier(itemRestriction.location);
            ItemStack.STREAM_CODEC.encode(buf, itemRestriction.icon);
            buf.writeCollection(itemRestriction.restrictionTypes, (byteBuf, restrictionType) -> byteBuf.writeUtf(restrictionType.name()));
            buf.writeCollection(itemRestriction.conditions, (byteBuf, condition) -> IConditionSerializer.toNetwork(condition, (RegistryFriendlyByteBuf) byteBuf, itemRestriction.getIdentifier()));
            buf.writeBoolean(itemRestriction.clientSide);
        }

        public static ItemRestriction fromNetwork(RegistryFriendlyByteBuf buf) {
            Identifier location = buf.readIdentifier();
            ItemStack icon = ItemStack.STREAM_CODEC.decode(buf);
            List<String> restrictionTypeStrings = buf.readList(FriendlyByteBuf::readUtf);
            List<RestrictionType> restrictionTypes = new ArrayList<>();
            restrictionTypeStrings.forEach(restrictionTypeString -> {
                try {
                    RestrictionType restrictionType = RestrictionType.valueOf(restrictionTypeString.toUpperCase());
                    restrictionTypes.add(restrictionType);
                } catch (IllegalArgumentException e) {
                    ItemRestrictions.LOGGER.error("Could not deserialize restriction type {} because: {}", restrictionTypeString, e.getMessage());
                }
            });
            List<ICondition> conditions = buf.readList(object -> IConditionSerializer.fromNetwork((RegistryFriendlyByteBuf) object));
            boolean clientSide = buf.readBoolean();
            return new ItemRestriction(location, icon, restrictionTypes, conditions, clientSide);
        }
    }

    @SuppressWarnings("unused")
    public Identifier getIdentifier() {
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

    public boolean isClientSide() {
        return clientSide;
    }
}
