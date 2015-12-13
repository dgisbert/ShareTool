package com.booreg.sharetool.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Entity class to store data of a Tool item retrieved after a search.
 */

@ParseClassName("Tool")
public class Tool extends ParseObject implements Serializable
{
    /** Name of name field */ public static final String NAME = "name";
    /** Name of dscr field */ public static final String DSCR = "dscr";
    /** Name of posi field */ public static final String POSI = "posi";
    /** Name of owid field */ public static final String OWID = "owid";
    /** Name of pric field */ public static final String PRIC = "pric";
    /** Name of city field */ public static final String CITY = "city";

    private Number dist;

    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    public static class OrderByPricComparator implements Comparator<Tool>
    {
        private int orderFactor = 1;

        @Override
        public int compare(Tool lhs, Tool rhs)
        {
            return new CompareToBuilder().append(lhs.getPric().doubleValue(), rhs.getPric().doubleValue())
                                         .append(lhs.getDist().doubleValue(), rhs.getDist().doubleValue())
                                         .append(lhs.getName(), rhs.getName())
                                         .append(lhs.getObjectId(), rhs.getObjectId())
                                         .toComparison() * orderFactor;
        }

        public OrderByPricComparator() {}

        public OrderByPricComparator(boolean descending)
        {
            if (descending) orderFactor = -1;
        }
    }

    public static class OrderByDistComparator implements Comparator<Tool>
    {
        private int orderFactor = 1;

        @Override
        public int compare(Tool lhs, Tool rhs)
        {
            return new CompareToBuilder().append(lhs.getDist().doubleValue(), rhs.getDist().doubleValue())
                                         .append(lhs.getPric().doubleValue(), rhs.getPric().doubleValue())
                                         .append(lhs.getName(), rhs.getName())
                                         .append(lhs.getObjectId(), rhs.getObjectId())
                                         .toComparison() * orderFactor;
        }

        public OrderByDistComparator() {}
        public OrderByDistComparator(boolean descending)
        {
            if (descending) orderFactor = -1;
        }
    }

    //*****************************************************************************************************************
    // Getters
    //*****************************************************************************************************************

    /** Gets field id   of this object */ public String        getId()   { return getObjectId();   }
    /** Gets field name of this object */ public String        getName() { return getString(NAME); }
    /** Gets field dscr of this object */ public String        getDscr() { return getString(DSCR); }
    /** Gets field lngi of this object */ public ParseGeoPoint getPosi() { return getParseGeoPoint(POSI); }
    /** Gets field owid of this object */ public ParseUser     getOwid() { return getParseUser(OWID); }
    /** Gets field pric of this object */ public Number        getPric() { return getNumber(PRIC); }
    /** Gets field city of this object */ public String        getCity() { return getString(CITY); }

    /** Gets field dist of this object */ public Number        getDist() { return dist; }

    //*****************************************************************************************************************
    // Setters
    //*****************************************************************************************************************

    /** Sets field dist of this object */ public void setDist(Number dist) { this.dist = dist; }
}
