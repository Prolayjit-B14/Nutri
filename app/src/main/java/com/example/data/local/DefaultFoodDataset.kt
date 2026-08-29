package com.example.data.local

import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.ServingUnit

object DefaultFoodDataset {
    val foods: List<FoodItem> = listOf(
        // --- GRAINS & BREADS ---
        FoodItem(
            id = 1,
            name = "White Rice (Cooked)",
            category = FoodCategory.GRAINS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 150.0,
            caloriesPer100g = 130.0,
            proteinPer100g = 2.7,
            carbsPer100g = 28.2,
            fatPer100g = 0.3,
            fiberPer100g = 0.4,
            sodiumMg = 1.0,
            potassiumMg = 35.0,
            calciumMg = 10.0,
            ironMg = 0.2,
            emoji = "🍚",
            description = "Cooked standard white grain rice, high in digestible carbs"
        ),
        FoodItem(
            id = 2,
            name = "Brown Rice (Cooked)",
            category = FoodCategory.GRAINS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 150.0,
            caloriesPer100g = 112.0,
            proteinPer100g = 2.6,
            carbsPer100g = 23.5,
            fatPer100g = 0.9,
            fiberPer100g = 1.8,
            sodiumMg = 5.0,
            potassiumMg = 79.0,
            calciumMg = 10.0,
            ironMg = 0.5,
            emoji = "🌾",
            description = "Whole grain brown rice with fiber-rich bran"
        ),
        FoodItem(
            id = 3,
            name = "Roti / Chapati (Whole Wheat)",
            category = FoodCategory.GRAINS,
            servingUnit = ServingUnit.ROTI,
            servingWeightGrams = 35.0,
            caloriesPer100g = 264.0, // ≈ 92 kcal per 35g roti
            proteinPer100g = 8.8,    // ≈ 3.1g protein per roti
            carbsPer100g = 52.0,    // ≈ 18.2g carbs per roti
            fatPer100g = 3.5,       // ≈ 1.2g fat per roti
            fiberPer100g = 7.0,     // ≈ 2.5g fiber per roti
            sodiumMg = 180.0,
            potassiumMg = 140.0,
            calciumMg = 25.0,
            ironMg = 2.4,
            emoji = "🫓",
            description = "Traditional Indian unleavened whole wheat flatbread"
        ),
        FoodItem(
            id = 4,
            name = "Rolled Oats (Raw)",
            category = FoodCategory.GRAINS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 40.0,
            caloriesPer100g = 389.0,
            proteinPer100g = 16.9,
            carbsPer100g = 66.3,
            fatPer100g = 6.9,
            fiberPer100g = 10.6,
            sodiumMg = 2.0,
            potassiumMg = 429.0,
            calciumMg = 54.0,
            ironMg = 4.7,
            emoji = "🥣",
            description = "Complex carbohydrate rich in beta-glucan soluble fiber"
        ),
        FoodItem(
            id = 5,
            name = "Whole Wheat Bread",
            category = FoodCategory.BAKERY_BREAD,
            servingUnit = ServingUnit.SLICE,
            servingWeightGrams = 30.0,
            caloriesPer100g = 247.0, // ≈ 74 kcal per slice
            proteinPer100g = 13.0,   // ≈ 3.9g protein per slice
            carbsPer100g = 41.0,
            fatPer100g = 3.4,
            fiberPer100g = 7.0,
            sodiumMg = 450.0,
            potassiumMg = 250.0,
            emoji = "🍞",
            description = "100% whole grain sliced sandwich bread"
        ),

        // --- PROTEIN & MEATS ---
        FoodItem(
            id = 6,
            name = "Whole Egg (Boiled)",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.EGG,
            servingWeightGrams = 50.0,
            caloriesPer100g = 155.0, // ≈ 78 kcal per egg
            proteinPer100g = 12.6,   // ≈ 6.3g protein per egg
            carbsPer100g = 1.1,      // ≈ 0.6g carbs
            fatPer100g = 10.6,      // ≈ 5.3g fat
            fiberPer100g = 0.0,
            sodiumMg = 124.0,
            potassiumMg = 126.0,
            calciumMg = 50.0,
            ironMg = 1.2,
            emoji = "🥚",
            description = "Large whole egg with complete amino acid profile"
        ),
        FoodItem(
            id = 7,
            name = "Egg White (Boiled)",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.PIECE,
            servingWeightGrams = 33.0,
            caloriesPer100g = 52.0,  // ≈ 17 kcal per white
            proteinPer100g = 10.9,   // ≈ 3.6g protein
            carbsPer100g = 0.7,
            fatPer100g = 0.2,
            fiberPer100g = 0.0,
            sodiumMg = 166.0,
            potassiumMg = 163.0,
            emoji = "🍳",
            description = "Pure lean protein with virtually zero fat and carbs"
        ),
        FoodItem(
            id = 8,
            name = "Chicken Breast (Cooked / Grilled)",
            category = FoodCategory.PROTEIN,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 150.0,
            caloriesPer100g = 165.0,
            proteinPer100g = 31.0,
            carbsPer100g = 0.0,
            fatPer100g = 3.6,
            fiberPer100g = 0.0,
            sodiumMg = 74.0,
            potassiumMg = 256.0,
            calciumMg = 15.0,
            ironMg = 1.0,
            emoji = "🍗",
            description = "Skinless, boneless ultra-lean high-protein poultry"
        ),
        FoodItem(
            id = 9,
            name = "Fish Fillet (Cooked Salmon / White Fish)",
            category = FoodCategory.SEAFOOD,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 150.0,
            caloriesPer100g = 206.0,
            proteinPer100g = 22.0,
            carbsPer100g = 0.0,
            fatPer100g = 12.0,
            fiberPer100g = 0.0,
            sodiumMg = 60.0,
            potassiumMg = 363.0,
            calciumMg = 12.0,
            ironMg = 0.8,
            emoji = "🐟",
            description = "Rich in Omega-3 fatty acids and high biological protein"
        ),
        FoodItem(
            id = 10,
            name = "Paneer (Indian Cottage Cheese)",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 100.0,
            caloriesPer100g = 265.0,
            proteinPer100g = 18.3,
            carbsPer100g = 3.4,
            fatPer100g = 20.8,
            fiberPer100g = 0.0,
            sodiumMg = 18.0,
            potassiumMg = 95.0,
            calciumMg = 480.0,
            emoji = "🧀",
            description = "Fresh unaged Indian cheese, high in calcium and dairy fats"
        ),
        FoodItem(
            id = 11,
            name = "Soybean / Soya Chunks (Dry)",
            category = FoodCategory.PULSES_LEGUMES,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 50.0,
            caloriesPer100g = 345.0,
            proteinPer100g = 52.0,
            carbsPer100g = 33.0,
            fatPer100g = 0.5,
            fiberPer100g = 13.0,
            sodiumMg = 20.0,
            potassiumMg = 1790.0,
            calciumMg = 350.0,
            ironMg = 10.5,
            emoji = "🫘",
            description = "Defatted textured plant protein, extraordinary 52% protein density"
        ),
        FoodItem(
            id = 12,
            name = "Dal / Cooked Yellow Lentils",
            category = FoodCategory.PULSES_LEGUMES,
            servingUnit = ServingUnit.BOWL,
            servingWeightGrams = 150.0,
            caloriesPer100g = 116.0,
            proteinPer100g = 9.0,
            carbsPer100g = 20.1,
            fatPer100g = 0.4,
            fiberPer100g = 7.9,
            sodiumMg = 2.0,
            potassiumMg = 369.0,
            calciumMg = 19.0,
            ironMg = 3.3,
            emoji = "🍲",
            description = "Cooked Indian spiced lentils, staple source of plant protein and fiber"
        ),
        FoodItem(
            id = 13,
            name = "Tofu (Firm)",
            category = FoodCategory.PULSES_LEGUMES,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 100.0,
            caloriesPer100g = 76.0,
            proteinPer100g = 8.1,
            carbsPer100g = 1.9,
            fatPer100g = 4.8,
            fiberPer100g = 0.3,
            calciumMg = 350.0,
            emoji = "🧊",
            description = "Soy-based vegan protein block with great mineral profile"
        ),

        // --- DAIRY & BEVERAGES ---
        FoodItem(
            id = 14,
            name = "Whole Milk",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.MILLILITER,
            servingWeightGrams = 250.0,
            caloriesPer100g = 61.0,  // ≈ 152 kcal per 250ml glass
            proteinPer100g = 3.2,    // ≈ 8.0g protein
            carbsPer100g = 4.8,      // ≈ 12.0g carbs
            fatPer100g = 3.3,        // ≈ 8.2g fat
            fiberPer100g = 0.0,
            sodiumMg = 43.0,
            potassiumMg = 132.0,
            calciumMg = 113.0,
            emoji = "🥛",
            description = "Pasteurized whole dairy cow milk"
        ),
        FoodItem(
            id = 15,
            name = "Curd / Plain Yogurt (Dahi)",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.BOWL,
            servingWeightGrams = 150.0,
            caloriesPer100g = 61.0,
            proteinPer100g = 3.5,
            carbsPer100g = 4.7,
            fatPer100g = 3.3,
            fiberPer100g = 0.0,
            calciumMg = 121.0,
            emoji = "🥣",
            description = "Fermented probiotic fresh dairy yogurt"
        ),
        FoodItem(
            id = 16,
            name = "Greek Yogurt (Non-Fat)",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 150.0,
            caloriesPer100g = 59.0,
            proteinPer100g = 10.0,
            carbsPer100g = 3.6,
            fatPer100g = 0.4,
            fiberPer100g = 0.0,
            calciumMg = 110.0,
            emoji = "🍦",
            description = "Strained high-protein creamy yogurt"
        ),
        FoodItem(
            id = 17,
            name = "Whey Protein Powder",
            category = FoodCategory.DAIRY_EGGS,
            servingUnit = ServingUnit.SCOOP,
            servingWeightGrams = 32.0,
            caloriesPer100g = 380.0, // ≈ 120 kcal per 32g scoop
            proteinPer100g = 75.0,   // ≈ 24.0g protein
            carbsPer100g = 9.0,      // ≈ 2.8g carbs
            fatPer100g = 5.0,        // ≈ 1.6g fat
            fiberPer100g = 1.0,
            calciumMg = 450.0,
            emoji = "🥤",
            description = "Fast-digesting concentrated whey isolate/concentrate"
        ),

        // --- VEGETABLES ---
        FoodItem(
            id = 18,
            name = "Potato (Boiled)",
            category = FoodCategory.VEGETABLES,
            servingUnit = ServingUnit.PIECE,
            servingWeightGrams = 150.0,
            caloriesPer100g = 87.0,
            proteinPer100g = 1.9,
            carbsPer100g = 20.1,
            fatPer100g = 0.1,
            fiberPer100g = 1.8,
            potassiumMg = 379.0,
            vitaminCMg = 13.0,
            emoji = "🥔",
            description = "Boiled root vegetable with high satiety index and potassium"
        ),
        FoodItem(
            id = 19,
            name = "Mixed Green Vegetables (Cooked)",
            category = FoodCategory.VEGETABLES,
            servingUnit = ServingUnit.CUP,
            servingWeightGrams = 100.0,
            caloriesPer100g = 35.0,
            proteinPer100g = 2.5,
            carbsPer100g = 6.0,
            fatPer100g = 0.5,
            fiberPer100g = 3.2,
            vitaminCMg = 30.0,
            emoji = "🥦",
            description = "Steamed broccoli, spinach, beans, and carrots"
        ),
        FoodItem(
            id = 20,
            name = "Spinach (Palak Raw / Cooked)",
            category = FoodCategory.VEGETABLES,
            servingUnit = ServingUnit.CUP,
            servingWeightGrams = 100.0,
            caloriesPer100g = 23.0,
            proteinPer100g = 2.9,
            carbsPer100g = 3.6,
            fatPer100g = 0.4,
            fiberPer100g = 2.2,
            ironMg = 2.7,
            vitaminCMg = 28.0,
            emoji = "🥬",
            description = "Leafy green superfood loaded with iron, folate and lutein"
        ),
        FoodItem(
            id = 21,
            name = "Cucumber & Tomato Salad",
            category = FoodCategory.VEGETABLES,
            servingUnit = ServingUnit.BOWL,
            servingWeightGrams = 150.0,
            caloriesPer100g = 18.0,
            proteinPer100g = 0.8,
            carbsPer100g = 3.8,
            fatPer100g = 0.2,
            fiberPer100g = 1.2,
            vitaminCMg = 15.0,
            emoji = "🥗",
            description = "Crisp refreshing hydrating raw salad"
        ),

        // --- FRUITS ---
        FoodItem(
            id = 22,
            name = "Banana",
            category = FoodCategory.FRUITS,
            servingUnit = ServingUnit.PIECE,
            servingWeightGrams = 120.0,
            caloriesPer100g = 89.0, // ≈ 107 kcal per banana
            proteinPer100g = 1.1,
            carbsPer100g = 22.8,
            fatPer100g = 0.3,
            fiberPer100g = 2.6,
            potassiumMg = 358.0,
            vitaminCMg = 8.7,
            emoji = "🍌",
            description = "Natural energy fruit rich in potassium and quick carbs"
        ),
        FoodItem(
            id = 23,
            name = "Apple",
            category = FoodCategory.FRUITS,
            servingUnit = ServingUnit.PIECE,
            servingWeightGrams = 150.0,
            caloriesPer100g = 52.0, // ≈ 78 kcal per medium apple
            proteinPer100g = 0.3,
            carbsPer100g = 13.8,
            fatPer100g = 0.2,
            fiberPer100g = 2.4,
            vitaminCMg = 4.6,
            emoji = "🍎",
            description = "Crisp fruit packed with pectin fiber and antioxidants"
        ),
        FoodItem(
            id = 24,
            name = "Papaya",
            category = FoodCategory.FRUITS,
            servingUnit = ServingUnit.CUP,
            servingWeightGrams = 140.0,
            caloriesPer100g = 43.0,
            proteinPer100g = 0.5,
            carbsPer100g = 10.8,
            fatPer100g = 0.3,
            fiberPer100g = 1.7,
            vitaminCMg = 62.0,
            emoji = "🍈",
            description = "Tropical fruit with digestive enzyme papain and Vitamin C"
        ),

        // --- NUTS & HEALTHY FATS ---
        FoodItem(
            id = 25,
            name = "Almonds",
            category = FoodCategory.NUTS_SEEDS,
            servingUnit = ServingUnit.GRAM,
            servingWeightGrams = 30.0,
            caloriesPer100g = 579.0,
            proteinPer100g = 21.2,
            carbsPer100g = 21.6,
            fatPer100g = 49.9,
            fiberPer100g = 12.5,
            calciumMg = 269.0,
            emoji = "🌰",
            description = "Heart-healthy tree nut rich in Vitamin E and healthy monounsaturated fats"
        ),
        FoodItem(
            id = 26,
            name = "Peanut Butter",
            category = FoodCategory.NUTS_SEEDS,
            servingUnit = ServingUnit.TABLESPOON,
            servingWeightGrams = 32.0,
            caloriesPer100g = 588.0,
            proteinPer100g = 25.0,
            carbsPer100g = 20.0,
            fatPer100g = 50.0,
            fiberPer100g = 6.0,
            emoji = "🥜",
            description = "Roasted peanut spread, calorie-dense plant protein"
        ),
        FoodItem(
            id = 27,
            name = "Olive Oil / Ghee",
            category = FoodCategory.NUTS_SEEDS,
            servingUnit = ServingUnit.TABLESPOON,
            servingWeightGrams = 14.0,
            caloriesPer100g = 884.0, // ≈ 124 kcal per tbsp
            proteinPer100g = 0.0,
            carbsPer100g = 0.0,
            fatPer100g = 100.0,
            fiberPer100g = 0.0,
            emoji = "🫒",
            description = "Pure healthy cooking fat"
        ),
        FoodItem(
            id = 28,
            name = "Kidney Beans / Rajma (Cooked)",
            category = FoodCategory.PULSES_LEGUMES,
            servingUnit = ServingUnit.BOWL,
            servingWeightGrams = 150.0,
            caloriesPer100g = 127.0,
            proteinPer100g = 8.7,
            carbsPer100g = 22.8,
            fatPer100g = 0.5,
            fiberPer100g = 7.4,
            ironMg = 2.9,
            emoji = "🫘",
            description = "North Indian red kidney beans in savory curry"
        ),
        FoodItem(
            id = 29,
            name = "Chickpeas / Chana (Cooked)",
            category = FoodCategory.PULSES_LEGUMES,
            servingUnit = ServingUnit.BOWL,
            servingWeightGrams = 150.0,
            caloriesPer100g = 164.0,
            proteinPer100g = 8.9,
            carbsPer100g = 27.4,
            fatPer100g = 2.6,
            fiberPer100g = 7.6,
            emoji = "🍲",
            description = "Garbanzo beans / chole, high fiber and complex carbs"
        ),
        FoodItem(
            id = 30,
            name = "Chia Seeds",
            category = FoodCategory.NUTS_SEEDS,
            servingUnit = ServingUnit.TABLESPOON,
            servingWeightGrams = 15.0,
            caloriesPer100g = 486.0,
            proteinPer100g = 16.5,
            carbsPer100g = 42.1,
            fatPer100g = 30.7,
            fiberPer100g = 34.4,
            calciumMg = 631.0,
            emoji = "🌱",
            description = "Nutrient-dense superfood loaded with soluble fiber and ALA Omega-3s"
        )
    )
}
