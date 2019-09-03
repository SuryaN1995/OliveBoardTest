package com.example.oliverboardtest.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type

object AndroidTestUtils {

    private val FILE_PATH_FORMAT = "%s.json"

    val currentActivity: Activity?
        @Throws(Throwable::class)
        get() {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            val activity = arrayOfNulls<Activity>(1)

            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                    Stage.RESUMED)
                activity[0] = Iterables.getOnlyElement(activities)
            }
            return activity[0]
        }

    enum class ResponseStatus(private val text: String) {
        SUCCESS("success"), FAILURE("failure"), INVALID("invalid");

        override fun toString(): String {
            return this.text
        }
    }

    /**
     *  Will fetch the response from assets/response_data folder based on responseName and responseStatus of
     * format response_data/responseName_responseStatus.json return the list of response
     */
    fun getResponseAsList(context: Context, type: Type, name: String): List<*> {
        return Gson().fromJson(getJsonResponse(context,
                name, ResponseStatus.SUCCESS), type)
    }

    /**
     * Will fetch the response from assets/response_data folder based on responseName and responseStatus of
     * format response_data/responseName_responseStatus.json
     *
     * @param context        context
     * @param responseName   will be the name of the file prefix
     * @param responseStatus will be of type [ResponseStatus]
     */
    fun getJsonResponse(context: Context, responseName: String, responseStatus: ResponseStatus): String {

        val assetManager = context.assets

        // To load text file
        val input: InputStream
        try {
            input = assetManager.open(String.format(FILE_PATH_FORMAT, responseName, responseStatus))

            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()

            // byte buffer into a string

            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }

    fun pagerIdAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with first child view of type parentMatcher")
            }

            public override fun matchesSafely(view: View): Boolean {

                if (view.parent !is ViewGroup) {
                    return parentMatcher.matches(view.parent)
                }
                val group = view.parent as ViewGroup
                return parentMatcher.matches(view.parent) && group.getChildAt(position) == view

            }
        }
    }


    fun hasChildren(numChildrenMatcher: Matcher<Int>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            /**
             * matching with viewgroup.getChildCount()
             */
            public override fun matchesSafely(view: View): Boolean {
                return view is ViewGroup && numChildrenMatcher.matches(view.childCount)
            }

            /**
             * gets the description
             */
            override fun describeTo(description: Description) {
                description.appendText(" a view with # children is ")
                numChildrenMatcher.describeTo(description)
            }
        }
    }

    fun withBackgroundColor(color: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View?): Boolean {
                return item != null && (item.background as ColorDrawable).color == color
            }

            override fun describeTo(description: Description) {
                description.appendText("with background color: ")
            }
        }
    }

    fun withBackgroundVectorDrawable(context: Context, expectedId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {

                if (expectedId < 0) {
                    return item.background == null
                }
                val expectedDrawable = ContextCompat.getDrawable(context, expectedId)
                if (item.background is VectorDrawable && expectedDrawable is VectorDrawable) {
                    val bitmap = convertToBitmap(item.background as VectorDrawable)
                    val otherBitmap = convertToBitmap(expectedDrawable)
                    return bitmap.sameAs(otherBitmap)
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("with background color: ")
            }
        }
    }

    fun withVectorDrawable(context: Context, expectedId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {
                if (item !is AppCompatImageView) {
                    return false
                }

                if (expectedId < 0) {
                    return item.drawable == null
                }
                val drawable = ContextCompat.getDrawable(context, expectedId)
                val alpha = drawable?.alpha
                return true
            }

            override fun describeTo(description: Description) {
                description.appendText("with background color: ")
            }
        }
    }

    fun convertToBitmap(vectorDrawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }



    fun hasInputType(inputType: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            internal var targetViewInputType = 0

            override fun describeTo(description: Description) {
                description.appendText("hasInputType: ").appendValue(Integer.toHexString(inputType))
                description.appendText(" actualInputType: ").appendValue(Integer.toHexString(targetViewInputType))
                description.appendText(" mask: ").appendValue(Integer.toHexString(inputType and targetViewInputType))
            }

            override fun matchesSafely(tv: TextView): Boolean {
                if (targetViewInputType == 0) {
                    targetViewInputType = tv.inputType
                }
                return targetViewInputType and inputType == inputType
            }
        }
    }

    fun hasPasswordNotVisible(): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("password is visible: ")
            }

            override fun matchesSafely(tv: TextView): Boolean {
                return tv.transformationMethod != null && tv.transformationMethod is PasswordTransformationMethod
            }
        }
    }


    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    fun chooser(matcher: Matcher<Intent>): Matcher<Intent> {
        return allOf<Intent>(hasAction(Intent.ACTION_CHOOSER), hasExtra(`is`<String>(Intent.EXTRA_INTENT), matcher))
    }

    fun withCustomConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController, view: View) {
                action.perform(uiController, view)
            }
        }
    }

    fun withTextSize(textSizeInSP: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun matchesSafely(view: View): Boolean {

                if (view !is TextView) return false

                val convertedTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        textSizeInSP.toFloat(), Resources.getSystem().displayMetrics).toInt()
                return view.textSize == convertedTextSize.toFloat()
            }

            override fun describeTo(description: Description) {
                description.appendText("Expected => " + textSizeInSP + "sp")
            }
        }
    }


    fun withTextColor(@ColorRes colorRes: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(view: View): Boolean {
                return if (view !is TextView) false else view.currentTextColor == ContextCompat.getColor(view.getContext(), colorRes)

            }

            override fun describeTo(description: Description) {
                description.appendText("Expected Color Resource Id => " + colorRes)
            }
        }
    }

}