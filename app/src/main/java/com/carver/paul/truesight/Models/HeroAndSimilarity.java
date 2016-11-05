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

import com.carver.paul.truesight.ImageRecognition.LoadedHeroImage;

/**
 * HeroAndSimilarity stores a LoadedHeroImage with a similarity value. It overrides the equals function
 * so that two heroes with the same name are equal.
 */
public class HeroAndSimilarity implements Comparable<HeroAndSimilarity> {
    public Double similarity;
    public LoadedHeroImage hero;

    public HeroAndSimilarity(LoadedHeroImage hero, double similarity) {
        this.hero = hero;
        this.similarity = similarity;
    }

    @Override
    public int compareTo(HeroAndSimilarity other) {
        return this.similarity.compareTo(other.similarity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof String) {
            String string = (String) o;
            return string.equals(this.hero.name);
        }
        if (!(o instanceof HeroAndSimilarity))
            return false;
        HeroAndSimilarity hhs = (HeroAndSimilarity) o;
        return this.hero.name.equals(hhs.hero.name);
    }

    @Override
    public int hashCode() {
        return hero.name.hashCode();
    }
}
