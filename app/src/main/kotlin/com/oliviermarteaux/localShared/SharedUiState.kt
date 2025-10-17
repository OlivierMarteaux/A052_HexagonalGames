package com.oliviermarteaux.localShared

/**
 * Represents the UI state for loading and displaying data in a generic way.
 *
 * @param T The type of items being displayed (e.g., Applicant, Item, etc.).
 */
sealed interface UiState<out T> {

    /** State representing successful data load with a list of items. */
    data class Success<T>(val data: T) : UiState<T>

    /** State representing an error during data loading. */
    data class Error(val throwable: Throwable? = null) : UiState<Throwable>

    /** State representing that data is currently loading. */
    data object Loading : UiState<Nothing>

    /** State representing that there are no items to display. */
    data object Empty : UiState<Nothing>
}