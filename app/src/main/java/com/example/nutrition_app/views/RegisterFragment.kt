package com.example.nutrition_app.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutrition_app.R
import com.example.nutrition_app.databinding.FragmentRegisterBinding
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.viewmodel.RegisterViewModel
import android.content.Context
import android.text.Editable
import android.text.TextWatcher

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModel.Factory(AppDatabase.getDatabase(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка списков выбора
        val genders = arrayOf("Мужской", "Женский")
        binding.genderSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genders)
        val goals = arrayOf("Поддержание веса", "Снижение веса", "Набор веса")
        binding.goalSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goals)

        // Валидация на лету
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    binding.emailEditText.error = "Некорректный email"
                } else {
                    binding.emailEditText.error = null
                }
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 6) {
                    binding.passwordEditText.error = "Минимум 6 символов"
                } else {
                    binding.passwordEditText.error = null
                }
            }
        })

        binding.confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != binding.passwordEditText.text.toString()) {
                    binding.confirmPasswordEditText.error = "Пароли не совпадают"
                } else {
                    binding.confirmPasswordEditText.error = null
                }
            }
        })

        // Обработка регистрации
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString()
            val height = binding.heightEditText.text.toString()
            val weight = binding.weightEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val age = binding.ageText.text.toString().toIntOrNull() ?: -1
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val goal = binding.goalSpinner.selectedItem.toString()

            viewModel.register(firstName, gender, age, height, weight, email, password, confirmPassword, goal)
        }

        // Наблюдение за результатом
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { userId ->
                requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    .edit().putInt("userId", userId).apply()
                findNavController().navigate(R.id.action_register_to_profile)
            }.onFailure { exception ->
                binding.errorTextView.text = exception.message
                binding.errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.emailEditText.error = error
        }

        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.passwordEditText.error = error
            binding.confirmPasswordEditText.error = error
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}