package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiNutritionService
import com.example.data.local.AppDatabase
import com.example.data.repository.NutritionRepository
import com.example.engine.NutritionCalculatorEngine
import com.example.model.DailyNutritionSummary
import com.example.model.DiaryEntry
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.MealType
import com.example.model.NutrientType
import com.example.model.NutritionResult
import com.example.model.ReverseCalculationResult
import com.example.model.ServingUnit
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = NutritionRepository(database)
    private val geminiService = GeminiNutritionService()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)

    // --- State: Foods & Search ---
    val allFoods: StateFlow<List<FoodItem>> = repository.allFoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCategory = MutableStateFlow(FoodCategory.ALL)
    val searchQuery = MutableStateFlow("")

    val filteredFoods: StateFlow<List<FoodItem>> = combine(
        allFoods,
        selectedCategory,
        searchQuery
    ) { foods, category, query ->
        foods.filter { food ->
            val matchesCategory = when (category) {
                FoodCategory.ALL -> true
                FoodCategory.CUSTOM -> food.isCustom
                else -> food.category == category
            }
            val matchesQuery = query.isBlank() ||
                    food.name.contains(query, ignoreCase = true) ||
                    food.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- State: Forward Calculator ---
    val selectedFoodForForward = MutableStateFlow<FoodItem?>(null)
    val forwardQuantity = MutableStateFlow("100")
    val forwardUnit = MutableStateFlow(ServingUnit.GRAM)

    val forwardResult: StateFlow<NutritionResult?> = combine(
        selectedFoodForForward,
        forwardQuantity,
        forwardUnit
    ) { food, qtyStr, unit ->
        if (food == null) return@combine null
        val qty = qtyStr.toDoubleOrNull() ?: 0.0
        NutritionCalculatorEngine.calculateForward(food, qty, unit)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- State: Reverse Calculator ---
    val selectedFoodForReverse = MutableStateFlow<FoodItem?>(null)
    val reverseTargetNutrient = MutableStateFlow(NutrientType.PROTEIN)
    val reverseTargetAmount = MutableStateFlow("50")

    val reverseResult: StateFlow<ReverseCalculationResult?> = combine(
        selectedFoodForReverse,
        reverseTargetNutrient,
        reverseTargetAmount
    ) { food, nutrient, amountStr ->
        if (food == null) return@combine null
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        NutritionCalculatorEngine.calculateReverse(food, nutrient, amount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- State: Food Comparison ---
    val comparisonFoodA = MutableStateFlow<FoodItem?>(null)
    val comparisonFoodB = MutableStateFlow<FoodItem?>(null)
    val comparisonPortionGrams = MutableStateFlow("100")

    val comparisonResult: StateFlow<NutritionCalculatorEngine.ComparisonResult?> = combine(
        comparisonFoodA,
        comparisonFoodB,
        comparisonPortionGrams
    ) { foodA, foodB, portionStr ->
        if (foodA == null || foodB == null) return@combine null
        val portion = portionStr.toDoubleOrNull() ?: 100.0
        NutritionCalculatorEngine.compareFoods(foodA, foodB, portion)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- State: Diary & Dates ---
    val selectedDate = MutableStateFlow(getTodayDateString())
    private val calendar = Calendar.getInstance()

    val dailySummary: StateFlow<DailyNutritionSummary> = selectedDate.flatMapLatest { date ->
        repository.getDailySummary(date)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DailyNutritionSummary(selectedDate.value, 0.0, 0.0, 0.0, 0.0, 0.0)
    )

    // --- State: User Profile & Goals ---
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // --- State: AI Assistant & Meal Synthesizer ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ai",
                message = "👋 Hello! I am your AI Nutrition Assistant. You can ask me questions about your daily nutrition intake, food comparisons (like Rice vs Roti), how to hit your protein targets, or ask me for personalized meal suggestions."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    val isAiLoading = MutableStateFlow(false)

    val aiMealRecommendationResult = MutableStateFlow<String?>(null)
    val isMealAiLoading = MutableStateFlow(false)

    init {
        // Automatically select default initial foods once available
        viewModelScope.launch {
            allFoods.collect { list ->
                if (list.isNotEmpty()) {
                    if (selectedFoodForForward.value == null) {
                        selectedFoodForForward.value = list.find { it.name.contains("Rice", ignoreCase = true) } ?: list.first()
                    }
                    if (selectedFoodForReverse.value == null) {
                        selectedFoodForReverse.value = list.find { it.name.contains("Egg", ignoreCase = true) } ?: list.first()
                    }
                    if (comparisonFoodA.value == null) {
                        comparisonFoodA.value = list.find { it.name.contains("Rice", ignoreCase = true) } ?: list.first()
                    }
                    if (comparisonFoodB.value == null) {
                        comparisonFoodB.value = list.find { it.name.contains("Roti", ignoreCase = true) } ?: list.getOrNull(1)
                    }
                }
            }
        }
    }

    private fun getTodayDateString(): String = dateFormatter.format(Date())

    fun getFormattedDisplayDate(dateStr: String): String {
        return try {
            val date = dateFormatter.parse(dateStr) ?: Date()
            val todayStr = getTodayDateString()
            if (dateStr == todayStr) {
                "Today, ${SimpleDateFormat("MMM d", Locale.US).format(date)}"
            } else {
                displayDateFormatter.format(date)
            }
        } catch (_: Exception) {
            dateStr
        }
    }

    fun changeDateOffset(offsetDays: Int) {
        try {
            val currentDate = dateFormatter.parse(selectedDate.value) ?: Date()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, offsetDays)
            selectedDate.value = dateFormatter.format(calendar.time)
        } catch (_: Exception) {
            selectedDate.value = getTodayDateString()
        }
    }

    fun setDate(dateStr: String) {
        selectedDate.value = dateStr
    }

    // --- Forward Calculator Controls ---
    fun selectFoodForForward(food: FoodItem) {
        selectedFoodForForward.value = food
        forwardUnit.value = food.servingUnit
        forwardQuantity.value = if (food.servingUnit == ServingUnit.EGG || food.servingUnit == ServingUnit.ROTI || food.servingUnit == ServingUnit.SLICE) "2" else "100"
    }

    fun setForwardQuantity(qty: String) {
        forwardQuantity.value = qty
    }

    fun setForwardUnit(unit: ServingUnit) {
        forwardUnit.value = unit
    }

    // --- Reverse Calculator Controls ---
    fun selectFoodForReverse(food: FoodItem) {
        selectedFoodForReverse.value = food
    }

    fun setReverseTargetNutrient(nutrient: NutrientType) {
        reverseTargetNutrient.value = nutrient
        // Set smart default values per nutrient type
        when (nutrient) {
            NutrientType.CALORIES -> reverseTargetAmount.value = "500"
            NutrientType.PROTEIN -> reverseTargetAmount.value = "50"
            NutrientType.CARBOHYDRATES -> reverseTargetAmount.value = "100"
            NutrientType.FAT -> reverseTargetAmount.value = "25"
            NutrientType.FIBER -> reverseTargetAmount.value = "15"
        }
    }

    fun setReverseTargetAmount(amount: String) {
        reverseTargetAmount.value = amount
    }

    // --- Add to Diary Functions ---
    fun logForwardResultToDiary(mealType: MealType) {
        val result = forwardResult.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val entry = DiaryEntry(
                date = selectedDate.value,
                mealType = mealType,
                foodId = result.foodItem.id,
                foodName = result.foodItem.name,
                foodEmoji = result.foodItem.emoji,
                quantity = result.inputQuantity,
                unit = result.inputUnit,
                grams = result.calculatedGrams,
                calories = result.calories,
                protein = result.protein,
                carbs = result.carbs,
                fat = result.fat,
                fiber = result.fiber
            )
            repository.logDiaryEntry(entry)
        }
    }

    fun logReverseResultToDiary(mealType: MealType) {
        val result = reverseResult.value ?: return
        if (result.requiredGrams <= 0) return
        viewModelScope.launch(Dispatchers.IO) {
            val entry = DiaryEntry(
                date = selectedDate.value,
                mealType = mealType,
                foodId = result.foodItem.id,
                foodName = result.foodItem.name,
                foodEmoji = result.foodItem.emoji,
                quantity = result.primaryRequiredUnitQuantity,
                unit = result.primaryUnit,
                grams = result.requiredGrams,
                calories = result.resultingCalories,
                protein = result.resultingProtein,
                carbs = result.resultingCarbs,
                fat = result.resultingFat,
                fiber = result.resultingFiber
            )
            repository.logDiaryEntry(entry)
        }
    }

    fun logFoodDirectly(
        food: FoodItem,
        quantity: Double,
        unit: ServingUnit,
        mealType: MealType
    ) {
        val calc = NutritionCalculatorEngine.calculateForward(food, quantity, unit)
        viewModelScope.launch(Dispatchers.IO) {
            val entry = DiaryEntry(
                date = selectedDate.value,
                mealType = mealType,
                foodId = food.id,
                foodName = food.name,
                foodEmoji = food.emoji,
                quantity = quantity,
                unit = unit,
                grams = calc.calculatedGrams,
                calories = calc.calories,
                protein = calc.protein,
                carbs = calc.carbs,
                fat = calc.fat,
                fiber = calc.fiber
            )
            repository.logDiaryEntry(entry)
        }
    }

    fun deleteDiaryEntry(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDiaryEntry(id)
        }
    }

    // --- Custom Food Management ---
    fun addCustomFood(
        name: String,
        category: FoodCategory,
        servingUnit: ServingUnit,
        servingGrams: Double,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
        fiber: Double,
        emoji: String = "⭐",
        description: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val food = FoodItem(
                name = name,
                category = category,
                servingUnit = servingUnit,
                servingWeightGrams = servingGrams,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                carbsPer100g = carbs,
                fatPer100g = fat,
                fiberPer100g = fiber,
                isCustom = true,
                emoji = emoji,
                description = description
            )
            repository.addCustomFood(food)
        }
    }

    // --- User Profile & Goal Settings ---
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserProfile(profile)
        }
    }

    fun calculateAndApplyRecommendedTargets() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = current.calculateRecommendedTargets()
            repository.saveUserProfile(updated)
        }
    }

    // --- Comparison Controls ---
    fun setComparisonFoodA(food: FoodItem) {
        comparisonFoodA.value = food
    }

    fun setComparisonFoodB(food: FoodItem) {
        comparisonFoodB.value = food
    }

    fun setComparisonPortionGrams(grams: String) {
        comparisonPortionGrams.value = grams
    }

    // --- AI Assistant Operations ---
    fun sendAiChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(sender = "user", message = userText)
        _chatMessages.value = _chatMessages.value + userMsg
        isAiLoading.value = true

        viewModelScope.launch {
            val history = _chatMessages.value.map { Pair(it.sender, it.message) }
            val responseText = geminiService.askNutritionAssistant(
                userMessage = userText,
                chatHistory = history,
                allFoods = allFoods.value,
                dailySummary = dailySummary.value,
                userProfile = userProfile.value
            )
            val aiMsg = ChatMessage(sender = "ai", message = responseText)
            _chatMessages.value = _chatMessages.value + aiMsg
            isAiLoading.value = false
        }
    }

    fun requestAiMealPlan(
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
        preference: String = ""
    ) {
        isMealAiLoading.value = true
        aiMealRecommendationResult.value = null

        viewModelScope.launch {
            val result = geminiService.getAiMealRecommendation(
                targetCalories = calories,
                targetProtein = protein,
                targetCarbs = carbs,
                targetFat = fat,
                mealPreference = preference,
                allFoods = allFoods.value,
                userProfile = userProfile.value
            )
            aiMealRecommendationResult.value = result
            isMealAiLoading.value = false
        }
    }
}
