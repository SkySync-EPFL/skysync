package ch.epfl.skysync.database

/** Implemented by schema classes that can be casted to a model class. */
interface Schema<Model> {
  /** Create a new instance of the `Model` class derived from this schema instance */
  fun toModel(): Model
}
