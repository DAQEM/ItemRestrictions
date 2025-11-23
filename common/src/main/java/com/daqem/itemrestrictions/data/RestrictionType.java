package com.daqem.itemrestrictions.data;

public enum RestrictionType {
    CRAFT("cant_craft"),
    SMELT("cant_smelt"),
    BREW("cant_brew"),
    ENCHANT("cant_enchant"),
    REPAIR("cant_repair"),
    USE_ITEM("cant_use_item"),
    BREAK_BLOCK("cant_break_block"),
    ITEM_BREAK_BLOCK("cant_item_break_block"),
    PLACE_BLOCK("cant_place_block"),
    HURT_ENTITY("cant_hurt_entity"),
    INTERACT_ENTITY("cant_interact_entity"),
    INTERACT_BLOCK("cant_interact_block"),
    NONE("");

    private final String translationKey;

    RestrictionType(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return "inventory." + translationKey;
    }

    public String getGuiTranslationKey() {
        return "gui." + translationKey;
    }
}
