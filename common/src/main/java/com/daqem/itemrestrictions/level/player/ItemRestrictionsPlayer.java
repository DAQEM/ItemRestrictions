package com.daqem.itemrestrictions.level.player;

import com.daqem.arc.data.ActionData;
import com.daqem.itemrestrictions.data.RestrictionResult;

public interface ItemRestrictionsPlayer {

    RestrictionResult itemrestrictions$isRestricted(ActionData actionData);
}
