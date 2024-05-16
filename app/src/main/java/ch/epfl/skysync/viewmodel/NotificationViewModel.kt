package ch.epfl.skysync.viewmodel

import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NotificationViewModel : ViewModel() {

    private val client = OkHttpClient()
    private val serverKey = "-jYP0tXl_jgJM9QHnvmfODxptHEFZbZAF_QASwcK0S0" // Replace with your actual Firebase server key

    fun sendPushNotification(token: String, message: String) {
        val json = JSONObject().apply {
            put("to", token)
            put("notification", JSONObject().apply {
                put("body", message)
                put("title", "Flight Confirmation")
            })
        }

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader("Authorization", "key=$serverKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                println(response.body()?.string())
            }
        })
    }

    fun sendNotificationsToUsers(tokens: List<String>, message: String) {
        tokens.forEach { token ->
            sendPushNotification(token, message)
        }
    }
}