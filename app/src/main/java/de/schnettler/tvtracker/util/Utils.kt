package de.schnettler.tvtracker.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.appbar.AppBarLayout
import de.schnettler.tvtracker.MainViewModel
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import kotlin.math.abs


const val TMDB_API_KEY = "***TMDB_API_KEY***"
const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val SHOW_LIST_PAGE_SIZE = 10
const val SHOW_LIST_PAGE_MAX = 5
const val TRAKT_API_BASE_URL = "https://api.trakt.tv/"
const val TRAKT_BASE_URL = "https://trakt.tv/"
const val TMD_BASE_URL = "https://api.themoviedb.org/"
const val TRAKT_CLIENT_ID = "***TRAKT_CLIENT_ID***"
const val TRAKT_REDIRECT_URI = "de.schnettler.tvtrack://auth"
const val RC_AUTH = 100

enum class ImageQuality(val quality: String) {
    LOW("w185"),
    MEDIUM("w300"),
    HIGH("w1280"),
    ORIGINAL("original")
}

enum class StatusCodes(val code: Int) {
    SUCCESS(200),
    SUCCESS_NEW_SOURCE(201)
}

enum class ShowListType() {
    TRENDING,
    POPULAR
}

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

class SwipeRefreshLayout(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var previousX = 0F
    private var declined: Boolean = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                declined = false
            }

            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - previousX)

                if (declined || xDiff > touchSlop) {
                    declined = true
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}

/**
 * Factory for constructing DevByteViewModel with parameter
 */
class ViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel(app) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}

class MaxLinesToggleClickListener(private val collapsedLines: Int) : View.OnClickListener {
    private val transition = ChangeBounds().apply {
        duration = 200
        interpolator = FastOutSlowInInterpolator()
    }

    override fun onClick(view: View) {
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
        val textView = view as TextView
        textView.maxLines = if (textView.maxLines > collapsedLines) collapsedLines else Int.MAX_VALUE
    }
}

fun clearLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }
}

fun setLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        view.systemUiVisibility = flags
    }
}

fun setStatusBarColor(resources: Resources, color: Int, window: Window, theme: Resources.Theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = resources.getColor(color, theme)
    }
}


abstract class AppBarStateChangedListener : AppBarLayout.OnOffsetChangedListener {
    private var mCurrentState = State.EXPANDED
    enum class State {EXPANDED, COLLAPSED}

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when (verticalOffset) {
            0 -> setCurrentStateAndNotify(State.EXPANDED)
            else -> setCurrentStateAndNotify(State.COLLAPSED)
        }
    }

    private fun setCurrentStateAndNotify(state: State) {
        if (mCurrentState != state) {
            onStateChanged(state)
        }
        mCurrentState = state
    }

    abstract fun onStateChanged(state: State)
}

fun isDarkTheme(res: Resources) = ((res.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)