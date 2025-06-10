package com.example.nutrition_app.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.nutrition_app.R
import com.example.nutrition_app.databinding.FragmentRecipeEditBinding
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.viewmodel.RecipeEditViewModel

class RecipeEditFragment : Fragment() {
    private var _binding: FragmentRecipeEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeEditViewModel by viewModels {
        RecipeEditViewModel.Factory(AppDatabase.getDatabase(requireContext()))
    }

    private val args: RecipeEditFragmentArgs by navArgs()
    private var selectedPhotoUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = result.data?.data
            selectedPhotoUri?.let {
                Glide.with(this).load(it).into(binding.recipePhoto)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Загрузка рецепта для редактирования
        viewModel.loadRecipe(args.recipeId)
        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                binding.titleEdit.setText(it.title)
                binding.caloriesEdit.setText(it.calories.toString())
                binding.proteinEdit.setText(it.protein.toString())
                binding.fatEdit.setText(it.fat.toString())
                binding.carbsEdit.setText(it.carbs.toString())
                binding.instructionsEdit.setText(it.instructions)
                if (it.photo != null) {
                    Glide.with(this).load(it.photo).into(binding.recipePhoto)
                    selectedPhotoUri = Uri.parse(it.photo)
                }
            }
        }

        // Выбор фото
        binding.recipePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
        }

        // Сохранение рецепта
        binding.saveButton.setOnClickListener {
            val title = binding.titleEdit.text.toString()
            val calories = binding.caloriesEdit.text.toString()
            val protein = binding.proteinEdit.text.toString()
            val fat = binding.fatEdit.text.toString()
            val carbs = binding.carbsEdit.text.toString()
            val instructions = binding.instructionsEdit.text.toString()
            val isFavorite = viewModel.recipe.value?.isFavorite ?: false

            viewModel.saveRecipe(
                context = requireContext(),
                recipeId = args.recipeId,
                title = title,
                photoUri = selectedPhotoUri?.toString(),
                calories = calories,
                protein = protein,
                fat = fat,
                carbs = carbs,
                instructions = instructions,
                isFavorite = isFavorite
            )
        }

        // Обработка результата сохранения
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().navigate(R.id.action_recipe_edit_to_recipes)
            }.onFailure { exception ->
                binding.errorText.text = exception.message
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}