package com.appdevgenie.bakingtime.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.appdevgenie.bakingtime.constants.Constants;

public class EspressoIdlingResourceUtility {

    private static CountingIdlingResource countingIdlingResource;

    @NonNull
    public static IdlingResource getIdlingResource(){
        if(countingIdlingResource == null){
            countingIdlingResource = new CountingIdlingResource(Constants.IDLING_RESOURCE);
        }
        return countingIdlingResource;
    }

    public static void increment(){
        countingIdlingResource.increment();
    }

    public static void decrement(){
        countingIdlingResource.decrement();
    }
}
