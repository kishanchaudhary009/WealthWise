package com.android.fintech

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.fintech.fragments.*
import com.android.fintech.models.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    private lateinit var sipTab: ImageButton
    private lateinit var stockTab: ImageButton
    private lateinit var newsTab: ImageButton
    private lateinit var portfolioTab: ImageButton
    private lateinit var expenseTrackerTab: ImageButton
    private lateinit var headingtitle: TextView
    private lateinit var mainframe: FrameLayout

     var curruser : User? = null

    // New order: Portfolio, Expense Tracker, News, Stock, SIP
    private val tabnames = listOf(
        "Portfolio",
        "Expense Tracker",
        "AI News Recommender",
        "Stock Holding Input",
        "SIP Calculator"
    )

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var bottomTabs: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sipTab = findViewById(R.id.sipcalculatortab)
        stockTab = findViewById(R.id.stocktab)
        newsTab = findViewById(R.id.newstab)
        portfolioTab = findViewById(R.id.portfoliotab)
        expenseTrackerTab = findViewById(R.id.expernsetrackertab)
        headingtitle = findViewById(R.id.titleheading)
        mainframe = findViewById(R.id.maincontentframe)
        rootLayout = findViewById(R.id.main)
        bottomTabs = findViewById(R.id.bottomtabs)

        // Default: Portfolio
        highlightSelectedTab(portfolioTab, 0)
        headingtitle.text = tabnames[0]
        lifecycleScope.launch {
            var fbid = intent.getStringExtra("uid")
            if (fbid != null) {
                curruser  = getuserfromid(fbid)
                Toast.makeText(this@MainActivity, "${curruser.toString()}", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "curruser: ${curruser.toString()}")
                setmainframe(PortfolioFragmain())
            }
        }
        setupKeyboardListener()

        // Click Listeners matching new order
        portfolioTab.setOnClickListener {
            highlightSelectedTab(portfolioTab, 0)
            setmainframe(PortfolioFragmain())
        }
        expenseTrackerTab.setOnClickListener {
            highlightSelectedTab(expenseTrackerTab, 1)
            setmainframe(ExpenseTrackerFragMain())
        }
        newsTab.setOnClickListener {
            highlightSelectedTab(newsTab, 2)
            setmainframe(AINewsRecommenderfragmain())
        }
        stockTab.setOnClickListener {
            highlightSelectedTab(stockTab, 3)
            setmainframe(StockMonitoringFragmain())
        }
        sipTab.setOnClickListener {
            highlightSelectedTab(sipTab, 4)
            setmainframe(SipCalculatorFrag())
        }
    }

    suspend fun getuserfromid(fbid: String): User? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = "https://fintechappbackend.onrender.com/getuserbyid?id=$fbid"
            val request = Request.Builder().url(url).get().build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                Log.d("Response user", "getuserfromid: $json")
                return@withContext Gson().fromJson(json, User::class.java)
            } else {
                Log.e("Response user", "Request failed: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("Response user", "Exception: ${e.message}")
        }
        return@withContext null
    }


    private fun setupKeyboardListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            val isKeyboardOpen = keypadHeight > screenHeight * 0.15

            bottomTabs.visibility = if (isKeyboardOpen) View.GONE else View.VISIBLE
        }
    }

    private fun highlightSelectedTab(selectedTab: ImageButton, idx: Int) {
        val allTabs = listOf(portfolioTab, expenseTrackerTab, newsTab, stockTab, sipTab)
        headingtitle.text = tabnames[idx]

        for (tab in allTabs) {
            val params = tab.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = if (tab == selectedTab) 20 else 0
            params.topMargin = if (tab != selectedTab) 20 else 0
            tab.layoutParams = params
        }
    }

    private fun setmainframe(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.maincontentframe)
        if (currentFragment != null && currentFragment::class == fragment::class) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.maincontentframe, fragment)
            .commit()
    }

}
