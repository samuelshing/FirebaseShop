package tw.samuel.firebaseshop

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import tw.samuel.firebaseshop.model.Item
import tw.samuel.firebaseshop.model.WatchItem

class DetailActivity : AppCompatActivity() {
	lateinit var item: Item
	private val TAG = DetailActivity::class.java.simpleName

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_detail)
		item = intent.getParcelableExtra<Item>("ITEM")
		Log.d(TAG, "onCreate: ${item.id} / ${item.title}")
		web.settings.javaScriptEnabled = true
		web.loadUrl(item.content)

		val uid = FirebaseAuth.getInstance().currentUser?.uid
		FirebaseFirestore.getInstance().collection("users")
			.document(uid!!)
			.collection("watchItems")
			.document(item.id).get()
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					val watchItem = task.result?.toObject(WatchItem::class.java)
					if (watchItem != null) {
						watch.isChecked = true
					}
				}
			}

		watch.setOnCheckedChangeListener { buttonView, isChecked ->
			if (isChecked) {
				FirebaseFirestore.getInstance().collection("users")
					.document(uid!!)
					.collection("watchItems")
					.document(item.id)
					.set(WatchItem(item.id))
			} else {
				FirebaseFirestore.getInstance().collection("users")
					.document(uid!!)
					.collection("watchItems")
					.document(item.id)
					.delete()
			}
		}
	}

	override fun onStart() {
		super.onStart()
		item.viewCount++
		item.id.let {
			FirebaseFirestore.getInstance().collection("items").document(it).update("viewCount", item.viewCount)
		}
	}
}
