package com.augustomancuso

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform