package com.stechoq.tugaskelompok3

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stechoq.tugaskelompok3.database.Note
import com.stechoq.tugaskelompok3.database.NoteDatabase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import androidx.test.platform.app.InstrumentationRegistry
import com.stechoq.tugaskelompok3.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class NoteInstrumentedTest {

    private lateinit var db: NoteDatabase

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        runBlocking {
            db.daoNote().insert(Note(
                title = "title",
                desc = "desc"
            ))
        }
        fun setUp() {
            val disableAnimation = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            if (disableAnimation) {
                InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                    "settings put global window_animation_scale 0"
                )
                InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                    "settings put global transition_animation_scale 0"
                )
                InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                    "settings put global animator_duration_scale 0"
                )
            }
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deleteNote() {
        onView(withText("title")).check(matches(isDisplayed()))
        onView(withText("title")).perform(click())
        onView(withId(R.id.deleteNoteButton)).check(matches(isDisplayed()))
        onView(withId(R.id.deleteNoteButton)).perform(click())
        onView(withText("Apakah kamu yakin ingin menghapus note ini?")).check(matches(isDisplayed()))
        onView(withId(R.id.confirmDeleteButton)).perform(click())
        onView(withText("title")).check(matches(not(isDisplayed())))
    }
}
