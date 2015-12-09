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

package com.carver.paul.dotavision;

import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//TODO-someday: Decide if I just want to load the XML for the heroes I've taken the photo of.
// That may be faster.

//TODO-someday: Look into using a database (sqlite?) for hero info instead of an XML file

public class LoadHeroXml {

    static final String sNullString = null;

    private static final String TAG = "LoadHeroXml";

    private LoadHeroXml() {}

    public static void Load(XmlResourceParser parser, List<HeroInfo> heroInfoList) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Starting XML Load.");

        if(heroInfoList == null)
            throw new RuntimeException("Trying to load XML, but the list to store it in hasn't " +
                    "been initialised");

        if(!heroInfoList.isEmpty())
            heroInfoList.clear();

        try {
            parser.next();
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, sNullString, "listOfHeroInfo");
            while (parser.next() != XmlPullParser.END_TAG) {
                parser.require(XmlPullParser.START_TAG, sNullString, "heroInfo");
                heroInfoList.add(LoadIndividualHeroInfo(parser));
                parser.require(XmlPullParser.END_TAG, sNullString, "heroInfo");
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "Loaded " + heroInfoList.size() + " heroes from XML.");
    }

    static private HeroInfo LoadIndividualHeroInfo(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNullString, "heroInfo");
        HeroInfo hero = new HeroInfo();

        while (parser.next() != XmlPullParser.END_TAG) {
            //String name = parser.getName();
            if (parser.getName().equals("name")) {
                hero.name = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "name");
            } else if (parser.getName().equals("imageName")) {
                hero.imageName = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "imageName");
            } else if (parser.getName().equals("bioRoles")) {
                hero.bioRoles = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "bioRoles");
            } else if (parser.getName().equals("intelligence")) {
                hero.intelligence = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "intelligence");
            } else if (parser.getName().equals("agility")) {
                hero.agility = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "agility");
            } else if (parser.getName().equals("strength")) {
                hero.strength = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "strength");
            } else if (parser.getName().equals("attack")) {
                hero.attack = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "attack");
            } else if (parser.getName().equals("speed")) {
                hero.speed = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "speed");
            } else if (parser.getName().equals("defence")) {
                hero.defence = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "defence");
            } else if (parser.getName().equals("abilities")) {
                hero.abilities.add(LoadHeroAbilities(parser));
                parser.require(XmlPullParser.END_TAG, sNullString, "abilities");
            } else {
                throw new RuntimeException("Loading XML Error, in LoadIndividualHeroInfo. Name:" + parser.getName());
            }
        }

        for(HeroAbility ability : hero.abilities)
            ability.heroName = hero.name;

        parser.require(XmlPullParser.END_TAG, sNullString, "heroInfo");

        return hero;
    }


    static private HeroAbility LoadHeroAbilities(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNullString, "abilities");
        HeroAbility ability = new HeroAbility();


        while (parser.next() != XmlPullParser.END_TAG) {
            //String name = parser.getName();
            if (parser.getName().equals("isStun")) {
                ability.isStun = readBoolean(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "isStun");
            } else if (parser.getName().equals("isDisable")) {
                ability.isDisable = readBoolean(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "isDisable");
            } else if (parser.getName().equals("isUltimate")) {
                ability.isUltimate = readBoolean(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "isUltimate");
            } else if (parser.getName().equals("isSilence")) {
                ability.isSilence = readBoolean(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "isSilence");
            } else if (parser.getName().equals("isMute")) {
                ability.isMute = readBoolean(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "isMute");
            } else if (parser.getName().equals("name")) {
                ability.name = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "name");
            } else if (parser.getName().equals("imageName")) {
                ability.imageName = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "imageName");
            } else if (parser.getName().equals("description")) {
                ability.description = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "description");
            } else if (parser.getName().equals("manaCost")) {
                ability.manaCost = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "manaCost");
            } else if (parser.getName().equals("cooldown")) {
                ability.cooldown = readText(parser);
                parser.require(XmlPullParser.END_TAG, sNullString, "cooldown");
            } else if (parser.getName().equals("abilityDetails")) {
                ability.abilityDetails.add(readText(parser));
                parser.require(XmlPullParser.END_TAG, sNullString, "abilityDetails");
            } else {
                throw new RuntimeException("Loading XML Error, in LoadHeroAbilities. Name:" + parser.getName());
            }
        }

        parser.require(XmlPullParser.END_TAG, sNullString, "abilities");
        return ability;
    }

    private static boolean readBoolean(XmlPullParser parser) throws IOException, XmlPullParserException {
        boolean result = false;
        if (parser.next() == XmlPullParser.TEXT) {
            if (parser.getText().equals("true")) {
                result = true;
            }
            parser.nextTag();
        } else {
            throw new RuntimeException("Loading XML Error, in realBoolean. Text not found!");
        }
        return result;
    }

    // Extracts text values. and goes to tag following the text
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        } else {
            throw new RuntimeException("Loading XML Error, in readText. Text not found!");
        }
        return result;
    }
}
