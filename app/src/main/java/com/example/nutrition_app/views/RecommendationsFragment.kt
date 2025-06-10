package com.example.nutrition_app.views

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nutrition_app.R
import com.example.nutrition_app.viewmodel.RecommendationsViewModel

class RecommendationsFragment : Fragment() {

    private lateinit var viewModel: RecommendationsViewModel
    private lateinit var waterConsumed: TextView
    private lateinit var waterRemaining: TextView
    private lateinit var caloriesConsumed: TextView
    private lateinit var caloriesRemaining: TextView
    private lateinit var proteinConsumed: TextView
    private lateinit var fatConsumed: TextView
    private lateinit var carbsConsumed: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var checkDayRunnable: Runnable
    private var lastCheckedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recommendations, container, false)

        // Инициализация UI
        waterConsumed = view.findViewById(R.id.water_consumed)
        waterRemaining = view.findViewById(R.id.water_remaining)
        caloriesConsumed = view.findViewById(R.id.calories_consumed)
        caloriesRemaining = view.findViewById(R.id.calories_remaining)
        proteinConsumed = view.findViewById(R.id.protein_consumed)
        fatConsumed = view.findViewById(R.id.fat_consumed)
        carbsConsumed = view.findViewById(R.id.carbs_consumed)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this, RecommendationsViewModel.Factory(requireContext())).get(RecommendationsViewModel::class.java)

        // Наблюдение за данными
        viewModel.dailySummary.observe(viewLifecycleOwner) { summary ->
            waterConsumed.text = "Выпито воды: ${summary.totalWater} мл"
            caloriesConsumed.text = "Потреблено калорий: ${summary.totalCalories}"
            proteinConsumed.text = "Белки: ${summary.totalProtein} г"
            fatConsumed.text = "Жиры: ${summary.totalFat} г"
            carbsConsumed.text = "Углеводы: ${summary.totalCarbs} г"
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { recs ->
            waterRemaining.text = "Осталось воды: ${recs.waterRemaining} мл"
            caloriesRemaining.text = "Осталось калорий: ${recs.caloriesRemaining}"
        }

        // Загрузка данных
        loadData()

        // Проверка смены дня
        checkDayRunnable = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                if (currentDate != lastCheckedDate) {
                    lastCheckedDate = currentDate
                    loadData()
                }
                handler.postDelayed(this, 60000) // Проверяем каждые 10 секунд
            }
        }
        handler.post(checkDayRunnable)

        return view
    }

    private fun loadData() {
        lastCheckedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        viewModel.loadRecommendations(userId = 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(checkDayRunnable)
    }
}