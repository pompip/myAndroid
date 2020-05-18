package com.pompip;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testPo() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        modifyPoint(p1, p2);
        System.out.println("[" + p1.x + "," + p1.y + "],[" + p2.x + "," + p2.y + "]");
    }


    private static void modifyPoint(Point p1, Point p2) {
        Point tmpPoint = p1;
        p1 = p2;
        p2 = tmpPoint;
        p1.setLocation(5, 5);
//        p2 = new Point(5, 5);
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void setLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Test
    public void testWord(){



        String scn = "i am   a student";
        ArrayList<String> wordList = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\w+").matcher(scn);
        while (matcher.find()){
            wordList.add(matcher.group());
        };
        Collections.reverse(wordList);
        StringBuilder builder = new StringBuilder();
        for(String s:wordList){
            builder.append(s).append(" ");
        }
        System.out.println(builder.toString().trim());








        ;
    }
}

