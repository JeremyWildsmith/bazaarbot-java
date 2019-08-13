package com.bazaarbot;

public class Good
{
    public String id = "";		//string id of good
    public float size = 1.0f;	//inventory size taken up

    public Good(String id_,float size_)
    {
        id = id_;
        size = size_;
    }

    public Good copy()
    {
        return new Good(id, size);
    }
}