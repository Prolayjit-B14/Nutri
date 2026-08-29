package com.example.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.FoodsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.NutritionViewModel

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    data object Home : Screen("home", "Overview", Icons.Filled.GridView, Icons.Outlined.GridView)
    data object Calculator : Screen("calculator", "Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate)
    data object Foods : Screen("foods", "Food DB", Icons.Filled.TableChart, Icons.Outlined.TableChart)
    data object Diary : Screen("diary", "Daily Log", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    data object AiAssistant : Screen("ai_assistant", "Clinical AI", Icons.Filled.Psychology, Icons.Outlined.Psychology)
    data object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun NavigationRoot(
    viewModel: NutritionViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var calculatorInitialTab by remember { mutableIntStateOf(0) }

    val screens = listOf(
        Screen.Home,
        Screen.Calculator,
        Screen.Foods,
        Screen.Diary,
        Screen.AiAssistant,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            Surface(
                color = ClinicalSurface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, ClinicalBorder)
            ) {
                NavigationBar(
                    containerColor = ClinicalSurface,
                    tonalElevation = 0.dp
                ) {
                    screens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    if (screen == Screen.Calculator) {
                                        calculatorInitialTab = 0
                                    }
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ClinicalTealPrimary,
                                selectedTextColor = ClinicalTealPrimary,
                                unselectedIconColor = SecondaryText,
                                unselectedTextColor = SecondaryText,
                                indicatorColor = ClinicalTealContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCalculator = { isReverse ->
                        calculatorInitialTab = if (isReverse) 1 else 0
                        navController.navigate(Screen.Calculator.route)
                    },
                    onNavigateToAi = {
                        navController.navigate(Screen.AiAssistant.route)
                    },
                    onNavigateToDiary = {
                        navController.navigate(Screen.Diary.route)
                    },
                    onNavigateToFoods = {
                        navController.navigate(Screen.Foods.route)
                    }
                )
            }

            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = viewModel,
                    initialTab = calculatorInitialTab
                )
            }

            composable(Screen.Foods.route) {
                FoodsScreen(
                    viewModel = viewModel,
                    onCalculateFood = { food ->
                        viewModel.selectFoodForForward(food)
                        calculatorInitialTab = 0
                        navController.navigate(Screen.Calculator.route)
                    },
                    onReverseFood = { food ->
                        viewModel.selectFoodForReverse(food)
                        calculatorInitialTab = 1
                        navController.navigate(Screen.Calculator.route)
                    }
                )
            }

            composable(Screen.Diary.route) {
                DiaryScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

