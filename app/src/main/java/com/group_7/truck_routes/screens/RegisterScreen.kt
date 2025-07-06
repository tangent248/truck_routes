package com.group_7.truck_routes.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.group_7.truck_routes.R
import com.group_7.truck_routes.data.FireBaseRepository
import com.group_7.truck_routes.data.UserData


@Composable
fun Registerscreen(navController: NavController) {

    val context = LocalContext.current

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
        Text(text = "Create new Account", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))


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
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                }

                !email.matches(emailRegex) -> {
                    Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                }

                password.length < 6 -> {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    FireBaseRepository.addUserData(UserData(email = email, password = password))
                    navController.popBackStack()
                    Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Register")
        }


        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account?")
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Blue,
                modifier = Modifier.clickable {
                    navController.popBackStack()

                    Log.i("Navigation", "Login Clicked")
                }
            )
        }
    }
}

