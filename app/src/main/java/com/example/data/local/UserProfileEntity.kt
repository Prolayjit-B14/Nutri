package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ActivityLevel
import com.example.model.Gender
import com.example.model.NutritionGoal
import com.example.model.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1,
    val name: String = "User",
    val age: Int = 26,
    val gender: String = "MALE",
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val activityLevel: String = "MODERATE",
    val goal: String = "MUSCLE_BUILDING",
    val targetCalories: Double = 2200.0,
    val targetProtein: Double = 140.0,
    val targetCarbs: Double = 250.0,
    val targetFat: Double = 70.0,
    val targetFiber: Double = 30.0,
    val targetWaterMl: Double = 3000.0
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            age = age,
            gender = try { Gender.valueOf(gender) } catch (_: Exception) { Gender.MALE },
            heightCm = heightCm,
            weightKg = weightKg,
            activityLevel = try { ActivityLevel.valueOf(activityLevel) } catch (_: Exception) { ActivityLevel.MODERATE },
            goal = try { NutritionGoal.valueOf(goal) } catch (_: Exception) { NutritionGoal.MUSCLE_BUILDING },
            targetCalories = targetCalories,
            targetProtein = targetProtein,
            targetCarbs = targetCarbs,
            targetFat = targetFat,
            targetFiber = targetFiber,
            targetWaterMl = targetWaterMl
        )
    }

    companion object {
        fun fromDomain(domain: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                id = domain.id,
                name = domain.name,
                age = domain.age,
                gender = domain.gender.name,
                heightCm = domain.heightCm,
                weightKg = domain.weightKg,
                activityLevel = domain.activityLevel.name,
                goal = domain.goal.name,
                targetCalories = domain.targetCalories,
                targetProtein = domain.targetProtein,
                targetCarbs = domain.targetCarbs,
                targetFat = domain.targetFat,
                targetFiber = domain.targetFiber,
                targetWaterMl = domain.targetWaterMl
            )
        }
    }
}
