package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DiaryEntry
import com.example.model.MealType
import com.example.model.ServingUnit

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String,
    val foodId: Long,
    val foodName: String,
    val foodEmoji: String,
    val quantity: Double,
    val unit: String,
    val grams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val loggedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): DiaryEntry {
        return DiaryEntry(
            id = id,
            date = date,
            mealType = MealType.fromString(mealType),
            foodId = foodId,
            foodName = foodName,
            foodEmoji = foodEmoji,
            quantity = quantity,
            unit = ServingUnit.fromCode(unit),
            grams = grams,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            loggedAtTimestamp = loggedAtTimestamp
        )
    }

    companion object {
        fun fromDomain(domain: DiaryEntry): DiaryEntryEntity {
            return DiaryEntryEntity(
                id = domain.id,
                date = domain.date,
                mealType = domain.mealType.name,
                foodId = domain.foodId,
                foodName = domain.foodName,
                foodEmoji = domain.foodEmoji,
                quantity = domain.quantity,
                unit = domain.unit.unitCode,
                grams = domain.grams,
                calories = domain.calories,
                protein = domain.protein,
                carbs = domain.carbs,
                fat = domain.fat,
                fiber = domain.fiber,
                loggedAtTimestamp = domain.loggedAtTimestamp
            )
        }
    }
}
