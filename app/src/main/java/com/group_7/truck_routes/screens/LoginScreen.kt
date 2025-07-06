package com.group_7.truck_routes.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.FirebaseApp
import com.group_7.truck_routes.R
import com.group_7.truck_routes.Routs
import com.group_7.truck_routes.data.FireBaseRepository

@Composable
fun Loginscreen(navController: NavController) {

    val context = LocalContext.current
    FirebaseApp.initializeApp(context)

    var email by remember {
        mutableStateOf(value = "")
    }
    var password by remember {
        mutableStateOf(value = "")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.truck),
            contentDescription = "Login image",
            modifier = Modifier.size(150.dp)
        )
        Text(text = "Welcome", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Login to your account")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, {
            email = it
        }, label = {
            Text(text = "Email address")
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, {
            password = it
        }, label = {
            Text(text = "Password")
        }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

            when {
                email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }

                !email.matches(emailRegex) -> {
                    Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                }

                password.length < 6 -> {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Log.i("Credential", "Email: $email Password: $password")
                    FireBaseRepository.validateUserLogin(email, password) { success, user ->
                        if (success) {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate(Routs.Home)
                        } else {
                            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Forgot Password ?", Modifier.clickable {
        })
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Or Sign In with")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Image(
                painter = painterResource(id = R.drawable.fb),
                contentDescription = "Facebook",
                modifier = Modifier.size(88.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.size(100.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.x),
                contentDescription = "Twitter",
                modifier = Modifier.size(85.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?")
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Register",
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Blue,
                modifier = Modifier.clickable {
                    Log.i("Navigation", "Register clicked")
                    navController.navigate(
                        Routs.Registerscreen
                    )
                }
            )
        }
    }
}
