package de.schnettler.tvtracker.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeViewModel(var episode: EpisodeDomain, private var show: ShowDomain, private val episodeRepository: EpisodeRepository) : ViewModel() {

    val episodeList = episodeRepository.getEpisodes(episode.showId)

    init {
        startRefresh(episode.season, episode.number)
    }

    fun refreshDetails(position: Int) {
        val episodes = episodeList.value
        episodes?.let {
            it[position]?.let {episodeAt ->
                startRefresh(episodeAt.season, episodeAt.number)
            }

//            when (position) {
//                0 -> startRefresh(it[position + 1])//Refresh next
//                it.size - 2 -> startRefresh(it[position - 1])//Refresh previous
//                it.size - 1 -> return@let
//                else -> {
//                    startRefresh(it[position + 1])
//                    startRefresh(it[position - 1])
//                }
//            }
        }
    }

    private fun startRefresh(season: Long, number: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                episodeRepository.refreshEpisodeDetails(show.id, show.tmdbId ?: return@withContext, season, number)
            }
        }
    }
}