package com.example.localnotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar

// minimum sdk to make channels needs to be 26
class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private var channelID = "come.example.MAD-155-Local_Notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create notify service
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val areNotificationsEnabled: Boolean = areNotificationsEnabled(this,channelID)


        val notifyBtn = findViewById<Button>(R.id.btnNotify)
        notifyBtn.setOnClickListener {
            // send notification
            if (areNotificationsEnabled(this,channelID)){
                val snackbar = Snackbar.make(it, "You need to enable App Notifications", Snackbar.LENGTH_LONG)
                snackbar.setAction("Open Settings", View.OnClickListener {
                    val intent = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName,null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                })
                snackbar.show()
            }else{
                sendNotification("Example Notification", "This is an example notification")
            }

        }

        // create default channel
        createNotificationChannel(
            channelID,
            "Local Notify Default")

    }

    fun sendNotification(title: String, content: String){
        val notificationID = 101
        val icon: Icon = Icon.createWithResource(this, android.R.drawable.ic_dialog_info)
        // where we want to go when notification is clicked
        val resultIntent = Intent(this, ResultActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val action: Notification.Action = Notification.Action.Builder(icon, "Open", pendingIntent).build()
        val notification = Notification.Builder(this@MainActivity, channelID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .setColor(Color.GREEN)
            .setContentIntent(pendingIntent)
            .setActions(action)
            .setNumber(notificationID)
            .build()

        notificationManager.notify(notificationID,notification)
    }

    // creating a default channel
    fun createNotificationChannel(id: String, name: String){
        // set how important the notification is
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        // create the channel, needs an id, a name and an importance value
        val channel = NotificationChannel(id,name,importance).apply{
            enableLights(true)
            lightColor = Color.RED
            // the channel will allow vibration
            enableVibration(true)
            // sets the vibration pattern using an array, short, longer, short (in milliseconds)
            vibrationPattern = longArrayOf(100, 200, 100)

        }
        // creates the notification channel with the stuff we applied when making channel val
        notificationManager?.createNotificationChannel(channel)
    }

    fun areNotificationsEnabled(context: Context, channelID: String?): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelID)) {
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelID)
                return channel.importance == NotificationManager.IMPORTANCE_NONE
            }
            false
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

}