package com.example.fserv.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreenView(navController: NavController){
    val bottomNavHost = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationPanel(navController = bottomNavHost) }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ){
            NavigationGraph(navController = navController, bottomNavHost = bottomNavHost)
        }
    }
}