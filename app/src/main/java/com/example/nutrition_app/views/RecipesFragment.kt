package com.example.nutrition_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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

    private lateinit var recipesAdapter: RecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesAdapter = RecipesAdapter { recipe ->
            viewModel.toggleFavorite(recipe.id)
        }
        binding.userRecipesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.userRecipesRecyclerView.adapter = recipesAdapter

        viewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->
            recipesAdapter.submitList(recipes)
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RecipesViewModel.NavigationEvent.NavigateToRecipeEdit -> {
                    findNavController().navigate(R.id.action_recipes_to_recipe_edit)
                }
            }
        }

        binding.fabAddRecipe.setOnClickListener {
            viewModel.onAddRecipeClicked()
        }

        viewModel.loadUserId(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}