package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.NutritionCalculatorEngine
import com.example.model.FoodItem
import com.example.model.MealType
import com.example.model.ServingUnit
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.NutrientCarbs
import com.example.ui.theme.NutrientEnergy
import com.example.ui.theme.NutrientFat
import com.example.ui.theme.NutrientProtein
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogFoodDialog(
    food: FoodItem,
    initialMealType: MealType = MealType.BREAKFAST,
    onDismiss: () -> Unit,
    onLogConfirmed: (FoodItem, Double, ServingUnit, MealType) -> Unit
) {
    var quantityStr by remember { mutableStateOf("100") }
    var selectedUnit by remember { mutableStateOf(food.servingUnit) }
    var selectedMeal by remember { mutableStateOf(initialMealType) }

    var unitExpanded by remember { mutableStateOf(false) }
    var mealExpanded by remember { mutableStateOf(false) }

    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val calculated = NutritionCalculatorEngine.calculateForward(food, qty, selectedUnit)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ClinicalSurface,
        shape = RoundedCornerShape(12.dp),
        title = {
            Column {
                Text(
                    text = "Log Intake: ${food.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = "${food.caloriesPer100g.toInt()} kcal/100g (${food.category.displayName})",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Meal Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = mealExpanded,
                    onExpandedChange = { mealExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMeal.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Slot", color = SecondaryText) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealExpanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("log_food_meal_selector")
                    )
                    ExposedDropdownMenu(
                        expanded = mealExpanded,
                        onDismissRequest = { mealExpanded = false }
                    ) {
                        MealType.entries.forEach { meal ->
                            DropdownMenuItem(
                                text = { Text(meal.displayName) },
                                onClick = {
                                    selectedMeal = meal
                                    mealExpanded = false
                                }
                            )
                        }
                    }
                }

                // Quantity & Unit
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity", color = SecondaryText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("log_food_quantity_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedUnit.unitCode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit", color = SecondaryText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            ServingUnit.entries.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.displayName) },
                                    onClick = {
                                        selectedUnit = unit
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Preview Nutrition Values Card
                Surface(
                    color = ClinicalBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CALCULATED PORTION (${String.format(Locale.US, "%.0f", calculated.calculatedGrams)} g):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${calculated.calories.toInt()} kcal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = "P: ${String.format(Locale.US, "%.1f", calculated.protein)}g",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NutrientProtein
                            )
                            Text(
                                text = "C: ${String.format(Locale.US, "%.1f", calculated.carbs)}g",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NutrientCarbs
                            )
                            Text(
                                text = "F: ${String.format(Locale.US, "%.1f", calculated.fat)}g",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NutrientFat
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (qty > 0) {
                        onLogConfirmed(food, qty, selectedUnit, selectedMeal)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalTealPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_log_food_button")
            ) {
                Text("Log Intake", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}
