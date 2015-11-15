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
    public static final int SILENCE = 1;
    public static final int ULTIMATE = 2;

    public boolean isUltimate;
    public boolean isStun;
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
        if(abilityType != STUN && abilityType != SILENCE)
            throw new RuntimeException("guessAbilityDuration passed wrong abilityType");

        if (abilityType == STUN && isStun != true) return null;
        if (abilityType == SILENCE && isSilence != true) return null;

        String abilityDescription = "";
        if (abilityType == STUN)
            abilityDescription = "STUN";
        else if (abilityType == SILENCE)
            abilityDescription = "SILENCE";

        for (String detail : abilityDetails) {
            if (detail.contains(abilityDescription + " DURATION:"))
                return detail;
            else if (detail.contains("MAX " + abilityDescription + ":"))
                return detail;
            else if (detail.startsWith("DURATION:"))
                return detail;
        }
        return null;
    }

}