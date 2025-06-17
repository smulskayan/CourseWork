package com.example.nutrition_app.views

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrition_app.R

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

        holder.entriesContainer.removeAllViews()

        dayEntry.entries.forEach { entry ->
            val context = holder.entriesContainer.context

            val entryLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 8, 16, 8)
            }

            val entryTitle = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = if (entry.waterAmount > 0) "Прием воды" else "Прием пищи"
                textSize = 16f
                typeface = Typeface.SERIF
                gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
                setTypeface(typeface, Typeface.BOLD)
            }

            val dataLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.END
            }

            val timeText = TextView(context).apply {
                text = entry.diaryTime
                textSize = 14f
                typeface = Typeface.create("serif", Typeface.ITALIC) // курсив и serif
                gravity = android.view.Gravity.END
            }
            dataLayout.addView(timeText)

            if (entry.waterAmount > 0) {
                val waterText = TextView(context).apply {
                    text = "${entry.waterAmount} мл"
                    textSize = 16f
                    typeface = Typeface.SERIF
                    gravity = android.view.Gravity.END
                }
                dataLayout.addView(waterText)
            } else {
                val macros = listOf(
                    "Калории: ${entry.calories}",
                    "Б: ${entry.protein} г",
                    "Ж: ${entry.fat} г",
                    "У: ${entry.carbs} г"
                )
                macros.forEach {
                    val macroText = TextView(context).apply {
                        text = it
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        typeface = Typeface.SERIF
                        gravity = android.view.Gravity.END
                    }
                    dataLayout.addView(macroText)
                }
            }

            entryLayout.addView(entryTitle)
            entryLayout.addView(dataLayout)

            holder.entriesContainer.addView(entryLayout)
        }
    }

    override fun getItemCount(): Int = dayEntries.size
}
