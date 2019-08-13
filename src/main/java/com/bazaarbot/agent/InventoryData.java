package com.bazaarbot.agent;

import java.util.Map;

public class InventoryData
{
    public float maxSize;
    public Map<String, Float> ideal;
    public Map<String, Float> start;
    public Map<String, Float> size;

    public InventoryData(float maxSize, Map<String,Float> ideal, Map<String,Float> start, Map<String,Float> size)
    {
        this.maxSize = maxSize;
        this.ideal = ideal;
        this.start = start;
        this.size = size;
    }
}
