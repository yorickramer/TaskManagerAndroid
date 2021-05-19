package hu.bme.aut.shoppinglist.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.hataridonaplo.R

import hu.bme.aut.shoppinglist.data.ShoppingItem

class NewShoppingItemDialogFragment : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var prioritySpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var alreadyDoneCheckBox: CheckBox

    interface NewShoppingItemDialogListener {
        fun onShoppingItemCreated(newItem: ShoppingItem)
    }

    private lateinit var listener: NewShoppingItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? NewShoppingItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewShoppingItemDialogListener interface!")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                onClick(dialogInterface,i)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    fun onClick(dialogInterface: DialogInterface?, i: Int) {
        if (isValid()) {
            listener.onShoppingItemCreated(getItem())
        }
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_shopping_item, null)
        nameEditText = contentView.findViewById(R.id.ItemNameEditText)
        descriptionEditText = contentView.findViewById(R.id.ItemDescriptionEditText)
        categorySpinner = contentView.findViewById(R.id.ItemCategorySpinner)
        categorySpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )
        )
        prioritySpinner = contentView.findViewById(R.id.ItemPrioritySpinner)
        prioritySpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.priority_items)
            )
        )

        datePicker = contentView.findViewById(R.id.DatePickerDesign)

        alreadyDoneCheckBox = contentView.findViewById(R.id.ItemIsDoneCheckBox)
        return contentView
    }


    companion object {
        const val TAG = "NewShoppingItemDialogFragment"
    }


    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getPrior() : String{
        if(prioritySpinner.selectedItemPosition == 0)
            return "High Priority";
        else if(prioritySpinner.selectedItemPosition == 1)
            return "Medium Priority";
        else if(prioritySpinner.selectedItemPosition == 2)
            return "Low Priority";
        else
            return "No Priority"
    }


    fun DateMaker(): String {
        var str = datePicker.year.toString() + "-"
        if(datePicker.month < 10)
            str = str + "0" + datePicker.month.toString() + "-"
        else
            str = str + (datePicker.month + 1).toString() + "-"
        if(datePicker.dayOfMonth < 10)
            str = str + "0" + datePicker.dayOfMonth.toString()
        else
            str = str + datePicker.dayOfMonth.toString()
        return str
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getItem() = ShoppingItem(
        id = null,
        name = nameEditText.text.toString(),
        description = descriptionEditText.text.toString(),

        priority = getPrior(),
        date = DateMaker(),
        category = ShoppingItem.Category.getByOrdinal(categorySpinner.selectedItemPosition)
            ?: ShoppingItem.Category.OTHER,
        isDone = alreadyDoneCheckBox.isChecked
    )


}