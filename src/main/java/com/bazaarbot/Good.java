//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;

public class Good
{
    public String id = "";
    //string id of good
    public double size = 1.0;
    //inventory size taken up
    public Good(String id_, double size_) throws Exception {
        id = id_;
        size = size_;
    }

    public Good copy() throws Exception {
        return new Good(id,size);
    }

}


