package tw.samuel.firebaseshop.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import tw.samuel.firebaseshop.data.ItemRepository
import tw.samuel.firebaseshop.model.Item

class ItemViewModel(application: Application) : AndroidViewModel(application) {
	private var itemRepository: ItemRepository

	init {
		itemRepository = ItemRepository(application)
	}

	fun getItems(): LiveData<List<Item>> {
		return itemRepository.getAllItems()
	}

	fun setCategory(categoryId : String) {
		itemRepository.setCategory(categoryId)
	}
}