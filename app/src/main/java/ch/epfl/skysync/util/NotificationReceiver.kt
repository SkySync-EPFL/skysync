package ch.epfl.skysync.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import ch.epfl.skysync.MainActivity
import ch.epfl.skysync.R
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging

class NotificationReceiver : FirebaseMessagingService() {

  // [START receive_message]
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    // Check if the message contains data payload.
    if (remoteMessage.data.isNotEmpty()) {
      // Handle data payload. (no data payload for our app right now)
    }

    // Check if the message contains notification payload.
    remoteMessage.notification?.let {
      // Handle notification payload.
      handleNotificationMessage(it)
    }
  }

  fun handleNotificationMessage(notification: RemoteMessage.Notification) {
    val title = notification.title ?: "No Title"
    val body = notification.body ?: "No Body"

    // Here you can use the title and body to display a notification to the user,
    // or perform any other action based on the notification payload.

    // For example, showing a notification using the NotificationManager:
    showNotification(title, body)
  }

  fun showNotification(title: String, body: String) {
    // Code to show a notification using NotificationManager goes here.
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Notification channel ID is required for Android Oreo and higher
    val channelId = "my_channel_id"
    val channelName = "My Channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
          NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(channel)
    }

    // Notification builder
    val notificationBuilder =
        NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // Notify
    notificationManager.notify(0, notificationBuilder.build())
  }
  // [END receive_message]

  // [START on_new_token]
  /**
   * Called if the FCM registration token is updated. This may occur if the security of the previous
   * token had been compromised. Note that this is called when the FCM registration token is
   * initially generated so this is where you would retrieve the token.
   */
  override fun onNewToken(token: String) {
    Log.d(TAG, "Refreshed token: $token")
    sendRegistrationToServer(token)
  }
  // [END on_new_token]

  private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    Log.d(TAG, "sendRegistrationTokenToServer($token)")
  }

  private fun sendNotification(messageBody: String, channelId: String) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val requestCode = 0
    val pendingIntent =
        PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

    val channelId = "fcm_default_channel"
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder =
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
          NotificationChannel(
              channelId,
              "Channel human readable title",
              NotificationManager.IMPORTANCE_DEFAULT,
          )
      notificationManager.createNotificationChannel(channel)
    }

    val notificationId = 0
    notificationManager.notify(notificationId, notificationBuilder.build())
  }

  companion object {
    private const val TAG = "MyFirebaseMsgService"
  }

  fun subscribeTopics(topic: String) {
    Firebase.messaging.subscribeToTopic(topic).addOnCompleteListener { task ->
      var msg = "Subscribed"
      if (!task.isSuccessful) {
        msg = "Subscribe failed"
      }
      Log.d(TAG, msg)
      Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    }
  }
}
