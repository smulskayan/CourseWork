package com.example.nutrition_app.views

import android.view.LayoutInflater
import androidx.navigation.findNavController
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nutrition_app.R
import com.example.nutrition_app.model.entities.Recipe

class RecipesAdapter(
    private val onFavoriteClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position), onFavoriteClick)
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImageView: ImageView = itemView.findViewById(R.id.recipe_photo)
        private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val macrosTextView: TextView = itemView.findViewById(R.id.recipe_macros)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_icon)
        private val instructionsTextView: TextView = itemView.findViewById(R.id.recipe_instructions)

        fun bind(recipe: Recipe, onFavoriteClick: (Recipe) -> Unit) {
            nameTextView.text = recipe.title
            macrosTextView.text = "Калории: ${recipe.calories} ккал, " +
                    "\nБелки: ${recipe.protein} г, \nЖиры: ${recipe.fat} г, \nУглеводы: ${recipe.carbs} г"
            instructionsTextView.text = "Инструкция: \n${recipe.instructions}\n"


            if (recipe.photo != null) {
                Glide.with(itemView.context)
                    .load(recipe.photo)
                    .into(photoImageView)
            } else {
                photoImageView.setImageResource(R.drawable.placeholder_image)
            }

            favoriteIcon.setImageResource(
                if (recipe.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
            favoriteIcon.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    if (recipe.isFavorite) R.color.red else R.color.gray
                )
            )

            favoriteIcon.setOnClickListener { onFavoriteClick(recipe) }

            itemView.setOnClickListener {
                val action = RecipesFragmentDirections.actionRecipesToRecipeEdit(recipe.id)
                itemView.findNavController().navigate(action)
            }
        }
    }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
}