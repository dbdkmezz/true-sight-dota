package com.carver.paul.dotavision.ImageRecognition;

/**
 * Created by paul on 25/10/15.
 */

//TODO-beauty: rename HeroHistAndSimilarity
public class HeroHistAndSimilarity implements Comparable<HeroHistAndSimilarity> {
    public Double similarity;
    public HeroWithHist hero;

    public HeroHistAndSimilarity(HeroWithHist hero, double similarity) {
        this.hero = hero;
        this.similarity = similarity;
    }

    public HeroHistAndSimilarity(String name) {
        this.hero = new HeroWithHist(name);
    }

    @Override
    public int compareTo(HeroHistAndSimilarity other) {
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
        if (!(o instanceof HeroHistAndSimilarity))
            return false;
        HeroHistAndSimilarity hhs = (HeroHistAndSimilarity) o;
        return this.hero.name.equals(hhs.hero.name);
    }

    @Override
    public int hashCode() {
        return hero.name.hashCode();
    }
}
