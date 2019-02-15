package com.violenthoboenterprises.tonetracker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.DatePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private String TEST_GUITAR = "Test Guitar";

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, true);

    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityTestRule.getActivity();
    }

    @Test
    public void testAdd() {
        //check if drawer is open
        DrawerLayout mDrawerLayout = mainActivity.findViewById(R.id.drawer_layout);
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        }

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.miAdd));

        //presence of add name edit text indicates that 'Add' activity was successfully opened
        onView(withId(R.id.etAddName)).check(matches(withText("")));

        //creating a test guitar
        onView(withId(R.id.etAddName)).perform(typeText(TEST_GUITAR));
        onView(withId(R.id.imgAddElectric)).perform(click());
        onView(withId(R.id.imgAddDaily)).perform(click());

        //attempting to submit guitar
        onView(withId(R.id.btnSubmit)).perform(click());
        //should still be in 'Add' activity because strings age not set
        onView(withId(R.id.tvAddName)).check(matches(withText(TEST_GUITAR)));

        //setting string age
        onView(withId(R.id.tvAddDateChanged)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2019, 1, 1));
        onView(withId(android.R.id.button1)).perform(click());

        //submitting guitar for real this time
        onView(withId(R.id.btnSubmit)).perform(click());

        //checking that recyclerview has been updated
        onView(withRecyclerView(R.id.recyclerView)
                .atPositionOnView(0, R.id.tvName))
                .check(matches(withText(TEST_GUITAR)));
    }

    @Test
    public void testEdit() {
        //open drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.miEdit));
        //presence of edit name text view indicates that 'Edit' activity was successfully opened
        onView(withId(R.id.tvUpdateName)).check(matches(withText(TEST_GUITAR)));
        onView(withId(R.id.tvUpdateName)).check(matches(isCompletelyDisplayed()));
        //change name of guitar
        onView(withId(R.id.tvUpdateName)).perform(click());
        onView(withId(R.id.etUpdateName)).perform(typeText(" Blah"));
        onView(withId(R.id.etUpdateName)).perform(pressImeActionButton());
        onView(withId(R.id.btnUpdate)).perform(click());
        //checking that recyclerview has been updated
        onView(withRecyclerView(R.id.recyclerView)
                .atPositionOnView(0, R.id.tvName))
                .check(matches(withText(TEST_GUITAR + " Blah")));

    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {

        return new RecyclerViewMatcher(recyclerViewId);
    }

}
