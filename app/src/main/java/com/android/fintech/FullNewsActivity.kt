package com.android.fintech

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FullNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_news)

        val companyLogo: ImageView = findViewById(R.id.companyLogo)
        val companyName: TextView = findViewById(R.id.companyName)
        val newsTitle: TextView = findViewById(R.id.newsTitle)
        val newsContent: TextView = findViewById(R.id.newsContent)
        val btnClose: ImageView = findViewById(R.id.btnClose)

        // Get data from intent
        val name = intent.getStringExtra("companyName") ?: ""
        val logoUrl = intent.getStringExtra("companyLogo") ?: ""
        val title = intent.getStringExtra("newsTitle") ?: ""
        val content = intent.getStringExtra("newsContent") ?: ""

        companyName.text = name
        newsTitle.text = title
        newsContent.text = content

        Glide.with(this).load(logoUrl).into(companyLogo)

        btnClose.setOnClickListener {
            finish()
        }
    }
}
