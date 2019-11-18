package de.schnettler.tvtracker.data.show

import de.schnettler.tvtracker.data.Result
import de.schnettler.tvtracker.data.api.tmdb.TmdbApiService
import de.schnettler.tvtracker.data.db.TrendingShowsDAO
import de.schnettler.tvtracker.data.api.trakt.TraktService
import de.schnettler.tvtracker.data.api.tvdb.TvdbService
import de.schnettler.tvtracker.data.show.model.*
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.data.show.model.cast.CastListRemote
import de.schnettler.tvtracker.util.safeApiCall
import timber.log.Timber
import java.io.IOException

/**
 * Work with the Trakt API to get shows. The class knows how to construct the requests.
 */
class ShowDataSourceRemote(val trakt: TraktService, val tvdb: TvdbService, val tmdb: TmdbApiService) {
    //Show Details
    suspend fun getShowDetails(showID: Long) = safeApiCall(
        call = { requestShowDetails(showID) },
        errorMessage = "Error getting Show Details"
    )
    private suspend fun requestShowDetails(showID: Long): Result<ShowDetailsRemote> {
        val response = trakt.getShowSummary(showID)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show details: ${response.code()} ${response.message()}"))
    }


    //Cast
    suspend fun getCast(showID: Long, token: String) = safeApiCall(
        call = { requestCast(showID, token) },
        errorMessage = "Error getting Cast"
    )
    private suspend fun requestCast(showID: Long, token: String): Result<CastListRemote> {
        val response = tvdb.getActors(TvdbService.AUTH_PREFIX + token, showID)
        Timber.i("RESPONSE $response.toString()")

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting cast: ${response.code()} ${response.message()}"))
    }

    //Related Shows
    suspend fun getRelated(showID: Long) = safeApiCall(
        call = { requestRelatedShows(showID) },
        errorMessage = "Error getting Related Shows"
    )
    private suspend fun requestRelatedShows(showID: Long): Result<List<ShowRemote>> {
        val response = trakt.getRelatedShows(showID)

        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting related shows: ${response.code()} ${response.message()}"))
    }


    //Poster
    suspend fun getImages(tmdbId: String) = safeApiCall(
        call = {requestShowImages(tmdbId)},
        errorMessage = "Error loading Poster for $tmdbId"
    )

    private suspend fun requestShowImages(tmdbId: String) : Result<ShowImagesRemote> {
        val response = tmdb.getShowPoster(tmdbId, TmdbApiService.API_KEY)
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(IOException("Error getting show images: ${response.code()} ${response.message()}"))
    }
}


class ShowDataSourceLocal(val dao: TrendingShowsDAO) {
    //Show
    suspend fun updateShow(show: ShowDB) {
        dao.updateShow(show)
    }

    //Show Details
    suspend fun insertShowDetails(showDetailsDB: ShowDetailsDB) {
        dao.insertShowDetails(showDetailsDB)
    }
    fun getShowDetail(showID: Long) = dao.getShowDetails(showID)


    //ShowCast
    suspend fun insertShowCast(cast: List<CastEntry>) {
        dao.insertCast(cast)
    }
    fun getShowCast(showID: Long) = dao.getCast(showID)


    //Related Shows
    suspend fun insertRelatedShows(relatedShows: List<ShowRelationEntity>?) {
        relatedShows?.let { dao.insertShowRelations(relatedShows) }
    }
    fun getRelatedShows(showID: Long) = dao.getShowRelations(showID)

}