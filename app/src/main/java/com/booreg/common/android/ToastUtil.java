package com.booreg.common.android;

import android.content.Context;
import android.widget.Toast;

/**
 * Offers convenience methods to use Toast messages easily.
 */

public class ToastUtil
{
    /**
     * Shows a long toast with the specified message given the id of the resource
     */

    public static void showLongToast(Context context, int resId)
    {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a long toast with the specified message given the id of the resource and it's parameters.
     */

    public static void showLongToast(Context context, int resId, Object...parameters)
    {
        Toast.makeText(context, context.getString(resId, parameters), Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a short toast with the specified message given the id of the resource.
     */

    public static void showShortToast(Context context, int resId)
    {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a short toast with the specified message given the id of the resource and it's parameters.
     */

    public static void showShortToast(Context context, int resId, Object...parameters)
    {
        Toast.makeText(context, context.getString(resId, parameters), Toast.LENGTH_SHORT).show();
    }
}
