package de.schnettler.tvtracker.ui.discover

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.ShowSmallBindingModel_
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.header
import de.schnettler.tvtracker.util.withModelsFrom
import timber.log.Timber

class DiscoverController(
    var trendingShows: List<ShowDomain>? = null,
    var popularShows: List<ShowDomain>? = null,
    var anticipatedShows: List<ShowDomain>? = null,
    var recommendedShows: List<ShowDomain>? = null): EpoxyController(){

    var callbacks: Callbacks? = null
    interface Callbacks {
        fun onItemClicked(view: View, item: ShowDomain)
    }

    override fun buildModels() {
        header {
            id("header_trending")
            title("Trending now")
            showExpand(true)
        }

        trendingShows?.let {
            carousel {
                id("trending")
                withModelsFrom(trendingShows!!) {
                    ShowSmallBindingModel_()
                        .id("trending_$it.id")
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("trending_${it.id}")
                        .onClickListener{ _, _, view, _ ->
                            Timber.i("Clicked on ${it.title}")
                            callbacks?.onItemClicked(view, it)
                        }
                }
            }
        }

        if (!recommendedShows.isNullOrEmpty()) {
            header {
                id("header_recommended")
                title("For you")
                showExpand(true)
            }
        }
        recommendedShows?.let {
            carousel {
                id("recommended")
                withModelsFrom(it) {
                    ShowSmallBindingModel_()
                        .id(it.id)
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("recommended_${it.id}")
                        .onClickListener{ _, _, view, _ ->
                            Timber.i("Clicked on ${it.title}")
                            callbacks?.onItemClicked(view, it)
                        }
                }
            }
        }

        header {
            id("header_popular")
            title("Popular")
            showExpand(true)
        }

        popularShows?.let {
            carousel {
                id("popular")
                withModelsFrom(popularShows!!) {
                    ShowSmallBindingModel_()
                        .id(it.id)
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("popular_${it.id}")
                        .onClickListener{ _, _, view, _ ->
                            Timber.i("Clicked on ${it.title}")
                            callbacks?.onItemClicked(view, it)
                        }
                }
            }
        }


        header {
            id("header_anticipated")
            title("Most Anticipated")
            showExpand(true)
        }

        anticipatedShows?.let {
            carousel {
                id("anticipated")
                withModelsFrom(it) {
                    ShowSmallBindingModel_()
                        .id(it.id)
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("anticipated_${it.id}")
                        .onClickListener{ _, _, view, _ ->
                            Timber.i("Clicked on ${it.title}")
                            callbacks?.onItemClicked(view, it)
                        }
                }
            }
        }
    }
}