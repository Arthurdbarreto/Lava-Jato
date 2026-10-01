package com.example.lavajato.ui.components

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
import com.example.lavajato.ui.theme.NavyDark
import com.example.lavajato.ui.theme.TextGraySecondary
import com.example.lavajato.ui.theme.YellowAccent

enum class BottomTab { INICIO, AGENDAMENTOS, PERFIL }

/**
 * Bottom navigation reutilizável, presente na Home e em Meus Agendamentos.
 */
@Composable
fun BottomNavigationBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar(containerColor = NavyDark) {
        NavigationBarItem(
            selected = selected == BottomTab.INICIO,
            onClick = { onSelect(BottomTab.INICIO) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selected == BottomTab.AGENDAMENTOS,
            onClick = { onSelect(BottomTab.AGENDAMENTOS) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agendamentos") },
            label = { Text("Agendamentos") },
            colors = navColors()
        )
        NavigationBarItem(
            selected = selected == BottomTab.PERFIL,
            onClick = { onSelect(BottomTab.PERFIL) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = YellowAccent,
    selectedTextColor = YellowAccent,
    unselectedIconColor = TextGraySecondary,
    unselectedTextColor = TextGraySecondary,
    indicatorColor = NavyDark
)
