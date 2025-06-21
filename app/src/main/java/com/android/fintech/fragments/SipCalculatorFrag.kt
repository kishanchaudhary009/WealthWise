package com.android.fintech.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.fintech.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class SipCalculatorFrag : Fragment() {

    private lateinit var investmentAmountInput: EditText
    private lateinit var yearsSeekbar: SeekBar
    private lateinit var yearsText: TextView
    private lateinit var returnSeekbar: SeekBar
    private lateinit var returnText: TextView
    private lateinit var stepUpSwitch: Switch
    private lateinit var stepUpLayout: LinearLayout
    private lateinit var stepUpInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultText: TextView
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sip_calculator, container, false)

        // Bind views
        investmentAmountInput = view.findViewById(R.id.investment_amount_input)
        yearsSeekbar = view.findViewById(R.id.years_seekbar)
        yearsText = view.findViewById(R.id.years_text)
        returnSeekbar = view.findViewById(R.id.return_seekbar)
        returnText = view.findViewById(R.id.return_text)
        stepUpSwitch = view.findViewById(R.id.step_up_switch)
        stepUpLayout = view.findViewById(R.id.step_up_layout)
        stepUpInput = view.findViewById(R.id.step_up_percentage_input)
        calculateButton = view.findViewById(R.id.calculate_button)
        resultText = view.findViewById(R.id.result_text)
        pieChart = view.findViewById(R.id.pie_chart)

        // SeekBar listeners
        yearsSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                yearsText.text = "Investment Duration: $progress years"
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        returnSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = 8 + progress
                returnText.text = "Expected Return Rate: $rate%"
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        stepUpSwitch.setOnCheckedChangeListener { _, isChecked ->
            stepUpLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        calculateButton.setOnClickListener {
            calculateSIP()
        }

        return view
    }

    private fun calculateSIP() {
        val monthlyAmount = investmentAmountInput.text.toString().toDoubleOrNull() ?: 0.0
        if(monthlyAmount>0.0) {
            val years = yearsSeekbar.progress
            val ratePercent = 8 + returnSeekbar.progress
            val rateMonthly = ratePercent / 12.0 / 100
            val months = years * 12

            var totalInvested = 0.0
            var maturityAmount = 0.0

            if (stepUpSwitch.isChecked) {
                val stepUpPercent = stepUpInput.text.toString().toDoubleOrNull() ?: 0.0
                val stepUpFactor = 1 + stepUpPercent / 100

                for (month in 0 until months) {
                    val yearIndex = month / 12
                    val currentMonthly =
                        monthlyAmount * Math.pow(stepUpFactor, yearIndex.toDouble())

                    maturityAmount += currentMonthly * Math.pow(
                        1 + rateMonthly,
                        (months - month).toDouble()
                    )
                    totalInvested += currentMonthly
                }
            } else {
                for (i in 0 until months) {
                    maturityAmount += monthlyAmount * Math.pow(
                        1 + rateMonthly,
                        (months - i).toDouble()
                    )
                }
                totalInvested = monthlyAmount * months
            }

            val totalInterest = maturityAmount - totalInvested

            resultText.text = """
        Total Invested: ₹%.2f
        Estimated Returns: ₹%.2f
        Maturity Value: ₹%.2f
    """.trimIndent().format(totalInvested, totalInterest, maturityAmount)

            showPieChart(totalInvested.toFloat(), totalInterest.toFloat())
        }
        else{
            Toast.makeText(requireActivity(), "Invested Amount is not entered", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showPieChart(invested: Float, interest: Float) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(invested, "% Invested"))
        entries.add(PieEntry(interest, "% Returns"))

        val colors = listOf(Color.parseColor("#FFA726"), Color.parseColor("#66BB6A"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 2f
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChart.animateY(1000)
        pieChart.visibility = View.VISIBLE
        pieChart.invalidate()
    }
}
