//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;

import com.bazaarbot.agent.BasicAgent;
import com.bazaarbot.market.ISignalBankrupt;
import com.bazaarbot.market.Market;

import java.util.ArrayList;
import java.util.List;

public class Economy implements ISignalBankrupt {
    private List<Market> markets;

    public Economy() {
        markets = new ArrayList<>();
    }

    public void addMarket(Market m) {
        if (!markets.contains(m)) {
            markets.add(m);
        }

    }

    public Market getMarket(String name) {
        for (Market m : markets) {
            if (m.getName().compareTo(name) == 0)
                return m;

        }
        return null;
    }

    public void simulate(int rounds) {
        for (Market m : markets) {
            m.simulate(rounds);
        }
    }

    public void signalBankrupt(Market m, BasicAgent a) {
    }
}