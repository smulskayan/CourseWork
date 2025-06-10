package com.example.nutrition_app.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrition_app.R
import com.example.nutrition_app.views.DayEntry
import com.example.nutrition_app.model.entities.FoodDiary

class DiaryAdapter(private var dayEntries: List<DayEntry>) :
    RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTitle: TextView = view.findViewById(R.id.dayTitle)
        val entriesContainer: LinearLayout = view.findViewById(R.id.entriesContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayEntry = dayEntries[position]
        holder.dayTitle.text = dayEntry.date
        Log.d("DiaryAdapter", "Binding day ${dayEntry.date} with ${dayEntry.entries.size} entries")

        // Очищаем контейнер
        holder.entriesContainer.removeAllViews()

        // Добавляем каждую запись
        dayEntry.entries.forEachIndexed { index, entry ->
            Log.d("DiaryAdapter", "Adding entry $index: ${if (entry.waterAmount > 0) "Water" else "Food"} at ${entry.diaryTime}")
            val entryLayout = LinearLayout(holder.entriesContainer.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                setPadding(0, 8, 0, 8)
            }

            val entryTitle = TextView(holder.entriesContainer.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = if (entry.waterAmount > 0) "Прием воды" else "Прием пищи"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val entryDetails = TextView(holder.entriesContainer.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = if (entry.waterAmount > 0) {
                    "${entry.diaryTime}: ${entry.waterAmount} мл"
                } else {
                    "${entry.diaryTime}: ${entry.quantity} г, Калории: ${entry.calories}, Б: ${entry.protein}, Ж: ${entry.fat}, У: ${entry.carbs}"
                }
                textSize = 14f
            }

            entryLayout.addView(entryTitle)
            entryLayout.addView(entryDetails)
            holder.entriesContainer.addView(entryLayout)
        }
    }

    override fun getItemCount(): Int = dayEntries.size
}