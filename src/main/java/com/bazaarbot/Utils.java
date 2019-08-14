//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.market.Offer;

import java.util.*;

public class Utils
{
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

    public static Comparator<Offer> sortOfferAcending = (Offer a, Offer b) -> {
        if (a.unit_price < b.unit_price)
            return -1;
         
        if (a.unit_price > b.unit_price)
            return 1;
         
        return 0;
    };

}