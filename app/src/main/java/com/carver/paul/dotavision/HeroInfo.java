package com.carver.paul.dotavision;

import java.util.ArrayList;
import java.util.List;

class HeroInfo {
    public String name;
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
        string = string.replace('_', ' ');
        return string.equalsIgnoreCase(this.name);
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
    public boolean isStun;
    public String name;
    public String description;
    public String manaCost;
    public List<String> abilityDetails;

    public HeroAbility() {
        abilityDetails = new ArrayList<>();
    }
}