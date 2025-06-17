package com.example.nutrition_app.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutrition_app.R
import com.example.nutrition_app.databinding.FragmentProfileBinding
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.Factory(AppDatabase.getDatabase(requireContext()))
    }

    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goals = arrayOf("Поддержание веса", "Снижение веса", "Набор веса")
        binding.goalSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goals)

        viewModel.loadUser(requireContext())
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.firstNameText.text = "Имя: ${it.firstName}"
                binding.genderText.text = "Пол: ${it.gender}"
                binding.heightText.text = "Рост: ${it.height} см"
                binding.weightText.text = "Вес: ${it.weight} кг"
                binding.emailText.text = "Email: ${it.email}"
                binding.goalText.text = "Цель: ${it.goal}"
                binding.ageText.text = "Возраст: ${it.age} лет"

                binding.ageEdit.setText(it.age.toString())
                binding.weightEdit.setText(it.weight.toString())
                binding.emailEdit.setText(it.email)
                binding.goalSpinner.setSelection(goals.indexOf(it.goal))

                // Очистка ошибок при загрузке
                binding.ageEdit.error = null
                binding.weightEdit.error = null
                binding.emailEdit.error = null
                binding.errorText.visibility = View.GONE
            }
        }

        binding.ageEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val ageInt = s.toString().toIntOrNull()
                if (ageInt == null || ageInt <= 18 || ageInt > 100) {
                    binding.ageEdit.error = "Некорректный возраст"
                } else {
                    binding.ageEdit.error = null
                }
            }
        })

        binding.weightEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val weightFloat = s.toString().toFloatOrNull()
                if (weightFloat == null || weightFloat <= 30 || weightFloat > 300) {
                    binding.weightEdit.error = "Некорректный вес"
                } else {
                    binding.weightEdit.error = null
                }
            }
        })

        binding.emailEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    binding.emailEdit.error = "Некорректный email"
                } else {
                    binding.emailEdit.error = null
                }
            }
        })

        binding.editButton.setOnClickListener {
            isEditing = !isEditing
            updateEditMode()
            if (!isEditing) {
                binding.ageEdit.error = null
                binding.weightEdit.error = null
                binding.emailEdit.error = null
                binding.errorText.visibility = View.GONE
            }
        }

        binding.saveButton.setOnClickListener {
            val age = binding.ageEdit.text.toString()
            val weight = binding.weightEdit.text.toString()
            val goal = binding.goalSpinner.selectedItem.toString()
            val email = binding.emailEdit.text.toString()

            val hasError = listOf(binding.ageEdit, binding.weightEdit, binding.emailEdit).any { it.error != null }
            if (hasError) {
                binding.errorText.text = "Пожалуйста, исправьте ошибки в форме"
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.errorText.visibility = View.GONE
                viewModel.updateUser(requireContext(), age, weight, goal, email)
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                isEditing = false
                updateEditMode()
                binding.errorText.visibility = View.GONE
            }.onFailure { exception ->
                binding.errorText.text = exception.message
                binding.errorText.visibility = View.VISIBLE
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout(requireContext())
            findNavController().navigate(R.id.action_profile_to_welcome)
        }

        updateEditMode()
    }

    private fun updateEditMode() {
        if (isEditing) {
            binding.editButton.text = "Отмена"
            binding.saveButton.visibility = View.VISIBLE
            binding.ageEdit.visibility = View.VISIBLE
            binding.weightEdit.visibility = View.VISIBLE
            binding.emailEdit.visibility = View.VISIBLE
            binding.goalSpinner.visibility = View.VISIBLE
            binding.ageText.visibility = View.GONE
            binding.weightText.visibility = View.GONE
            binding.emailText.visibility = View.GONE
            binding.goalText.visibility = View.GONE
        } else {
            binding.editButton.text = "Редактировать"
            binding.saveButton.visibility = View.GONE
            binding.ageEdit.visibility = View.GONE
            binding.weightEdit.visibility = View.GONE
            binding.emailEdit.visibility = View.GONE
            binding.goalSpinner.visibility = View.GONE
            binding.ageText.visibility = View.VISIBLE
            binding.weightText.visibility = View.VISIBLE
            binding.emailText.visibility = View.VISIBLE
            binding.goalText.visibility = View.VISIBLE
            binding.errorText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
