package com.carver.paul.dotavision.ImageRecognition;

import java.util.Arrays;
import java.util.List;

public class Variables {
    private static final List<Integer> l0DarkBlueRange = Arrays.asList(104, 117); //108, 117);
    private static final List<Integer> l1CyanRange = Arrays.asList(88, 95); //88);
    private static final List<Integer> l2PurpleRange = Arrays.asList(135, 170); // went up to 154 until 4 Oct. was 148 until 14 oct
    private static final List<Integer> l3YellowRange = Arrays.asList(25, 35);
    private static final List<Integer> l4OrangeRange = Arrays.asList(8, 18);

    private static final List<Integer> r0PurpleRange = Arrays.asList(140, 180); //was 150,180 until 7 oct.
    private static final List<Integer> r1YellowRange = Arrays.asList(26, 85);//70);//35);
    private static final List<Integer> r2LightBlueRange = Arrays.asList(90, 108);
    private static final List<Integer> r3GreenRange = Arrays.asList(50, 75);//68);
    private static final List<Integer> r4OrangeRange = Arrays.asList(8, 25);

    public static final List<List<Integer>> leftColoursRanges = Arrays.asList(l0DarkBlueRange, l1CyanRange, l2PurpleRange, l3YellowRange, l4OrangeRange);
    public static final List<List<Integer>> rightColoursRanges = Arrays.asList(r0PurpleRange, r1YellowRange, r2LightBlueRange, r3GreenRange, r4OrangeRange);

    public static final List<Integer> sRange = Arrays.asList(15, 255); //50, 255
    public static final List<Integer> vRange = Arrays.asList(34, 255);
}
