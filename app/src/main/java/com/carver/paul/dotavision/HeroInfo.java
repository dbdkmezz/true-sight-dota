/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision;

import java.util.ArrayList;
import java.util.List;

class HeroInfo {
    public String name;
    public String imageName;
    public String bioRoles;
    public String intelligence;
    public String agility;
    public String strength;
    public String attack;
    public String speed;
    public String defence;
    public List<HeroAbility> abilities;

    public HeroInfo() {
        abilities = new ArrayList<>();
    }

    public boolean HasName(String string) {
        return (string.equals(imageName) || string.equalsIgnoreCase(name));
/*        string = string.replace('_', ' ');
        return string.equalsIgnoreCase(this.name);*/
    }

    public int CountStuns() {
        int count = 0;
        for (HeroAbility ability : abilities) {
            if (ability.isStun)
                count++;
        }
        return count;
    }

/*    public String StunSummary() {
        for(HeroAbility ability : abilities) {

        }
    }*/
}

class HeroAbility {
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