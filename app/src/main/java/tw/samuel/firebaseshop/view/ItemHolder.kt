package tw.samuel.firebaseshop.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_row.view.*
import tw.samuel.firebaseshop.R
import tw.samuel.firebaseshop.model.Item

class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var titleText = view.item_title
    var priceText = view.item_price
    var image = view.item_image
    var countText = view.item_count

    fun bindTo(item: Item) {
        titleText.text = item.title
        priceText.text = item.price.toString()
        Glide.with(itemView.context).load(item.imageUrl).apply(RequestOptions().override(200)).into(image)
        countText.text = item.viewCount.toString()
        countText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.eye, 0, 0, 0)
    }
}