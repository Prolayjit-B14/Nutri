package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.DefaultFoodDataset
import com.example.engine.NutritionCalculatorEngine
import com.example.model.ServingUnit
import com.example.ui.components.NutritionResultCard
import com.example.ui.theme.NutriFitTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val rice = DefaultFoodDataset.foods.first()
    val result = NutritionCalculatorEngine.calculateForward(rice, 100.0, ServingUnit.GRAM)

    composeTestRule.setContent {
      NutriFitTheme {
        NutritionResultCard(
          result = result,
          onAddToDiary = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
