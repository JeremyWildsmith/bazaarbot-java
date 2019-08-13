//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;

import java.util.ArrayList;
import java.util.List;

public class Economy   implements ISignalBankrupt
{
    private List<Market> _markets;
    public Economy() throws Exception {
        _markets = new ArrayList<Market>();
    }

    public void addMarket(Market m) throws Exception {
        if (!_markets.contains(m))
        {
            _markets.add(m);
        }
         
    }

    public Market getMarket(String name) throws Exception {
        for (Market m : _markets)
        {
            if (m.name.compareTo(name) == 0)
                return m;
             
        }
        return null;
    }

    public void simulate(int rounds) throws Exception {
        for (Market m : _markets)
        {
            m.simulate(rounds);
        }
    }

    public void signalBankrupt(Market m, BasicAgent a) throws Exception {
    }
}