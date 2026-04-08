package com.daqem.itemrestrictions;

import com.daqem.arc.registry.ArcRegistry;
import com.daqem.itemrestrictions.config.ItemRestrictionsConfig;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.itemrestrictions.event.ItemRestrictionsEvents;
import com.daqem.itemrestrictions.networking.ItemRestrictionsNetworking;
import com.daqem.knot.Knot;

public class ItemRestrictions {

    public static final String MOD_ID = "itemrestrictions";
    public static final Knot API = new Knot(MOD_ID);

    public static void init() {
        ItemRestrictionsConfig.init();
        registerEvents();
        initRegistry();
        initNetworking();
        Knot.RELOAD_REGISTRY.registerData(API.getId(MOD_ID), new ItemRestrictionManager());
    }

    private static void initNetworking() {
        ItemRestrictionsNetworking.init();
    }

    private static void initRegistry() {
        ArcRegistry.init();
    }

    private static void registerEvents() {
        ItemRestrictionsEvents.registerEvents();
    }

}
