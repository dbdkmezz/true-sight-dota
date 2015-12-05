/**
 * True Sight for Dota 2
 * Copyright (C) 2015 Paul Broadbent
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
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
        return string.equals(this.imageName);
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
    public List<String> abilityDetails;

    public HeroAbility() {
        abilityDetails = new ArrayList<>();
    }

    public String guessAbilityDuration(int abilityType) {
        if(abilityType != STUN && abilityType != SILENCE && abilityType != DISABLE_NOT_STUN)
            throw new RuntimeException("guessAbilityDuration passed wrong abilityType");

        if (abilityType == STUN && isStun != true) return null;
        if (abilityType == DISABLE_NOT_STUN && isDisable != true) return null;
        if (abilityType == SILENCE && isSilence != true) return null;

        String abilityDescription = "";
        if (abilityType == STUN)
            abilityDescription = "STUN";
        else if (abilityType == SILENCE)
            abilityDescription = "SILENCE";
        else if (abilityType != DISABLE_NOT_STUN)
            abilityDescription = "XXXXX";

        for (String detail : abilityDetails) {
            if (abilityType != DISABLE_NOT_STUN
                    && detail.contains(abilityDescription + " DURATION:"))
                return detail;
            else if (abilityType != DISABLE_NOT_STUN
                    && detail.contains("MAX " + abilityDescription + ":"))
                return detail;
            else if (detail.startsWith("DURATION:"))
                return detail;
            else if (detail.startsWith("HERO DURATION:"))
                return detail;
            else if (detail.startsWith("CHANNEL DURATION:"))
                return detail;
        }
        return null;
    }

}