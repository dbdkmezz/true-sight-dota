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

package com.carver.paul.truesight.Models;

import java.util.ArrayList;
import java.util.List;

public class HeroInfo {
    public String name;
    public String imageName;
    public String bioRoles;
    public String intelligence;
    public String agility;
    public String strength;
    public String attack;
    public String speed;
    public String defence;
    public List<HeroAbilityInfo> abilities;
    public List<HeroAbilityInfo.Talent> talents;

    public HeroInfo() {
        abilities = new ArrayList<>();
        talents = new ArrayList<>();
    }

    public boolean hasName(String string) {
        return (string.equals(imageName) || string.equalsIgnoreCase(name));
    }
}

