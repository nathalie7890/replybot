package com.nathalie.replybot

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.receiver.MyBroadcastReceiver
import com.nathalie.replybot.service.MyService
import com.nathalie.replybot.service.NotificationService
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
    lateinit var authRepo: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeDrawerLayout()

        NotificationUtils.createNotificationChannel(this)
        checkPermission("android.permission.POST_NOTIFICATIONS", NOTIFICATION_REQ_CODE)
        checkPermission("android.permission.FOREGROUND_SERVICE", FOREGROUND_REQ_CODE)

        //if user if not logged in, navigate to login fragment else set username is left drawer
        if (!authRepo.isLoggedIn()) {
            findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
        } else {
            setUsername()
        }


        // First time launch, open notification settings
       startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        startService(Intent(this, NotificationService::class.java))
        startService()
    }

    fun startService() {
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

    //create drawer layout
    private fun makeDrawerLayout() {
        navController = findNavController(R.id.navHostFragment)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        logout(drawerLayout)
    }

    //btn logout
    private fun logout(drawerLayout: DrawerLayout) {
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        //when clicked, sign out the user, navigate to login fragment and close left drawer
        btnLogout.setOnClickListener {
            authRepo.signOut()
            findNavController(R.id.navHostFragment).navigate(R.id.to_login_fragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    //set username im left drawer
    fun setUsername() {
        //get current user
        lifecycleScope.launch {
            viewModel.getCurrentUser()
        }

        //if user logs out and logs in again, update username
        viewModel.user.observe(this) { user ->
            val username = findViewById<TextView>(R.id.tvUserName)
            username.text = "@${user.name}"
        }
    }

    //called when user chooses to navigate up within app's activity hierarchy from the action bar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onNavigateUp()
    }


//    private fun resultLauncher() {
//        resultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    val data = it.data
//                    data?.let {
//                        val msg = it.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).toString()
//                        val otp = Regex("\\d{4,6}").find(msg)?.value ?: ""
//                    }
//                }
//            }
//    }

    //check for permissions
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

//    private fun registerBroadcastReceiver() {
//        NotificationUtils.createNotificationChannel(this)
//        checkPermission(
//            "android.permission.POST_NOTIFICATIONS",
//            NOTIFICATION_REQ_CODE
//        )
//        checkPermission(
//            "android.permission.FOREGROUND_SERVICE",
//            FOREGROUND_REQ_CODE
//        )
//
//        val filter = IntentFilter()
//        filter.addAction("com.replyBot.MyBroadcast")
//
//        myReceiver = MyBroadcastReceiver()
//        registerReceiver(myReceiver, filter)
//    }

    //unregister my receiver when user exits the app
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    //after getting result back from permission request
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

    //displays toast
    fun makeToast(reply: String) {
        return Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
    }
}
