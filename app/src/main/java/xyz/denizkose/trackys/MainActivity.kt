package xyz.denizkose.trackys

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import xyz.denizkose.trackys.core.WorkScheduler
import xyz.denizkose.trackys.presentation.navigation.Screen
import xyz.denizkose.trackys.presentation.screens.add_edit.AddEditScreen
import xyz.denizkose.trackys.presentation.screens.analytics.AnalyticsScreen
import xyz.denizkose.trackys.presentation.screens.dashboard.DashboardScreen
import xyz.denizkose.trackys.presentation.screens.settings.SettingsScreen
import xyz.denizkose.trackys.ui.theme.TrackYSTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Дозвіл отримано або відхилено
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        WorkScheduler.scheduleDailyReminder(this)
        enableEdgeToEdge()
        setContent {
            TrackYSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination?.route

                    // Показуємо BottomBar тільки на головних екранах
                    val isBottomBarVisible =
                        currentDestination?.contains("AddEditSubscription") != true

                    Scaffold(
                        bottomBar = {
                            if (isBottomBarVisible) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentDestination?.contains("Dashboard") == true,
                                        onClick = {
                                            navController.navigate(Screen.Dashboard) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                Icons.Default.Home,
                                                contentDescription = stringResource(R.string.nav_dashboard)
                                            )
                                        },
                                        label = { Text(stringResource(R.string.nav_dashboard)) }
                                    )

                                    NavigationBarItem(
                                        selected = currentDestination?.contains("Analytics") == true,
                                        onClick = {
                                            navController.navigate(Screen.Analytics) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                Icons.Default.PieChart,
                                                contentDescription = stringResource(R.string.nav_analytics)
                                            )
                                        },
                                        label = { Text(stringResource(R.string.nav_analytics)) }
                                    )

                                    NavigationBarItem(
                                        selected = currentDestination?.contains("Settings") == true,
                                        onClick = {
                                            navController.navigate(Screen.Settings) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                Icons.Default.Settings,
                                                contentDescription = stringResource(R.string.nav_settings)
                                            )
                                        },
                                        label = { Text(stringResource(R.string.nav_settings)) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Dashboard,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<Screen.Dashboard> {
                                DashboardScreen(
                                    onNavigateToAddEdit = { id ->
                                        navController.navigate(
                                            Screen.AddEditSubscription(subscriptionId = id ?: -1L)
                                        )
                                    }
                                )
                            }

                            composable<Screen.Analytics> {
                                AnalyticsScreen()
                            }

                            composable<Screen.Settings> {
                                SettingsScreen()
                            }

                            composable<Screen.AddEditSubscription> {
                                AddEditScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}