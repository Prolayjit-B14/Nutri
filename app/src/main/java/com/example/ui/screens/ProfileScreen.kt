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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActivityLevel
import com.example.model.Gender
import com.example.model.NutritionGoal
import com.example.ui.components.FoodSelectorBottomSheet
import com.example.ui.components.MedicalDisclaimerCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NutritionViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()

    val compFoodA by viewModel.comparisonFoodA.collectAsState()
    val compFoodB by viewModel.comparisonFoodB.collectAsState()
    val compResult by viewModel.comparisonResult.collectAsState()

    var showFoodASelector by remember { mutableStateOf(false) }
    var showFoodBSelector by remember { mutableStateOf(false) }

    // Profile form state
    var name by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var ageStr by remember(userProfile.age) { mutableStateOf(userProfile.age.toString()) }
    var heightStr by remember(userProfile.heightCm) { mutableStateOf(userProfile.heightCm.toInt().toString()) }
    var weightStr by remember(userProfile.weightKg) { mutableStateOf(userProfile.weightKg.toInt().toString()) }
    var selectedGender by remember(userProfile.gender) { mutableStateOf(userProfile.gender) }
    var selectedActivity by remember(userProfile.activityLevel) { mutableStateOf(userProfile.activityLevel) }
    var selectedGoal by remember(userProfile.goal) { mutableStateOf(userProfile.goal) }

    var targetCalStr by remember(userProfile.targetCalories) { mutableStateOf(userProfile.targetCalories.toInt().toString()) }
    var targetProtStr by remember(userProfile.targetProtein) { mutableStateOf(userProfile.targetProtein.toInt().toString()) }
    var targetCarbsStr by remember(userProfile.targetCarbs) { mutableStateOf(userProfile.targetCarbs.toInt().toString()) }
    var targetFatStr by remember(userProfile.targetFat) { mutableStateOf(userProfile.targetFat.toInt().toString()) }

    var genderExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }

    val bmr = userProfile.calculateBMR()
    val tdee = userProfile.calculateTDEE()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Clinical Profile & Targets",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Text(
                text = "Metabolic energy estimation (Mifflin-St Jeor) & dietary prescription",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }

        // BMR & TDEE Scientific Energy Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "METABOLIC ENERGY EXPENDITURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ClinicalBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Basal BMR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${bmr.toInt()} kcal",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Basal metabolism",
                                    fontSize = 10.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ClinicalTealContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Daily TDEE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ClinicalTealPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${tdee.toInt()} kcal",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalTealPrimary
                                )
                                Text(
                                    text = "Total expenditure",
                                    fontSize = 10.sp,
                                    color = ClinicalTealPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.calculateAndApplyRecommendedTargets() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalTealPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("apply_recommended_targets_button")
                    ) {
                        Text(
                            text = "Auto-Calculate Target Macro Distribution",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Biometrics & Personal Settings Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PATIENT BIOMETRICS & CLINICAL PARAMETERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                        color = SecondaryText
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Patient / User Name", color = SecondaryText) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("profile_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text("Age (yrs)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .testTag("profile_age_input"),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("Height (cm)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_height_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text("Weight (kg)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_weight_input"),
                            singleLine = true
                        )
                    }

                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedGender.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Biological Sex (Metabolic Baseline)", color = SecondaryText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            Gender.entries.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g.displayName) },
                                    onClick = {
                                        selectedGender = g
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Activity Level Dropdown
                    ExposedDropdownMenuBox(
                        expanded = activityExpanded,
                        onExpandedChange = { activityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedActivity.displayName} (${selectedActivity.description})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Physical Activity Factor", color = SecondaryText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = activityExpanded,
                            onDismissRequest = { activityExpanded = false }
                        ) {
                            ActivityLevel.entries.forEach { act ->
                                DropdownMenuItem(
                                    text = { Text("${act.displayName} (${act.description})") },
                                    onClick = {
                                        selectedActivity = act
                                        activityExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Goal Dropdown
                    ExposedDropdownMenuBox(
                        expanded = goalExpanded,
                        onExpandedChange = { goalExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedGoal.displayName} - ${selectedGoal.description}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Nutritional Objective", color = SecondaryText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = goalExpanded,
                            onDismissRequest = { goalExpanded = false }
                        ) {
                            NutritionGoal.entries.forEach { goal ->
                                DropdownMenuItem(
                                    text = { Text("${goal.displayName} - ${goal.description}") },
                                    onClick = {
                                        selectedGoal = goal
                                        goalExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = ClinicalBorder)
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "PRESCRIBED DAILY NUTRITION TARGETS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                        color = SecondaryText
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = targetCalStr,
                            onValueChange = { targetCalStr = it },
                            label = { Text("Energy (kcal)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("target_calories_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = targetProtStr,
                            onValueChange = { targetProtStr = it },
                            label = { Text("Protein (g)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("target_protein_input"),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = targetCarbsStr,
                            onValueChange = { targetCarbsStr = it },
                            label = { Text("Carbohydrates (g)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("target_carbs_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = targetFatStr,
                            onValueChange = { targetFatStr = it },
                            label = { Text("Total Fat (g)", color = SecondaryText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("target_fat_input"),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            val updated = userProfile.copy(
                                name = name,
                                age = ageStr.toIntOrNull() ?: userProfile.age,
                                heightCm = heightStr.toDoubleOrNull() ?: userProfile.heightCm,
                                weightKg = weightStr.toDoubleOrNull() ?: userProfile.weightKg,
                                gender = selectedGender,
                                activityLevel = selectedActivity,
                                goal = selectedGoal,
                                targetCalories = targetCalStr.toDoubleOrNull() ?: userProfile.targetCalories,
                                targetProtein = targetProtStr.toDoubleOrNull() ?: userProfile.targetProtein,
                                targetCarbs = targetCarbsStr.toDoubleOrNull() ?: userProfile.targetCarbs,
                                targetFat = targetFatStr.toDoubleOrNull() ?: userProfile.targetFat
                            )
                            viewModel.updateUserProfile(updated)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClinicalTealPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Profile & Prescriptions", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Food Comparison Tool
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = ClinicalTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMPARATIVE NUTRITION DENSITY ANALYSIS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = SecondaryText
                        )
                    }

                    Text(
                        text = "Side-by-side nutrient density evaluation per 100g portion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )

                    // Food A and Food B Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { showFoodASelector = true },
                            shape = RoundedCornerShape(8.dp),
                            color = ClinicalBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comparison_food_a_btn")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SAMPLE A",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = compFoodA?.name ?: "Select Food A",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText,
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            onClick = { showFoodBSelector = true },
                            shape = RoundedCornerShape(8.dp),
                            color = ClinicalBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comparison_food_b_btn")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SAMPLE B",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = compFoodB?.name ?: "Select Food B",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Comparison Results Table
                    compResult?.let { comp ->
                        HorizontalDivider(color = ClinicalBorder)

                        Text(
                            text = "COMPARISON TABLE (${comp.portionGrams.toInt()}g standard basis):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = SecondaryText
                        )

                        // Table Rows
                        ComparisonRow(label = "Energy", valA = "${comp.resultA.calories.toInt()} kcal", valB = "${comp.resultB.calories.toInt()} kcal", color = PrimaryText)
                        ComparisonRow(label = "Protein", valA = "${String.format(Locale.US, "%.1f", comp.resultA.protein)} g", valB = "${String.format(Locale.US, "%.1f", comp.resultB.protein)} g", color = NutrientProtein)
                        ComparisonRow(label = "Carbohydrates", valA = "${String.format(Locale.US, "%.1f", comp.resultA.carbs)} g", valB = "${String.format(Locale.US, "%.1f", comp.resultB.carbs)} g", color = NutrientCarbs)
                        ComparisonRow(label = "Total Fat", valA = "${String.format(Locale.US, "%.1f", comp.resultA.fat)} g", valB = "${String.format(Locale.US, "%.1f", comp.resultB.fat)} g", color = NutrientFat)
                        ComparisonRow(label = "Dietary Fiber", valA = "${String.format(Locale.US, "%.1f", comp.resultA.fiber)} g", valB = "${String.format(Locale.US, "%.1f", comp.resultB.fiber)} g", color = NutrientFiber)

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = ClinicalTealContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = ClinicalTealPrimary,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = comp.summaryNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryText,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Safety Disclaimer
        item {
            MedicalDisclaimerCard()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showFoodASelector) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { viewModel.setComparisonFoodA(it) },
            onDismiss = { showFoodASelector = false },
            title = "Choose Sample A"
        )
    }

    if (showFoodBSelector) {
        FoodSelectorBottomSheet(
            foods = allFoods,
            onFoodSelected = { viewModel.setComparisonFoodB(it) },
            onDismiss = { showFoodBSelector = false },
            title = "Choose Sample B"
        )
    }
}

@Composable
fun ComparisonRow(
    label: String,
    valA: String,
    valB: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryText,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = valA,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valB,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.weight(1f)
        )
    }
}
