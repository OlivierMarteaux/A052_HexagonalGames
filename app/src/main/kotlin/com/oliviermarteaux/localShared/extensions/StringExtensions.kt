package com.oliviermarteaux.localShared.extensions

//fun String.isValidEmail(): Boolean {
//    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
//    return this.matches(emailRegex) && isNotEmpty()
//}
//
//fun String.isHardEnough(minChar: Int): Boolean {
//    if (length < minChar) return false
//
//    val hasLetter = any { it.isLetter() }
//    val hasDigit = any { it.isDigit() }
//    val hasSpecial = any { !it.isLetterOrDigit() }
//
//    return hasLetter && hasDigit && hasSpecial
//}