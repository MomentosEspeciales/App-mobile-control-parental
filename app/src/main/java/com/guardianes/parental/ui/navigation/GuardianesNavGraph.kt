package com.guardianes.parental.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guardianes.parental.core.domain.model.UserRole
import com.guardianes.parental.ui.child.ChildHomeScreen
import com.guardianes.parental.ui.onboarding.OnboardingScreen
import com.guardianes.parental.ui.onboarding.RootViewModel
import com.guardianes.parental.ui.parent.ParentScaffold

object Routes {
    const val ONBOARDING = "onboarding"
    const val PARENT = "parent"
    const val CHILD = "child"
}

/**
 * Decide la pantalla inicial según el rol persistido y aloja el grafo principal.
 */
@Composable
fun GuardianesNavGraph(rootViewModel: RootViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val role by rootViewModel.role.collectAsStateWithLifecycle()

    val start = when (role) {
        UserRole.PARENT -> Routes.PARENT
        UserRole.CHILD -> Routes.CHILD
        UserRole.UNSET -> Routes.ONBOARDING
    }

    NavHost(navController = navController, startDestination = start) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onParentSelected = {
                    navController.navigate(Routes.PARENT) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                onChildSelected = {
                    navController.navigate(Routes.CHILD) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.PARENT) { ParentScaffold() }
        composable(Routes.CHILD) { ChildHomeScreen() }
    }
}
