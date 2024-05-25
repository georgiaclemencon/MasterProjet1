package com.example.masterprojet1


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme


@Composable
fun AnimatedText() {
    // Définir un état animé pour l'opacité
    val opacity = animateFloatAsState(
        targetValue = 1f, // L'opacité cible est 1 (complètement visible)
        animationSpec = tween(
            durationMillis = 2000, // L'animation durera 2000 millisecondes (2 secondes)
            easing = LinearEasing // L'animation sera linéaire
        )
    )

    // Utiliser l'opacité animée dans le composant Text
    Text(
        text = "Run with precision, run with SoundPaceRunners",
        modifier = Modifier

            .padding(top = 150.dp)
            .alpha(opacity.value), // Utiliser l'opacité animée ici
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    )
}

class ActivityMenu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    var context = LocalContext.current
                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Text("MENU", modifier = Modifier.padding(16.dp))
                                HorizontalDivider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Profile") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, ProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Demarer une course") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, DeviceActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Historique") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, Historique::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Ankle Bracelet Parametrization") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(
                                            context,
                                            AnkleBraceletParametrization::class.java
                                        )
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "LEDs Ribbon Parametrization") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, LEDsParamActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Help") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, HelpActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Who are we ? What is SoundPaceRunners ?") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, CreatorsActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Contact Form") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, ContactForm::class.java)
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.soundpacerunnerslogo),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(width = 240.dp, height = 240.dp)
                                    .align(Alignment.TopCenter),
                                contentScale = ContentScale.FillBounds
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Run with precision, run with SoundPaceRunners",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Red
                                    )
                                )
                            }
//                            Button(
//                                onClick = { FirebaseAuth.getInstance().signOut() },
//                                modifier = Modifier
//                                    .size(50.dp) // Définir la taille du bouton
//                                    .align(Alignment.TopEnd) // Positionner le bouton en haut à droite
//                                    .padding(
//                                        top = 26.dp,
//                                        end = 26.dp
//                                    ), // Ajouter un padding en haut et à droite
//                                shape = CircleShape // Définir la forme du bouton comme un cercle
//                            ) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.ic_logout), // Utiliser une icône de déconnexion
//                                    contentDescription = "Sign Out"
//                                )
//                            }
                           IconButton(
    onClick = { /* Votre action ici */ },
    modifier = Modifier
        .align(Alignment.TopStart) // Positionne l'icône en haut à gauche
) {
    Icon(
        imageVector = Icons.Filled.Menu,
        contentDescription = "Menu"
    )
}
                            Button(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            context,
                                            ScanActivity::class.java
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 320.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "se connecter au bracelet",
                                )
                            }
                        }

                    }


                }


            }

        }
    }
}