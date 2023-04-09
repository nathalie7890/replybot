package com.nathalie.replybot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.nathalie.replybot.service.AuthService
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var authRepo: AuthService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        makeDrawerLayout(drawerLayout)

        if (!authRepo.isLoggedIn()) {
            findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
        }

        btnLogout.setOnClickListener {
            logout(drawerLayout)
        }
    }

    fun makeDrawerLayout(drawerLayout: DrawerLayout) {
        navController = findNavController(R.id.navHostFragment)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    fun setUsername() {
        lifecycleScope.launch {
            viewModel.getCurrentUser()
        }

        viewModel.user.observe(this) { user ->
            val username = findViewById<TextView>(R.id.tvUserName)
            username.text = "@${user.name}"
        }
    }

    fun logout(drawerLayout: DrawerLayout) {
        authRepo.signOut()
        findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onNavigateUp()
    }
}