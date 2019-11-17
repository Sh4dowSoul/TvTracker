package de.schnettler.tvtracker.ui.discover

import android.app.Application
import androidx.lifecycle.*
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.show.ShowDataSourceLocal
import de.schnettler.tvtracker.data.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.show.ShowRepository
import de.schnettler.tvtracker.util.ShowListType
import kotlinx.coroutines.launch


class DiscoverViewModel(val context: Application) : AndroidViewModel(context) {
    private val showRepository = ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        ShowDataSourceLocal(getDatabase(context).trendingShowsDao)
    )

    //Trending Shows
    var trendingShows = showRepository.getTrendingShows()
    var popularShows = showRepository.getPopularShows()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        onRefresh()
    }

     fun onRefresh() {
         _isRefreshing.value = true
         viewModelScope.launch {
             showRepository.loadNewShowListPage(page = 1, type = ShowListType.TRENDING)
             showRepository.loadNewShowListPage(page = 1, type = ShowListType.POPULAR)
         }
         _isRefreshing.value = false
    }
}
