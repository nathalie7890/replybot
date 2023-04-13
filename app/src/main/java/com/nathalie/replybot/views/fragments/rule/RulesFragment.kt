package com.nathalie.replybot.views.fragments.rule

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.nathalie.replybot.MainActivity
import com.nathalie.replybot.R
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.databinding.FragmentRulesBinding
import com.nathalie.replybot.service.NotificationService
import com.nathalie.replybot.viewModel.rule.RulesViewModel
import com.nathalie.replybot.views.adapters.RuleAdapter
import com.nathalie.replybot.views.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RulesFragment : BaseFragment<FragmentRulesBinding>() {

    private lateinit var adapter: RuleAdapter
    override val viewModel: RulesViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_rules

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        setupAdapter()

        binding?.run {
            btnAdd.setOnClickListener {
                val action = RulesFragmentDirections.actionRulesToAddRule()
                navController.navigate(action)
            }

            btnStartService.setOnClickListener {
                NotificationService.start()
                (requireActivity() as MainActivity).startService()
                startServiceBtnClicked(btnStartService, btnStopService, tvServiceIsRunning)
            }

            btnStopService.setOnClickListener {
                NotificationService.stop()
                (requireActivity() as MainActivity).stopService()
                stopServiceBtnClicked(btnStartService, btnStopService, tvServiceIsRunning)
            }

        }

        fragmentResultRefresh("finish_add_rule")
        fragmentResultRefresh("finish_edit_rule")
        fragmentResultRefresh("finish_delete_rule")
    }


    override fun onBindData(view: View) {
        super.onBindData(view)

        viewModel.rules.observe(viewLifecycleOwner) {
            adapter.setRules(it.toMutableList())
        }
    }

    private fun fragmentResultRefresh(requestKey: String) {
        setFragmentResultListener(requestKey) { _, result ->
            val refresh = result.getBoolean("refresh")
            if (refresh) {
                viewModel.onRefresh()
            }
        }
    }

    fun startServiceBtnClicked(btnStart: MaterialButton, btnStop: MaterialButton, tv: TextView) {
        btnStart.isVisible = false
        btnStop.isVisible = true
        tv.text =
            "Reply bot is running. To disable, tap Disable button below."
    }

    fun stopServiceBtnClicked(btnStart: MaterialButton, btnStop: MaterialButton, tv: TextView) {
        btnStart.isVisible = true
        btnStop.isVisible = false
        tv.text =
            "Reply bot is currently disabled. To enable, tap Enable button below."
    }

    fun setupAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = RuleAdapter(mutableListOf())
        adapter.listener = object : RuleAdapter.Listener {
            override fun onClick(rule: Rule) {
                val action = rule.id?.let {
                    RulesFragmentDirections.actionRulesToEditRule(it)
                }

                if (action != null) {
                    NavHostFragment.findNavController(this@RulesFragment).navigate(action)
                }
            }
        }

        binding?.rvRules?.adapter = adapter
        binding?.rvRules?.layoutManager = layoutManager
    }
}

