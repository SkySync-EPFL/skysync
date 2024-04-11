package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.flight.Basket
import com.google.firebase.firestore.DocumentId

data class BasketSchema(
    @DocumentId val id: String? = null,
    val name: String? = null,
    val hasDoor: Boolean? = null,
) : Schema<Basket> {
  override fun toModel(): Basket {
    return Basket(
        id = id!!,
        name = name!!,
        hasDoor = hasDoor!!,
    )
  }

  companion object {
    fun fromModel(model: Basket): BasketSchema {
      return BasketSchema(id = model.id, name = model.name, hasDoor = model.hasDoor)
    }
  }
}
