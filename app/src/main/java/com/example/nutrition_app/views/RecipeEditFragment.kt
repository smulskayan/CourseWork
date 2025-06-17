package com.example.nutrition_app.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        fun fieldValidator(view: EditText, errorMsg: String) = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.error = if (s.isNullOrBlank()) errorMsg else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fun numberValidator(view: EditText, errorMsg: String) = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toFloatOrNull()
                view.error = if (value == null || value <= 0f) errorMsg else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }


        binding.titleEdit.addTextChangedListener(fieldValidator(binding.titleEdit, "Название обязательно"))
        binding.caloriesEdit.addTextChangedListener(numberValidator(binding.caloriesEdit, "Введите калории > 0"))
        binding.proteinEdit.addTextChangedListener(numberValidator(binding.proteinEdit, "Введите белки > 0"))
        binding.fatEdit.addTextChangedListener(numberValidator(binding.fatEdit, "Введите жиры > 0"))
        binding.carbsEdit.addTextChangedListener(numberValidator(binding.carbsEdit, "Введите углеводы > 0"))
        binding.instructionsEdit.addTextChangedListener(fieldValidator(binding.instructionsEdit, "Введите инструкцию"))

        binding.recipePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEdit.text.toString()
            val calories = binding.caloriesEdit.text.toString()
            val protein = binding.proteinEdit.text.toString()
            val fat = binding.fatEdit.text.toString()
            val carbs = binding.carbsEdit.text.toString()
            val instructions = binding.instructionsEdit.text.toString()
            val isFavorite = viewModel.recipe.value?.isFavorite ?: false

            val hasErrors = listOf(
                binding.titleEdit,
                binding.caloriesEdit,
                binding.proteinEdit,
                binding.fatEdit,
                binding.carbsEdit,
                binding.instructionsEdit
            ).any { it.error != null || it.text.isNullOrBlank() }

            if (hasErrors) {
                binding.errorText.text = "Пожалуйста, заполните корректно все поля"
                binding.errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

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

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().navigate(R.id.action_recipe_edit_to_recipes)
            }.onFailure { exception ->
                binding.errorText.text = exception.message
                binding.errorText.visibility = View.VISIBLE
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipe_edit_to_recipes)
        }
    }

    private fun fieldValidator(view: androidx.appcompat.widget.AppCompatEditText, errorMsg: String) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            view.error = if (s.isNullOrBlank()) errorMsg else null
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun numberValidator(view: androidx.appcompat.widget.AppCompatEditText, errorMsg: String) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val value = s.toString().toFloatOrNull()
            view.error = if (value == null || value <= 0f) errorMsg else null
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
