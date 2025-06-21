package com.android.fintech.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.fintech.R
import com.bumptech.glide.Glide
import java.util.Calendar

class StockMonitoringFragmain : Fragment() {

    data class Company(val name: String, val ticker: String, val logoUrl: String)

    private lateinit var autoCompleteCompany: AutoCompleteTextView
    private lateinit var imageViewLogo: ImageView
    private lateinit var editTextDate: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var savebtn: Button

    // Hardcoded sample companies
    private val companies = listOf(
        Company("Apple Inc.", "AAPL", "https://logo.clearbit.com/apple.com"),
        Company("Google LLC", "GOOGL", "https://logo.clearbit.com/google.com"),
        Company("Microsoft Corp.", "MSFT", "https://logo.clearbit.com/microsoft.com"),
        Company("Tata Consultancy Services", "TCS.NS", "https://logo.clearbit.com/tcs.com"),
        Company("Infosys Ltd.", "INFY.NS", "https://logo.clearbit.com/infosys.com")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_monitoring_fragmain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        autoCompleteCompany = view.findViewById(R.id.autoCompleteCompany)
        imageViewLogo = view.findViewById(R.id.imageViewLogo)
        editTextDate = view.findViewById(R.id.editTextDate)
        editTextQuantity = view.findViewById(R.id.editTextQuantity)
        savebtn = view.findViewById(R.id.saveBtn)

        val companyNames = companies.map { it.name }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        autoCompleteCompany.setAdapter(adapter)
        
        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                    editTextDate.setText(date)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        autoCompleteCompany.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedCompany = companies.find { it.name == selectedName }

            selectedCompany?.let {
                Glide.with(this)
                    .load(it.logoUrl)

                    .into(imageViewLogo)

                imageViewLogo.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Selected: ${it.name} (${it.ticker})", Toast.LENGTH_SHORT).show()
            }
        }

        savebtn.setOnClickListener {

        }
    }
}
