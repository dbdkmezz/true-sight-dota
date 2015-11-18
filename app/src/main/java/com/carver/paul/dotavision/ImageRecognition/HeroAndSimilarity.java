package com.carver.paul.dotavision.ImageRecognition;

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
