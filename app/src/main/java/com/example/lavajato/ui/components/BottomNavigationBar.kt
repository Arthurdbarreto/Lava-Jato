
package com.example.lavajato.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomTab {
    INICIO,
    AGENDAMENTOS,
    PERFIL
}

private val BluePrimary = Color(0xFF1677FF)
private val GrayInactive = Color(0xFF8A9BB0)

@Composable
fun BottomNavigationBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == BottomTab.INICIO,
            onClick = { onSelect(BottomTab.INICIO) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp
                )
            },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selected == BottomTab.AGENDAMENTOS,
            onClick = { onSelect(BottomTab.AGENDAMENTOS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Agendamentos"
                )
            },
            label = {
                Text(
                    text = "Agendamentos",
                    fontSize = 11.sp
                )
            },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selected == BottomTab.PERFIL,
            onClick = { onSelect(BottomTab.PERFIL) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    fontSize = 11.sp
                )
            },
            colors = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = BluePrimary,
    selectedTextColor = BluePrimary,
    unselectedIconColor = GrayInactive,
    unselectedTextColor = GrayInactive,
    indicatorColor = Color.Transparent
)
