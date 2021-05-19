package hu.bme.aut.shoppinglist

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.DrawableRes
import hu.bme.aut.hataridonaplo.R
import kotlinx.android.synthetic.main.activity_item_spec.*

class ItemSpecActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_spec)
        Initialize()
    }

    @DrawableRes
    private fun getImageResource(category: String?) = when (category) {
        "OTHER" -> R.drawable.other
        "EDUCATION" -> R.drawable.education
        "WORK" -> R.drawable.work
        "GROCERIES" -> R.drawable.groceries
        "HOBBY" -> R.drawable.hobby
        else -> R.drawable.other
    }

    fun Initialize() {
        ItemNameTextView.text = intent.getStringExtra(NAME)
        ItemDescriptionTextView.text = intent.getStringExtra(DESC)
        ItemPriorityTextView.text = intent.getStringExtra(PRIOR)
        ItemDateTextView.text = intent.getStringExtra(DATE)
        ItemIconImageView.setImageResource(getImageResource(intent.getStringExtra(CAT)))
        if(intent.getBooleanExtra(ISDONE,false)){
            ItemIsDoneTextView.text = "Done, Good job"
            //ItemIsDoneTextView.setBackgroundColor(Color.GREEN)
            ItemPage.setBackgroundColor(Color.GREEN)
        }

        else{
            ItemIsDoneTextView.text = "Not Done yet, Come on mate"
            //ItemIsDoneTextView.setBackgroundColor(Color.RED)
            ItemPage.setBackgroundColor(Color.RED)
        }

    }

    companion object {
        const val NAME = "name"
        const val DESC = "desc"
        const val DATE = "date"
        const val PRIOR = "prior"
        const val CAT = "cat"
        const val ISDONE = "isdone"
    }
}