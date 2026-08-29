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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.MealType
import com.example.ui.components.AddCustomFoodDialog
import com.example.ui.components.LogFoodDialog
import com.example.ui.components.MacroBadge
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.NutrientCarbs
import com.example.ui.theme.NutrientFat
import com.example.ui.theme.NutrientFiber
import com.example.ui.theme.NutrientProtein
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.NutritionViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodsScreen(
    viewModel: NutritionViewModel,
    onCalculateFood: (FoodItem) -> Unit,
    onReverseFood: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredFoods by viewModel.filteredFoods.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddCustomDialog by remember { mutableStateOf(false) }
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }
    var foodToLog by remember { mutableStateOf<FoodItem?>(null) }

    Scaffold(
        containerColor = ClinicalBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCustomDialog = true },
                containerColor = ClinicalTealPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_custom_food_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Food")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Header (Scientific & Minimal)
            Column {
                Text(
                    text = "Food Database",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${filteredFoods.size} verified items with standard nutritional composition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar (Clinical Input)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search foods (e.g. Rice, Chicken, Oats, Lentils)", color = SecondaryText, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryText) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = SecondaryText)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("foods_screen_search")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Filter Bar (Clean Chips)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FoodCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.selectedCategory.value = category },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ClinicalSurface,
                            labelColor = SecondaryText,
                            selectedContainerColor = ClinicalTealPrimary,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == category,
                            borderColor = ClinicalBorder,
                            selectedBorderColor = ClinicalTealPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Food Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFoods, key = { it.id }) { food ->
                    ClinicalFoodCard(
                        food = food,
                        onClick = { selectedFoodForDetail = food }
                    )
                }

                if (filteredFoods.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No matching items found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try adjusting your search query or add a custom item.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedFoodForDetail?.let { food ->
        ClinicalFoodDetailBottomSheet(
            food = food,
            onDismiss = { selectedFoodForDetail = null },
            onCalculate = {
                selectedFoodForDetail = null
                onCalculateFood(food)
            },
            onReverse = {
                selectedFoodForDetail = null
                onReverseFood(food)
            },
            onLog = {
                selectedFoodForDetail = null
                foodToLog = food
            }
        )
    }

    // Add Custom Food Dialog
    if (showAddCustomDialog) {
        AddCustomFoodDialog(
            onDismiss = { showAddCustomDialog = false },
            onAddFood = { name, cat, unit, grams, cal, p, c, f, fib, emoji, desc ->
                viewModel.addCustomFood(name, cat, unit, grams, cal, p, c, f, fib, emoji, desc)
                showAddCustomDialog = false
            }
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
fun ClinicalFoodCard(
    food: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("food_card_${food.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Text(
                        text = food.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }

                Surface(
                    color = ClinicalTealContainer,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                ) {
                    Text(
                        text = "${food.caloriesPer100g.toInt()} kcal / 100g",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClinicalTealPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = ClinicalBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Macronutrient values row (Clean data representation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientDataItem(label = "Protein", value = "${food.proteinPer100g}g")
                NutrientDataItem(label = "Carbohydrate", value = "${food.carbsPer100g}g")
                NutrientDataItem(label = "Fat", value = "${food.fatPer100g}g")
                NutrientDataItem(label = "Fiber", value = "${food.fiberPer100g}g")
            }
        }
    }
}

@Composable
fun NutrientDataItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = SecondaryText
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalFoodDetailBottomSheet(
    food: FoodItem,
    onDismiss: () -> Unit,
    onCalculate: () -> Unit,
    onReverse: () -> Unit,
    onLog: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var servingGramsText by remember { mutableStateOf("100") }
    val servingGrams = servingGramsText.toDoubleOrNull() ?: 100.0
    val scale = servingGrams / 100.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ClinicalSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Column {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = "${food.category.displayName} • Standard Serving: ${food.servingWeightGrams.toInt()}g (${food.servingUnit.unitCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }

            if (food.description.isNotBlank()) {
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }

            HorizontalDivider(color = ClinicalBorder)

            // Serving Size Recalculator Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Portion Size (g)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                OutlinedTextField(
                    value = servingGramsText,
                    onValueChange = { servingGramsText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(110.dp)
                )
            }

            // 2x2 Nutrition Facts Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClinicalNutritionGridBox(
                        label = "ENERGY",
                        value = "${String.format(Locale.US, "%.1f", food.caloriesPer100g * scale)} kcal",
                        modifier = Modifier.weight(1f)
                    )
                    ClinicalNutritionGridBox(
                        label = "PROTEIN",
                        value = "${String.format(Locale.US, "%.1f", food.proteinPer100g * scale)} g",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClinicalNutritionGridBox(
                        label = "CARBOHYDRATE",
                        value = "${String.format(Locale.US, "%.1f", food.carbsPer100g * scale)} g",
                        modifier = Modifier.weight(1f)
                    )
                    ClinicalNutritionGridBox(
                        label = "FAT",
                        value = "${String.format(Locale.US, "%.1f", food.fatPer100g * scale)} g",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clinical Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCalculate,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Portion Calculator", maxLines = 1)
                }

                OutlinedButton(
                    onClick = onReverse,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Target Calculator", maxLines = 1)
                }
            }

            Button(
                onClick = onLog,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log to Food Diary", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ClinicalNutritionGridBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ClinicalBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        }
    }
}
