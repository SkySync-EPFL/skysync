package ch.epfl.skysync.components

import android.content.Context
import android.net.ConnectivityManager

/** Interface to check the connectivity status. */
interface ConnectivityStatus {
  /**
   * Checks if the device is currently online.
   *
   * @return Boolean indicating whether the device is online.
   */
  fun isOnline(): Boolean
}

/**
 * Implementation of the ConnectivityStatus interface using the Android Context.
 *
 * @param context The Android Context.
 */
class ContextConnectivityStatus(private val context: Context) : ConnectivityStatus {

  /**
   * Checks if the device is currently online using the Android ConnectivityManager.
   *
   * @return Boolean indicating whether the device is online.
   */
  override fun isOnline(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }
}

/**
 * Dummy implementation of the ConnectivityStatus interface for testing purposes.
 *
 * @param isOnline The connectivity status to be returned by the isOnline function.
 */
class DummyConnectivityStatus(private val isOnline: Boolean) : ConnectivityStatus {

  /**
   * Returns the connectivity status provided during instantiation.
   *
   * @return Boolean indicating whether the device is online.
   */
  override fun isOnline(): Boolean = isOnline
}
