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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MealType
import com.example.model.ReverseCalculationResult
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
fun ReverseResultCard(
    result: ReverseCalculationResult,
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
            .testTag("reverse_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Target Badge
            Surface(
                color = ClinicalTealContainer,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARGET: ${String.format(Locale.US, "%.1f", result.targetAmount)} ${result.targetNutrient.unit} of ${result.targetNutrient.displayName.uppercase(Locale.US)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        color = ClinicalTealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Required Quantity Display
            Column {
                Text(
                    text = "Required Portion",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = result.formattedQuantityString,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalTealPrimary
                )
                Text(
                    text = "of ${result.foodItem.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = ClinicalBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Resulting Nutrition Breakdown at this quantity
            Text(
                text = "ASSOCIATED NUTRIENT INTAKE",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroBadge(
                    label = "Calories",
                    value = "${String.format(Locale.US, "%.0f", result.resultingCalories)} kcal",
                    color = PrimaryText,
                    bgColor = ClinicalTealContainer,
                    modifier = Modifier.weight(1f)
                )
                MacroBadge(
                    label = "Protein",
                    value = "${String.format(Locale.US, "%.1f", result.resultingProtein)} g",
                    color = NutrientProtein,
                    bgColor = ClinicalBackground,
                    modifier = Modifier.weight(1f)
                )
                MacroBadge(
                    label = "Carbs",
                    value = "${String.format(Locale.US, "%.1f", result.resultingCarbs)} g",
                    color = NutrientCarbs,
                    bgColor = ClinicalBackground,
                    modifier = Modifier.weight(1f)
                )
                MacroBadge(
                    label = "Fat",
                    value = "${String.format(Locale.US, "%.1f", result.resultingFat)} g",
                    color = NutrientFat,
                    bgColor = ClinicalBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            // Surplus warnings & educational insight
            if (result.surplusWarnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ClinicalBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, ClinicalBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Clinical Warning",
                            tint = NutrientFat,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nutritional Co-Factor Load",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    result.surplusWarnings.forEach { warning ->
                        Text(
                            text = "• $warning",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ClinicalBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, ClinicalBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ClinicalTealPrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = result.insightText,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add to Diary
            if (result.requiredGrams > 0) {
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
                                        .testTag("reverse_log_meal_${meal.name.lowercase()}")
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
                            .testTag("add_reverse_to_diary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Calculated Portion",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Log Portion to Food Diary",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
