package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.data.local.LocationBreadcrumbEntity
import com.example.data.local.WalkingSessionEntity
import com.example.data.repository.PujoWalkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class TrackerState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val distanceKm: Double = 0.0,
    val steps: Int = 0,
    val durationSeconds: Long = 0L,
    val isContinuous: Boolean = true,
    val currentSpeedKmh: Double = 0.0,
    val currentLat: Double = 22.5726,
    val currentLng: Double = 88.3639, // Kolkata coordinates
    val isGpsActive: Boolean = false,
    val isSimulationMode: Boolean = false,
    val estimatedRewardInr: Double = 0.0,
    val is10KmUnlocked: Boolean = false
)

class WalkingTrackerManager(
    private val context: Context,
    private val repository: PujoWalkRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _trackerState = MutableStateFlow(TrackerState())
    val trackerState: StateFlow<TrackerState> = _trackerState.asStateFlow()

    private var timerJob: Job? = null
    private var simulationJob: Job? = null
    private val breadcrumbs = mutableListOf<LocationBreadcrumbEntity>()
    private var lastLocation: Location? = null
    private var sessionStartTime: Long = 0L

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            handleNewLocation(location)
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    fun startWalk(useSimulation: Boolean = false) {
        if (_trackerState.value.isTracking) return

        breadcrumbs.clear()
        lastLocation = null
        sessionStartTime = System.currentTimeMillis()

        _trackerState.value = TrackerState(
            isTracking = true,
            isPaused = false,
            distanceKm = 0.0,
            steps = 0,
            durationSeconds = 0L,
            isContinuous = true,
            currentSpeedKmh = 0.0,
            isGpsActive = !useSimulation,
            isSimulationMode = useSimulation,
            estimatedRewardInr = 0.0,
            is10KmUnlocked = false
        )

        startTimer()

        if (useSimulation) {
            startSimulationTrail()
        } else {
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000L,
                    3f,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    3000L,
                    5f,
                    locationListener
                )
            } catch (e: SecurityException) {
                // Fallback to simulation if permissions not granted yet
                _trackerState.value = _trackerState.value.copy(
                    isGpsActive = false,
                    isSimulationMode = true
                )
                startSimulationTrail()
            }
        }
    }

    fun pauseWalk() {
        if (!_trackerState.value.isTracking || _trackerState.value.isPaused) return
        _trackerState.value = _trackerState.value.copy(
            isPaused = true,
            isContinuous = false // Pausing disqualifies from continuous single-session bonus!
        )
    }

    fun resumeWalk() {
        if (!_trackerState.value.isTracking || !_trackerState.value.isPaused) return
        _trackerState.value = _trackerState.value.copy(
            isPaused = false
        )
    }

    fun endWalk(onComplete: (WalkingSessionEntity) -> Unit = {}) {
        if (!_trackerState.value.isTracking) return

        try {
            locationManager?.removeUpdates(locationListener)
        } catch (_: Exception) {}

        timerJob?.cancel()
        simulationJob?.cancel()

        val state = _trackerState.value
        val session = WalkingSessionEntity(
            userId = 1L,
            startTime = sessionStartTime,
            endTime = System.currentTimeMillis(),
            distanceKm = state.distanceKm,
            steps = state.steps,
            durationSeconds = state.durationSeconds,
            isContinuous = state.isContinuous,
            status = "RECORDING"
        )

        coroutineScope.launch {
            val savedSession = repository.recordWalkingSession(session, breadcrumbs.toList())
            _trackerState.value = TrackerState() // Reset
            onComplete(savedSession)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000L)
                if (_trackerState.value.isTracking && !_trackerState.value.isPaused) {
                    val newDuration = _trackerState.value.durationSeconds + 1
                    _trackerState.value = _trackerState.value.copy(
                        durationSeconds = newDuration
                    )
                }
            }
        }
    }

    private fun handleNewLocation(location: Location) {
        if (!_trackerState.value.isTracking || _trackerState.value.isPaused) return

        val prev = lastLocation
        var addedDistKm = 0.0
        if (prev != null) {
            val deltaMeters = prev.distanceTo(location)
            if (deltaMeters in 1.0..50.0) { // filter erratic jumps
                addedDistKm = deltaMeters / 1000.0
            }
        }
        lastLocation = location

        val newDistKm = _trackerState.value.distanceKm + addedDistKm
        val speedKmh = if (location.hasSpeed()) (location.speed * 3.6) else 4.5
        val newSteps = (newDistKm * 1350).toInt()

        recordPoint(
            lat = location.latitude,
            lng = location.longitude,
            speedKmh = speedKmh,
            accuracy = location.accuracy,
            newDistKm = newDistKm,
            newSteps = newSteps
        )
    }

    private fun startSimulationTrail() {
        simulationJob?.cancel()
        simulationJob = coroutineScope.launch(Dispatchers.Default) {
            var simLat = 22.6033 // Starts near Bagbazar
            var simLng = 88.3683
            while (isActive) {
                delay(2000L)
                if (_trackerState.value.isTracking && !_trackerState.value.isPaused) {
                    // Simulate moving southwards towards Central Kolkata pandals at ~4.8 km/h
                    val deltaDist = 0.0026 // ~2.6 meters per 2 seconds (~4.7 km/h)
                    simLat -= 0.00002
                    simLng += 0.000008

                    val newDist = _trackerState.value.distanceKm + deltaDist
                    val newSteps = (newDist * 1350).toInt()

                    recordPoint(
                        lat = simLat,
                        lng = simLng,
                        speedKmh = 4.8,
                        accuracy = 4.0f,
                        newDistKm = newDist,
                        newSteps = newSteps
                    )
                }
            }
        }
    }

    /**
     * Testing fast-forward step for user / testing demonstration:
     * Allows simulating completing a specific distance (e.g. 1 km, 5 km, or 10 km continuous challenge).
     */
    fun simulateDistanceProgress(addKm: Double) {
        if (!_trackerState.value.isTracking) return
        val newDist = _trackerState.value.distanceKm + addKm
        val newSteps = (newDist * 1350).toInt()
        val newDuration = _trackerState.value.durationSeconds + (addKm * 750).toLong()

        recordPoint(
            lat = _trackerState.value.currentLat + 0.001,
            lng = _trackerState.value.currentLng + 0.001,
            speedKmh = 5.0,
            accuracy = 3.0f,
            newDistKm = newDist,
            newSteps = newSteps,
            overrideDuration = newDuration
        )
    }

    private fun recordPoint(
        lat: Double,
        lng: Double,
        speedKmh: Double,
        accuracy: Float,
        newDistKm: Double,
        newSteps: Int,
        overrideDuration: Long? = null
    ) {
        val verifiedKm = minOf(newDistKm.toInt(), 10)
        val standardReward = verifiedKm.toDouble()
        val qualifies10Km = _trackerState.value.isContinuous && newDistKm >= 10.0
        val totalEstimatedReward = standardReward + if (qualifies10Km) 5.0 else 0.0

        breadcrumbs.add(
            LocationBreadcrumbEntity(
                sessionId = 0L,
                latitude = lat,
                longitude = lng,
                timestamp = System.currentTimeMillis(),
                speedKmh = speedKmh,
                accuracyMeters = accuracy
            )
        )

        _trackerState.value = _trackerState.value.copy(
            distanceKm = newDistKm,
            steps = newSteps,
            currentSpeedKmh = speedKmh,
            currentLat = lat,
            currentLng = lng,
            durationSeconds = overrideDuration ?: _trackerState.value.durationSeconds,
            estimatedRewardInr = totalEstimatedReward,
            is10KmUnlocked = qualifies10Km
        )
    }
}
