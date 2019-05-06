package tw.samuel.firebaseshop.view

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import tw.samuel.firebaseshop.model.Item

class FirestoreQueryLiveData : LiveData<List<Item>>(), EventListener<QuerySnapshot> {
	lateinit var registration: ListenerRegistration
	var query = FirebaseFirestore.getInstance().collection("items").orderBy("viewCount", Query.Direction.DESCENDING).limit(10)
	var isRegistered = false

	override fun onActive() {
		registration = query.addSnapshotListener(this)
		isRegistered = true
	}

	override fun onInactive() {
		super.onInactive()
		if (isRegistered) {
			registration.remove()
		}
	}

	override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
		if (querySnapshot != null && !querySnapshot.isEmpty) {
			val list = mutableListOf<Item>()
			for (doc in querySnapshot.documents) {
				val item = doc.toObject(Item::class.java) ?: Item()
				item.id = doc.id
				list.add(item)
			}
			value = list
		}
	}

	fun setCategory(categoryId: String) {
		if (isRegistered) {
			registration.remove()
			isRegistered = false
		}
		query = if (categoryId.isNotEmpty()) {
			FirebaseFirestore.getInstance()
				.collection("items")
				.whereEqualTo("category", categoryId)
				.orderBy("viewCount", Query.Direction.DESCENDING)
				.limit(10)
		} else {
			FirebaseFirestore.getInstance()
				.collection("items")
				.orderBy("viewCount", Query.Direction.DESCENDING)
				.limit(10)
		}
		registration = query.addSnapshotListener(this)
		isRegistered = true
	}
}