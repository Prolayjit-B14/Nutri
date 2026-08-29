package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodItem
import com.example.model.MealType
import com.example.ui.components.FoodSelectorBottomSheet
import com.example.ui.components.LogFoodDialog
import com.example.ui.components.MacroProgressBar
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSuccess
import com.example.ui.theme.ClinicalSuccessLight
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.NutrientCarbs
import com.example.ui.theme.NutrientFat
import com.example.ui.theme.NutrientProtein
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.NutritionViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: NutritionViewModel,
    onNavigateToCalculator: (isReverse: Boolean) -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToFoods: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dailySummary by viewModel.dailySummary.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showFoodPickerForLog by remember { mutableStateOf(false) }
    var foodToLog by remember { mutableStateOf<FoodItem?>(null) }

    val caloriesRemaining = (userProfile.targetCalories - dailySummary.totalCalories).coerceAtLeast(0.0)
    val targetPct = if (userProfile.targetCalories > 0) {
        ((dailySummary.totalCalories / userProfile.targetCalories) * 100).toInt().coerceIn(0, 100)
    } else 0

    val displayDate = remember(selectedDate) {
        viewModel.getFormattedDisplayDate(selectedDate)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section (Scientific & Clinical)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nutrition Overview",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }

                // Date Navigation Buttons
                Surface(
                    color = ClinicalSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.changeDateOffset(-1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day",
                                tint = PrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.changeDateOffset(1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Next Day",
                                tint = PrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Nutrition Card (Clean & Data-Focused)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_daily_nutrition_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY NUTRITION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = SecondaryText
                        )
                        Surface(
                            color = if (targetPct >= 100) ClinicalSuccessLight else ClinicalTealContainer,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (targetPct >= 100) ClinicalSuccess.copy(alpha = 0.3f) else ClinicalBorder
                            )
                        ) {
                            Text(
                                text = "$targetPct% of daily target",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (targetPct >= 100) ClinicalSuccess else ClinicalTealPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Large Nutrition Energy Value
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = String.format(Locale.US, "%,d", dailySummary.totalCalories.toInt()),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = " / ${String.format(Locale.US, "%,d", userProfile.targetCalories.toInt())} kcal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = ClinicalBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Macronutrients Breakdown
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MacroProgressBar(
                            label = "Protein",
                            currentValue = dailySummary.totalProtein,
                            targetValue = userProfile.targetProtein,
                            unit = "g",
                            barColor = NutrientProtein
                        )
                        MacroProgressBar(
                            label = "Carbohydrate",
                            currentValue = dailySummary.totalCarbs,
                            targetValue = userProfile.targetCarbs,
                            unit = "g",
                            barColor = NutrientCarbs
                        )
                        MacroProgressBar(
                            label = "Fat",
                            currentValue = dailySummary.totalFat,
                            targetValue = userProfile.targetFat,
                            unit = "g",
                            barColor = NutrientFat
                        )
                    }
                }
            }
        }

        // Quick Actions (Minimal Clinical Actions)
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClinicalActionButton(
                    title = "Add Food",
                    icon = Icons.Default.Add,
                    onClick = { showFoodPickerForLog = true },
                    modifier = Modifier.weight(1f)
                )
                ClinicalActionButton(
                    title = "Calculator",
                    icon = Icons.Default.Calculate,
                    onClick = { onNavigateToCalculator(false) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClinicalActionButton(
                    title = "Food Database",
                    icon = Icons.Default.Search,
                    onClick = onNavigateToFoods,
                    modifier = Modifier.weight(1f)
                )
                ClinicalActionButton(
                    title = "View Diary",
                    icon = Icons.Default.MenuBook,
                    onClick = onNavigateToDiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Today's Food List Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Food",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = "Full Diary",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalTealPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToDiary() }
                        .testTag("view_diary_button")
                )
            }
        }

        if (dailySummary.entries.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No foods logged today",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start tracking your meals to see your daily nutrition summary.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showFoodPickerForLog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Food", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } else {
            MealType.entries.forEach { mealType ->
                val mealEntries = dailySummary.entries.filter { it.mealType == mealType }
                if (mealEntries.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mealType.displayName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    )
                                    val mealCalories = mealEntries.sumOf { it.calories }
                                    Text(
                                        text = "${mealCalories.toInt()} kcal",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ClinicalTealPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = ClinicalBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                mealEntries.forEach { entry ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${entry.quantity.let { if (it % 1.0 == 0.0) it.toInt().toString() else "%.1f".format(Locale.US, it) }} ${entry.unit.unitCode} ${entry.foodName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = PrimaryText
                                        )
                                        Text(
                                            text = "${entry.calories.toInt()} kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = SecondaryText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Clinical Medical Disclaimer Note
        item {
            MedicalDisclaimerCard()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Food Selector Bottom Sheet
    if (showFoodPickerForLog) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { selected ->
                foodToLog = selected
            },
            onDismiss = { showFoodPickerForLog = false }
        )
    }

    // Log Food Dialog
    foodToLog?.let { food ->
        LogFoodDialog(
            food = food,
            onDismiss = { foodToLog = null },
            onLogConfirmed = { loggedFood, qty, unit, mealType ->
                viewModel.logFoodDirectly(loggedFood, qty, unit, mealType)
                foodToLog = null
            }
        )
    }
}

@Composable
fun ClinicalActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = ClinicalSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ClinicalTealPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PrimaryText,
                maxLines = 1
            )
        }
    }
}

