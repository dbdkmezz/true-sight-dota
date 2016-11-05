package com.carver.paul.truesight.Models.AdvantagesDownloader;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// Crated with http://www.jsonschema2pojo.org/
public class AdvantagesDatum {

    @SerializedName("is_carry")
    @Expose
    private Boolean isCarry;
    @SerializedName("id_num")
    @Expose
    private Integer idNum;
    @SerializedName("is_roaming")
    @Expose
    private Boolean isRoaming;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("advantages")
    @Expose
    private List<Double> advantages = new ArrayList<Double>();
    @SerializedName("is_mid")
    @Expose
    private Boolean isMid;
    @SerializedName("is_jungler")
    @Expose
    private Boolean isJungler;
    @SerializedName("is_off_lane")
    @Expose
    private Boolean isOffLane;
    @SerializedName("is_support")
    @Expose
    private Boolean isSupport;

    /**
     *
     * @return
     * The isCarry
     */
    public Boolean getIsCarry() {
        return isCarry;
    }

    /**
     *
     * @param isCarry
     * The is_carry
     */
    public void setIsCarry(Boolean isCarry) {
        this.isCarry = isCarry;
    }

    /**
     *
     * @return
     * The idNum
     */
    public Integer getIdNum() {
        return idNum;
    }

    /**
     *
     * @param idNum
     * The id_num
     */
    public void setIdNum(Integer idNum) {
        this.idNum = idNum;
    }

    /**
     *
     * @return
     * The isRoaming
     */
    public Boolean getIsRoaming() {
        return isRoaming;
    }

    /**
     *
     * @param isRoaming
     * The is_roaming
     */
    public void setIsRoaming(Boolean isRoaming) {
        this.isRoaming = isRoaming;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The advantages
     */
    public List<Double> getAdvantages() {
        return advantages;
    }

    /**
     *
     * @param advantages
     * The advantages
     */
    public void setAdvantages(List<Double> advantages) {
        this.advantages = advantages;
    }

    /**
     *
     * @return
     * The isMid
     */
    public Boolean getIsMid() {
        return isMid;
    }

    /**
     *
     * @param isMid
     * The is_mid
     */
    public void setIsMid(Boolean isMid) {
        this.isMid = isMid;
    }

    /**
     *
     * @return
     * The isJungler
     */
    public Boolean getIsJungler() {
        return isJungler;
    }

    /**
     *
     * @param isJungler
     * The is_jungler
     */
    public void setIsJungler(Boolean isJungler) {
        this.isJungler = isJungler;
    }

    /**
     *
     * @return
     * The isOffLane
     */
    public Boolean getIsOffLane() {
        return isOffLane;
    }

    /**
     *
     * @param isOffLane
     * The is_off_lane
     */
    public void setIsOffLane(Boolean isOffLane) {
        this.isOffLane = isOffLane;
    }

    /**
     *
     * @return
     * The isSupport
     */
    public Boolean getIsSupport() {
        return isSupport;
    }

    /**
     *
     * @param isSupport
     * The is_support
     */
    public void setIsSupport(Boolean isSupport) {
        this.isSupport = isSupport;
    }

}