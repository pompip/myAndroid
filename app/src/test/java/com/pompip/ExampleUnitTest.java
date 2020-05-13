package com.pompip;


import org.junit.Test;

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
}

