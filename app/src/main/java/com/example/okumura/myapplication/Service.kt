package com.example.okumura.myapplication

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.NonNull
import com.example.okumura.myapplication.geofence.GeofenceReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class BackgroundService : Service() {

    companion object {
        fun start(@NonNull context: Context) {
            Notification.createChannel(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(getIntent(context))
            } else {
                context.startService(getIntent(context))
            }

        }

        fun stop(@NonNull context: Context) {
            context.stopService(getIntent(context))
        }

        private fun getIntent(@NonNull context: Context): Intent {
            return Intent(context, BackgroundService::class.java)
        }

        private fun getGeofence(id: String, latitude: Double, longitude: Double, radius: Float): Geofence {
            return Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(0)
                .build()
        }

        private fun getRequest(): GeofencingRequest {
            val list: ArrayList<Geofence> = arrayListOf()
            list.add(getGeofence("OPL", 35.733186, 139.715729, 100f))

            return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL or GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(list)
                .build()
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val builder = Notification.builder(applicationContext, Notification.SERVICE_CHANNEL, "test", "test")
        startForeground(100, builder.build())

        startGeofence()

        return Service.START_STICKY
    }

    private fun startGeofence() {
        val client: GeofencingClient = LocationServices.getGeofencingClient(application)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((application.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ) {
                client.addGeofences(getRequest(), getPendingIntent(applicationContext))
                    .addOnSuccessListener {
                        Notification.notify(applicationContext, "success")
                    }
                    .addOnFailureListener {
                        Notification.notify(applicationContext, "failure")
                    }
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Notification.notify(applicationContext, "complete")
                        } else {
                            Notification.notify(applicationContext, "error")
                        }
                    }
                    .addOnCanceledListener {
                        Notification.notify(applicationContext, "cancel")
                    }
            }
        } else {
            client.addGeofences(getRequest(), getPendingIntent(applicationContext))
        }
    }
}