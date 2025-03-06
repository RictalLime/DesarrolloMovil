package com.example.artspace

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.ui.theme.ArtSpaceTheme

data class ArtPiece(val imageId: Int, val title: String, val artist: String, val year: String)

val artList = listOf(
    ArtPiece(R.drawable.image1, "Navegando bajo el puente", "Kat Kuan", "2017"),
    ArtPiece(R.drawable.image2, "Noche Estrellada", "Vincent van Gogh", "1889"),
    ArtPiece(R.drawable.image3, "La Persistencia de la Memoria", "Salvador Dalí", "1931")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtSpaceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ArtSpaceApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun ArtSpaceApp(modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableStateOf(0) }
    val artPiece = artList[currentIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Marco de la imagen
        Box(
            modifier = Modifier
                .size(300.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = artPiece.imageId),
                contentDescription = artPiece.title,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = artPiece.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = artPiece.artist, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = "(${artPiece.year})", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Text("Previous")
            }
            Button(
                onClick = { if (currentIndex < artList.size - 1) currentIndex++ },
                enabled = currentIndex < artList.size - 1
            ) {
                Text("Next")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpacePreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}
