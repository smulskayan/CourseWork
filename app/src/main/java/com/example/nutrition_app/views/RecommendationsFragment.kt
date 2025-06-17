package com.example.nutrition_app.views

import android.content.Context
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
    private lateinit var proteinTargetTextView: TextView
    private lateinit var fatTargetTextView: TextView
    private lateinit var carbsTargetTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var proteinRemainingTextView: TextView
    private lateinit var fatRemainingTextView: TextView
    private lateinit var carbsRemainingTextView: TextView
    private lateinit var goalMessageTextView: TextView
    private lateinit var checkDayRunnable: Runnable
    private var lastCheckedDate: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recommendations, container, false)
        val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        waterConsumed = view.findViewById(R.id.water_consumed)
        waterRemaining = view.findViewById(R.id.water_remaining)
        caloriesConsumed = view.findViewById(R.id.calories_consumed)
        caloriesRemaining = view.findViewById(R.id.calories_remaining)
        proteinTargetTextView = view.findViewById(R.id.protein_target)
        fatTargetTextView = view.findViewById(R.id.fat_target)
        carbsTargetTextView = view.findViewById(R.id.carbs_target)
        proteinRemainingTextView = view.findViewById(R.id.protein_remaining)
        fatRemainingTextView = view.findViewById(R.id.fat_remaining)
        carbsRemainingTextView = view.findViewById(R.id.carbs_remaining)
        goalMessageTextView = view.findViewById(R.id.goal_message)
        viewModel = ViewModelProvider(this, RecommendationsViewModel.Factory(requireContext())).get(RecommendationsViewModel::class.java)

        viewModel.dailySummary.observe(viewLifecycleOwner) { summary ->
            waterConsumed.text = "${summary.totalWater} мл"
            caloriesConsumed.text = "${summary.totalCalories}"
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { recs ->
            waterConsumed.text = "${recs.targetWater.toInt()} мл"
            caloriesConsumed.text = "${recs.targetCalories.toInt()}"

            waterRemaining.text = "${recs.waterRemaining.toInt()} мл"
            caloriesRemaining.text = "${recs.caloriesRemaining.toInt()}"

            proteinTargetTextView.text = "${recs.proteinTarget.toInt()} г"
            fatTargetTextView.text = "${recs.fatTarget.toInt()} г"
            carbsTargetTextView.text = "${recs.carbsTarget.toInt()} г"

            val summary = viewModel.dailySummary.value ?: return@observe

            val proteinRemaining = (recs.proteinTarget - summary.totalProtein).coerceAtLeast(0f)
            val fatRemaining = (recs.fatTarget - summary.totalFat).coerceAtLeast(0f)
            val carbsRemaining = (recs.carbsTarget - summary.totalCarbs).coerceAtLeast(0f)

            proteinRemainingTextView.text = "${proteinRemaining.toInt()} г"
            fatRemainingTextView.text = "${fatRemaining.toInt()} г"
            carbsRemainingTextView.text = "${carbsRemaining.toInt()} г"

            val reached = mutableListOf<String>()
            if (recs.waterRemaining <= 0) reached.add("воде")
            if (recs.caloriesRemaining <= 0) reached.add("калориям")
            if (proteinRemaining <= 0) reached.add("белкам")
            if (fatRemaining <= 0) reached.add("жирам")
            if (carbsRemaining <= 0) reached.add("углеводам")

            goalMessageTextView.text = if (reached.isNotEmpty()) {
                "Цели по ${reached.joinToString(", ")} достигнуты!"
            } else {
                ""
            }
        }
        loadData(userId)
        checkDayRunnable = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                if (currentDate != lastCheckedDate) {
                    lastCheckedDate = currentDate
                    loadData(userId)
                }
                handler.postDelayed(this, 60000)
            }
        }
        handler.post(checkDayRunnable)

        return view
    }

    private fun loadData(userId: Int) {
        lastCheckedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        viewModel.loadRecommendations(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(checkDayRunnable)
    }
}