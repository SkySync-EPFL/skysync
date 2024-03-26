package ch.epfl.skysync.database

/** Implemented by schema classes that can be casted to a model class. */
interface SchemaDecode<Model> {
  /** Create a new instance of the `Model` class derived from this schema instance */
  fun toModel(): Model
}

/**
 * Implemented by the companion object of schema classes for which an instance can be constructed
 * from a model.
 *
 * This is done this way as kotlin does not support static functions in interfaces:
 * https://stackoverflow.com/questions/40370471/is-it-possible-to-specify-a-static-function-in-a-kotlin-interface
 */
interface SchemaEncode<Model, Schema> {
  /** Create a new instance of the `Schema` class derived from the given `Model` instance */
  fun fromModel(model: Model): Schema
}
