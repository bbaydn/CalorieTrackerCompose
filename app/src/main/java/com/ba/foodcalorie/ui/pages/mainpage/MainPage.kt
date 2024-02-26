package com.ba.foodcalorie.ui.pages.mainpage

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import com.ba.foodcalorie.R
import com.ba.foodcalorie.ui.pages.calorietrack.CalorieTrackingPage

@Composable
fun MainPage() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainPage") {
        composable("mainPage") {
            BigButton(navController)
        }
        composable("calorieTrackingPage") {
            CalorieTrackingPage(navController)
        }
    }
}

@Composable
private fun BigButton(navController: NavController) {
    OutlinedButton(onClick = { navController.navigate("calorieTrackingPage") }, modifier = Modifier.size(350.dp)) {
        Text(text = stringResource(id = R.string.list_it))
    }
}