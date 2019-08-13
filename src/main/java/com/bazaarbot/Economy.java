package com.bazaarbot;

import com.bazaarbot.agent.BasicAgent;

public class Economy
{
    private List<Market> _markets = new ArrayList<Market>();

    public Economy()
    {
    }

    public void addMarket(Market m)
    {
        if (_markets.indexOf(m) == -1)
        {
            _markets.add(m);
            m.signalBankrupt.add(onBankruptcy);
        }
    }

    public Market getMarket(String name)
    {
        for (m in _markets)
        {
            if (m.name == name) return m;
        }
        return null;
    }

    public void simulate(int rounds)
    {
        for (Market m : _markets)
        {
            m.simulate(rounds);
        }
    }

    /***PRIVATE***/

    private void onBankruptcy(Market m, BasicAgent a)
    {
        //no implemenation -- provide your own in a subclass
    }

}
