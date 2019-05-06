package tw.samuel.firebaseshop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDao {
	@Query("Select * from Item order by viewCount")
	fun getItems(): List<Item>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun addItem(item:Item)
}