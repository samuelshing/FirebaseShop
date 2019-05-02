package tw.samuel.firebaseshop

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
	private val TAG = MainActivity::class.java.simpleName
	private val RC_SIGNIN: Int = 100

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
		verify_email.setOnClickListener {
			FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
					?.addOnCompleteListener { task ->
						if (task.isSuccessful) {
							Snackbar.make(it, "Verify email sent", Snackbar.LENGTH_LONG).show()
						}
					}
		}
		_hashKey()
	}

	override fun onStart() {
		super.onStart()
		FirebaseAuth.getInstance().addAuthStateListener(this)
	}

	override fun onStop() {
		super.onStop()
		FirebaseAuth.getInstance().removeAuthStateListener(this)
	}

	override fun onAuthStateChanged(auth: FirebaseAuth) {
		val user = auth.currentUser
		Log.d(TAG, "onAuthStateChanged: ${user?.uid}")
		if (user != null) {
			user_info.setText("Email: ${user.email} / ${user.isEmailVerified}")
			verify_email.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
		} else {
			user_info.setText("Not login")
			verify_email.visibility = View.GONE
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			R.id.action_signin -> {
//				startActivityForResult(Intent(this, SignInActivity::class.java), RC_SIGNIN)
				startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
					.setAvailableProviders(Arrays.asList(
						AuthUI.IdpConfig.EmailBuilder().build(),
						AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.FacebookBuilder().build(),
						AuthUI.IdpConfig.PhoneBuilder().setWhitelistedCountries(listOf("tw", "hk", "cn", "au")).setDefaultCountryIso("tw").build()
					))
					.setIsSmartLockEnabled(false)
					.setLogo(R.mipmap.ic_launcher)
					.setTheme(R.style.SignUp)
					.setAuthMethodPickerLayout(AuthMethodPickerLayout.Builder(R.layout.firebase_signin)
						.setEmailButtonId(R.id.sign_in_with_email)
						.setGoogleButtonId(R.id.sign_in_with_google)
						.setFacebookButtonId(R.id.sign_in_with_facebook)
						.setPhoneButtonId(R.id.sign_in_with_phone).build())
					.build(), RC_SIGNIN)
				true
			}
			R.id.action_signout -> {
				FirebaseAuth.getInstance().signOut()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun _hashKey() {
		try {
			val info = packageManager.getPackageInfo("tw.samuel.firebaseshop", PackageManager.GET_SIGNATURES)
			for (signature in info.signatures) {
				val md = MessageDigest.getInstance("SHA")
				md.update(signature.toByteArray())
				Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
			}
		} catch (e: NoSuchAlgorithmException) {
		}
	}
}
