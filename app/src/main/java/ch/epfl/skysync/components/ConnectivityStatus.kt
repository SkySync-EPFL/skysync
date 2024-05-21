package ch.epfl.skysync.components

import android.content.Context
import android.net.ConnectivityManager

class ConnectivityStatus(private val context: Context) {

  fun isOnline(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }
}
