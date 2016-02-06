/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
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

package com.carver.paul.dotavision.Models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//TODO-next: add roaming role

public class HeroAndAdvantages implements Comparable<HeroAndAdvantages> {
    public static final int NEUTRAL_ADVANTAGE = 999;

    private static final String ID_COLUMN = "_id";
    private static final String NAME_COLUMN = "name";
    private static final String CARRY_COLUMN = "is_carry";
    private static final String SUPPORT_COLUMN = "is_support";
    private static final String MID_COLUMN = "is_mid";
    private static final String JUNGLER_COLUMN = "is_jungler";
    private static final String OFF_LANE_COLUMN = "is_off_lane";

    private final int mId;
    private final String mName;
    private final boolean mIsCarry;
    private final boolean mIsSupport;
    private final boolean mIsMid;
    private final boolean mIsJungler;
    private final boolean mIsOffLane;

    // The list of advantages this hero has over those in the photo
    private List<Double> mAdvantages;
    private double mTotalAdvantage = 0;

    @Override
    public int compareTo(HeroAndAdvantages other) {
        return Double.compare(other.getTotalAdvantage(), this.mTotalAdvantage);
    }

    public HeroAndAdvantages clone() {
        HeroAndAdvantages heroClone =
                new HeroAndAdvantages(mId, mName, mIsCarry, mIsSupport, mIsMid, mIsJungler, mIsOffLane);

        List<Double> advantagesClone = new ArrayList<>();
        for(double advantage : mAdvantages) {
            advantagesClone.add(advantage);
        }
        heroClone.setAdvantages(advantagesClone);

        return heroClone;
    }

    public String getName() { return mName; }

    public List<Double> getAdvantages() {
        return mAdvantages;
    }

    public Double getTotalAdvantage() {
        return mTotalAdvantage;
    }

    public boolean isCarry() {
        return mIsCarry;
    }

    public boolean isSupport() {
        return mIsSupport;
    }

    public boolean isMid() {
        return mIsMid;
    }

    public boolean isJunger() {
        return mIsJungler;
    }

    public boolean isOffLane() {
        return mIsOffLane;
    }

    protected HeroAndAdvantages(Cursor c) {
        mId = c.getInt(c.getColumnIndexOrThrow(ID_COLUMN));
        String name = c.getString(c.getColumnIndexOrThrow(NAME_COLUMN));
        // The SQL currently ignores ' characters, so need to put it back in
        if(name.equals("Natures Prophet")) {
            mName = "Nature's Prophet";
        } else {
            mName = name;
        }
        mIsCarry = intToBool(c.getInt(c.getColumnIndexOrThrow(CARRY_COLUMN)));
        mIsSupport = intToBool(c.getInt(c.getColumnIndexOrThrow(SUPPORT_COLUMN)));
        mIsMid = intToBool(c.getInt(c.getColumnIndexOrThrow(MID_COLUMN)));
        mIsJungler = intToBool(c.getInt(c.getColumnIndexOrThrow(JUNGLER_COLUMN)));
        mIsOffLane = intToBool(c.getInt(c.getColumnIndexOrThrow(OFF_LANE_COLUMN)));
    }

    protected void setAdvantages(List<Double> advantages) {
        mAdvantages = advantages;
        calculateTotalAdvantage();
    }

    protected void setAdvantage(double advantage, int position) {
        mAdvantages.set(position, advantage);
        calculateTotalAdvantage();
    }

    protected int getId() {
        return mId;
    }

    private HeroAndAdvantages(int id, String name, boolean isCarry, boolean isSupport,
                              boolean isMid, boolean isJungler, boolean isOffLane) {
        mId = id;
        mName = name;
        mIsCarry = isCarry;
        mIsSupport = isSupport;
        mIsMid = isMid;
        mIsJungler = isJungler;
        mIsOffLane = isOffLane;
    }

    private void calculateTotalAdvantage() {
        mTotalAdvantage = 0;
        for(Double d : mAdvantages) {
            if(d != NEUTRAL_ADVANTAGE) {
                mTotalAdvantage += d;
            }
        }
    }

    private static boolean intToBool(int i) {
        if(i == 0) return false;
        return true;
    }
}
