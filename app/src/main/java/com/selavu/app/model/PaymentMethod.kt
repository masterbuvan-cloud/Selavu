package com.selavu.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.ui.graphics.vector.ImageVector

enum class PaymentMethod(
    val displayName: String,
    val icon: ImageVector
) {
    CASH("Cash", Icons.Default.Payments),
    UPI("UPI", Icons.Default.QrCode2),
    DEBIT_CARD("Debit Card", Icons.Default.CreditCard),
    CREDIT_CARD("Credit Card", Icons.Default.CreditScore),
    OTHER("Other", Icons.Default.AccountBalanceWallet);

    companion object {
        fun fromName(name: String?): PaymentMethod {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: CASH
        }
    }
}

