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

public class HeroAndAdvantages implements Comparable<HeroAndAdvantages> {
    private int mId;
    private String mName;
    private boolean mIsCarry;
    private boolean mIsSupport;
    private boolean mIsMid;
    // The list of advantages this hero has over those in the photo
    private List<Double> mAdvantages;
    private double mTotalAdvantage = 0;

    private static final String ID_COLUMN = "_id";
    private static final String NAME_COLUMN = "name";
    private static final String CARRY_COLUMN = "is_support";
    private static final String SUPPORT_COLUMN = "is_carry";
    private static final String MID_COLUMN = "is_mid";

    @Override
    public int compareTo(HeroAndAdvantages other) {
        return Double.compare(this.mTotalAdvantage, other.getTotalAdvantage());
    }

    public String getName() { return mName; }

    public List<Double> getAdvantages() {
        return mAdvantages;
    }

    public Double getTotalAdvantage() {
        return mTotalAdvantage;
    }

    //TODO-now: remove debug HeroAndAdvantages constructor
    public HeroAndAdvantages(String name) {
        mName = name;

        List<Double> advs  = new ArrayList<>();
        advs.add(1.2);
        advs.add(3.2);
        advs.add(-0.2);
        advs.add(-4.0);
        advs.add(3.0);

        setAdvantages(advs);
    }

    protected HeroAndAdvantages(Cursor c) {
        mId = c.getInt(c.getColumnIndexOrThrow(ID_COLUMN));
        mName = c.getString(c.getColumnIndexOrThrow(NAME_COLUMN));
        // The SQL currently ignores ' characters, so need to put it back in
        if(mName.equals("Natures Prophet")) {
            mName = "Nature's Prophet";
        }
        mIsCarry = intToBool(c.getInt(c.getColumnIndexOrThrow(CARRY_COLUMN)));
        mIsSupport = intToBool(c.getInt(c.getColumnIndexOrThrow(SUPPORT_COLUMN)));
        mIsMid = intToBool(c.getInt(c.getColumnIndexOrThrow(MID_COLUMN)));
    }

    protected void setAdvantages(List<Double> advantages) {
        mAdvantages = advantages;

        mTotalAdvantage = 0;
        for(Double d : mAdvantages) {
            mTotalAdvantage += d;
        }
    }

    protected int getId() {
        return mId;
    }

    private static boolean intToBool(int i) {
        if(i == 0) return false;
        return true;
    }
}
