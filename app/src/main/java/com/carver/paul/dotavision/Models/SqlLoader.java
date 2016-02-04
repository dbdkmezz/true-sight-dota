/**
 * True Sight for Dota 2
 * Copyright (C) 2016 Paul Broadbent
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

package com.carver.paul.dotavision.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlLoader {
    private static final String TAG = "SqlLoader";

    private Context mContext;
    private List<HeroAndAdvantages> mHeroes;
    private List<String> mHeroesInPhoto;

    protected SqlLoader(Context context) {
        mContext = context;
        DataBaseHelper dbHelper = new DataBaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        mHeroes = loadHeroes(db);
        db.close();
    }

    protected List<HeroAndAdvantages> calculateAdvantages(List<String> heroesInPhoto) {
        if(listsEqual(mHeroesInPhoto, heroesInPhoto)) {
            return mHeroes;
        }

        DataBaseHelper dbHelper = new DataBaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (mHeroesInPhoto == null) {
            loadAllAdvantages(heroesInPhoto, db);
            mHeroesInPhoto = heroesInPhoto;
        } else {
            if (heroesInPhoto.size() != mHeroesInPhoto.size()) {
                throw new RuntimeException("Updating hero advantages, but number of heroes " +
                        "doesn't match currently loaded number");
            }

            for (int i = 0; i < heroesInPhoto.size(); i++) {
                if (!heroesInPhoto.get(i).equals(mHeroesInPhoto.get(i))) {
                    updateOneAdvantage(heroesInPhoto.get(i), i, db);
                    mHeroesInPhoto.set(i, heroesInPhoto.get(i));
                }
            }
        }

        db.close();

        Collections.sort(mHeroes);
        return mHeroes;
    }

    private static List<HeroAndAdvantages> loadHeroes(SQLiteDatabase db) {
        List<HeroAndAdvantages> heroes = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM Heroes", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            heroes.add(new HeroAndAdvantages(c));
            c.moveToNext();
        }
        c.close();

        return heroes;
    }

    private void loadAllAdvantages(List<String> heroesInPhoto,
                                   SQLiteDatabase db) {
        for (HeroAndAdvantages hero : mHeroes) {
            List<Double> advantages = new ArrayList<>();
            for (String enemyName : heroesInPhoto) {
                advantages.add(findAdvantage(hero, enemyName, db));
            }
            hero.setAdvantages(advantages);
        }
    }

    /**
     * Updates the advantages for the heroes only against enemyName
     * @param enemyName
     * @param enemyPosition
     * @param db
     */
    private void updateOneAdvantage(String enemyName,
                                    int enemyPosition,
                                    SQLiteDatabase db) {
        for (HeroAndAdvantages hero : mHeroes) {
            hero.setAdvantage(findAdvantage(hero, enemyName, db), enemyPosition);
        }
    }

    private static double findAdvantage(HeroAndAdvantages hero, String enemyName, SQLiteDatabase db) {
        if (enemyName.equals(hero.getName())) {
            // If the the enemy hero is the same as this one then neither has any advantage
            return 0d;
        } else {
            String sqlSafeEnemyName = enemyName.replace("'", "");
            Cursor c = db.rawQuery(
                    "SELECT advantage " +
                            "FROM Heroes, Advantages " +
                            "WHERE Heroes.name = '" + sqlSafeEnemyName + "' " +
                            "  AND Heroes._id = Advantages.enemy_id " +
                            "  AND Advantages.hero_id = " + hero.getId(), null);
            c.moveToFirst();
            double advantage = c.getDouble(c.getColumnIndexOrThrow("advantage"));
            c.close();
            return advantage;
        }
    }

    private static boolean listsEqual(final List<String> list1, final List<String> list2) {
        if(list1 == null || list2 == null) return false;
        if(list1.size() != list2.size()) return false;

        for(int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    // This method seems ~ 5% quicker, but the code is less nice, so might as well use the method
    // with a join
/*    protected void calculateAdvantagesNoJoin(Context context, List<String> heroesInPhoto) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Integer> enemyIds = new ArrayList<>();
        //Cursor c;
        for(String enemyName : heroesInPhoto) {
            String sqlSafeEnemyName = enemyName.replace("'", "");
            Cursor c = db.rawQuery(
                    "SELECT _id " +
                            "FROM Heroes " +
                            "WHERE Heroes.name = '" + sqlSafeEnemyName + "'", null);
            c.moveToFirst();
            enemyIds.add(c.getInt(c.getColumnIndexOrThrow("_id")));
            c.close();
        }

        for(HeroAndAdvantages hero : mHeroes) {
            int heroId = hero.getId();
            List<Double> advantages = new ArrayList<>();
            for(Integer enemyId : enemyIds) {
                // If the the enemy hero is the same as this one then neither has any advantage
                if(enemyId == heroId) {
                    advantages.add(0d);
                } else {
                    Cursor c = db.rawQuery(
                            "SELECT advantage " +
                                    "FROM Advantages " +
                                    "WHERE hero_id = " + heroId + " " +
                                    "  AND enemy_id = " + enemyId, null);
                    c.moveToFirst();
                    advantages.add(c.getDouble(c.getColumnIndexOrThrow("advantage")));
                    c.close();
                }
            }
            hero.setAdvantages(advantages);
        }

        Collections.sort(mHeroes);

        db.close();
    }*/
}
