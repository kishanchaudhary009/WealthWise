package com.android.fintech.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.fintech.R
import com.android.fintech.models.Stock
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class PortfolioFragmain : Fragment() {



    // Temporary Adapter
    class StockAdapter(private val stockList: List<Stock>) :
        RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

        class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val stockName: TextView = itemView.findViewById(R.id.stockName)
            val stockQuantity: TextView = itemView.findViewById(R.id.stockQuantity)
            val stockBuyPrice: TextView = itemView.findViewById(R.id.stockBuyPrice)
            val stockCurrentPrice: TextView = itemView.findViewById(R.id.stockCurrentPrice)
            val stockTotalValue: TextView = itemView.findViewById(R.id.stockTotalValue)
            val stockcompanyimg : ImageView = itemView.findViewById(R.id.companyLogo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_item, parent, false)
            return StockViewHolder(view)
        }

        override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
            val stock = stockList[position]
            holder.stockName.text = "${stock.name} (${stock.ticker})"
            holder.stockQuantity.text = "Quantity: ${stock.quantity}"
            holder.stockBuyPrice.text = "Buy Price: $${stock.buyPrice}"
            holder.stockCurrentPrice.text = "Current Price: $${stock.currentPrice}"
            holder.stockTotalValue.text = "Total Value: $${"%.2f".format(stock.totalValue)}"
            Glide.with(holder.itemView.context)
                .load(stock.stockimgurl)
                .into(holder.stockcompanyimg)
        }

        override fun getItemCount(): Int = stockList.size
    }

    lateinit var pieChart : PieChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_portfolio_fragmain, container, false)
        pieChart = view.findViewById(R.id.pieChart)
        displaypiechart(1000,2500)
        // Dummy stock list
        val dummyStocks = listOf(
            Stock("Apple Inc.", "AAPL", 150.0, 180.0, 15, "https://logo.clearbit.com/apple.com"),
            Stock("Google LLC", "GOOGL", 2700.0, 2900.0, 6, "https://logo.clearbit.com/google.com"),
            Stock("Microsoft Corp.", "MSFT", 280.0, 320.0, 8, "https://logo.clearbit.com/microsoft.com"),
            Stock("Tata Consultancy Services", "TCS.NS", 3200.0, 3400.0, 12, "https://logo.clearbit.com/tcs.com"),
            Stock("Infosys Ltd.", "INFY.NS", 1450.0, 1500.0, 10, "https://logo.clearbit.com/infosys.com"),

        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerStocks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = StockAdapter(dummyStocks)

        return view
    }
    fun displaypiechart(totalinvested : Int,currentvalue : Int){
        var totalprofit = currentvalue-totalinvested
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry((totalinvested).toFloat(), "% Invested"))
        entries.add(PieEntry(totalprofit.toFloat(), "% Returns"))

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
