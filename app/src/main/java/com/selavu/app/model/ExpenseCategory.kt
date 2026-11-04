package com.selavu.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val displayName: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val backgroundColor: Color
) {
    FOOD(
        displayName = "Food",
        icon = Icons.Default.Restaurant,
        primaryColor = Color(0xFFEA580C),      // Warm Orange
        backgroundColor = Color(0xFFFFEDD5)   // Light Orange
    ),
    TRANSPORTATION(
        displayName = "Transportation",
        icon = Icons.Default.DirectionsCar,
        primaryColor = Color(0xFF2563EB),      // Bright Blue
        backgroundColor = Color(0xFFDBEAFE)   // Light Blue
    ),
    SHOPPING(
        displayName = "Shopping",
        icon = Icons.Default.ShoppingBag,
        primaryColor = Color(0xFFDB2777),      // Deep Pink
        backgroundColor = Color(0xFFFCE7F3)   // Soft Pink
    ),
    BILLS(
        displayName = "Bills",
        icon = Icons.Default.ReceiptLong,
        primaryColor = Color(0xFFCA8A04),      // Amber
        backgroundColor = Color(0xFFFEF9C3)   // Light Amber
    ),
    ENTERTAINMENT(
        displayName = "Entertainment",
        icon = Icons.Default.Movie,
        primaryColor = Color(0xFF7C3AED),      // Royal Purple
        backgroundColor = Color(0xFFEDE9FE)   // Soft Purple
    ),
    HEALTHCARE(
        displayName = "Healthcare",
        icon = Icons.Default.LocalHospital,
        primaryColor = Color(0xFFDC2626),      // Red
        backgroundColor = Color(0xFFFEE2E2)   // Soft Red
    ),
    EDUCATION(
        displayName = "Education",
        icon = Icons.Default.School,
        primaryColor = Color(0xFF0891B2),      // Cyan / Teal
        backgroundColor = Color(0xFFCFFAFE)   // Light Cyan
    ),
    OTHERS(
        displayName = "Others",
        icon = Icons.Default.Category,
        primaryColor = Color(0xFF475569),      // Slate
        backgroundColor = Color(0xFFF1F5F9)   // Light Slate
    );

    companion object {
        fun fromName(name: String?): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: OTHERS
        }
    }
}

