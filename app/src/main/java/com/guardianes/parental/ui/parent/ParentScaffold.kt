package com.guardianes.parental.ui.parent

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private enum class ParentTab(val route: String, val label: String, val icon: ImageVector) {
    DASHBOARD("dashboard", "Inicio", Icons.Default.Dashboard),
    MAP("map", "Mapa", Icons.Default.Map),
    ALERTS("alerts", "Alertas", Icons.Default.Notifications),
    REWARDS("rewards", "Recompensas", Icons.Default.CardGiftcard),
    SETTINGS("settings", "Ajustes", Icons.Default.Settings),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentScaffold(viewModel: ParentViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    val unacknowledged = state.alerts.count { !it.acknowledged }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            val backStack by nav.currentBackStackEntryAsState()
            val current = backStack?.destination
            NavigationBar {
                ParentTab.entries.forEach { tab ->
                    val selected = current?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            if (tab == ParentTab.ALERTS && unacknowledged > 0) {
                                BadgedBox(badge = { Badge { Text("$unacknowledged") } }) {
                                    Icon(tab.icon, contentDescription = tab.label)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.label)
                            }
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = ParentTab.DASHBOARD.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(ParentTab.DASHBOARD.route) { DashboardScreen(state, viewModel) }
            composable(ParentTab.MAP.route) { MapScreen(state, viewModel) }
            composable(ParentTab.ALERTS.route) { AlertsScreen(state, viewModel) }
            composable(ParentTab.REWARDS.route) { RewardsScreen() }
            composable(ParentTab.SETTINGS.route) { SettingsScreen() }
        }
    }
}
