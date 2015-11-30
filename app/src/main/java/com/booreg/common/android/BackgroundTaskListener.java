package com.booreg.common.android;

/**
 * Interface for all background tasks being done
 */

public interface BackgroundTaskListener<E>
{
    /** Called when the background task is finished without errors */ public void onFinish(E result);
}
