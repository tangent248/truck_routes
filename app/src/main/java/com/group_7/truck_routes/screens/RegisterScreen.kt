package com.group_7.truck_routes.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.group_7.truck_routes.R


@Composable
fun Registerscreen(navController: NavController) {

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
            Log.i("Credential", "Email: $email Password: $password")
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

                    Log.i("Navigation", "Login Clicked")
                }
            )
        }
    }
}

