package com.carver.paul.dotavision;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadHeroXml {

    static final String ns = null;

    public static List<HeroInfo> Load(XmlResourceParser parser) {
        List<HeroInfo> heroInfoList = new ArrayList<>();


        try {
            parser.next();
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "listOfHeroInfo");
            // while(parser.nextTag() == XmlPullParser.START_TAG) {
            parser.nextTag();
            HeroInfo hero = LoadIndividualHeroInfo(parser);
            heroInfoList.add(hero);
            // }

/*
            parser.require(XmlPullParser.START_TAG, ns, "heroInfo");
            parser.nextTag();
            int a = parser.getAttributeCount();
            parser.require(XmlPullParser.START_TAG, ns, "name");
            hero.name = parser.nextText();
            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, ns, "name");
*/

/*            boolean keepGoing = true;
            while (keepGoing == true) {
                parser.next();

                switch (parser.getEventType()) {
                    case XmlPullParser.END_TAG:
                        System.out.println("END_TAG: " + parser.getName());
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        System.out.println("END_DOCUMENT.");
                        keepGoing = false;
                        break;
                    case XmlPullParser.START_TAG:
                        System.out.println("START_TAG: " + parser.getName());
                        break;
                    case XmlPullParser.TEXT:
                        System.out.println("TEXT: " + parser.getText());
                        break;
                    default:
                        System.out.println("unknown tag: " + parser.getEventType());
                        break;
                }*/
        } catch (XmlPullParserException e) {
            System.err.println("XmlPullParserException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return heroInfoList;
    }

    static private HeroInfo LoadIndividualHeroInfo(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "heroInfo");
        HeroInfo hero = new HeroInfo();

        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (parser.getName().equals("name")) {
                hero.name = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "name");
            } else if (parser.getName().equals("bioRoles")) {
                hero.bioRoles = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "bioRoles");
            } else if (parser.getName().equals("intelligence")) {
                hero.intelligence = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "intelligence");
            } else if (parser.getName().equals("agility")) {
                hero.agility = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "agility");
            } else if (parser.getName().equals("strength")) {
                hero.strength = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "strength");
            } else if (parser.getName().equals("attack")) {
                hero.attack = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "attack");
            } else if (parser.getName().equals("speed")) {
                hero.speed = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "speed");
            } else if (parser.getName().equals("defence")) {
                hero.defence = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "defence");
            } else if (parser.getName().equals("abilities")) {
                int b = 6;
                break;
            }
        }


        return hero;
    }


/*
String ns = null;
parser.require(XmlPullParser.START_TAG, ns, "listOfHeroInfo");*/


    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        String ns = null;

        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
