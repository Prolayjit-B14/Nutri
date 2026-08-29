package com.example.engine

import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.NutrientType
import com.example.model.NutritionResult
import com.example.model.ReverseCalculationResult
import com.example.model.ServingUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

object NutritionCalculatorEngine {

    /**
     * Forward Calculation: Food + Quantity + Unit -> Complete Nutrition Result
     */
    fun calculateForward(
        food: FoodItem,
        quantity: Double,
        unit: ServingUnit
    ): NutritionResult {
        val safeQuantity = quantity.coerceAtLeast(0.0)
        val calculatedGrams = food.convertToGrams(safeQuantity, unit)
        val factor = calculatedGrams / 100.0

        val calories = food.caloriesPer100g * factor
        val protein = food.proteinPer100g * factor
        val carbs = food.carbsPer100g * factor
        val fat = food.fatPer100g * factor
        val fiber = food.fiberPer100g * factor
        val sodium = food.sodiumMg * factor
        val potassium = food.potassiumMg * factor
        val calcium = food.calciumMg * factor
        val iron = food.ironMg * factor
        val vitaminC = food.vitaminCMg * factor

        val totalMacroCalories = (protein * 4.0) + (carbs * 4.0) + (fat * 9.0)
        val pPct = if (totalMacroCalories > 0) (protein * 4.0 / totalMacroCalories) * 100.0 else 0.0
        val cPct = if (totalMacroCalories > 0) (carbs * 4.0 / totalMacroCalories) * 100.0 else 0.0
        val fPct = if (totalMacroCalories > 0) (fat * 9.0 / totalMacroCalories) * 100.0 else 0.0

        return NutritionResult(
            foodItem = food,
            inputQuantity = safeQuantity,
            inputUnit = unit,
            calculatedGrams = calculatedGrams,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            sodiumMg = sodium,
            potassiumMg = potassium,
            calciumMg = calcium,
            ironMg = iron,
            vitaminCMg = vitaminC,
            proteinCaloriePercent = pPct,
            carbsCaloriePercent = cPct,
            fatCaloriePercent = fPct
        )
    }

    /**
     * Reverse Calculation: Target Nutrient & Amount + Selected Food -> Required Food Quantity + Complete Resulting Nutrition
     */
    fun calculateReverse(
        food: FoodItem,
        targetNutrient: NutrientType,
        targetAmount: Double
    ): ReverseCalculationResult {
        val safeTarget = targetAmount.coerceAtLeast(0.1)

        val nutrientPer100g = when (targetNutrient) {
            NutrientType.CALORIES -> food.caloriesPer100g
            NutrientType.PROTEIN -> food.proteinPer100g
            NutrientType.CARBOHYDRATES -> food.carbsPer100g
            NutrientType.FAT -> food.fatPer100g
            NutrientType.FIBER -> food.fiberPer100g
        }

        if (nutrientPer100g <= 0.001) {
            return ReverseCalculationResult(
                foodItem = food,
                targetNutrient = targetNutrient,
                targetAmount = safeTarget,
                requiredGrams = 0.0,
                primaryRequiredUnitQuantity = 0.0,
                primaryUnit = food.servingUnit,
                formattedQuantityString = "Unavailable (contains 0 ${targetNutrient.displayName})",
                resultingCalories = 0.0,
                resultingProtein = 0.0,
                resultingCarbs = 0.0,
                resultingFat = 0.0,
                resultingFiber = 0.0,
                insightText = "${food.name} contains virtually 0 ${targetNutrient.displayName.lowercase(Locale.ROOT)}, so reaching ${safeTarget} ${targetNutrient.unit} is not possible with this food alone.",
                isExcessiveQuantity = true,
                surplusWarnings = listOf("This food does not contain ${targetNutrient.displayName}.")
            )
        }

        // Core Reverse Formula: Required Quantity = (Target / Nutrient_per_100g) * 100
        val requiredGrams = (safeTarget / nutrientPer100g) * 100.0
        val factor = requiredGrams / 100.0

        val resultingCalories = food.caloriesPer100g * factor
        val resultingProtein = food.proteinPer100g * factor
        val resultingCarbs = food.carbsPer100g * factor
        val resultingFat = food.fatPer100g * factor
        val resultingFiber = food.fiberPer100g * factor

        // Format user-friendly quantity
        val primaryUnit = when {
            food.servingUnit == ServingUnit.EGG -> ServingUnit.EGG
            food.servingUnit == ServingUnit.ROTI -> ServingUnit.ROTI
            food.servingUnit == ServingUnit.SLICE -> ServingUnit.SLICE
            food.servingUnit == ServingUnit.SCOOP -> ServingUnit.SCOOP
            food.servingUnit == ServingUnit.PIECE -> ServingUnit.PIECE
            requiredGrams >= 1000.0 && food.servingUnit == ServingUnit.MILLILITER -> ServingUnit.LITER
            requiredGrams >= 1000.0 -> ServingUnit.KILOGRAM
            food.servingUnit == ServingUnit.MILLILITER -> ServingUnit.MILLILITER
            else -> ServingUnit.GRAM
        }

        val primaryUnitQuantity = food.convertFromGrams(requiredGrams, primaryUnit)

        val formattedQuantity = when (primaryUnit) {
            ServingUnit.KILOGRAM -> String.format(Locale.US, "%.2f kg", primaryUnitQuantity)
            ServingUnit.LITER -> String.format(Locale.US, "%.2f L", primaryUnitQuantity)
            ServingUnit.EGG -> {
                val wholeEggs = round(primaryUnitQuantity * 10) / 10.0
                String.format(Locale.US, "%.1f eggs (≈ %.0f g)", wholeEggs, requiredGrams)
            }
            ServingUnit.ROTI -> {
                val rotis = round(primaryUnitQuantity * 10) / 10.0
                String.format(Locale.US, "%.1f rotis (≈ %.0f g)", rotis, requiredGrams)
            }
            ServingUnit.PIECE, ServingUnit.SLICE, ServingUnit.SCOOP -> {
                val pieces = round(primaryUnitQuantity * 10) / 10.0
                String.format(Locale.US, "%.1f %ss (≈ %.0f g)", pieces, primaryUnit.unitCode, requiredGrams)
            }
            ServingUnit.MILLILITER -> String.format(Locale.US, "%.0f ml", primaryUnitQuantity)
            else -> String.format(Locale.US, "%.0f g", requiredGrams)
        }

        // Generate educational insights & macro surplus flags
        val surplusWarnings = mutableListOf<String>()
        val isExcessive = requiredGrams > 1000.0 || resultingCalories > 2500.0

        if (targetNutrient != NutrientType.CALORIES && resultingCalories > 1500.0) {
            surplusWarnings.add(String.format(Locale.US, "High Calorie Load: Consumes %.0f kcal (%.0f%% of typical 2,000 kcal day)", resultingCalories, (resultingCalories / 2000.0) * 100.0))
        }
        if (targetNutrient != NutrientType.CARBOHYDRATES && resultingCarbs > 200.0) {
            surplusWarnings.add(String.format(Locale.US, "Carbohydrate Surge: You would also ingest %.1fg of carbs", resultingCarbs))
        }
        if (targetNutrient != NutrientType.FAT && resultingFat > 60.0) {
            surplusWarnings.add(String.format(Locale.US, "High Fat Intake: You would also consume %.1fg of fat", resultingFat))
        }
        if (targetNutrient != NutrientType.PROTEIN && resultingProtein > 100.0) {
            surplusWarnings.add(String.format(Locale.US, "Protein Concentration: Resulting protein is %.1fg", resultingProtein))
        }

        val insightText = buildString {
            append("To obtain ${String.format(Locale.US, "%.1f", safeTarget)} ${targetNutrient.unit} of ${targetNutrient.displayName} from ${food.name}, you need approximately $formattedQuantity.")
            if (isExcessive) {
                append(" Notice that relying solely on ${food.name} for this target results in ${String.format(Locale.US, "%.0f", resultingCalories)} kcal and significant companion macronutrients.")
            }
        }

        return ReverseCalculationResult(
            foodItem = food,
            targetNutrient = targetNutrient,
            targetAmount = safeTarget,
            requiredGrams = requiredGrams,
            primaryRequiredUnitQuantity = primaryUnitQuantity,
            primaryUnit = primaryUnit,
            formattedQuantityString = formattedQuantity,
            resultingCalories = resultingCalories,
            resultingProtein = resultingProtein,
            resultingCarbs = resultingCarbs,
            resultingFat = resultingFat,
            resultingFiber = resultingFiber,
            insightText = insightText,
            isExcessiveQuantity = isExcessive,
            surplusWarnings = surplusWarnings
        )
    }

    /**
     * Comparison Result between 2 foods for a given portion weight (default 100g)
     */
    data class ComparisonResult(
        val foodA: FoodItem,
        val foodB: FoodItem,
        val portionGrams: Double,
        val resultA: NutritionResult,
        val resultB: NutritionResult,
        val calorieDelta: Double,
        val proteinDelta: Double,
        val carbsDelta: Double,
        val fatDelta: Double,
        val fiberDelta: Double,
        val summaryNote: String
    )

    fun compareFoods(foodA: FoodItem, foodB: FoodItem, portionGrams: Double = 100.0): ComparisonResult {
        val resultA = calculateForward(foodA, portionGrams, ServingUnit.GRAM)
        val resultB = calculateForward(foodB, portionGrams, ServingUnit.GRAM)

        val calDiff = resultA.calories - resultB.calories
        val pDiff = resultA.protein - resultB.protein
        val cDiff = resultA.carbs - resultB.carbs
        val fDiff = resultA.fat - resultB.fat
        val fibDiff = resultA.fiber - resultB.fiber

        val note = buildString {
            if (pDiff > 2.0) {
                append("${foodA.name} offers ${String.format(Locale.US, "%.1f", pDiff)}g more protein per ${portionGrams.toInt()}g. ")
            } else if (pDiff < -2.0) {
                append("${foodB.name} offers ${String.format(Locale.US, "%.1f", abs(pDiff))}g more protein per ${portionGrams.toInt()}g. ")
            }
            if (calDiff < -30.0) {
                append("${foodA.name} is significantly lower in calories (${String.format(Locale.US, "%.0f", abs(calDiff))} kcal less).")
            } else if (calDiff > 30.0) {
                append("${foodB.name} is lower in calories (${String.format(Locale.US, "%.0f", calDiff)} kcal less).")
            }
        }

        return ComparisonResult(
            foodA = foodA,
            foodB = foodB,
            portionGrams = portionGrams,
            resultA = resultA,
            resultB = resultB,
            calorieDelta = calDiff,
            proteinDelta = pDiff,
            carbsDelta = cDiff,
            fatDelta = fDiff,
            fiberDelta = fibDiff,
            summaryNote = note.ifEmpty { "Both foods have comparable nutritional profiles for this portion size." }
        )
    }

    /**
     * Algorithmic Meal Combination Recommender ("What Should I Eat?")
     * Minimizes difference between target requirements and suggested meal components
     */
    data class MealSuggestion(
        val title: String,
        val description: String,
        val items: List<MealItemPortion>,
        val totalCalories: Double,
        val totalProtein: Double,
        val totalCarbs: Double,
        val totalFat: Double,
        val totalFiber: Double,
        val calorieDelta: Double,
        val proteinDelta: Double,
        val carbsDelta: Double,
        val fatDelta: Double
    )

    data class MealItemPortion(
        val foodItem: FoodItem,
        val quantity: Double,
        val unit: ServingUnit,
        val grams: Double,
        val calories: Double,
        val protein: Double,
        val carbs: Double,
        val fat: Double
    )

    fun generateDeterministicMealSuggestions(
        allFoods: List<FoodItem>,
        targetCalories: Double,
        targetProtein: Double,
        targetCarbs: Double,
        targetFat: Double
    ): List<MealSuggestion> {
        val safeCal = targetCalories.coerceIn(200.0, 1500.0)
        val safeP = targetProtein.coerceIn(10.0, 120.0)
        val safeC = targetCarbs.coerceIn(10.0, 200.0)
        val safeF = targetFat.coerceIn(5.0, 80.0)

        // Find best categorized foods in dataset
        val proteinFoods = allFoods.filter { it.proteinPer100g >= 8.0 && it.category != FoodCategory.GRAINS }
        val grainFoods = allFoods.filter { it.category == FoodCategory.GRAINS || it.category == FoodCategory.BAKERY_BREAD || it.category == FoodCategory.PULSES_LEGUMES }
        val veggieFoods = allFoods.filter { it.category == FoodCategory.VEGETABLES || it.category == FoodCategory.FRUITS }
        val healthyFats = allFoods.filter { it.category == FoodCategory.NUTS_SEEDS || it.fatPer100g >= 15.0 }

        val suggestions = mutableListOf<MealSuggestion>()

        // Preset 1: High Protein Lean Bowl (e.g. Chicken/Paneer + Rice/Roti + Veggies)
        val primaryProt1 = proteinFoods.find { it.name.contains("Chicken", ignoreCase = true) }
            ?: proteinFoods.find { it.name.contains("Paneer", ignoreCase = true) }
            ?: proteinFoods.firstOrNull()

        val primaryGrain1 = grainFoods.find { it.name.contains("Rice", ignoreCase = true) && !it.name.contains("Brown", ignoreCase = true) }
            ?: grainFoods.find { it.name.contains("Roti", ignoreCase = true) }
            ?: grainFoods.firstOrNull()

        val primaryVeg1 = veggieFoods.find { it.name.contains("Vegetables", ignoreCase = true) || it.name.contains("Spinach", ignoreCase = true) }
            ?: veggieFoods.firstOrNull()

        if (primaryProt1 != null && primaryGrain1 != null) {
            val s1 = buildMealSuggestion(
                title = "Balanced Power Plate",
                description = "Optimized combination of lean protein, energy grains, and micronutrient-dense vegetables",
                primaryProtein = primaryProt1,
                primaryGrain = primaryGrain1,
                primaryVeg = primaryVeg1,
                targetCalories = safeCal,
                targetProtein = safeP,
                targetCarbs = safeC,
                targetFat = safeF
            )
            suggestions.add(s1)
        }

        // Preset 2: Vegetarian / Indian Comfort (e.g. Dal / Paneer / Egg + Roti / Brown Rice)
        val primaryProt2 = proteinFoods.find { it.name.contains("Egg", ignoreCase = true) }
            ?: proteinFoods.find { it.name.contains("Soya", ignoreCase = true) || it.name.contains("Tofu", ignoreCase = true) }
            ?: proteinFoods.find { it.name.contains("Paneer", ignoreCase = true) }

        val primaryGrain2 = grainFoods.find { it.name.contains("Roti", ignoreCase = true) || it.name.contains("Dal", ignoreCase = true) }
            ?: grainFoods.find { it.name.contains("Brown Rice", ignoreCase = true) }

        val primaryVeg2 = veggieFoods.find { it.name.contains("Salad", ignoreCase = true) || it.name.contains("Broccoli", ignoreCase = true) }
            ?: veggieFoods.firstOrNull()

        if (primaryProt2 != null && primaryGrain2 != null) {
            val s2 = buildMealSuggestion(
                title = "Nourishing Wellness Bowl",
                description = "Plant/egg protein balanced with complex carbohydrates and crisp greens",
                primaryProtein = primaryProt2,
                primaryGrain = primaryGrain2,
                primaryVeg = primaryVeg2,
                targetCalories = safeCal,
                targetProtein = safeP,
                targetCarbs = safeC,
                targetFat = safeF
            )
            suggestions.add(s2)
        }

        // Preset 3: Light & Quick Macro Meal (e.g. Fish/Curd/Oats + Fruit/Nuts)
        val primaryProt3 = proteinFoods.find { it.name.contains("Fish", ignoreCase = true) || it.name.contains("Curd", ignoreCase = true) }
            ?: proteinFoods.find { it.name.contains("Milk", ignoreCase = true) }
            ?: proteinFoods.getOrNull(1)

        val primaryGrain3 = grainFoods.find { it.name.contains("Oats", ignoreCase = true) || it.name.contains("Bread", ignoreCase = true) }
            ?: grainFoods.getOrNull(1)

        val primaryVeg3 = veggieFoods.find { it.name.contains("Banana", ignoreCase = true) || it.name.contains("Apple", ignoreCase = true) }
            ?: veggieFoods.firstOrNull()

        if (primaryProt3 != null && primaryGrain3 != null) {
            val s3 = buildMealSuggestion(
                title = "Quick Digestible Macro Fuel",
                description = "Easily digestible lean nutrition perfect for active recovery and steady energy",
                primaryProtein = primaryProt3,
                primaryGrain = primaryGrain3,
                primaryVeg = primaryVeg3,
                targetCalories = safeCal,
                targetProtein = safeP,
                targetCarbs = safeC,
                targetFat = safeF
            )
            suggestions.add(s3)
        }

        return suggestions
    }

    private fun buildMealSuggestion(
        title: String,
        description: String,
        primaryProtein: FoodItem,
        primaryGrain: FoodItem,
        primaryVeg: FoodItem?,
        targetCalories: Double,
        targetProtein: Double,
        targetCarbs: Double,
        targetFat: Double
    ): MealSuggestion {
        // Calculate protein portion to cover ~60% of target protein
        val proteinTarget = targetProtein * 0.65
        val proteinGrams = ((proteinTarget / primaryProtein.proteinPer100g) * 100.0).coerceIn(50.0, 350.0)

        // Calculate grain portion to cover ~70% of target carbs
        val grainTarget = targetCarbs * 0.70
        val grainGrams = ((grainTarget / primaryGrain.carbsPer100g) * 100.0).coerceIn(50.0, 400.0)

        val vegGrams = if (primaryVeg != null) 100.0 else 0.0

        val items = mutableListOf<MealItemPortion>()

        val protNut = calculateForward(primaryProtein, proteinGrams, ServingUnit.GRAM)
        val protUnit = if (primaryProtein.servingUnit == ServingUnit.EGG) ServingUnit.EGG else ServingUnit.GRAM
        val protQty = primaryProtein.convertFromGrams(proteinGrams, protUnit)
        items.add(MealItemPortion(primaryProtein, round(protQty * 10) / 10.0, protUnit, proteinGrams, protNut.calories, protNut.protein, protNut.carbs, protNut.fat))

        val grainNut = calculateForward(primaryGrain, grainGrams, ServingUnit.GRAM)
        val grainUnit = if (primaryGrain.servingUnit == ServingUnit.ROTI) ServingUnit.ROTI else ServingUnit.GRAM
        val grainQty = primaryGrain.convertFromGrams(grainGrams, grainUnit)
        items.add(MealItemPortion(primaryGrain, round(grainQty * 10) / 10.0, grainUnit, grainGrams, grainNut.calories, grainNut.protein, grainNut.carbs, grainNut.fat))

        if (primaryVeg != null && vegGrams > 0) {
            val vegNut = calculateForward(primaryVeg, vegGrams, ServingUnit.GRAM)
            items.add(MealItemPortion(primaryVeg, vegGrams, ServingUnit.GRAM, vegGrams, vegNut.calories, vegNut.protein, vegNut.carbs, vegNut.fat))
        }

        val totalCal = items.sumOf { it.calories }
        val totalP = items.sumOf { it.protein }
        val totalC = items.sumOf { it.carbs }
        val totalF = items.sumOf { it.fat }
        val totalFib = (primaryProtein.fiberPer100g * proteinGrams / 100.0) + (primaryGrain.fiberPer100g * grainGrams / 100.0) + ((primaryVeg?.fiberPer100g ?: 0.0) * vegGrams / 100.0)

        return MealSuggestion(
            title = title,
            description = description,
            items = items,
            totalCalories = totalCal,
            totalProtein = totalP,
            totalCarbs = totalC,
            totalFat = totalF,
            totalFiber = totalFib,
            calorieDelta = totalCal - targetCalories,
            proteinDelta = totalP - targetProtein,
            carbsDelta = totalC - targetCarbs,
            fatDelta = totalF - targetFat
        )
    }
}
