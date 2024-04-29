package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme

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
                                Text("SPR MENU", modifier = Modifier.padding(16.dp))
                                Divider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Contact") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ContactForm::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "LED parametrization") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, LEDsParamActivity::class.java)
                                        startActivity(intent) }
                                )
                                // ...other drawer items
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
                                    .size(width = 80.dp, height = 80.dp), // Redimensionner l'image ici
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
            }
        }
    }
}
