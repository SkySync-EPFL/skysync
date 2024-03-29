package ch.epfl.skysync.model

// Only define a global constant to be used as default value for models id field
// when creating a new instance of the model in the database.
// Do not opt for a hierarchical data class pattern with an parent `IdentifiableModel` class
// as kotlin doesn't support inheritance in data class well:
// https://stackoverflow.com/questions/26444145/extend-data-class-in-kotlin

/**
 * Used for an ID that has not yet been generated
 *
 * This should only be used when adding a new item to a database.
 */
const val UNSET_ID = "__unset_id__"
