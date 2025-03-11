package com.example.planetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetas.ui.theme.PlanetasTheme

data class Planet(val imageId: Int, val name: String, val gravity: Double, val info: String)

val planetList = listOf(
    Planet(R.drawable.luna, "Luna", 0.165, "Categoría: Satélite natural\nÓrbita: Tierra\nDistancia estelar: 356,565 km\nMagnitud aparente: -12.6"),
    Planet(R.drawable.marte, "Marte", 0.38, "Categoría: Planeta\nÓrbita: Sol\nDistancia estelar: 54,600,000 km\nMagnitud aparente: 1.6 a -3"),
    Planet(R.drawable.venus, "Venus", 0.91, "Categoría: Planeta\nÓrbita: Sol\nDistancia estelar: 261,000,000 km\nMagnitud aparente: -4.4")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanetasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PlanetApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PlanetApp(modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val planet = planetList[currentIndex]
    var weight by remember { mutableStateOf("") }
    var calculatedWeight by remember { mutableDoubleStateOf(0.0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen del planeta
        Box(
            modifier = Modifier
                .size(250.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = planet.imageId),
                contentDescription = planet.name,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información del planeta
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = planet.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = planet.info, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Entrada para el peso
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Ingresa tu peso (kg)") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            calculatedWeight = weight.toDoubleOrNull()?.times(planet.gravity) ?: 0.0
        }) {
            Text("Calcular tu peso en ${planet.name}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Tu peso en ${planet.name} es: ${"%.2f".format(calculatedWeight)} kg", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Text("Planeta anterior")
            }
            Button(
                onClick = { if (currentIndex < planetList.size - 1) currentIndex++ },
                enabled = currentIndex < planetList.size - 1
            ) {
                Text("Planeta siguiente")
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun PlanetAppPreview() {
    PlanetasTheme {
        PlanetApp()
    }
}
