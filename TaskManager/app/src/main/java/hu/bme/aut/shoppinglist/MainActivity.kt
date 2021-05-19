package hu.bme.aut.shoppinglist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.hataridonaplo.R
import hu.bme.aut.shoppinglist.adapter.ShoppingAdapter
import hu.bme.aut.shoppinglist.data.ShoppingItem
import hu.bme.aut.shoppinglist.data.ShoppingListDatabase
import hu.bme.aut.shoppinglist.fragments.NewShoppingItemDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ShoppingAdapter.ShoppingItemClickListener, NewShoppingItemDialogFragment.NewShoppingItemDialogListener, ShoppingAdapter.OnCitySelectedListener {

    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: ShoppingAdapter
    private lateinit var database: ShoppingListDatabase
    private var futureTasks = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        fab.setOnClickListener{
            NewShoppingItemDialogFragment().show(
                supportFragmentManager,
                NewShoppingItemDialogFragment.TAG
            )

        }

        fab2.setOnClickListener{
            futureTasks = !futureTasks
            if(!futureTasks){
                fab.hide()
                fab3.hide()
                adapter.changeFuture()
                adapter.filterCategory("All")
                fab2.setImageResource(R.drawable.newtasks)


            }


            else{
                fab.show()
                fab3.show()
                adapter.changeFuture()
                adapter.filterCategory("All")
                fab2.setImageResource(R.drawable.oldtasks)

            }

        }

        fab3.setOnClickListener{

            val array = adapter.GetStatData()
            val showStatisticIntent = Intent()
            showStatisticIntent.setClass(this, StatisticActivity::class.java)
            showStatisticIntent.putExtra(StatisticActivity.HIGH, array[0])
            showStatisticIntent.putExtra(StatisticActivity.MEDIUM, array[1])
            showStatisticIntent.putExtra(StatisticActivity.LOW, array[2])
            showStatisticIntent.putExtra(StatisticActivity.NO, array[3])
            showStatisticIntent.putExtra(StatisticActivity.DONE, array[4])
            startActivity(showStatisticIntent)

        }

        database = Room.databaseBuilder(
                applicationContext,
                ShoppingListDatabase::class.java,
                "shopping-list"
        ).build()
        initRecyclerView()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onShoppingItemCreated(newItem: ShoppingItem) {
        thread {
            val newId = database.shoppingItemDao().insert(newItem)
            val newShoppingItem = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newShoppingItem)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = ShoppingAdapter(this, this)

        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadItemsInBackground() {
        thread {
            val items = database.shoppingItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: ShoppingItem) {
        thread {
            database.shoppingItemDao().update(item)
            recyclerView.invalidate()
            Log.d("MainActivity", "ShoppingItem update was successful")
        }
    }


    override fun deleteItem(item: ShoppingItem) {
        thread {
            database.shoppingItemDao().deleteItem(item)
            recyclerView.invalidate()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.delete_all -> {
                adapter.deleteAll()
                true
            }
            R.id.cat_All -> {
                adapter.filterCategory("All")
                true
            }
            R.id.cat_Edu -> {
                adapter.filterCategory("EDUCATION")
                true
            }
            R.id.cat_Gro -> {
                adapter.filterCategory("GROCERIES")
                true
            }
            R.id.cat_Hobby -> {
                adapter.filterCategory("HOBBY")
                true
            }
            R.id.cat_Other -> {
                adapter.filterCategory("OTHER")
                true
            }
            R.id.cat_Work -> {
                adapter.filterCategory("WORK")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCitySelected(item: ShoppingItem?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this, ItemSpecActivity::class.java)
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.NAME, item.name)
        }
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.DESC, item.description)
        }
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.DATE, item.date)
        }
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.PRIOR, item.priority)
        }
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.CAT, item.category.name)
        }
        if (item != null) {
            showDetailsIntent.putExtra(ItemSpecActivity.ISDONE, item.isDone)
        }
        startActivity(showDetailsIntent)
    }
}