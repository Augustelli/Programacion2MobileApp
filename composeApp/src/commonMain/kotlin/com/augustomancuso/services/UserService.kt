/*
package com.augustomancuso.services


import com.augustomancuso.models.LoginDto
import com.augustomancuso.models.RegisterDto
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.ContentType

class UserService(private val client: HttpClient) {

    suspend fun login(loginDto: LoginDto): Result<LoginResponse> {
        return try {
            val response: HttpResponse = client.post("https://your-backend-url.com/login") {
                contentType(ContentType.Application.Json)
                body = loginDto
            }
            if (response.status == HttpStatusCode.OK) {
                val loginResponse = Json.decodeFromString<LoginResponse>(response.readText())
                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Login failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(registerDto: RegisterDto): Result<RegisterResponse> {
        return try {
            val response: HttpResponse = client.post("https://your-backend-url.com/register") {
                contentType(ContentType.Application.Json)
                body = registerDto
            }
            if (response.status == HttpStatusCode.OK) {
                val registerResponse = Json.decodeFromString<RegisterResponse>(response.readText())
                Result.success(registerResponse)
            } else {
                Result.failure(Exception("Registration failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class RegisterResponse(val message: String)*/
