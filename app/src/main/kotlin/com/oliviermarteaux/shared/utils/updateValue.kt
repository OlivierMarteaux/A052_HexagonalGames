package com.oliviermarteaux.shared.utils

fun <T> updateValue(value: T, update: (T) -> T): T {
    return update(value)
}