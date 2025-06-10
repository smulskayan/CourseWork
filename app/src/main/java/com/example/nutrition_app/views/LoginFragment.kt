package com.example.nutrition_app.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.content.Context
import androidx.navigation.fragment.findNavController
import com.example.nutrition_app.R
import com.example.nutrition_app.databinding.FragmentLoginBinding
import com.example.nutrition_app.model.AppDatabase
import com.example.nutrition_app.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(AppDatabase.getDatabase(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                if (s.toString().isBlank()) {
                    binding.passwordEditText.error = "Введите пароль"
                } else {
                    binding.passwordEditText.error = null
                }
            }
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { userId ->
                requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    .edit().putInt("userId", userId).apply()
                findNavController().navigate(R.id.action_login_to_profile)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}