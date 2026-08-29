package com.example

import com.example.data.local.DefaultFoodDataset
import com.example.engine.NutritionCalculatorEngine
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.Gender
import com.example.model.NutrientType
import com.example.model.NutritionGoal
import com.example.model.ServingUnit
import com.example.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testForwardCalculation_Rice100g() {
    val whiteRice = DefaultFoodDataset.foods.first { it.name.contains("White Rice") }
    val result = NutritionCalculatorEngine.calculateForward(whiteRice, 100.0, ServingUnit.GRAM)

    assertEquals(130.0, result.calories, 0.1)
    assertEquals(2.7, result.protein, 0.1)
    assertEquals(28.2, result.carbs, 0.1)
    assertEquals(0.3, result.fat, 0.1)
  }

  @Test
  fun testForwardCalculation_TwoEggs() {
    val wholeEgg = DefaultFoodDataset.foods.first { it.name.contains("Whole Egg") }
    val result = NutritionCalculatorEngine.calculateForward(wholeEgg, 2.0, ServingUnit.EGG)

    // 2 eggs = 100g
    assertEquals(155.0, result.calories, 0.1)
    assertEquals(12.6, result.protein, 0.1)
  }

  @Test
  fun testReverseCalculation_RiceFor50gProtein() {
    val whiteRice = DefaultFoodDataset.foods.first { it.name.contains("White Rice") }
    val reverseResult = NutritionCalculatorEngine.calculateReverse(whiteRice, NutrientType.PROTEIN, 50.0)

    // 50g protein from 2.7g/100g = (50 / 2.7) * 100 ≈ 1851.85g ≈ 1.85 kg
    assertTrue("Required grams should be around 1851g", reverseResult.requiredGrams > 1800.0)
    assertTrue("Should flag surplus / high calorie load", reverseResult.resultingCalories > 2000.0)
  }

  @Test
  fun testReverseCalculation_EggFor50gProtein() {
    val wholeEgg = DefaultFoodDataset.foods.first { it.name.contains("Whole Egg") }
    val reverseResult = NutritionCalculatorEngine.calculateReverse(wholeEgg, NutrientType.PROTEIN, 50.0)

    // 50g protein from egg (6.3g per egg) ≈ 7.9 - 8.0 eggs
    assertTrue("Required eggs should be around 7.9 eggs", reverseResult.primaryRequiredUnitQuantity in 7.5..8.5)
  }

  @Test
  fun testUserProfile_BmrAndTdee() {
    val profile = UserProfile(
      age = 25,
      gender = Gender.MALE,
      heightCm = 180.0,
      weightKg = 75.0,
      goal = NutritionGoal.FAT_LOSS
    )
    val bmr = profile.calculateBMR()
    assertTrue("BMR for 75kg 180cm 25yo male should be around 1755 kcal", bmr in 1700.0..1800.0)
  }
}
