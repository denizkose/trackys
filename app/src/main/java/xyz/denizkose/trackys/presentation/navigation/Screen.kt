package xyz.denizkose.trackys.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data class AddEditSubscription(val subscriptionId: Long? = null) : Screen

    @Serializable
    data object Analytics : Screen

    @Serializable
    data object Settings : Screen
}