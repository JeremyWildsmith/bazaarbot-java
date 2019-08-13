//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:30 PM
//

package com.bazaarbot;

/**
 * The most fundamental agent class, and has as little implementation as possible.
 * In most cases you should start by extending Agent instead of this.
 * @author larsiusprime
 */
public class AgentData   
{
    public String className;
    public double money;
    public InventoryData inventory;
    public String logicName;
    public Logic logic;
    public Integer lookBack;

    public AgentData(String className, double money, String logicName) throws Exception {
        this.className = className;
        this.money = money;
        this.logicName = logicName;
    }

}


