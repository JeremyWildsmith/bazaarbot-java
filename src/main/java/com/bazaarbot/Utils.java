//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.market.Offer;

import java.util.*;

public class Utils
{
    public static Random rnd = new Random();
    public static double avgf(double a, double b) {
        return (a + b) / 2;
    }

    public static double listAvgf(List<Double> list) {
        double avg = 0;
        for (int j = 0;j < list.size();j++)
        {
            avg += list.get(j);
        }
        avg /= list.size();
        return avg;
    }

    public static double minArr(List<Double> a, int window) {
        double min = 99999999;
        //Math.POSITIVE_INFINITY;
        if (window > a.size())
            window = a.size();
         
        for (int i = 0;i < window - 1;i++)
        {
            Double f = a.get(a.size() - 1 - i);
            if (f < min)
            {
                min = f;
            }
             
        }
        return min;
    }

    public static double maxArr(List<Double> a, int window) {
        double max = -9999999;
        /**
        * Math.NEGATIVE_INFINITY;
        */
        if (window > a.size())
            window = a.size();
         
        for (int i = 0;i < window - 1;i++)
        {
            Double f = a.get(a.size() - 1 - i);
            if (f > max)
            {
                max = f;
            }
             
        }
        return max;
    }

    /**
             * Turns a number into a string with the specified number of decimal points
             * @param	num
             * @param	decimals
             * @return
             */
    public static String numStr(double num, int decimals) {
        String s = String.format("%." + decimals + "f", num);
        return s;
    }

    public static double positionInRange(double value, double min, double max) {
        return positionInRange(value, min, max, true);
    }

    public static double positionInRange(double value, double min, double max, boolean clamp) {
        value -= min;
        max -= min;
        min = 0;
        value = (value / (max - min));
        if (clamp)
        {
            if (value < 0)
            {
                value = 0;
            }
             
            if (value > 1)
            {
                value = 1;
            }
             
        }
         
        return value;
    }

    public static double randomRange(double a, double b) {
        double r = rnd.nextDouble();
        double min = a < b ? a : b;
        double max = a < b ? b : a;
        double range = max - min;
        return r * range + min;
    }


    public static List<Offer> shuffle(List<Offer> list) {
        Collections.shuffle(list);

        return list;
    }


    public static Comparator<BasicAgent> sortAgentAlpha = (BasicAgent a, BasicAgent b) -> {
        return a.getClassName().compareTo(b.getClassName());
    };

    public static int sortAgentId(BasicAgent a, BasicAgent b) {
        if (a.id < b.id)
            return -1;
         
        if (a.id > b.id)
            return 1;
         
        return 0;
    }

    public static Comparator<Offer> sortOfferAcending = (Offer a, Offer b) -> {
        if (a.unit_price < b.unit_price)
            return -1;
         
        if (a.unit_price > b.unit_price)
            return 1;
         
        return 0;
    };

    public static int sortOfferDecending(Offer a, Offer b) {
        if (a.unit_price > b.unit_price)
            return -1;
         
        if (a.unit_price < b.unit_price)
            return 1;
         
        return 0;
    }

}