package com.example.masterprojet1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class ContactForm : ComponentActivity() {
    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    var context = LocalContext.current
                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                Text("SPR MENU", modifier = Modifier.padding(16.dp))
                                Divider()
                                NavigationDrawerItem(
                                    label = { Text(text = "Profile") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ProfileActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Historique") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, DeviceActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Ankle Bracelet Parametrization") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, AnkleBraceletParametrization::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "LEDs Ribbon Parametrization") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, DeviceActivity_LEDs::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Help") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, HelpActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Who are we ? What is SoundPaceRunners ?") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, CreatorsActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Contact Form") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ContactForm::class.java)
                                        startActivity(intent) }
                                )
                            }
                        }
                    ){
                        Column(
                            modifier = Modifier.fillMaxSize() // Utilisez Modifier.fillMaxSize() pour que la colonne occupe toute la taille disponible
                        ) {
                            ContactFormContent()
                            Spacer(modifier = Modifier.height(80.dp)) // Ajoutez un espace vertical de 16dp
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.CenterHorizontally) // Alignez le contenu au centre horizontalement
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.instagramicon),
                                    contentDescription = "Instagram Icon",
                                    modifier = Modifier
                                        .size(50.dp) // Ajustez la taille selon vos besoins
                                        .align(Alignment.Center) // Centrez l'image horizontalement et verticalement
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data = Uri.parse("https://www.instagram.com/soundpace_runners/")
                                            intent.setPackage("com.instagram.android")
                                            startActivity(intent)
                                        }
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}
// Fonction pour envoyer un email dans une coroutine
@SuppressLint("QueryPermissionsNeeded")
fun sendEmailAsync(
    senderEmail: String,
    recipientEmail: String,
    subject: String,
    messageText: String,
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val messageWithSender = "Sender: $senderEmail\nMessage: $messageText" // Ajouter les adresses de l'expéditeur et de la destination au contenu du message
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, messageWithSender) // Utiliser le message avec les adresses de l'expéditeur et de la destination
        val chooserIntent = Intent.createChooser(intent, "Send Email")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooserIntent)
            onSuccess()
        } else {
            throw Exception("No email app found")
        }
    } catch (e: Exception) {
        onError(e.message ?: "Unknown error occurred")
    }
}

@SuppressLint("IntentReset")
@Composable
fun ContactFormContent() {
    val context = LocalContext.current
    val senderEmail = remember { mutableStateOf("") }
    val emailPassword = remember { mutableStateOf("") } // Nouveau champ pour le mot de passe
    val subject = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val recipientEmail = "soundpacerunners@gmail.com"
    val messageText = "Contenu de l'email"

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Form",
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Champs de texte pour l'expéditeur (senderEmail)
            TextField(
                value = senderEmail.value,
                onValueChange = { senderEmail.value = it },
                label = { Text("Your Email Address") }, // Modifier le libellé
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nouveau champ de texte pour le mot de passe
            TextField(
                value = emailPassword.value,
                onValueChange = { emailPassword.value = it },
                label = { Text("Your Email Password") }, // Modifier le libellé
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = subject.value,
                onValueChange = { subject.value = it },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    sendEmailAsync(
                        senderEmail = senderEmail.value,
                        recipientEmail = recipientEmail,
                        subject = subject.value,
                        messageText = message.value,
                        context = context, // Passer le contexte de l'activité
                        onSuccess = {
                            Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show()
                            // Gérer le succès de l'envoi d'email, si nécessaire
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, "Failed to send email: $errorMessage", Toast.LENGTH_SHORT).show()
                            // Gérer l'échec de l'envoi d'email, si nécessaire
                        }
                    )
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Send Email")
            }
        }
    }
}