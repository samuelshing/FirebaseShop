package tw.samuel.firebaseshop.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
	private val TAG: String = "FCMService"

	override fun onNewToken(token: String?) {
		Log.d(TAG, "onNewToken: $token")
	}
}