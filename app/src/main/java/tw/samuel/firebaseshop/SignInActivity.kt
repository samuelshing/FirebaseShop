package tw.samuel.firebaseshop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
	private val TAG = SignInActivity::class.java.simpleName
	private val RC_GOOGLE_SIGNIN: Int = 200
	private lateinit var  googleSignInClient: GoogleSignInClient

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sign_in)

		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build()
		googleSignInClient = GoogleSignIn.getClient(this, gso)
		google_sign_in.setOnClickListener {
			startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGNIN)
		}

		signUp.setOnClickListener {
			signUp()
		}
		login.setOnClickListener {
			login()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == RC_GOOGLE_SIGNIN) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			val account = task.getResult(ApiException::class.java)
			Log.d(TAG, "onActivityResult: ${account?.id}")
			val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
			FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
				if (it.isSuccessful) {
					setResult(Activity.RESULT_OK)
					finish()
				} else {
					Log.d(TAG, "onActivityResult: ${it.exception?.message}")
					Snackbar.make(sign_in_layout, "Firebase authentication failed.", Snackbar.LENGTH_LONG).show()
				}
			}
		}
	}

	private fun login() {
		val sEmail = email.text.toString()
		val sPassword = password.text.toString()
		FirebaseAuth.getInstance()
				.signInWithEmailAndPassword(sEmail, sPassword)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						setResult(Activity.RESULT_OK)
						finish()
					} else {
						AlertDialog.Builder(this@SignInActivity)
								.setTitle("Login")
								.setMessage(task.exception?.message)
								.setPositiveButton("OK", null).show()
					}
				}
	}

	private fun signUp() {
		val sEmail = email.text.toString()
		val sPassword = password.text.toString()
		FirebaseAuth.getInstance()
				.createUserWithEmailAndPassword(sEmail, sPassword)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						AlertDialog.Builder(this@SignInActivity)
								.setTitle("Sign In")
								.setMessage("Account create")
								.setPositiveButton("OK") { dialog, which ->
									setResult(Activity.RESULT_OK)
									finish()
								}.show()
					} else {
						AlertDialog.Builder(this@SignInActivity)
								.setTitle("Sign Up")
								.setMessage(task.exception?.message)
								.setPositiveButton("OK", null).show()
					}
				}
	}
}
