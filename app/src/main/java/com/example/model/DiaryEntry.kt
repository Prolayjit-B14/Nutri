package com.example.model

enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    DINNER("Dinner");

    val iconEmoji: String get() = ""

    companion object {
        fun fromString(type: String): MealType {
            return entries.find { it.name.equals(type, ignoreCase = true) } ?: BREAKFAST
        }
    }
}

data class DiaryEntry(
    val id: Long = 0,
    val date: String, // Format: YYYY-MM-DD
    val mealType: MealType,
    val foodId: Long,
    val foodName: String,
    val foodEmoji: String = "",
    val quantity: Double,
    val unit: ServingUnit,
    val grams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val loggedAtTimestamp: Long = System.currentTimeMillis()
)

data class DailyNutritionSummary(
    val date: String,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val totalFiber: Double,
    val entries: List<DiaryEntry> = emptyList()
)

