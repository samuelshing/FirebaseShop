package tw.samuel.firebaseshop.model

data class Category(var id:String, var name:String) {
    override fun toString(): String {
        return name
    }
}