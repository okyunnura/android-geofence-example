package com.example.okumura.myapplication.geofence

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.text.TextUtils
import com.example.okumura.myapplication.Notification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceJobIntentService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceJobIntentService::class.java, 101, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()) {
            Notification.notify(applicationContext, String.format("geofence error [%d]", event.errorCode))
            return
        }

        if (event.triggeringGeofences == null || event.triggeringGeofences.isEmpty()) {
            Notification.notify(applicationContext, "geofence is empty")
            return
        }

        val message = getGeofenceTransitionDetails(event.geofenceTransition, event.triggeringGeofences)
        Notification.notify(applicationContext, message)
    }

    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList: ArrayList<String> = arrayListOf()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun getTransitionString(transitionType: Int): String {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> return "GEOFENCE_TRANSITION_ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> return "GEOFENCE_TRANSITION_EXIT"
            Geofence.GEOFENCE_TRANSITION_DWELL -> return "GEOFENCE_TRANSITION_DWELL"
            else -> return "GEOFENCE_TRANSITION_UNKNOWN"
        }
    }
}