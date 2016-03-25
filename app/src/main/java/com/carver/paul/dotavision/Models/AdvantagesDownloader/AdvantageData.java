package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import com.carver.paul.dotavision.Models.HeroAndAdvantages;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Crated with http://www.jsonschema2pojo.org/
public class AdvantageData {

    @SerializedName("data")
    @Expose
    private List<AdvantagesDatum> data = new ArrayList<AdvantagesDatum>();

    /**
     *
     * @return
     * The data
     */
    public List<AdvantagesDatum> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<AdvantagesDatum> data) {
        this.data = data;
    }

    protected static List<HeroAndAdvantages> createFullAdvantagesList(
            AdvantageData newAdvantageData) {
        List<HeroAndAdvantages> newList = new ArrayList<>();
        for (AdvantagesDatum datum : newAdvantageData.getData()) {
            newList.add(new HeroAndAdvantages(datum));
        }
        Collections.sort(newList);
        return newList;
    }

    /**
     * Given
     * @param oldAdvantages
     * @param newAdvantageData
     * @param posOfNewAdvantage
     * @return
     */
    protected static List<HeroAndAdvantages> mergeIntoAdvantagesList(
            List<HeroAndAdvantages> oldAdvantages,
            AdvantageData newAdvantageData,
            int posOfNewAdvantage) {
        if(newAdvantageData.getData().get(0).getAdvantages().size() != 0) {
            throw new RuntimeException("ERROR, using advantages list with more than one advantage" +
                    "when just merging a single entry into a new list with mergeIntoAdvantagesList");
        }
        for(HeroAndAdvantages hero : oldAdvantages) {
            for(AdvantagesDatum newDatum : newAdvantageData.getData()) {
                if(newDatum.getName().equals("Natures Prophet")) {
                    newDatum.setName("Nature's Prophet");
                }
                if(hero.getName().equals(newDatum.getName())) {
                    hero.setAdvantage(newDatum.getAdvantages().get(0),
                            posOfNewAdvantage);
                    break;
                }
            }
        }
        Collections.sort(oldAdvantages);
        return oldAdvantages;
    }

    /**
     * Given advantage data from the server with information on the advantages each of the heroes
     * have over a single opponent this will create a new list of advantage data.
     * @param singleAdvantageData
     * @param posOfNewAdvantage - the position of the enemy in the list of five enemy heroes given
     *                          by the user
     * @return
     */
    protected static List<HeroAndAdvantages> createAdvantagesListFromSingleAdvantage(
            AdvantageData singleAdvantageData,
            int posOfNewAdvantage) {
        List<HeroAndAdvantages> newList = new ArrayList<>();
        for(AdvantagesDatum hero : singleAdvantageData.getData()) {
            List<Double> fullAdvantages = Arrays.asList(
                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                    HeroAndAdvantages.NEUTRAL_ADVANTAGE,
                    HeroAndAdvantages.NEUTRAL_ADVANTAGE);
            fullAdvantages.set(posOfNewAdvantage, hero.getAdvantages().get(0));
            hero.setAdvantages(fullAdvantages);
            newList.add(new HeroAndAdvantages(hero));
        }
        Collections.sort(newList);
        return newList;
    }
}
