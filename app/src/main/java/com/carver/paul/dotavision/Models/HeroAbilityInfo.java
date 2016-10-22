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

package com.carver.paul.dotavision.Models;

import java.util.ArrayList;
import java.util.List;

public class HeroAbilityInfo {
    public static final int STUN = 0;
    public static final int DISABLE_NOT_STUN = 1;
    public static final int SILENCE = 2;
    public static final int ULTIMATE = 3;

    public boolean isUltimate;
    public boolean isStun;
    public boolean isDisable;
    public boolean isSilence;
    public boolean isMute;
    public Boolean piercesSpellImmunity = null;
    public String heroName;
    public String name;
    public String imageName;
    public String description;
    public String manaCost;
    public String cooldown;
    public String disableDuration;
    public List<String> abilityDetails;
    public List<RemovableBuff> removableDebuffs;

    public HeroAbilityInfo() {
        abilityDetails = new ArrayList<>();
        removableDebuffs = new ArrayList<>();
    }

    public HeroAbilityInfo Copy()
    {
        HeroAbilityInfo ability = new HeroAbilityInfo();
        ability.isUltimate = isUltimate;ability.isStun = isStun;
        ability.isDisable = isDisable;
        ability.isSilence = isSilence;
        ability.isMute = isMute;
        ability.heroName = heroName;
        ability.name = name;
        ability.imageName = imageName;
        ability.description = description;
        ability.manaCost = manaCost;
        ability.cooldown = cooldown;
        ability.disableDuration = disableDuration;
        ability.abilityDetails = abilityDetails;
        ability.removableDebuffs = removableDebuffs;
        ability.piercesSpellImmunity = piercesSpellImmunity;

        return ability;
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

    public static class RemovableBuff {
        public String description;
        public boolean basicDispel;
        public boolean strongDispel;
    }
}
