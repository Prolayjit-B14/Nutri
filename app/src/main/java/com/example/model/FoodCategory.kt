package com.example.model

enum class FoodCategory(val displayName: String) {
    ALL("All Foods"),
    GRAINS("Rice & Grains"),
    PROTEIN("Meat & Poultry"),
    SEAFOOD("Fish & Seafood"),
    DAIRY_EGGS("Dairy & Eggs"),
    PULSES_LEGUMES("Dal & Legumes"),
    VEGETABLES("Vegetables"),
    FRUITS("Fruits"),
    NUTS_SEEDS("Nuts & Seeds"),
    BAKERY_BREAD("Breads & Cereals"),
    BEVERAGES("Beverages"),
    CUSTOM("My Custom Foods");

    val iconEmoji: String get() = ""
}

enum class ServingUnit(
    val unitCode: String,
    val displayName: String,
    val defaultGrams: Double
) {
    GRAM("g", "Grams (g)", 1.0),
    KILOGRAM("kg", "Kilograms (kg)", 1000.0),
    MILLILITER("ml", "Milliliters (ml)", 1.0),
    LITER("l", "Liters (L)", 1000.0),
    PIECE("piece", "Piece", 50.0),
    EGG("egg", "Whole Egg", 50.0),
    ROTI("roti", "Medium Roti / Chapati", 35.0),
    CUP("cup", "Standard Cup (cooked)", 150.0),
    BOWL("bowl", "Medium Bowl / Katori", 150.0),
    SERVING("serving", "Standard Serving", 100.0),
    TABLESPOON("tbsp", "Tablespoon (tbsp)", 15.0),
    SLICE("slice", "Slice", 30.0),
    SCOOP("scoop", "Scoop", 32.0);

    companion object {
        fun fromCode(code: String): ServingUnit {
            return entries.find { it.unitCode.equals(code, ignoreCase = true) } ?: GRAM
        }
    }
}

