package com.example.tallerucc.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.FloatingActionButtonCustom
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.NavigationViewModel

@Composable
fun CommunitiesPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    navigationViewModel: NavigationViewModel
) {
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold (
        modifier = modifier,
        topBar = {
            Header(title = "Tu Taller UCC")
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { navigationViewModel.selectIndex(it) }
            )
        },
        floatingActionButton = {
            //FAB personalizado
            FloatingActionButtonCustom(
                onFabClick = {
                    navController.navigate("createPage")
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Communities Page",
                style = Typography.titleLarge,
                fontSize = 32.sp
            )

            TextButton(onClick = {
                authViewModel.signout()
            }) {
                Text(
                    text = "Sign out",
                    style = Typography.labelMedium,
                )
            }
        }

    }

}
