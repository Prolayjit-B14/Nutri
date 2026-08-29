package com.example.model

enum class NutrientType(
    val id: String,
    val displayName: String,
    val unit: String,
    val emoji: String,
    val caloriesPerUnit: Double // 4 for protein/carbs, 9 for fat, 1 for cal
) {
    CALORIES("calories", "Calories", "kcal", "🔥", 1.0),
    PROTEIN("protein", "Protein", "g", "💪", 4.0),
    CARBOHYDRATES("carbs", "Carbohydrates", "g", "🍚", 4.0),
    FAT("fat", "Fat", "g", "🥑", 9.0),
    FIBER("fiber", "Fiber", "g", "🌾", 2.0);

    companion object {
        fun fromId(id: String): NutrientType {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: PROTEIN
        }
    }
}

data class NutritionResult(
    val foodItem: FoodItem,
    val inputQuantity: Double,
    val inputUnit: ServingUnit,
    val calculatedGrams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val sodiumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val ironMg: Double = 0.0,
    val vitaminCMg: Double = 0.0,
    // Macro Energy Percentages
    val proteinCaloriePercent: Double = 0.0,
    val carbsCaloriePercent: Double = 0.0,
    val fatCaloriePercent: Double = 0.0
)

data class ReverseCalculationResult(
    val foodItem: FoodItem,
    val targetNutrient: NutrientType,
    val targetAmount: Double,
    val requiredGrams: Double,
    val primaryRequiredUnitQuantity: Double,
    val primaryUnit: ServingUnit,
    val formattedQuantityString: String,
    // Complete resulting nutrition at this required quantity
    val resultingCalories: Double,
    val resultingProtein: Double,
    val resultingCarbs: Double,
    val resultingFat: Double,
    val resultingFiber: Double,
    // Educational / Practical insights
    val insightText: String,
    val isExcessiveQuantity: Boolean = false,
    val surplusWarnings: List<String> = emptyList()
)
