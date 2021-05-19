package hu.bme.aut.shoppinglist.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.hataridonaplo.R
import hu.bme.aut.shoppinglist.MainActivity
import hu.bme.aut.shoppinglist.data.ShoppingItem
import java.time.LocalDateTime

class ShoppingAdapter(private val listener: MainActivity, private val listenerr: OnCitySelectedListener?) :
    RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {

    private var items = mutableListOf<ShoppingItem>()
    private var Allitems = mutableListOf<ShoppingItem>()
    private var futureTasks = false

    interface OnCitySelectedListener {
        fun onCitySelected(city: ShoppingItem?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingViewHolder(itemView)
    }



    @DrawableRes
    private fun getImageResource(category: ShoppingItem.Category) = when (category) {
        ShoppingItem.Category.OTHER -> R.drawable.other
        ShoppingItem.Category.EDUCATION -> R.drawable.education
        ShoppingItem.Category.WORK -> R.drawable.work
        ShoppingItem.Category.GROCERIES -> R.drawable.groceries
        ShoppingItem.Category.HOBBY -> R.drawable.hobby
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addItem(item: ShoppingItem) {
        items.add(item)
        Allitems.add(item)
        orderItems()
        filterCategory("All")
        notifyItemInserted(items.size - 1)
    }

    fun deleteAll() {
        items.clear()
        Allitems.clear()
        notifyDataSetChanged()
    }

    fun RemoveItem(item: ShoppingItem) {
        items.remove(item)
        Allitems.remove(item)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(shoppingItems: List<ShoppingItem>) {
        items.clear()
        Allitems.clear()
        items.addAll(shoppingItems)
        Allitems.addAll(shoppingItems)
        orderItems()
        filterCategory("All")
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun GetStatData(): Array<Int> {

        var high = 0
        var medium = 0
        var low = 0
        var no = 0
        var done = 0

        for(i in Allitems){
            if(DateMaker() <= i.date){
                if(i.isDone)
                    done = done + 1
                else{
                    when(i.priority){
                        "High Priority" -> high++
                        "Medium Priority" -> medium++
                        "Low Priority" -> low++
                        "No Priority" -> no++
                    }
                }
            }
        }
        return arrayOf(high, medium, low, no, done)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterCategory(cat: String){
        items.clear()
        for(i in Allitems){
            if((i.category.name.equals(cat) || cat == "All") && futureTasks && i.date < DateMaker())
                items.add(i)
            else if((i.category.name.equals(cat) || cat == "All") && !futureTasks && i.date >= DateMaker())
                items.add(i)
        }
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DateMaker(): String {
        var str = LocalDateTime.now().year.toString() + "-"
        if(LocalDateTime.now().monthValue < 10)
            str = str + "0" + LocalDateTime.now().monthValue.toString() + "-"
        else
            str = str + LocalDateTime.now().monthValue.toString() + "-"
        if(LocalDateTime.now().dayOfMonth < 10)
            str = str + "0" + LocalDateTime.now().dayOfMonth.toString()
        else
            str = str + LocalDateTime.now().dayOfMonth.toString()
        Log.d("TAG", str)
        return str
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changeFuture(){
        futureTasks = !futureTasks
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun orderItems(){
        items.sortByDescending { it.date }
        Allitems.sortByDescending { it.date }
        items.reverse()
        Allitems.reverse()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        if(item.description.length > 15)
            holder.descriptionTextView.text = item.description.substring(0,15) + "..."
        else
            holder.descriptionTextView.text = item.description
        holder.priorityTextView.text = item.priority
        holder.dateTextView.text = item.date
        holder.iconImageView.setImageResource(getImageResource(item.category))
        holder.isBoughtCheckBox.isChecked = item.isDone

        holder.item = item

        /*
        //val oneItemgec: LinearLayout
        //oneItemgec = (R.layout.item_shopping_list).findViewById(R.id.ShoppingItemIconImageView)

        if(item.priority == "High Priority")
            oneItemgec.setBackgroundColor(Color.RED)
        else if(item.priority == "Medium Priority")
            oneItemgec.setBackgroundColor(Color.YELLOW)
        else if(item.priority == "Low Priority")
            oneItemgec.setBackgroundColor(Color.GREEN)
        else
            oneItemgec.setBackgroundColor(Color.WHITE)
*/
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ShoppingItemClickListener {
        fun onItemChanged(item: ShoppingItem)
        fun deleteItem(item: ShoppingItem)
    }


    inner class ShoppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconImageView: ImageView
        val nameTextView: TextView
        val descriptionTextView: TextView
        val priorityTextView : TextView
        val dateTextView : TextView
        val isBoughtCheckBox: CheckBox
        val removeButton: ImageButton



        var item: ShoppingItem? = null

        init {
            iconImageView = itemView.findViewById(R.id.ShoppingItemIconImageView)
            nameTextView = itemView.findViewById(R.id.ShoppingItemNameTextView)
            descriptionTextView = itemView.findViewById(R.id.ShoppingItemDescriptionTextView)
            priorityTextView = itemView.findViewById(R.id.ShoppingItemPriorityTextView)
            dateTextView = itemView.findViewById(R.id.ShoppingItemDateTextView)


            isBoughtCheckBox = itemView.findViewById(R.id.ShoppingItemIsDoneCheckBox)
            removeButton = itemView.findViewById(R.id.ShoppingItemRemoveButton)
            removeButton.setOnClickListener(){
                    item?.let { it1 -> RemoveItem(it1)  }
                item?.let { it1 -> listener.deleteItem(it1) }
            }
            isBoughtCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                item?.let {
                    val newItem = it.copy(
                        isDone = isChecked
                    )
                    Allitems.remove(item!!)
                    items.remove(item!!)
                    item = newItem
                    Log.d("Tag","Csekkolva")
                    Allitems.add(newItem)
                    items.add(newItem)
                    listener.onItemChanged(newItem)
                }
            })

            itemView.setOnClickListener{
                itemView.setOnClickListener { listenerr?.onCitySelected(item) }
            }
        }
    }
}