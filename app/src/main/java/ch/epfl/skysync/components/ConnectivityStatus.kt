package ch.epfl.skysync.components

import android.content.Context
import android.net.ConnectivityManager

interface ConnectivityStatus {
  fun isOnline(): Boolean
}

class ContextConnectivityStatus(private val context: Context) : ConnectivityStatus {

  override fun isOnline(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }
}

class DummyConnectivityStatus(private val isOnline: Boolean) : ConnectivityStatus {
  override fun isOnline(): Boolean = isOnline
}
