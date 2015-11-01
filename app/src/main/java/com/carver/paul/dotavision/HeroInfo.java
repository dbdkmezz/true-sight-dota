package com.carver.paul.dotavision;

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
}

class HeroAbility {
    public boolean isStun = false;
    public String name;
    public String description;
    public String manaCost;
    public List<String> abilityDetails;
}