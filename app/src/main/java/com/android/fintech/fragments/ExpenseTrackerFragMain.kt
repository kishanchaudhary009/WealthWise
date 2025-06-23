package com.android.fintech.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.fintech.R
import com.android.fintech.models.Expense
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ExpenseTrackerFragMain : Fragment() {


    private lateinit var pieChart: PieChart
    private lateinit var recyclerView: RecyclerView
    private lateinit var addExpenseButton: Button
    private lateinit var submitButton: Button
    private lateinit var etCategory: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var expandableLayout: LinearLayout

    private val expenseList = mutableListOf(
        Expense("Rent", 12000f, "Apr 4, 2024"),
        Expense("Food", 7500f, "Apr 14, 2024"),
        Expense("Travel", 6700f, "Apr 18, 2024"),
        Expense("Clubs", 4000f, "Apr 22, 2024")
    )

    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_tracker_frag_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        pieChart = view.findViewById(R.id.pieChart)
        recyclerView = view.findViewById(R.id.recyclerViewExpenses)
        addExpenseButton = view.findViewById(R.id.btnAddExpense)
        submitButton = view.findViewById(R.id.btnSubmitExpense)
        etCategory = view.findViewById(R.id.etCategory)
        etAmount = view.findViewById(R.id.etAmount)
        etDate = view.findViewById(R.id.etDate)
        expandableLayout = view.findViewById(R.id.addExpenseLayout)

        // Setup RecyclerView
        adapter = ExpenseAdapter(expenseList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Setup Pie Chart
        setupPieChart()
        updatePieChart()

        // Toggle Add Expense View
        addExpenseButton.setOnClickListener {
            expandableLayout.visibility =
                if (expandableLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Add new expense
        submitButton.setOnClickListener {
            val category = etCategory.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (category.isNotEmpty() && amountStr.isNotEmpty() && date.isNotEmpty()) {
                val amount = amountStr.toFloatOrNull()
                if (amount != null) {
                    val newExpense = Expense(category, amount, date)
                    expenseList.add(0, newExpense)
                    adapter.notifyItemInserted(0)
                    recyclerView.scrollToPosition(0)
                    updatePieChart()

                    etCategory.text.clear()
                    etAmount.text.clear()
                    etDate.text.clear()
                    expandableLayout.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "Enter valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPieChart() {
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(android.graphics.Color.TRANSPARENT)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isEnabled = true
    }

    private fun updatePieChart() {
        val categoryTotals = mutableMapOf<String, Float>()
        for (expense in expenseList) {
            categoryTotals[expense.category] = categoryTotals.getOrDefault(expense.category, 0f) + expense.amount
        }

        val entries = categoryTotals.map { PieEntry(it.value, it.key) }

        val dataSet = PieDataSet(entries, "Expense Distribution")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueTextColor(android.graphics.Color.WHITE)

        pieChart.data = data
        pieChart.invalidate() // refresh
    }

    // RecyclerView Adapter
    inner class ExpenseAdapter(private val items: MutableList<Expense>) :
        RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

        inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvCategory: TextView = view.findViewById(R.id.tvCategory)
            val tvAmount: TextView = view.findViewById(R.id.tvAmount)
            val tvDate: TextView = view.findViewById(R.id.tvDate)
            val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.expense_recycler_item, parent, false)
            return ExpenseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            val item = items[position]
            holder.tvCategory.text = item.category
            holder.tvAmount.text = "₹${item.amount}"
            holder.tvDate.text = item.date

            holder.btnDelete.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
                updatePieChart()
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
