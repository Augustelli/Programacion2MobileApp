package com.augustomancuso.models

import kotlinx.serialization.Serializable


@Serializable()
data class LoginDto(
    var username: String,
    var password: String,
    var rememberMe: Boolean = false
)
