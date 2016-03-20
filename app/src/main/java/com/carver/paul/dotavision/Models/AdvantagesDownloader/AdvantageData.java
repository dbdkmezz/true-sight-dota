package com.carver.paul.dotavision.Models.AdvantagesDownloader;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

}
