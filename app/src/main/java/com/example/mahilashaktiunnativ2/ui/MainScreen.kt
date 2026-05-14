package com.example.mahilashaktiunnativ2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mahilashaktiunnativ2.R
import com.example.mahilashaktiunnativ2.ui.theme.*

sealed class BottomNavItem(

    val title: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector

) {

    object Dashboard : BottomNavItem(
        R.string.home,
        Icons.Default.Home
    )

    object Members : BottomNavItem(
        R.string.members,
        Icons.Default.Person
    )

    object Loans : BottomNavItem(
        R.string.loans,
        Icons.Default.Star
    )

    object Learn : BottomNavItem(
        R.string.learn,
        Icons.Default.Info
    )
}

@Composable
fun MainScreen() {

    var selectedItem by remember {

        mutableIntStateOf(0)
    }

    val items = listOf(

        BottomNavItem.Dashboard,
        BottomNavItem.Members,
        BottomNavItem.Loans,
        BottomNavItem.Learn
    )

    Scaffold(

        containerColor = AppBackground,

        bottomBar = {

            NavigationBar(

                containerColor = CardBackground,

                tonalElevation = 8.dp,

                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )

            ) {

                items.forEachIndexed { index, item ->

                    NavigationBarItem(

                        selected =
                            selectedItem == index,

                        onClick = {

                            selectedItem = index
                        },

                        icon = {

                            Icon(
                                imageVector = item.icon,
                                contentDescription =
                                    stringResource(item.title)
                            )
                        },

                        label = {

                            Text(
                                stringResource(item.title)
                            )
                        },

                        colors =
                            NavigationBarItemDefaults.colors(

                                selectedIconColor =
                                    CardBackground,

                                selectedTextColor =
                                    PrimaryGreen,

                                indicatorColor =
                                    PrimaryGreen,

                                unselectedIconColor =
                                    SecondaryText,

                                unselectedTextColor =
                                    SecondaryText
                            )
                    )
                }
            }
        }

    ) { paddingValues ->

        Surface(

            modifier =
                Modifier.padding(paddingValues),

            color = AppBackground

        ) {

            when (selectedItem) {

                0 -> DashboardScreen()

                1 -> MembersScreen()

                2 -> LoansScreen()

                3 -> LearnScreen()
            }
        }
    }
}
