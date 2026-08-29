package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiaryEntry
import com.example.model.FoodItem
import com.example.model.MealType
import com.example.ui.components.FoodSelectorBottomSheet
import com.example.ui.components.LogFoodDialog
import com.example.ui.components.MacroBadge
import com.example.ui.components.MacroProgressBar
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.NutrientCarbs
import com.example.ui.theme.NutrientEnergy
import com.example.ui.theme.NutrientFat
import com.example.ui.theme.NutrientFiber
import com.example.ui.theme.NutrientProtein
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.NutritionViewModel
import java.util.Locale

@Composable
fun DiaryScreen(
    viewModel: NutritionViewModel,
    modifier: Modifier = Modifier
) {
    val dailySummary by viewModel.dailySummary.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()

    var showFoodSelector by remember { mutableStateOf(false) }
    var selectedMealForLog by remember { mutableStateOf(MealType.BREAKFAST) }
    var foodToLog by remember { mutableStateOf<FoodItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Screen Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Nutrition Diary",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = "Daily dietary intake & clinical logging",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }

            // Date navigation controls
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
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Day",
                            tint = SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = viewModel.getFormattedDisplayDate(selectedDate),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { viewModel.changeDateOffset(1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Day",
                            tint = SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Daily Total Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diary_daily_total_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CUMULATIVE DAILY INTAKE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )
                            Text(
                                text = "${dailySummary.entries.size} logged entries",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Energy Progress
                        MacroProgressBar(
                            label = "Energy Intake",
                            currentValue = dailySummary.totalCalories,
                            targetValue = userProfile.targetCalories,
                            unit = "kcal",
                            barColor = NutrientEnergy
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroBadge(
                                label = "Protein",
                                value = "${String.format(Locale.US, "%.1f", dailySummary.totalProtein)} / ${userProfile.targetProtein.toInt()}g",
                                color = NutrientProtein,
                                bgColor = ClinicalBackground,
                                modifier = Modifier.weight(1f)
                            )
                            MacroBadge(
                                label = "Carbs",
                                value = "${String.format(Locale.US, "%.1f", dailySummary.totalCarbs)} / ${userProfile.targetCarbs.toInt()}g",
                                color = NutrientCarbs,
                                bgColor = ClinicalBackground,
                                modifier = Modifier.weight(1f)
                            )
                            MacroBadge(
                                label = "Fat",
                                value = "${String.format(Locale.US, "%.1f", dailySummary.totalFat)} / ${userProfile.targetFat.toInt()}g",
                                color = NutrientFat,
                                bgColor = ClinicalBackground,
                                modifier = Modifier.weight(1f)
                            )
                            MacroBadge(
                                label = "Fiber",
                                value = "${String.format(Locale.US, "%.1f", dailySummary.totalFiber)}g",
                                color = NutrientFiber,
                                bgColor = ClinicalBackground,
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                    }
                }
            }

            // Meal Sections
            MealType.entries.forEach { mealType ->
                val mealEntries = dailySummary.entries.filter { it.mealType == mealType }
                val mealCal = mealEntries.sumOf { it.calories }
                val mealP = mealEntries.sumOf { it.protein }
                val mealC = mealEntries.sumOf { it.carbs }
                val mealF = mealEntries.sumOf { it.fat }

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Meal Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = mealType.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    )
                                    Text(
                                        text = "${mealCal.toInt()} kcal • P: ${String.format(Locale.US, "%.1f", mealP)}g • C: ${String.format(Locale.US, "%.1f", mealC)}g • F: ${String.format(Locale.US, "%.1f", mealF)}g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SecondaryText
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedMealForLog = mealType
                                        showFoodSelector = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ClinicalTealContainer,
                                        contentColor = ClinicalTealPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("add_meal_btn_${mealType.name.lowercase()}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add to ${mealType.displayName}",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Log",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (mealEntries.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = ClinicalBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                mealEntries.forEach { entry ->
                                    DiaryEntryRow(
                                        entry = entry,
                                        onDelete = { viewModel.deleteDiaryEntry(entry.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Food Selector
    if (showFoodSelector) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { selected ->
                foodToLog = selected
            },
            onDismiss = { showFoodSelector = false },
            title = "Log Entry: ${selectedMealForLog.displayName}"
        )
    }

    // Log Dialog
    foodToLog?.let { food ->
        LogFoodDialog(
            food = food,
            initialMealType = selectedMealForLog,
            onDismiss = { foodToLog = null },
            onLogConfirmed = { loggedFood, qty, unit, mealType ->
                viewModel.logFoodDirectly(loggedFood, qty, unit, mealType)
                foodToLog = null
            }
        )
    }
}

@Composable
fun DiaryEntryRow(
    entry: DiaryEntry,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${entry.quantity} ${entry.unit.unitCode} ${entry.foodName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PrimaryText
            )
            Text(
                text = "${entry.calories.toInt()} kcal • P: ${String.format(Locale.US, "%.1f", entry.protein)}g • C: ${String.format(Locale.US, "%.1f", entry.carbs)}g • F: ${String.format(Locale.US, "%.1f", entry.fat)}g",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete Entry",
                tint = SecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
