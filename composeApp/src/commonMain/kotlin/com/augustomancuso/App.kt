package com.augustomancuso

import com.augustomancuso.views.LoginView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.augustomancuso.views.RegisterView
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.augustomancuso.views.DeviceSaleView
import io.ktor.client.*
import kotlinx.coroutines.launch
import com.augustomancuso.models.LoginDto
import com.augustomancuso.models.RegisterDto
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.core.String
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
@Preview
fun App() {
    val client = HttpClient()
    var isLoggedIn by remember { mutableStateOf(false) }
    var showRegister by remember { mutableStateOf(false) }
    var showActivate by remember { mutableStateOf(false) }
    var token by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf<String?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        if (isLoggedIn) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                token?.let {
                    DeviceSaleView(
                        client = client,
                        token = it,
                        userName = username ?: "",
                        onPurchase = { device -> /* Handle purchase */ }
                    )
                }
            }
        } else {
            when {
                showRegister -> {
                    RegisterView(
                        onRegister = { RegisterDto ->
                            coroutineScope.launch {
                                val result = register(client, RegisterDto)
                                if (result) {
                                    showRegister = false
                                    showActivate = true
                                } else {
                                    registerError = "Username or password is incorrect"
                                }
                            }
                        }
                    )
                }
                else -> {
                    LoginView(
                        onLogin = { inputUsername, password ->
                            coroutineScope.launch {
                                val result = login(client, inputUsername, password)
                                if (result != null) {
                                    token = result.token // Set token to the value of sub field
                                    username = result.username // Set username to the value of sub field
                                    isLoggedIn = true
                                    loginError = null
                                } else {
                                    loginError = "Username or password is incorrect"
                                }
                            }
                        },
                        onNavigateToRegister = { showRegister = true },
                        loginError = loginError
                    )
                }
            }
        }
    }
}



@OptIn(InternalAPI::class)
suspend fun login(client: HttpClient, username: String, password: String): LoginResult? {
    val loginDto = LoginDto(username, password, false)
    val response: HttpResponse = client.post("http://localhost:8080/api/authenticate") {
        contentType(ContentType.Application.Json)
        body = Json.encodeToString(loginDto)
    }
    return if (response.status == HttpStatusCode.OK) {
        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        val token = json["id_token"]?.jsonPrimitive?.content
        token?.let {
            val payload = it.split(".")[1]
            val decodedBytes = payload.decodeBase64Bytes()
            val decodedString = String(decodedBytes)
            val payloadJson = Json.parseToJsonElement(decodedString).jsonObject
            val sub = payloadJson["sub"]?.jsonPrimitive?.content
            sub?.let { username ->
                TokenStorage.setToken(token)
                LoginResult(token, username)
            }
        }
    } else {
        null
    }
}

data class LoginResult(val token: String, val username: String)


@OptIn(InternalAPI::class)
suspend fun register(client: HttpClient, register: RegisterDto): Boolean {
    val response: HttpResponse = client.post("http://localhost:8080/api/register") {
        contentType(ContentType.Application.Json)
        body = Json.encodeToString(register)
    }
    if (response.status != HttpStatusCode.OK) {
        throw Exception("Registration failed with status: ${response.status}")
    }
    return true;

}

object TokenStorage{
    private var token: String? = null

    fun getToken(): String? {
        return token
    }

    fun setToken(newToken: String) {
        token = newToken
    }

    fun clearToken() {
        token = null
    }
}
