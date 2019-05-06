package tw.samuel.firebaseshop.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tw.samuel.firebaseshop.model.Item

@Dao
interface ItemDao {
	@Query("Select * from Item order by viewCount")
	fun getItems(): LiveData<List<Item>>

	@Query("select * from Item where category == :categoryId order by viewCount")
	fun getItemsByCategory(categoryId:String):LiveData<List<Item>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun addItem(item: Item)
}