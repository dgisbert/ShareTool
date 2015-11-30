package com.booreg.sharetool.model;

import com.booreg.sharetool.App;
import com.booreg.sharetool.R;

/**
 * Represents the different kinds of order that user can search tool data. Every value has a string text associated.
 */

public enum ToolSearchOrder
{
      /** By price ascending     */ BY_PRIC_DESC (R.string.ENM00001)
    , /** By price descending    */ BY_DIST_DESC (R.string.ENM00002)
    , /** By distance ascending  */ BY_PRIC_ASC  (R.string.ENM00003)
    , /** By distance descending */ BY_DIST_ASC  (R.string.ENM00004)
    ;

    private int resId;

    @Override public String toString() { return App.getContext().getString(resId); }

    ToolSearchOrder(int resId) { this.resId = resId; }
}
