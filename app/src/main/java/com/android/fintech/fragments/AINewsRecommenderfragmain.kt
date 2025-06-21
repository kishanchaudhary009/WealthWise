package com.android.fintech.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.fintech.FullNewsActivity
import com.android.fintech.R
import com.android.fintech.models.News
import com.android.fintech.models.Stock
import com.bumptech.glide.Glide

class AINewsRecommenderfragmain : Fragment() {

    private lateinit var tagRecyclerView: RecyclerView
    private lateinit var newsRecyclerView: RecyclerView





    private lateinit var stocks: List<Stock>
    private lateinit var allNews: List<News>
    private lateinit var newsAdapter: RecyclerView.Adapter<*>
    private lateinit var tagAdapter: RecyclerView.Adapter<*>
    private var filteredNews: List<News> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a_i_news_recommenderfragmain, container, false)

        tagRecyclerView = view.findViewById(R.id.tagRecyclerView)
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView)

        setupSampleData()
        setupTagRecycler()
        setupNewsRecycler()

        return view
    }

    private fun setupSampleData() {
        stocks = listOf(
            Stock("Apple Inc.", "AAPL", 150.0, 180.0, 15, "https://logo.clearbit.com/apple.com"),
            Stock("Google LLC", "GOOGL", 2700.0, 2900.0, 6, "https://logo.clearbit.com/google.com"),
            Stock("Microsoft Corp.", "MSFT", 280.0, 320.0, 8, "https://logo.clearbit.com/microsoft.com"),
            Stock("Tata Consultancy Services", "TCS.NS", 3200.0, 3400.0, 12, "https://logo.clearbit.com/tcs.com"),
            Stock("Infosys Ltd.", "INFY.NS", 1450.0, 1500.0, 10, "https://logo.clearbit.com/infosys.com")
        )

        allNews = listOf(
            News("Apple Hits New High", "Investors optimistic about iPhone 15 sales.Investors optimistic about iPhone 15 sales.Investors optimistic about iPhone 15 sales.Investors optimistic about iPhone 15 sales.Investors optimistic about iPhone 15 sales.Investors optimistic about iPhone 15 sales.", "Apple Inc.", "AAPL"),
            News("Google Invests in AI", "Alphabet expands its AI research unit.", "Google LLC", "GOOGL"),
            News("Microsoft Azure Growth", "Azure revenue continues to rise.", "Microsoft Corp.", "MSFT"),
            News("TCS Announces Dividend", "TCS announces interim dividend for Q2.", "Tata Consultancy Services", "TCS.NS"),
            News("Infosys Bags New Project", "Infosys to develop banking platform for UK client.", "Infosys Ltd.", "INFY.NS"),
            News("Apple Launch Event", "Tim Cook announces new hardware lineup.", "Apple Inc.", "AAPL")
        )

        filteredNews = allNews
    }
    private fun getLogoUrl(ticker: String): String {
        return stocks.find { it.ticker == ticker }?.stockimgurl ?: ""
    }

    private fun setupTagRecycler() {
        val tagList = mutableListOf("All")
        tagList.addAll(stocks.map { it.name })

        tagAdapter = object : RecyclerView.Adapter<TagViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
                return TagViewHolder(view)
            }

            override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
                val tagName = tagList[position]
                holder.tagButton.text = tagName
                holder.tagButton.setOnClickListener {
                    filteredNews = if (tagName == "All") {
                        allNews
                    } else {
                        allNews.filter { it.stockName == tagName }
                    }
                    newsAdapter.notifyDataSetChanged()
                }
            }

            override fun getItemCount(): Int = tagList.size
        }

        tagRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tagRecyclerView.adapter = tagAdapter
    }

    private fun setupNewsRecycler() {
        newsAdapter = object : RecyclerView.Adapter<NewsViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.news_recycler_item, parent, false)
                return NewsViewHolder(view)
            }

            override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
                val news = filteredNews[position]

                holder.tag.text = news.stockTickerId
                holder.title.text = news.newsTitle
                holder.description.text = news.newsDescription
                Glide.with(requireContext()).load(getLogoUrl(news.stockTickerId)).into(holder.companylogo)
                holder.companylogo
                holder.itemView.setOnClickListener {
                    val logoUrl = getLogoUrl(news.stockTickerId)

                    val intent = Intent(requireContext(), FullNewsActivity::class.java)
                    intent.putExtra("companyName", news.stockName)
                    intent.putExtra("companyLogo", logoUrl)
                    intent.putExtra("newsTitle", news.newsTitle)
                    intent.putExtra("newsContent", news.newsDescription)
                    startActivity(intent)
                }
            }

            override fun getItemCount(): Int = filteredNews.size
        }

        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsRecyclerView.adapter = newsAdapter
    }





    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagButton: TextView = itemView.findViewById(R.id.tagButton)
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tag: TextView = itemView.findViewById(R.id.newsTag)
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val companylogo : ImageView = itemView.findViewById(R.id.newsLogo)
    }
}
