package com.example.ai

import com.example.BuildConfig
import com.example.engine.NutritionCalculatorEngine
import com.example.model.DailyNutritionSummary
import com.example.model.FoodItem
import com.example.model.NutrientType
import com.example.model.UserProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Locale
import java.util.concurrent.TimeUnit

// --- Gemini Request / Response DTOs ---

@JsonClass(generateAdapter = true)
data class GeminiContentRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float = 0.7f,
    @Json(name = "topP") val topP: Float = 0.95f,
    @Json(name = "topK") val topK: Int = 40
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiContentRequest
    ): GeminiResponse
}

class GeminiNutritionService {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    /**
     * Answers conversational user queries grounded with verified local nutrition data and current diary logs
     */
    suspend fun askNutritionAssistant(
        userMessage: String,
        chatHistory: List<Pair<String, String>>, // (role: "user"|"model", message)
        allFoods: List<FoodItem>,
        dailySummary: DailyNutritionSummary,
        userProfile: UserProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // Build verified context string from local database & logs
        val foodDatabaseContext = allFoods.joinToString("; ") {
            "${it.name}: ${it.caloriesPer100g.toInt()} kcal, ${it.proteinPer100g}g P, ${it.carbsPer100g}g C, ${it.fatPer100g}g F /100g"
        }

        val diaryContext = buildString {
            append("Today's Logged Totals: ")
            append("${String.format(Locale.US, "%.0f", dailySummary.totalCalories)} / ${String.format(Locale.US, "%.0f", userProfile.targetCalories)} kcal, ")
            append("Protein: ${String.format(Locale.US, "%.1f", dailySummary.totalProtein)} / ${String.format(Locale.US, "%.0f", userProfile.targetProtein)}g, ")
            append("Carbs: ${String.format(Locale.US, "%.1f", dailySummary.totalCarbs)} / ${String.format(Locale.US, "%.0f", userProfile.targetCarbs)}g, ")
            append("Fat: ${String.format(Locale.US, "%.1f", dailySummary.totalFat)} / ${String.format(Locale.US, "%.0f", userProfile.targetFat)}g. ")
            if (dailySummary.entries.isNotEmpty()) {
                append("Logged meals: ")
                append(dailySummary.entries.joinToString(", ") { "${it.mealType.displayName}: ${it.quantity} ${it.unit.unitCode} ${it.foodName} (${it.calories.toInt()} kcal)" })
            } else {
                append("No meals logged yet today.")
            }
        }

        val remainingCalories = (userProfile.targetCalories - dailySummary.totalCalories).coerceAtLeast(0.0)
        val remainingProtein = (userProfile.targetProtein - dailySummary.totalProtein).coerceAtLeast(0.0)
        val remainingCarbs = (userProfile.targetCarbs - dailySummary.totalCarbs).coerceAtLeast(0.0)
        val remainingFat = (userProfile.targetFat - dailySummary.totalFat).coerceAtLeast(0.0)

        val systemPrompt = """
            You are NutriFit's expert AI Nutrition Assistant.
            Your role is to help users understand their nutrition, recommend meals, explain food comparisons, and assist with their daily targets.
            
            RULES & CONSTRAINTS:
            1. GROUNDING: Strictly rely on the verified food database and user diary context provided below. DO NOT invent fake nutritional values.
            2. CALCULATION: When the user asks how much food is needed to hit a target (reverse calculation), use exact mathematics: Required Grams = (Target / Nutrient_per_100g) * 100. Always mention companion calories and companion macronutrients consumed at that portion.
            3. PERSONALIZATION: Always factor in the user's logged intake today and remaining targets when recommending meals.
            4. TONE: Friendly, concise, supportive, and formatted cleanly with markdown bullet points.
            5. MEDICAL DISCLAIMER: For any health or medical inquiries, remind the user that nutritional values and calculations are estimates and to consult a registered dietitian or healthcare professional.
            
            USER PROFILE & GOAL:
            - Goal: ${userProfile.goal.displayName} (${userProfile.goal.description})
            - Daily Target: ${userProfile.targetCalories.toInt()} kcal | ${userProfile.targetProtein.toInt()}g Protein | ${userProfile.targetCarbs.toInt()}g Carbs | ${userProfile.targetFat.toInt()}g Fat
            
            CURRENT DIARY STATUS:
            $diaryContext
            Remaining budget today: ${remainingCalories.toInt()} kcal | ${remainingProtein.toInt()}g P | ${remainingCarbs.toInt()}g C | ${remainingFat.toInt()}g F
            
            VERIFIED FOOD DATABASE SNIPPET:
            $foodDatabaseContext
        """.trimIndent()

        // If no valid API key or network fails, provide smart local deterministic response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalGroundedResponse(userMessage, allFoods, dailySummary, userProfile)
        }

        try {
            val contentList = mutableListOf<GeminiContent>()

            // Add history
            for ((role, text) in chatHistory.takeLast(6)) {
                val geminiRole = if (role.equals("user", ignoreCase = true)) "user" else "model"
                contentList.add(GeminiContent(parts = listOf(GeminiPart(text = text)), role = geminiRole))
            }

            // Add current message
            contentList.add(GeminiContent(parts = listOf(GeminiPart(text = userMessage)), role = "user"))

            val request = GeminiContentRequest(
                contents = contentList,
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(temperature = 0.6f)
            )

            val response = api.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!responseText.isNullOrBlank()) {
                return@withContext responseText
            } else {
                return@withContext generateLocalGroundedResponse(userMessage, allFoods, dailySummary, userProfile)
            }
        } catch (_: Exception) {
            return@withContext generateLocalGroundedResponse(userMessage, allFoods, dailySummary, userProfile)
        }
    }

    /**
     * AI Meal Recommender ("What Should I Eat?")
     */
    suspend fun getAiMealRecommendation(
        targetCalories: Double,
        targetProtein: Double,
        targetCarbs: Double,
        targetFat: Double,
        mealPreference: String,
        allFoods: List<FoodItem>,
        userProfile: UserProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val deterministicSuggestions = NutritionCalculatorEngine.generateDeterministicMealSuggestions(
            allFoods, targetCalories, targetProtein, targetCarbs, targetFat
        )

        val deterministicSummary = deterministicSuggestions.joinToString("\n\n") { s ->
            "${s.title}:\n" + s.items.joinToString("\n") { "• ${it.quantity} ${it.unit.unitCode} ${it.foodItem.name} (${it.calories.toInt()} kcal, ${it.protein}g P, ${it.carbs}g C, ${it.fat}g F)" } +
                    "\nTotal: ${s.totalCalories.toInt()} kcal | ${String.format(Locale.US, "%.1f", s.totalProtein)}g P | ${String.format(Locale.US, "%.1f", s.totalCarbs)}g C | ${String.format(Locale.US, "%.1f", s.totalFat)}g F"
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext buildString {
                append("Here are balanced meal recommendations mathematically tailored to your target of **${targetCalories.toInt()} kcal, ${targetProtein.toInt()}g protein, ${targetCarbs.toInt()}g carbs, and ${targetFat.toInt()}g fat**:\n\n")
                append(deterministicSummary)
                append("\n\n💡 *Tip: You can easily log any of these foods directly in your Food Diary!*")
            }
        }

        try {
            val prompt = """
                The user requested a meal recommendation to fulfill this exact nutritional target:
                - Calories: ${targetCalories.toInt()} kcal
                - Protein: ${targetProtein.toInt()}g
                - Carbohydrates: ${targetCarbs.toInt()}g
                - Fat: ${targetFat.toInt()}g
                - Meal Preference: ${mealPreference.ifBlank { "Any balanced meal" }}
                
                Here are the mathematically verified meal options calculated from the local food database:
                $deterministicSummary
                
                Please present a structured, delicious, and easy-to-prepare meal plan based on these verified food combinations. Highlight the itemized portions and show how closely it hits their calorie and macro target.
            """.trimIndent()

            val request = GeminiContentRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user")),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a master dietitian providing structured, grounded meal plans with exact grams, calories, and macronutrient breakdowns.")))
            )

            val response = api.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            return@withContext responseText ?: deterministicSummary
        } catch (_: Exception) {
            return@withContext deterministicSummary
        }
    }

    /**
     * Local Grounded Rule & Calculation Engine Fallback (Instant, deterministic, zero-hallucination)
     */
    private fun generateLocalGroundedResponse(
        query: String,
        allFoods: List<FoodItem>,
        dailySummary: DailyNutritionSummary,
        userProfile: UserProfile
    ): String {
        val q = query.lowercase(Locale.ROOT)

        // Case: "How much protein did I eat today?"
        if (q.contains("protein") && (q.contains("eat") || q.contains("today") || q.contains("consumed") || q.contains("logged"))) {
            val remaining = (userProfile.targetProtein - dailySummary.totalProtein).coerceAtLeast(0.0)
            return buildString {
                append("📊 **Today's Protein Intake:**\n\n")
                append("• **Consumed:** ${String.format(Locale.US, "%.1f", dailySummary.totalProtein)} g\n")
                append("• **Daily Target:** ${userProfile.targetProtein.toInt()} g\n")
                append("• **Remaining:** ${String.format(Locale.US, "%.1f", remaining)} g\n\n")
                if (remaining > 0) {
                    append("To hit your protein goal, you could have **150g Chicken Breast** (≈ 46.5g protein) or **200g Paneer** (≈ 36.6g protein) or **3 Eggs** (≈ 18.9g protein).")
                } else {
                    append("🎉 Great job! You have reached your protein target for today.")
                }
            }
        }

        // Case: "Compare rice and roti" / "compare X and Y"
        if (q.contains("compare") || (q.contains("vs") || q.contains("versus"))) {
            val food1 = allFoods.find { q.contains(it.name.lowercase(Locale.ROOT)) || (q.contains("rice") && it.name.contains("Rice", ignoreCase = true)) }
                ?: allFoods.find { it.name.contains("Rice", ignoreCase = true) }
            val food2 = allFoods.find { it != food1 && (q.contains(it.name.lowercase(Locale.ROOT)) || (q.contains("roti") && it.name.contains("Roti", ignoreCase = true))) }
                ?: allFoods.find { it.name.contains("Roti", ignoreCase = true) }

            if (food1 != null && food2 != null) {
                val comp = NutritionCalculatorEngine.compareFoods(food1, food2, 100.0)
                return buildString {
                    append("⚖️ **${food1.name} vs ${food2.name} (per 100g):**\n\n")
                    append("| Nutrient | ${food1.name} | ${food2.name} |\n")
                    append("|---|---|---|\n")
                    append("| 🔥 **Calories** | ${comp.resultA.calories.toInt()} kcal | ${comp.resultB.calories.toInt()} kcal |\n")
                    append("| 💪 **Protein** | ${comp.resultA.protein} g | ${comp.resultB.protein} g |\n")
                    append("| 🍚 **Carbohydrates** | ${comp.resultA.carbs} g | ${comp.resultB.carbs} g |\n")
                    append("| 🥑 **Fat** | ${comp.resultA.fat} g | ${comp.resultB.fat} g |\n")
                    append("| 🌾 **Fiber** | ${comp.resultA.fiber} g | ${comp.resultB.fiber} g |\n\n")
                    append("💡 **Key Insight:** ${comp.summaryNote}")
                }
            }
        }

        // Case: Reverse calculation query like "How much chicken do I need for 40g protein?"
        val chickenFood = allFoods.find { q.contains(it.name.lowercase(Locale.ROOT)) }
        if (chickenFood != null && (q.contains("need") || q.contains("how much") || q.contains("require"))) {
            val targetNutrient = when {
                q.contains("protein") -> NutrientType.PROTEIN
                q.contains("cal") -> NutrientType.CALORIES
                q.contains("carb") -> NutrientType.CARBOHYDRATES
                q.contains("fat") -> NutrientType.FAT
                else -> NutrientType.PROTEIN
            }
            // extract number from query
            val regex = Regex("""(\d+(\.\d+)?)""")
            val match = regex.find(q)
            val targetValue = match?.value?.toDoubleOrNull() ?: 40.0
            val reverseResult = NutritionCalculatorEngine.calculateReverse(chickenFood, targetNutrient, targetValue)
            return buildString {
                append("🧮 **Reverse Nutrition Calculation:**\n\n")
                append(reverseResult.insightText)
                append("\n\n**At this quantity (${reverseResult.formattedQuantityString}), you also consume:**\n")
                append("• 🔥 **Calories:** ${reverseResult.resultingCalories.toInt()} kcal\n")
                append("• 💪 **Protein:** ${String.format(Locale.US, "%.1f", reverseResult.resultingProtein)} g\n")
                append("• 🍚 **Carbs:** ${String.format(Locale.US, "%.1f", reverseResult.resultingCarbs)} g\n")
                append("• 🥑 **Fat:** ${String.format(Locale.US, "%.1f", reverseResult.resultingFat)} g\n")
                if (reverseResult.surplusWarnings.isNotEmpty()) {
                    append("\n⚠️ **Companion Intake Notes:**\n")
                    reverseResult.surplusWarnings.forEach { append("• $it\n") }
                }
            }
        }

        // Case: "I have X calories left today / suggest meal"
        val remainingCalories = (userProfile.targetCalories - dailySummary.totalCalories).coerceAtLeast(300.0)
        val remainingProtein = (userProfile.targetProtein - dailySummary.totalProtein).coerceAtLeast(25.0)
        val remainingCarbs = (userProfile.targetCarbs - dailySummary.totalCarbs).coerceAtLeast(30.0)
        val remainingFat = (userProfile.targetFat - dailySummary.totalFat).coerceAtLeast(10.0)

        val suggestions = NutritionCalculatorEngine.generateDeterministicMealSuggestions(
            allFoods, remainingCalories, remainingProtein, remainingCarbs, remainingFat
        )

        return buildString {
            append("🥗 **Personalized Nutrition Summary & Meal Plan:**\n\n")
            append("Today you have **${remainingCalories.toInt()} kcal** and **${remainingProtein.toInt()}g protein** remaining in your target.\n\n")
            append("Recommended balanced meal from your food database:\n")
            suggestions.firstOrNull()?.let { s ->
                append("### ${s.title}\n")
                s.items.forEach { item ->
                    append("• **${item.quantity} ${item.unit.unitCode} ${item.foodItem.name}** → ${item.calories.toInt()} kcal | ${item.protein}g P | ${item.carbs}g C\n")
                }
                append("\n**Total:** ${s.totalCalories.toInt()} kcal | ${String.format(Locale.US, "%.1f", s.totalProtein)}g Protein | ${String.format(Locale.US, "%.1f", s.totalCarbs)}g Carbs | ${String.format(Locale.US, "%.1f", s.totalFat)}g Fat\n")
            }
            append("\n*Note: Nutritional calculations are estimates based on standard references. Consult a qualified professional for clinical dietary advice.*")
        }
    }
}
