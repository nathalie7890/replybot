package com.nathalie.replybot.views.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nathalie.replybot.R
import com.nathalie.replybot.databinding.FragmentSignupBinding
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.viewModel.auth.SignupViewModel
import com.nathalie.replybot.views.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>() {
    override val viewModel: SignupViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_signup

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        binding?.viewModel = viewModel


        //when clicked, navigate to LoginFragment
        binding?.run {
            btnGoToLogin.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        //after user signs up, navigate to LoginFragment
        lifecycleScope.launch {
            viewModel.signupFinish.collect {
                val action = LoginFragmentDirections.toLoginFragment()
                navController.navigate(action)
            }
        }
    }

    //navgate to LoginFragment
    private fun navigateToLogin() {
        val action = SignupFragmentDirections.actionSignupToLogin()
        navController.navigate(action)
    }
}