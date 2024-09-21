package com.augustomancuso.models

data class RegisterDto(
    var login: String,
    var email: String,
    var password: String,
    var langKey: String = "es",
    var descripcion: String,
    var nombres: String,
    var rememberMe: Boolean = false

)