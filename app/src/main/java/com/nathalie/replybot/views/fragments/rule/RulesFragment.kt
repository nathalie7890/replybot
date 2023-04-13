package com.nathalie.replybot.views.fragments.rule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
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
            }

            btnStopService.setOnClickListener {
                NotificationService.stop()
                (requireActivity() as MainActivity).stopService()
            }

        }

        fragmentResultRefresh("finish_add_rule")
        fragmentResultRefresh("finish_edit_rule")
        fragmentResultRefresh("finish_delete_rule")
    }

    private fun fragmentResultRefresh(requestKey: String) {
        setFragmentResultListener(requestKey) { _, result ->
            val refresh = result.getBoolean("refresh")
            if (refresh) {
                viewModel.onRefresh()
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        viewModel.rules.observe(viewLifecycleOwner) {
            adapter.setRules(it.toMutableList())
        }
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

