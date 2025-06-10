package com.example.nutrition_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutrition_app.R
import com.example.nutrition_app.databinding.FragmentRecipesBinding
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.viewmodel.RecipesViewModel

class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipesViewModel by viewModels {
        RecipesViewModel.Factory(AppDatabase.getDatabase(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView для избранных рецептов
        val favoriteAdapter = RecipesAdapter { recipe ->
            viewModel.toggleFavorite(recipe.id)
        }
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.favoriteRecipesRecyclerView.adapter = favoriteAdapter

        // Настройка RecyclerView для всех рецептов
        val userAdapter = RecipesAdapter { recipe ->
            viewModel.toggleFavorite(recipe.id)
        }
        binding.userRecipesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.userRecipesRecyclerView.adapter = userAdapter

        // Загрузка рецептов
        val userId = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)
        if (userId != -1) {
            viewModel.loadRecipes(userId)
        }

        // Наблюдение за избранными рецептами
        viewModel.favoriteRecipes.observe(viewLifecycleOwner) { recipes ->
            favoriteAdapter.submitList(recipes)
            binding.favoriteRecipesTitle.visibility = if (recipes.isEmpty()) View.GONE else View.VISIBLE
        }

        // Наблюдение за всеми рецептами
        viewModel.userRecipes.observe(viewLifecycleOwner) { recipes ->
            userAdapter.submitList(recipes)
        }

        // Переход к созданию рецепта
        binding.fabAddRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_recipes_to_recipe_edit)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}