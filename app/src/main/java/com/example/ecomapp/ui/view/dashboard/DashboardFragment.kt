package com.example.ecomapp.ui.view.dashboard

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecomapp.R
import com.example.ecomapp.data.Revenue
import com.example.ecomapp.data.SuggestAi
import com.example.ecomapp.databinding.FragmentDashboardBinding
import com.example.ecomapp.ui.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.apply {

            barChart = binding.bcSales
            pieChart = binding.bcViewsNumber

            getSuggestByAiData(7)

            etStartDate.setOnClickListener {
                showDatePickerDialog(etStartDate)
            }

            etEndDate.setOnClickListener {
                showDatePickerDialog(etEndDate)
            }

            ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            btnGetRevenue.setOnClickListener {
                getRevenueBetweenDates(etStartDate.text.toString(), etEndDate.text.toString())
            }

        }

        return binding.root
    }

    private fun getRevenueBetweenDates(startDate: String, endDate: String) {
        val dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel.fetchRevenueBetweenDates(startDate, endDate)
        dashboardViewModel.revenue.observeForever {
            if (it != null) {
                addDataToBarChart(it)
            }
        }
    }

    private fun getSuggestByAiData(number : Int) {
        val dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel.getSuggestByAiData(number)
        dashboardViewModel.suggestAi.observeForever {
            if (it != null) {
                addDataToPieChart(it)
            }
        }
    }

    private fun addDataToPieChart(suggestList: List<SuggestAi>) {

        val viewsTable = arrayListOf<PieEntry>()

        for (s in suggestList) {
            viewsTable.add(PieEntry(s.click_after_suggest_byai.toFloat(), s.title))
        }

        val pieDataSet = PieDataSet(viewsTable, "Suggest Book Data")

        // css value
        pieDataSet.setSliceSpace(2f);
        pieDataSet.valueTextSize = 16f;
        pieDataSet.selectionShift = 10f; // shift from center if click
        pieDataSet.valueLinePart1OffsetPercentage = 70f; // line will start 80% of radius from the center of the slice
        pieDataSet.valueLinePart1Length = 0.5f; //
        pieDataSet.valueLinePart2Length = 0.3f;
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(pieDataSet)
        data.setValueTextSize(10f)
        pieChart.data = data

        // css labels
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        val description = Description()
        description.text = "Suggest Data"
        barChart.description = description

        pieChart.legend.isEnabled = false

        pieChart.invalidate()
    }

    private fun addDataToBarChart(revenueList: List<Revenue>) {

        val revenueTable = arrayListOf<BarEntry>()
        val xAxisLabels = arrayListOf<String>()
        var totalPrice = 0

        for ((index, revenue) in revenueList.withIndex()) {
            revenueTable.add(BarEntry(index.toFloat(), revenue.total_price.toFloat()))
            totalPrice += revenue.total_price.toInt()
            xAxisLabels.add(revenue.order_date.substring(5))
        }

        binding.apply {
            tvSalesNumber.text = formatPrice(totalPrice)
        }

        val barDataSet = BarDataSet(revenueTable, "Revenue Per Day")

        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        // add data to bar chart
        val data = BarData(barDataSet)
        data.setValueTextSize(12f)
        barChart.data = data

        // set x axis labels
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.xAxis.labelCount = data.entryCount

        val description = Description()
        description.text = "Revenue Data"
        barChart.description = description

        barChart.setVisibleXRangeMaximum(5f); // allow n values to be displayed at once on the x-axis, not more

        barChart.invalidate()

    }

    private fun formatPrice(price: Int): String {
        return price.toString().reversed().chunked(3).joinToString(".").reversed() + " đ"
    }

    private fun showDatePickerDialog(editText: EditText) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("vi", "VN"))
                val formattedDate = dateFormat.format(selectedDate.time)
                editText.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


}