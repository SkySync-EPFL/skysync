package ch.epfl.skysync.models.message

import androidx.compose.ui.graphics.vector.ImageVector

data class GroupDetails(
    val id: String,
    val name: String,
    val image: ImageVector?,
    val lastMessage: Message?,
)
