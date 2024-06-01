package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.Report
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * ViewModel for the user for the finished flights
 *
 * @param repository The app repository
 * @param userId The user id
 * @return The finished flights view model
 */
class FinishedFlightsViewModel(val repository: Repository, val userId: String) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String,
    ): FinishedFlightsViewModel {
      return viewModel<FinishedFlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FinishedFlightsViewModel(repository, userId) as T
                }
              })
    }
  }

  private val _currentFlights: MutableStateFlow<List<FinishedFlight>?> = MutableStateFlow(null)
  private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
  private val isLoading = MutableStateFlow(false)
  private val _flightReports: MutableStateFlow<List<Report>?> = MutableStateFlow(null)
  private val _flightReportsUsers: MutableStateFlow<List<User>?> = MutableStateFlow(null)
  private val _searchResults: MutableStateFlow<List<LocationPoint>?> = MutableStateFlow(null)

  val currentFlights = _currentFlights.asStateFlow()
  val currentUser = _currentUser.asStateFlow()
  val flightReports = _flightReports.asStateFlow()
  val flightReportsUsers = _flightReportsUsers.asStateFlow()
  val searchResults = _searchResults.asStateFlow()

  /** Refreshes the view model values */
  fun refresh() {
    refreshUserAndFlights()
  }

  /** Refreshes the user logged in */
  private fun refreshUser() =
      viewModelScope.launch {
        _currentUser.value = repository.userTable.get(userId, onError = { onError(it) })
      }

  /** Refreshes the finished flights */
  private fun refreshFlights() =
      viewModelScope.launch {
        lateinit var fetchedFlights: List<FinishedFlight>
        if (_currentUser.value is Admin) {
          fetchedFlights =
              repository.flightTable
                  .getAll(onError = { onError(it) })
                  .filterIsInstance<FinishedFlight>()
        } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
          fetchedFlights =
              repository.userTable
                  .retrieveAssignedFlights(
                      repository.flightTable, userId, onError = { onError(it) })
                  .filterIsInstance<FinishedFlight>()
        }
        _currentFlights.value = fetchedFlights.map { it.updateFlightStatus(_currentUser.value!!) }
      }

  /** Refreshes the user logged in and its finished flights */
  fun refreshUserAndFlights() =
      viewModelScope.launch {
        isLoading.value = true
        refreshUser().join()
        refreshFlights().join()
        isLoading.value = false
      }

  /**
   * Add a flight to the database
   *
   * @param flight The flight to add
   */
  fun addFlight(flight: FinishedFlight) =
      viewModelScope.launch {
        repository.flightTable.add(flight, onError = { onError(it) })
        refreshUserAndFlights()
      }

  /**
   * Get a flight from the database
   *
   * @param flightId The flight id
   * @return The finished flight
   */
  fun getFlight(flightId: String): StateFlow<FinishedFlight?> {
    return _currentFlights
        .map { flights -> flights?.find { it.id == flightId } }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = null)
  }

  /**
   * Add a report which is linked to a flight to the database
   *
   * @param report The report to add
   * @param flightId The flight id
   */
  fun addReport(report: Report, flightId: String) =
      viewModelScope.launch {
        repository.reportTable.add(report, flightId, onError = { onError(it) })
      }

  /**
   * Get all reports of a given flight from the database
   *
   * @param flightId The flight id
   */
  fun getAllReports(flightId: String) =
      viewModelScope.launch {
        _flightReports.value =
            repository.reportTable.retrieveReports(flightId, onError = { onError(it) })
        _flightReportsUsers.value =
            _flightReports.value!!.map { report ->
              repository.userTable.get(report.author, onError = { onError(it) })!!
            }
      }

  /**
   * Search for the location of a given query with openstreetmap propose at most 4 of the most
   * probable results
   *
   * @param query The query to search
   * @return The search results
   */
  private suspend fun searchLocation(query: String): String? {
    var result: String? = null
    viewModelScope
        .launch(Dispatchers.IO) {
          val client = OkHttpClient()
          val url = "https://nominatim.openstreetmap.org/search?q=$query&format=json&limit=4"
          val request = Request.Builder().url(url).build()
          try {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            result = responseBody
          } catch (e: Exception) {
            onError(e)
          }
        }
        .join()
    return result
  }

  /**
   * Get the search location of a given query
   *
   * @param query The query to search
   * @param time The time of measure of the location to replace
   */
  fun getSearchLocation(query: String, time: Int) =
      viewModelScope.launch {
        val response = searchLocation(query)
        val gson = Gson()
        if (response == null) {
          _searchResults.value = null
        } else if (response != "") {
          val jsonArray = gson.fromJson(response, JsonArray::class.java)
          _searchResults.value =
              jsonArray.map { jsonString ->
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val displayName = jsonObject.get("display_name")?.asString ?: ""
                val latitude = jsonObject.get("lat")?.asDouble ?: 0.0
                val longitude = jsonObject.get("lon")?.asDouble ?: 0.0
                LocationPoint(time, latitude, longitude, displayName)
              }
        } else {
          _searchResults.value = emptyList()
        }
      }

  /**
   * Get the reports list with all the reports the user can see according to its role
   *
   * @param reportIds The list of reports
   * @param isAdmin The user is an admin
   * @return The list of reports
   */
  fun reportList(reportIds: List<Report>?, isAdmin: Boolean): List<Report>? {
    return if (isAdmin) reportIds else reportIds!!.filter { (it.author == userId) }
  }

  /**
   * Callback executed when an error occurs on database-related operations
   *
   * @param e The exception that occurred
   */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }
}
