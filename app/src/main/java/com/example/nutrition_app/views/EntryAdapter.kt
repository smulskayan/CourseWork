package com.example.nutrition_app.views


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrition_app.R
import com.example.nutrition_app.model.entities.FoodDiary
import com.example.nutrition_app.views.DayEntry

class EntryAdapter(private val entries: List<FoodDiary>) :
    RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entryTitle: TextView = view.findViewById(R.id.entryTitle)
        val entryDetails: TextView = view.findViewById(R.id.entryDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.entryTitle.text = if (entry.waterAmount > 0) "Прием воды" else "Прием пищи"
        holder.entryDetails.text = if (entry.waterAmount > 0) {
            "${entry.diaryTime}: ${entry.waterAmount} мл"
        } else {
            "${entry.diaryTime}: ${entry.quantity} г, Калории: ${entry.calories}, Б: ${entry.protein}, Ж: ${entry.fat}, У: ${entry.carbs}"
        }
    }

    override fun getItemCount(): Int = entries.size
}