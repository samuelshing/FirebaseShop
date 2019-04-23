package tw.samuel.firebaseshop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signUp.setOnClickListener {
            signUp()
        }
        login.setOnClickListener {
            login()
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
