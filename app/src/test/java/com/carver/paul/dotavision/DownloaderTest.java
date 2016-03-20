package com.carver.paul.dotavision;

import com.carver.paul.dotavision.Models.AdvantagesDownloader.Downloader;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class DownloaderTest {
    @Test
    public void identical(){
        List<String> list1 = Arrays.asList("a", "b", "c", "dog", "e");
        List<String> list2 = Arrays.asList("a", "b", "c", "dog", "e");
        int returnVal = Downloader.findSingleDifference(list1, list2);

        Assert.assertEquals(returnVal, Downloader.NO_DIFFERENCES_FOUND);
    }

    @Test
    public void oneDifferenceAtStart(){
        List<String> list1 = Arrays.asList("a", "b", "c", "d", "e");
        List<String> list2 = Arrays.asList("badger", "b", "c", "d", "e");
        int returnVal = Downloader.findSingleDifference(list1, list2);

        Assert.assertEquals(returnVal, 0);
    }

    @Test
    public void oneDifferenceAtEnd(){
        List<String> list1 = Arrays.asList("a", "b", "c", "d", "e");
        List<String> list2 = Arrays.asList("a", "b", "c", "d", "hat");
        int returnVal = Downloader.findSingleDifference(list1, list2);

        Assert.assertEquals(returnVal, 4);
    }

    @Test
    public void oneDifferenceInMiddle(){
        List<String> list1 = Arrays.asList("a", "b", "e", "d", "e");
        List<String> list2 = Arrays.asList("a", "b", "c", "d", "e");
        int returnVal = Downloader.findSingleDifference(list1, list2);

        Assert.assertEquals(returnVal, 2);
    }

    @Test
    public void twoDifferences(){
        List<String> list1 = Arrays.asList("a", "b", "c", "d", "e");
        List<String> list2 = Arrays.asList("3", "b", "cd", "d", "e");
        int returnVal = Downloader.findSingleDifference(list1, list2);

        Assert.assertEquals(returnVal, Downloader.MULTIPLE_DIFFERENCES_FOUND);
    }
}