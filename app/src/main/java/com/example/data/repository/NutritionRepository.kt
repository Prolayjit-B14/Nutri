package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DiaryEntryEntity
import com.example.data.local.FoodEntity
import com.example.data.local.UserProfileEntity
import com.example.model.DailyNutritionSummary
import com.example.model.DiaryEntry
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NutritionRepository(private val database: AppDatabase) {
    private val foodDao = database.foodDao()
    private val diaryDao = database.diaryDao()
    private val userDao = database.userDao()

    val allFoods: Flow<List<FoodItem>> = foodDao.getAllFoods().map { list ->
        list.map { it.toDomain() }
    }

    fun getFoodsByCategory(category: FoodCategory): Flow<List<FoodItem>> {
        return if (category == FoodCategory.ALL) {
            allFoods
        } else if (category == FoodCategory.CUSTOM) {
            allFoods.map { list -> list.filter { it.isCustom } }
        } else {
            foodDao.getFoodsByCategory(category.name).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    fun searchFoods(query: String): Flow<List<FoodItem>> {
        return foodDao.searchFoods(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getFoodById(id: Long): FoodItem? {
        return foodDao.getFoodById(id)?.toDomain()
    }

    suspend fun addCustomFood(food: FoodItem): Long {
        return foodDao.insertFood(FoodEntity.fromDomain(food.copy(isCustom = true)))
    }

    suspend fun deleteFood(id: Long) {
        foodDao.deleteFoodById(id)
    }

    // --- Diary Operations ---
    fun getDiaryEntries(date: String): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getDailySummary(date: String): Flow<DailyNutritionSummary> {
        return diaryDao.getEntriesForDate(date).map { list ->
            val entries = list.map { it.toDomain() }
            val cal = entries.sumOf { it.calories }
            val p = entries.sumOf { it.protein }
            val c = entries.sumOf { it.carbs }
            val f = entries.sumOf { it.fat }
            val fib = entries.sumOf { it.fiber }
            DailyNutritionSummary(
                date = date,
                totalCalories = cal,
                totalProtein = p,
                totalCarbs = c,
                totalFat = f,
                totalFiber = fib,
                entries = entries
            )
        }
    }

    suspend fun logDiaryEntry(entry: DiaryEntry): Long {
        return diaryDao.insertEntry(DiaryEntryEntity.fromDomain(entry))
    }

    suspend fun deleteDiaryEntry(id: Long) {
        diaryDao.deleteEntryById(id)
    }

    // --- User Profile & Goals ---
    val userProfile: Flow<UserProfile> = userDao.getUserProfile().map { entity ->
        entity?.toDomain() ?: UserProfile()
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.saveUserProfile(UserProfileEntity.fromDomain(profile))
    }
}
