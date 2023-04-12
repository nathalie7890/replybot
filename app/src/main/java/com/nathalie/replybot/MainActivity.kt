package com.nathalie.replybot

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.nathalie.replybot.receiver.MyBroadcastReceiver
import com.nathalie.replybot.service.AuthService
import com.nathalie.replybot.service.MyService
import com.nathalie.replybot.service.NotificationService
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.utils.Constants.DEBUG
import com.nathalie.replybot.utils.NotificationUtils
import com.nathalie.replybot.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val NOTIFICATION_REQ_CODE = 0
    private val FOREGROUND_REQ_CODE = 1
    private lateinit var myService: MyService
    private lateinit var myReceiver: MyBroadcastReceiver
    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var authRepo: AuthService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeDrawerLayout()
        Log.d(Constants.DEBUG, "hello")

        NotificationUtils.createNotificationChannel(this)
        checkPermission("android.permission.POST_NOTIFICATIONS", NOTIFICATION_REQ_CODE)
        checkPermission("android.permission.FOREGROUND_SERVICE", FOREGROUND_REQ_CODE)

        if (!authRepo.isLoggedIn()) {
            findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
        } else {
            setUsername()
        }


        // First time launch, open notification settings
//        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        startService(Intent(this, NotificationService::class.java))
        startService()
    }

    fun startService() {
        Log.d(Constants.DEBUG, "starting service")
        serviceIntent().also {
            intent.putExtra("EXTRA_DATA", "Hello from MainActivity")
            startService(it)
        }
    }

    fun stopService() {
        serviceIntent().also {
            stopService(it)
        }
    }

    private fun serviceIntent(): Intent {
        myService = MyService()
        return Intent(this, MyService::class.java)
    }

    private fun makeDrawerLayout() {
        navController = findNavController(R.id.navHostFragment)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        logout(drawerLayout)
    }

    private fun logout(drawerLayout: DrawerLayout) {
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            authRepo.signOut()
            findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onNavigateUp()
    }

    private fun resultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    data?.let {
                        val msg = it.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).toString()
                        val otp = Regex("\\d{4,6}").find(msg)?.value ?: ""
                    }
                }
            }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Log.d(DEBUG, "Permission granted from MainActivity")
        }
    }

    private fun registerBroadcastReceiver() {
        NotificationUtils.createNotificationChannel(this)
        checkPermission(
            "android.permission.POST_NOTIFICATIONS",
            NOTIFICATION_REQ_CODE
        )
        checkPermission(
            "android.permission.FOREGROUND_SERVICE",
            FOREGROUND_REQ_CODE
        )

        val filter = IntentFilter()
        filter.addAction("com.replyBot.MyBroadcast")

        myReceiver = MyBroadcastReceiver()
        registerReceiver(myReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_REQ_CODE -> {
                makeToast("Notification permission granted")
            }

            FOREGROUND_REQ_CODE -> {
                makeToast("Foreground service permission granted")
            }
            else -> {
                makeToast("Permission Denied")
            }
        }
    }

    fun makeToast(reply: String) {
        return Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
    }
}
