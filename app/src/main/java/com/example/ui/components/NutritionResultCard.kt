package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MealType
import com.example.model.NutritionResult
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
import java.util.Locale

@Composable
fun NutritionResultCard(
    result: NutritionResult,
    onAddToDiary: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMealPicker by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutrition_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Food Name & Serving
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.foodItem.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Text(
                        text = "${result.inputQuantity} ${result.inputUnit.displayName} (≈ ${String.format(Locale.US, "%.0f", result.calculatedGrams)} g)",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }

                // Calorie badge
                Surface(
                    color = ClinicalTealContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${String.format(Locale.US, "%.0f", result.calories)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryText
                        )
                        Text(
                            text = " kcal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = ClinicalBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Primary Macronutrient Bars
            Text(
                text = "MACRONUTRIENT BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Protein
            ClinicalNutrientRowItem(
                label = "Protein",
                value = String.format(Locale.US, "%.1f g", result.protein),
                barColor = NutrientProtein,
                percentage = (result.protein / 50.0).coerceIn(0.0, 1.0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Carbohydrates
            ClinicalNutrientRowItem(
                label = "Carbohydrates",
                value = String.format(Locale.US, "%.1f g", result.carbs),
                barColor = NutrientCarbs,
                percentage = (result.carbs / 100.0).coerceIn(0.0, 1.0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fat
            ClinicalNutrientRowItem(
                label = "Fat",
                value = String.format(Locale.US, "%.1f g", result.fat),
                barColor = NutrientFat,
                percentage = (result.fat / 35.0).coerceIn(0.0, 1.0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fiber
            ClinicalNutrientRowItem(
                label = "Dietary Fiber",
                value = String.format(Locale.US, "%.1f g", result.fiber),
                barColor = NutrientFiber,
                percentage = (result.fiber / 15.0).coerceIn(0.0, 1.0)
            )

            // Macro Energy Calorie Distribution Ratio
            if (result.calories > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                MacroSplitBar(
                    proteinPct = result.proteinCaloriePercent,
                    carbsPct = result.carbsCaloriePercent,
                    fatPct = result.fatCaloriePercent
                )
            }

            // Micronutrients if available
            if (result.calciumMg > 0 || result.ironMg > 0 || result.potassiumMg > 0 || result.vitaminCMg > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (result.calciumMg > 0) {
                        MicroChip(label = "Calcium", value = "${result.calciumMg.toInt()} mg")
                    }
                    if (result.ironMg > 0) {
                        MicroChip(label = "Iron", value = "${String.format(Locale.US, "%.1f", result.ironMg)} mg")
                    }
                    if (result.potassiumMg > 0) {
                        MicroChip(label = "Potassium", value = "${result.potassiumMg.toInt()} mg")
                    }
                    if (result.vitaminCMg > 0) {
                        MicroChip(label = "Vit C", value = "${result.vitaminCMg.toInt()} mg")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Picker or Add Button
            if (showMealPicker) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select Meal Slot:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealType.entries.forEach { meal ->
                            Surface(
                                onClick = {
                                    onAddToDiary(meal)
                                    showMealPicker = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = ClinicalTealContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("log_meal_${meal.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = meal.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ClinicalTealPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = { showMealPicker = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ClinicalTealPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("add_forward_to_diary_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log to Diary",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Log Entry to Daily Diary",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ClinicalNutrientRowItem(
    label: String,
    value: String,
    barColor: Color,
    percentage: Double
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryText
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color(0xFFE2ECEC),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun MacroSplitBar(
    proteinPct: Double,
    carbsPct: Double,
    fatPct: Double
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Energy Calorie Distribution",
                fontSize = 12.sp,
                color = SecondaryText
            )
            Text(
                text = "P: ${proteinPct.toInt()}% • C: ${carbsPct.toInt()}% • F: ${fatPct.toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            val pWeight = (proteinPct.toFloat() / 100f).coerceAtLeast(0.01f)
            val cWeight = (carbsPct.toFloat() / 100f).coerceAtLeast(0.01f)
            val fWeight = (fatPct.toFloat() / 100f).coerceAtLeast(0.01f)

            Box(
                modifier = Modifier
                    .weight(pWeight)
                    .background(NutrientProtein)
            )
            Box(
                modifier = Modifier
                    .weight(cWeight)
                    .background(NutrientCarbs)
            )
            Box(
                modifier = Modifier
                    .weight(fWeight)
                    .background(NutrientFat)
            )
        }
    }
}

@Composable
fun MicroChip(label: String, value: String) {
    Surface(
        color = ClinicalBackground,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                fontSize = 11.sp,
                color = SecondaryText
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
        }
    }
}
