package com.appdevgenie.bakingtime;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.appdevgenie.bakingtime.activities.MainListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityFavouritesTest {

    @Rule
    public ActivityTestRule<MainListActivity> activityTestRule = new ActivityTestRule<>(MainListActivity.class);

    @Test
    public void clickMenuSelection_populateFavourites(){

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getContext());

        onView(allOf(withId(R.id.title), withText("Favourites"))).perform(click());
        onView(withId(R.id.rvMainRecipeList)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.tvIngredients)).check(matches(withText(containsString("sugar"))));
        onView(withId(R.id.rvDetailsRecipeSteps)).check(matches(hasDescendant(withText("Recipe Introduction"))));
        onView(withId(R.id.rvDetailsRecipeSteps)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.tvStepDetailNumber)).check(matches(withText("Introduction")));
        onView(withId(R.id.ibStepDetailNext)).perform(click());
        onView(withId(R.id.ibStepDetailPrevious)).perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();
        pressBack();
    }

}
