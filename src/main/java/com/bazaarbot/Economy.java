//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.market.ISignalBankrupt;
import com.bazaarbot.market.Market;

import java.util.ArrayList;
import java.util.List;

public class Economy   implements ISignalBankrupt
{
    private List<Market> _markets;
    public Economy() {
        _markets = new ArrayList<Market>();
    }

    public void addMarket(Market m) {
        if (!_markets.contains(m))
        {
            _markets.add(m);
        }
         
    }

    public Market getMarket(String name) {
        for (Market m : _markets)
        {
            if (m.name.compareTo(name) == 0)
                return m;
             
        }
        return null;
    }

    public void simulate(int rounds) {
        for (Market m : _markets)
        {
            m.simulate(rounds);
        }
    }

    public void signalBankrupt(Market m, BasicAgent a) {
    }
}