package com.example.model

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val category: FoodCategory,
    val servingUnit: ServingUnit = ServingUnit.GRAM,
    val servingWeightGrams: Double = 100.0,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double = 0.0,
    val sodiumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val ironMg: Double = 0.0,
    val vitaminCMg: Double = 0.0,
    val isCustom: Boolean = false,
    val emoji: String = "",
    val description: String = ""
) {
    /**
     * Converts a given quantity in the specified unit to normalized grams
     */
    fun convertToGrams(quantity: Double, unit: ServingUnit): Double {
        return when (unit) {
            ServingUnit.GRAM -> quantity
            ServingUnit.KILOGRAM -> quantity * 1000.0
            ServingUnit.MILLILITER -> quantity // 1ml ≈ 1g for liquids
            ServingUnit.LITER -> quantity * 1000.0
            ServingUnit.PIECE, ServingUnit.EGG, ServingUnit.ROTI, ServingUnit.SLICE, ServingUnit.SCOOP -> {
                // If item has specific serving weight, use that, else fallback to unit default
                quantity * (if (servingWeightGrams > 0) servingWeightGrams else unit.defaultGrams)
            }
            ServingUnit.CUP -> quantity * (if (category == FoodCategory.GRAINS) 150.0 else 120.0)
            ServingUnit.BOWL -> quantity * 150.0
            ServingUnit.SERVING -> quantity * servingWeightGrams
            ServingUnit.TABLESPOON -> quantity * 15.0
        }
    }

    /**
     * Converts normalized grams to unit quantity
     */
    fun convertFromGrams(grams: Double, unit: ServingUnit): Double {
        return when (unit) {
            ServingUnit.GRAM -> grams
            ServingUnit.KILOGRAM -> grams / 1000.0
            ServingUnit.MILLILITER -> grams
            ServingUnit.LITER -> grams / 1000.0
            ServingUnit.PIECE, ServingUnit.EGG, ServingUnit.ROTI, ServingUnit.SLICE, ServingUnit.SCOOP -> {
                val weight = if (servingWeightGrams > 0) servingWeightGrams else unit.defaultGrams
                grams / weight
            }
            ServingUnit.CUP -> {
                val weight = if (category == FoodCategory.GRAINS) 150.0 else 120.0
                grams / weight
            }
            ServingUnit.BOWL -> grams / 150.0
            ServingUnit.SERVING -> grams / servingWeightGrams
            ServingUnit.TABLESPOON -> grams / 15.0
        }
    }
}
