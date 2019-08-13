//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

import java.util.List;

public class MarketReport   
{
    public String strListGood = "";
    public String strListGoodPrices = "";
    public String strListGoodTrades = "";
    public String strListGoodAsks = "";
    public String strListGoodBids = "";
    public String strListAgent = "";
    public String strListAgentCount = "";
    public String strListAgentMoney = "";
    public String strListAgentProfit = "";
    private List<String> __arrStrListInventory;
    public List<String> getarrStrListInventory() {
        return __arrStrListInventory;
    }

    public void setarrStrListInventory(List<String> value) {
        __arrStrListInventory = value;
    }

}


