package tw.samuel.firebaseshop

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
	private val TAG = MainActivity::class.java.simpleName
	private val RC_SIGNIN: Int = 100
	var categories = mutableListOf<Category>()
	lateinit var adapter: ItemAdapter
	lateinit var itemViewModel: ItemViewModel


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
		}
		verify_email.setOnClickListener {
			FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					Snackbar.make(it, "Verify email sent", Snackbar.LENGTH_LONG).show()
				}
			}
		}

		FirebaseFirestore.getInstance().collection("categories").get().addOnCompleteListener { task ->
			if (task.isSuccessful) {
				task.result?.let {
					categories.add(Category("", "不分類"))
					for (doc in it) {
						categories.add(Category(doc.id, doc.data.get("name").toString()))
					}
					spinner.adapter = ArrayAdapter<Category>(this@MainActivity, android.R.layout.simple_spinner_item, categories).apply {
						setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
					}
					spinner.setSelection(0, false)
					spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
						override fun onNothingSelected(parent: AdapterView<*>?) {
							TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
						}

						override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
							TODO()
						}
					}
				}
			}
		}

		recycler.setHasFixedSize(true)
		recycler.layoutManager = LinearLayoutManager(this)
		adapter = ItemAdapter(mutableListOf())
		recycler.adapter = adapter
		itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
		itemViewModel.getItems().observe(this, androidx.lifecycle.Observer {
			Log.d(TAG, "observe: ${it.size}")
			adapter.items = it
			adapter.notifyDataSetChanged()
		})

		_hashKey()
	}

	inner class ItemAdapter(var items: List<Item>) : RecyclerView.Adapter<ItemHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
			return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))
		}

		override fun getItemCount(): Int {
			return items.size
		}

		override fun onBindViewHolder(holder: ItemHolder, position: Int) {
			holder.bindTo(items[position])
			holder.itemView.setOnClickListener {
				itemClicked(items[position], position)
			}
		}
	}

	private fun itemClicked(item: Item, position: Int) {
		Log.d(TAG, "itemClicked: ${item.title} $position")
		val intent = Intent(this, DetailActivity::class.java)
		intent.putExtra("ITEM", item)
		startActivity(intent)
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
			user_info.text = "Email: ${user.email} / ${user.isEmailVerified}"
//			verify_email.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
		} else {
			user_info.text = "Not login"
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
				startActivityForResult(
					AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
						Arrays.asList(
							AuthUI.IdpConfig.EmailBuilder().build(),
							AuthUI.IdpConfig.GoogleBuilder().build(),
							AuthUI.IdpConfig.FacebookBuilder().build(),
							AuthUI.IdpConfig.PhoneBuilder().setWhitelistedCountries(
								listOf(
									"tw",
									"hk",
									"cn",
									"au"
								)
							).setDefaultCountryIso("tw").build()
						)
					)
						.setIsSmartLockEnabled(false)
						.setLogo(R.mipmap.ic_launcher)
						.setTheme(R.style.SignUp)
						.setAuthMethodPickerLayout(
							AuthMethodPickerLayout.Builder(R.layout.firebase_signin)
								.setEmailButtonId(R.id.sign_in_with_email)
								.setGoogleButtonId(R.id.sign_in_with_google)
								.setFacebookButtonId(R.id.sign_in_with_facebook)
								.setPhoneButtonId(R.id.sign_in_with_phone).build()
						)
						.build(), RC_SIGNIN
				)
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
