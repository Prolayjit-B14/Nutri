package com.example.model

enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other")
}

enum class ActivityLevel(val displayName: String, val multiplier: Double, val description: String) {
    SEDENTARY("Sedentary", 1.2, "Little or no exercise, desk job"),
    LIGHT("Lightly Active", 1.375, "Light exercise 1-3 days/week"),
    MODERATE("Moderately Active", 1.55, "Moderate exercise 3-5 days/week"),
    VERY_ACTIVE("Very Active", 1.725, "Hard exercise 6-7 days/week"),
    EXTRA_ACTIVE("Extremely Active", 1.9, "Physical job or intense training 2x/day")
}

enum class NutritionGoal(
    val displayName: String,
    val description: String,
    val calorieAdjustment: Int,
    val proteinPerKg: Double,
    val fatCalorieRatio: Double // % of calories from fat
) {
    GENERAL_HEALTH("General Nutrition", "Balanced daily nourishment and vitality", 0, 1.2, 0.25),
    FAT_LOSS("Fat Loss", "Gradual sustainable fat reduction (500 kcal deficit)", -500, 2.0, 0.25),
    WEIGHT_MANAGEMENT("Weight Management", "Maintain stable weight and optimize energy", 0, 1.4, 0.25),
    MUSCLE_BUILDING("Muscle Building", "Lean mass growth with caloric surplus (+300 kcal)", 300, 2.0, 0.25),
    MAINTENANCE("Maintenance", "Sustain current body composition and performance", 0, 1.5, 0.25),
    CUSTOM("Custom Goal", "Set your own exact calorie and macronutrient targets", 0, 1.5, 0.25)
}

data class UserProfile(
    val id: Long = 1,
    val name: String = "User",
    val age: Int = 26,
    val gender: Gender = Gender.MALE,
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goal: NutritionGoal = NutritionGoal.MUSCLE_BUILDING,
    // Custom overrides or calculated targets
    val targetCalories: Double = 2200.0,
    val targetProtein: Double = 140.0,
    val targetCarbs: Double = 250.0,
    val targetFat: Double = 70.0,
    val targetFiber: Double = 30.0,
    val targetWaterMl: Double = 3000.0
) {
    /**
     * Calculates Basal Metabolic Rate using Mifflin-St Jeor equation
     */
    fun calculateBMR(): Double {
        return if (gender == Gender.FEMALE) {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161.0
        } else {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5.0
        }
    }

    /**
     * Calculates Total Daily Energy Expenditure (TDEE)
     */
    fun calculateTDEE(): Double {
        return calculateBMR() * activityLevel.multiplier
    }

    /**
     * Generates recommended macro targets based on goal and biometrics
     */
    fun calculateRecommendedTargets(): UserProfile {
        if (goal == NutritionGoal.CUSTOM) return this

        val tdee = calculateTDEE()
        val recCalories = (tdee + goal.calorieAdjustment).coerceAtLeast(1200.0)
        
        // Protein: based on bodyweight in kg
        val recProtein = (weightKg * goal.proteinPerKg).coerceAtLeast(50.0)
        val proteinCalories = recProtein * 4.0

        // Fat: 25% of total calories (9 kcal/g)
        val fatCalories = recCalories * goal.fatCalorieRatio
        val recFat = (fatCalories / 9.0).coerceAtLeast(30.0)

        // Carbs: Remaining calories (4 kcal/g)
        val remainingCalories = (recCalories - proteinCalories - fatCalories).coerceAtLeast(200.0)
        val recCarbs = remainingCalories / 4.0

        val recFiber = (recCalories / 1000.0 * 14.0).coerceIn(25.0, 45.0)
        val recWater = weightKg * 35.0 // 35 ml per kg

        return this.copy(
            targetCalories = Math.round(recCalories).toDouble(),
            targetProtein = Math.round(recProtein).toDouble(),
            targetCarbs = Math.round(recCarbs).toDouble(),
            targetFat = Math.round(recFat).toDouble(),
            targetFiber = Math.round(recFiber).toDouble(),
            targetWaterMl = Math.round(recWater).toDouble()
        )
    }
}
