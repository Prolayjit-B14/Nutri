package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.FoodCategory
import com.example.model.FoodItem
import com.example.model.ServingUnit

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val servingUnit: String,
    val servingWeightGrams: Double,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double,
    val sodiumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val ironMg: Double = 0.0,
    val vitaminCMg: Double = 0.0,
    val isCustom: Boolean = false,
    val emoji: String = "🥗",
    val description: String = ""
) {
    fun toDomain(): FoodItem {
        val cat = try {
            FoodCategory.valueOf(category)
        } catch (_: Exception) {
            FoodCategory.GRAINS
        }
        return FoodItem(
            id = id,
            name = name,
            category = cat,
            servingUnit = ServingUnit.fromCode(servingUnit),
            servingWeightGrams = servingWeightGrams,
            caloriesPer100g = caloriesPer100g,
            proteinPer100g = proteinPer100g,
            carbsPer100g = carbsPer100g,
            fatPer100g = fatPer100g,
            fiberPer100g = fiberPer100g,
            sodiumMg = sodiumMg,
            potassiumMg = potassiumMg,
            calciumMg = calciumMg,
            ironMg = ironMg,
            vitaminCMg = vitaminCMg,
            isCustom = isCustom,
            emoji = emoji,
            description = description
        )
    }

    companion object {
        fun fromDomain(domain: FoodItem): FoodEntity {
            return FoodEntity(
                id = if (domain.id > 0) domain.id else 0,
                name = domain.name,
                category = domain.category.name,
                servingUnit = domain.servingUnit.unitCode,
                servingWeightGrams = domain.servingWeightGrams,
                caloriesPer100g = domain.caloriesPer100g,
                proteinPer100g = domain.proteinPer100g,
                carbsPer100g = domain.carbsPer100g,
                fatPer100g = domain.fatPer100g,
                fiberPer100g = domain.fiberPer100g,
                sodiumMg = domain.sodiumMg,
                potassiumMg = domain.potassiumMg,
                calciumMg = domain.calciumMg,
                ironMg = domain.ironMg,
                vitaminCMg = domain.vitaminCMg,
                isCustom = domain.isCustom,
                emoji = domain.emoji,
                description = domain.description
            )
        }
    }
}
