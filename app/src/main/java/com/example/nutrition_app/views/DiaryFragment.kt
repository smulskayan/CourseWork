package com.example.nutrition_app.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrition_app.R
import com.example.nutrition_app.model.entities.FoodDiary
import com.example.nutrition_app.viewmodel.DiaryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DiaryFragment : Fragment() {

    private lateinit var viewModel: DiaryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var waterForm: LinearLayout
    private lateinit var foodForm: LinearLayout
    private lateinit var waterAmountInput: EditText
    private lateinit var foodGramsInput: EditText
    private lateinit var foodCaloriesInput: EditText
    private lateinit var foodProteinInput: EditText
    private lateinit var foodFatInput: EditText
    private lateinit var foodCarbsInput: EditText
    private lateinit var submitWaterButton: Button
    private lateinit var submitFoodButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary, container, false)

        val userId = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        viewModel = ViewModelProvider(
            this,
            DiaryViewModel.Factory(requireContext(), userId)
        ).get(DiaryViewModel::class.java)

        recyclerView = view.findViewById(R.id.diaryRecyclerView)
        addButton = view.findViewById(R.id.addEntryButton)
        waterForm = view.findViewById(R.id.waterForm)
        foodForm = view.findViewById(R.id.foodForm)
        waterAmountInput = view.findViewById(R.id.waterAmountInput)
        foodGramsInput = view.findViewById(R.id.foodGramsInput)
        foodCaloriesInput = view.findViewById(R.id.foodCaloriesInput)
        foodProteinInput = view.findViewById(R.id.foodProteinInput)
        foodFatInput = view.findViewById(R.id.foodFatInput)
        foodCarbsInput = view.findViewById(R.id.foodCarbsInput)
        submitWaterButton = view.findViewById(R.id.submitWaterButton)
        submitFoodButton = view.findViewById(R.id.submitFoodButton)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = DiaryAdapter(emptyList())

        viewModel.diaryEntries.observe(viewLifecycleOwner) { entries ->
            val groupedEntries = groupEntriesByDay(entries)
            Log.d("DiaryFragment", "Grouped into ${groupedEntries.size} days")
            recyclerView.adapter = DiaryAdapter(groupedEntries)
        }

        val cancelWaterButton = view.findViewById<Button>(R.id.cancelWaterButton)
        val cancelFoodButton = view.findViewById<Button>(R.id.cancelFoodButton)

        cancelWaterButton.setOnClickListener {
            waterForm.visibility = View.GONE
            waterAmountInput.text.clear()
        }

        cancelFoodButton.setOnClickListener {
            foodForm.visibility = View.GONE
            foodGramsInput.text.clear()
            foodCaloriesInput.text.clear()
            foodProteinInput.text.clear()
            foodFatInput.text.clear()
            foodCarbsInput.text.clear()
        }

        addButton.setOnClickListener {
            showAddMenu(it)
        }

        waterAmountInput.addTextChangedListener(numberValidator(waterAmountInput, "Введите положительное значение"))

        foodGramsInput.addTextChangedListener(numberValidator(foodGramsInput, "Введите массу > 0"))
        foodCaloriesInput.addTextChangedListener(numberValidator(foodCaloriesInput, "Введите калории > 0"))
        foodProteinInput.addTextChangedListener(numberValidator(foodProteinInput, "Введите белки ≥ 0"))
        foodFatInput.addTextChangedListener(numberValidator(foodFatInput, "Введите жиры ≥ 0"))
        foodCarbsInput.addTextChangedListener(numberValidator(foodCarbsInput, "Введите углеводы ≥ 0"))

        submitWaterButton.setOnClickListener {
            val waterMl = waterAmountInput.text.toString().toFloatOrNull()
            if (waterMl == null || waterMl <= 0f) {
                waterAmountInput.error = "Введите значение > 0"
            } else {
                viewModel.addWaterEntry(waterMl)
                waterForm.visibility = View.GONE
                waterAmountInput.text.clear()
            }
        }

        submitFoodButton.setOnClickListener {
            val grams = foodGramsInput.text.toString().toFloatOrNull()
            val calories = foodCaloriesInput.text.toString().toFloatOrNull()
            val protein = foodProteinInput.text.toString().toFloatOrNull()
            val fat = foodFatInput.text.toString().toFloatOrNull()
            val carbs = foodCarbsInput.text.toString().toFloatOrNull()

            var isValid = true

            if (grams == null || grams <= 0f) {
                foodGramsInput.error = "Введите массу > 0"
                isValid = false
            }
            if (calories == null || calories <= 0f) {
                foodCaloriesInput.error = "Введите калории > 0"
                isValid = false
            }
            if (protein == null) {
                foodProteinInput.error = "Введите количество белков"
                isValid = false
            }
            if (fat == null) {
                foodFatInput.error = "Введите количество жиров"
                isValid = false
            }
            if (carbs == null) {
                foodCarbsInput.error = "Введите количество углеводов"
                isValid = false
            }

            if (isValid) {
                viewModel.addFoodEntry(
                    grams!!, calories!!,
                    protein!!, fat!!, carbs!!
                )
                foodForm.visibility = View.GONE
                foodGramsInput.text.clear()
                foodCaloriesInput.text.clear()
                foodProteinInput.text.clear()
                foodFatInput.text.clear()
                foodCarbsInput.text.clear()
            }
        }

        return view
    }

    private fun showAddMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add("Добавить воду")
        popup.menu.add("Добавить еду")
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Добавить воду" -> {
                    waterForm.visibility = View.VISIBLE
                    foodForm.visibility = View.GONE
                }
                "Добавить еду" -> {
                    foodForm.visibility = View.VISIBLE
                    waterForm.visibility = View.GONE
                }
            }
            true
        }
        popup.show()
    }

    private fun numberValidator(view: EditText, errorMsg: String) = object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {
            val value = s.toString().toFloatOrNull()
            view.error = if (value == null || value < 0f) errorMsg else null
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun groupEntriesByDay(entries: List<FoodDiary>): List<DayEntry> {
        return entries.groupBy { it.diaryDate }
            .map { (date, entries) ->
                DayEntry(date, entries.sortedBy { it.diaryTime })
            }
            .sortedByDescending { it.date }
    }
}
