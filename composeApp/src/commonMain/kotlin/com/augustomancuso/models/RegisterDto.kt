package com.augustomancuso.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    var login: String,
    var email: String,
    var password: String,
    var langKey: String = "es",
    var firstName: String,
    var lastName: String
)