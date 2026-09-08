package com.blitzlabx.zcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blitzlabx.zcode.ui.screens.*
import com.blitzlabx.zcode.ui.theme.ZCodeTheme
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel
import com.blitzlabx.zcode.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as ZCodeApplication
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(app.database, app.settingsRepository)
            )
            val theme by viewModel.theme.collectAsState(initial = "system")
            val darkTheme = when (theme) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            ZCodeTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ZCodeApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun ZCodeApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val onboardingDone by viewModel.onboardingDone.collectAsState(initial = false)

    val startDest = if (onboardingDone) "home" else "splash"

    NavHost(navController = navController, startDestination = startDest) {
        composable("splash") {
            SplashScreen(
                onFinished = {
                    if (onboardingDone) navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    } else navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    viewModel.setOnboardingDone(true)
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) },
                onOpenDrawer = { navController.navigate("drawer") }
            )
        }
        composable("drawer") {
            NavigationDrawerScreen(
                onNavigate = { route ->
                    navController.popBackStack()
                    navController.navigate(route)
                },
                onClose = { navController.popBackStack() }
            )
        }
        composable("generator") {
            GeneratorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onResult = { navController.navigate("generated") }
            )
        }
        composable("generated") {
            GeneratedOutputScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNew = {
                    navController.popBackStack()
                    navController.navigate("generator")
                }
            )
        }
        composable("translator") {
            TranslatorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onResult = { navController.navigate("translated") }
            )
        }
        composable("translated") {
            TranslatedOutputScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNew = {
                    navController.popBackStack()
                    navController.navigate("translator")
                }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("languages") {
            LanguagesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("modes") {
            ModesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { navController.navigate(it) }
            )
        }
        composable("security") {
            SecuritySettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("import_export") {
            ImportExportScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("qr") {
            QrSupportScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("documentation") {
            DocumentationScreen(onBack = { navController.popBackStack() })
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
