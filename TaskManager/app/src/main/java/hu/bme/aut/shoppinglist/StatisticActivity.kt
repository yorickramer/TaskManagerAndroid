package hu.bme.aut.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.hataridonaplo.R
import kotlinx.android.synthetic.main.activity_statistic.*

class StatisticActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        loadHolidays()
    }

    private fun GetDoneCnt(): Int {
        return intent.getIntExtra(StatisticActivity.HIGH,0) + intent.getIntExtra(StatisticActivity.MEDIUM,0) + intent.getIntExtra(StatisticActivity.LOW,0) + intent.getIntExtra(StatisticActivity.NO,0)
    }

    private fun loadHolidays(){
        val entries = listOf(
            PieEntry(intent.getIntExtra(StatisticActivity.HIGH,0).toFloat(), "High Priority"),
            PieEntry(intent.getIntExtra(StatisticActivity.MEDIUM,0).toFloat(), "Medium Priority"),
            PieEntry(intent.getIntExtra(StatisticActivity.LOW,0).toFloat(), "Low Priority"),
            PieEntry(intent.getIntExtra(StatisticActivity.NO,0).toFloat(), "No Priority")
        )


        val entries2 = listOf(
            PieEntry(GetDoneCnt().toFloat(), "Not Done"),
            PieEntry(intent.getIntExtra(StatisticActivity.DONE,0).toFloat(), "Done")
        )

        val dataSet = PieDataSet(entries, "")
        val dataSet2 = PieDataSet(entries2, "")


        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet2.colors = ColorTemplate.MATERIAL_COLORS.toList()


        val data = PieData(dataSet)
        val data2 = PieData(dataSet2)

        chartDone.data = data2
        chartHoliday.data = data

        chartHoliday.invalidate()
        chartDone.invalidate()
    }

    companion object {
        private const val TAG = "DetailsActivity"
        const val HIGH = "high"
        const val MEDIUM = "medium"
        const val LOW = "low"
        const val NO = "no"
        const val DONE = "done"
    }
}