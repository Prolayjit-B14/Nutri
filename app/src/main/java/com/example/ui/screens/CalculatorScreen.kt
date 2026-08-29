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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.FoodItem
import com.example.model.NutrientType
import com.example.model.ServingUnit
import com.example.ui.components.FoodSelectorBottomSheet
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.NutritionResultCard
import com.example.ui.components.ReverseResultCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: NutritionViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    val allFoods by viewModel.allFoods.collectAsState()

    // Forward Calculator State
    val selectedFoodForward by viewModel.selectedFoodForForward.collectAsState()
    val forwardQuantity by viewModel.forwardQuantity.collectAsState()
    val forwardUnit by viewModel.forwardUnit.collectAsState()
    val forwardResult by viewModel.forwardResult.collectAsState()

    // Reverse Calculator State
    val selectedFoodReverse by viewModel.selectedFoodForReverse.collectAsState()
    val reverseNutrient by viewModel.reverseTargetNutrient.collectAsState()
    val reverseAmount by viewModel.reverseTargetAmount.collectAsState()
    val reverseResult by viewModel.reverseResult.collectAsState()

    var showFoodSelectorForForward by remember { mutableStateOf(false) }
    var showFoodSelectorForReverse by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Screen Top Bar (Scientific Header)
        Column {
            Text(
                text = "Nutrition Calculator",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Two-way precision portion and macronutrient target analysis",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Two-way Mode Tabs (Clinical Segmented Control)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ClinicalSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { selectedTab = 0 },
                    color = if (selectedTab == 0) ClinicalTealPrimary else ClinicalSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .testTag("tab_forward_calculator")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Food → Nutrition",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == 0) androidx.compose.ui.graphics.Color.White else SecondaryText
                        )
                    }
                }

                Surface(
                    onClick = { selectedTab = 1 },
                    color = if (selectedTab == 1) ClinicalTealPrimary else ClinicalSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .testTag("tab_reverse_calculator")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Target → Quantity",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == 1) androidx.compose.ui.graphics.Color.White else SecondaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                // ==========================================
                // 1. FORWARD CALCULATOR (Food -> Nutrition)
                // ==========================================
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "1. SELECT FOOD ITEM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )

                            // Food Picker Selector
                            Surface(
                                onClick = { showFoodSelectorForForward = true },
                                shape = RoundedCornerShape(8.dp),
                                color = ClinicalBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forward_food_selector_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = selectedFoodForward?.name ?: "Select a food item...",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (selectedFoodForward != null) PrimaryText else SecondaryText
                                        )
                                        if (selectedFoodForward != null) {
                                            Text(
                                                text = "${selectedFoodForward?.category?.displayName} • ${selectedFoodForward?.caloriesPer100g?.toInt()} kcal / 100g",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SecondaryText
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select",
                                        tint = SecondaryText
                                    )
                                }
                            }

                            HorizontalDivider(color = ClinicalBorder)

                            Text(
                                text = "2. QUANTITY & SERVING UNIT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )

                            // Quantity Text Field
                            OutlinedTextField(
                                value = forwardQuantity,
                                onValueChange = { viewModel.setForwardQuantity(it) },
                                label = { Text("Quantity", color = SecondaryText) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forward_quantity_input"),
                                singleLine = true
                            )

                            // Quick Quantity Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("50", "100", "150", "200", "250", "500").forEach { preset ->
                                    Surface(
                                        onClick = { viewModel.setForwardQuantity(preset) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (forwardQuantity == preset) ClinicalTealPrimary else ClinicalBackground,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (forwardQuantity == preset) ClinicalTealPrimary else ClinicalBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = preset,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (forwardQuantity == preset) androidx.compose.ui.graphics.Color.White else PrimaryText
                                            )
                                        }
                                    }
                                }
                            }

                            // Serving Unit Filter Chips
                            Text(
                                text = "Serving Unit:",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(ServingUnit.entries) { unit ->
                                    FilterChip(
                                        selected = forwardUnit == unit,
                                        onClick = { viewModel.setForwardUnit(unit) },
                                        label = { Text(unit.displayName, fontSize = 13.sp) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = ClinicalBackground,
                                            labelColor = SecondaryText,
                                            selectedContainerColor = ClinicalTealPrimary,
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = forwardUnit == unit,
                                            borderColor = ClinicalBorder,
                                            selectedBorderColor = ClinicalTealPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Forward Result Card
                forwardResult?.let { result ->
                    item {
                        NutritionResultCard(
                            result = result,
                            onAddToDiary = { mealType ->
                                viewModel.logForwardResultToDiary(mealType)
                            }
                        )
                    }
                }
            } else {
                // ==========================================
                // 2. REVERSE CALCULATOR (Target -> Quantity)
                // ==========================================
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "1. SELECT TARGET NUTRIENT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )

                            // Target Nutrient Chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(NutrientType.entries) { nutrient ->
                                    val isSelected = reverseNutrient == nutrient
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setReverseTargetNutrient(nutrient) },
                                        label = {
                                            Text(
                                                text = nutrient.displayName,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = ClinicalBackground,
                                            labelColor = SecondaryText,
                                            selectedContainerColor = ClinicalTealPrimary,
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = ClinicalBorder,
                                            selectedBorderColor = ClinicalTealPrimary
                                        ),
                                        modifier = Modifier.testTag("reverse_nutrient_${nutrient.id}")
                                    )
                                }
                            }

                            // Target Amount Input
                            OutlinedTextField(
                                value = reverseAmount,
                                onValueChange = { viewModel.setReverseTargetAmount(it) },
                                label = { Text("Target Amount (${reverseNutrient.unit})", color = SecondaryText) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reverse_target_amount_input"),
                                singleLine = true
                            )

                            // Quick Amount Increment Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = when (reverseNutrient) {
                                    NutrientType.CALORIES -> listOf("300", "500", "750", "1000", "1500")
                                    NutrientType.PROTEIN -> listOf("20", "30", "40", "50", "75", "100")
                                    NutrientType.CARBOHYDRATES -> listOf("50", "100", "150", "200")
                                    NutrientType.FAT -> listOf("15", "25", "40", "60")
                                    NutrientType.FIBER -> listOf("10", "15", "25", "35")
                                }
                                presets.forEach { preset ->
                                    Surface(
                                        onClick = { viewModel.setReverseTargetAmount(preset) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (reverseAmount == preset) ClinicalTealPrimary else ClinicalBackground,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (reverseAmount == preset) ClinicalTealPrimary else ClinicalBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = preset,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (reverseAmount == preset) androidx.compose.ui.graphics.Color.White else PrimaryText
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = ClinicalBorder)

                            Text(
                                text = "2. SELECT SOURCE FOOD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )

                            // Food Selector for Reverse
                            Surface(
                                onClick = { showFoodSelectorForReverse = true },
                                shape = RoundedCornerShape(8.dp),
                                color = ClinicalBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reverse_food_selector_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = selectedFoodReverse?.name ?: "Select a food to hit target...",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (selectedFoodReverse != null) PrimaryText else SecondaryText
                                        )
                                        if (selectedFoodReverse != null) {
                                            Text(
                                                text = "P: ${selectedFoodReverse?.proteinPer100g}g • C: ${selectedFoodReverse?.carbsPer100g}g • F: ${selectedFoodReverse?.fatPer100g}g (/100g)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SecondaryText
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select",
                                        tint = SecondaryText
                                    )
                                }
                            }
                        }
                    }
                }

                // Reverse Calculation Result Card
                reverseResult?.let { result ->
                    item {
                        ReverseResultCard(
                            result = result,
                            onAddToDiary = { mealType ->
                                viewModel.logReverseResultToDiary(mealType)
                            }
                        )
                    }
                }
            }

            item {
                MedicalDisclaimerCard()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Food Selectors
    if (showFoodSelectorForForward) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { viewModel.selectFoodForForward(it) },
            onDismiss = { showFoodSelectorForForward = false },
            title = "Select Food Item"
        )
    }

    if (showFoodSelectorForReverse) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { viewModel.selectFoodForReverse(it) },
            onDismiss = { showFoodSelectorForReverse = false },
            title = "Select Food for Target"
        )
    }
}
