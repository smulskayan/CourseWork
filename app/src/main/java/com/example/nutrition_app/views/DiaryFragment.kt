package com.example.nutrition_app.views

import android.os.Bundle
import android.util.Log // ✅ Добавлено
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrition_app.R
import com.example.nutrition_app.model.entities.FoodDiary
import com.example.nutrition_app.viewmodel.DiaryViewModel

class DiaryFragment : Fragment() {

    private lateinit var viewModel: DiaryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
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

        val userId = 1 // 🔁 Временно жёстко задан

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

        addButton.setOnClickListener {
            showAddMenu(it)
        }

        submitWaterButton.setOnClickListener {
            val waterMl = waterAmountInput.text.toString().toFloatOrNull() ?: 0f
            if (waterMl > 0) {
                viewModel.addWaterEntry(waterMl) // ✅ userId убран
                waterForm.visibility = View.GONE
                waterAmountInput.text.clear()
            }
        }

        submitFoodButton.setOnClickListener {
            val grams = foodGramsInput.text.toString().toFloatOrNull() ?: 0f
            val calories = foodCaloriesInput.text.toString().toFloatOrNull() ?: 0f
            val protein = foodProteinInput.text.toString().toFloatOrNull() ?: 0f
            val fat = foodFatInput.text.toString().toFloatOrNull() ?: 0f
            val carbs = foodCarbsInput.text.toString().toFloatOrNull() ?: 0f
            if (grams > 0 && calories > 0f) {
                viewModel.addFoodEntry(grams, calories, protein, fat, carbs) // ✅ userId убран
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

    private fun groupEntriesByDay(entries: List<FoodDiary>): List<DayEntry> {
        return entries.groupBy { it.diaryDate }
            .map { (date, entries) ->
                DayEntry(date, entries.sortedBy { it.diaryTime })
            }
            .sortedByDescending { it.date }
    }
}
