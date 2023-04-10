package com.nathalie.replybot.views.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nathalie.replybot.MainActivity
import com.nathalie.replybot.R
import com.nathalie.replybot.databinding.FragmentLoginBinding
import com.nathalie.replybot.viewModel.auth.LoginViewModel
import com.nathalie.replybot.views.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val viewModel: LoginViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_login

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)

        binding?.viewModel = viewModel

        binding?.run {
            btnGoToSignup.setOnClickListener {
                navigateToSignUp()
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        lifecycleScope.launch {
            viewModel.loginFinish.collect {
                (activity as MainActivity).setUsername()
                navigateToHome()
            }
        }
    }


    private fun navigateToSignUp() {
        val action = LoginFragmentDirections.actionLoginToSignup()
        navController.navigate(action)
    }

    private fun navigateToHome() {
//        (requireContext().applicationContext as MyApplication).fetchUser()
        val action = LoginFragmentDirections.toRulesFragment()
        navController.navigate(action)
    }
}