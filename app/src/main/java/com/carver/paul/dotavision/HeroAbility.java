package com.carver.paul.dotavision;

import java.util.ArrayList;
import java.util.List;

public class HeroAbility {
    public static final int STUN = 0;
    public static final int DISABLE_NOT_STUN = 1;
    public static final int SILENCE = 2;
    public static final int ULTIMATE = 3;

    public boolean isUltimate;
    public boolean isStun;
    public boolean isDisable;
    public boolean isSilence;
    public boolean isMute;
    public String heroName;
    public String name;
    public String imageName;
    public String description;
    public String manaCost;
    public String cooldown;
    public String disableDuration;
    public List<String> abilityDetails;

    public HeroAbility() {
        abilityDetails = new ArrayList<>();
    }

    //TODO: save silence durations in the XML too
    public String guessAbilityDuration(int abilityType) {
        if (abilityType != STUN && abilityType != SILENCE && abilityType != DISABLE_NOT_STUN)
            throw new RuntimeException("guessAbilityDuration passed wrong abilityType");

        if (abilityType == STUN || abilityType == DISABLE_NOT_STUN) {
            if (isDisable != true) {
                return null;
            }
            else {
                return disableDuration;
            }
        }

        if (abilityType != SILENCE)
            throw new RuntimeException("abilityType should be silence by now");

        for (String detail : abilityDetails) {
            if (detail.contains("SILENCE")
                    && (detail.contains("MAX") || detail.contains("DURATION"))) {
                return detail;
            } else if (detail.startsWith("DURATION:")) {
                return detail;
            } else if (detail.startsWith("HERO DURATION:")) {
                return detail;
            }
        }

        // If still not found the duration, then just use any old detail string with a DURATION or
        // MAX
        for (String detail : abilityDetails) {
            if (detail.contains("DURATION") || detail.contains("MAX")) {
                return detail;
            }
        }

        return null;
    }

}
