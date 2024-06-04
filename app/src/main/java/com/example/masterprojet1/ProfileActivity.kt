package com.example.masterprojet1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentUsername = mutableStateOf("")
        var photoUrl = mutableStateOf("")

        val authEmail = FirebaseAuth.getInstance().currentUser?.email
        if (authEmail != null) {
            Log.d("EMAIL", "Email: $authEmail")
            findUsernameByEmail(authEmail) { username ->
                if (username != null) {
                    currentUsername.value = username
                    Log.d("ProfileActivity", "Username found: $username")
                } else {
                    Log.d("ProfileActivity", "Username not found for email: $authEmail")
                }
            }
            findPhotoUrlByEmail(authEmail) { url ->
                if (url != null) {
                    photoUrl.value = url
                    Log.d("ProfileActivity", "Photo URL found: $url")
                } else {
                    Log.d("ProfileActivity", "Photo URL not found for email: $authEmail")
                }
            }
        }

        Log.d("currentusername", "currentUsername : ${currentUsername.value}")
        Log.d("photourl", "photoUrl : ${photoUrl.value}")

        val uidUser = FirebaseAuth.getInstance().currentUser?.uid
        if (uidUser != null) {
            Log.d("UID", "Uid: $uidUser")
        }

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
                                    label = { Text(text = "Profile") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ProfileActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Start a training") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ScanActivity::class.java)
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
                    ) {
                        Log.e("currentusernameprintinfos","currentuname : $currentUsername")
                        PrintInfos(
                            currentUsername,
                            ::navigateToModifyActivity,
                            authEmail ?: "",
                            photoUrl
                        )
                    }
                }
            }
        }
    }

    private fun findUsernameByEmail(email: String, callback: (String?) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/").getReference("RegisterUser")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    Log.d("userSnapshot", "userSnapshot : $userSnapshot")
                    val userEmail = userSnapshot.child("email").getValue(String::class.java)
                    Log.d("useremail", "useremailchild : $userEmail")
                    if (userEmail == email) {
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        callback(username)
                        return
                    }
                }
                callback(null) // Email not found
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ProfileActivity", "loadPost:onCancelled", databaseError.toException())
                callback(null)
            }
        })
    }

    private fun findPhotoUrlByEmail(email: String, callback: (String?) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/").getReference("RegisterUser")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    Log.d("userSnapshot", "userSnapshot : $userSnapshot")
                    val userEmail = userSnapshot.child("email").getValue(String::class.java)
                    Log.d("useremail", "useremailchild : $userEmail")
                    if (userEmail == email) {
                        val photoUrl = userSnapshot.child("photoUrl").getValue(String::class.java)
                        callback(photoUrl)
                        return
                    }
                }
                callback(null) // Email not found
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ProfileActivity", "loadPost:onCancelled", databaseError.toException())
                callback(null)
            }
        })
    }

    fun navigateToModifyActivity(email: String) {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        intent.putExtra("Email", email)
        startActivity(intent)
    }

    fun navigateToHistoricActivity(email: String) {
        val intent = Intent(this, Historique::class.java)
        intent.putExtra("Email", email)
        startActivity(intent)
    }

    @Composable
    fun PrintInfos(
        username: MutableState<String>,
        navigateToModifyActivity: (String) -> Unit,
        email: String,
        photourl: MutableState<String>
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()  // Remplir toute la taille disponible
                .padding(16.dp),  // Ajouter un padding autour de la colonne
            horizontalAlignment = Alignment.CenterHorizontally  // Centrer horizontalement les éléments
        ) {
            val imagePainter = painterResource(id = R.drawable.soundpacerunnerslogo)

            // Afficher l'image
            Image(
                painter = imagePainter,
                contentDescription = "SoundPaceRunners logo",
                modifier = Modifier.size(100.dp) // Modifier la taille selon vos besoins
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Profile Page",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Afficher le nom d'utilisateur
            Text(
                text = if (username.value.isNotEmpty()) username.value else "No username",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
            Log.d("affichage photo", "photo : ${photourl.value}")
            // Afficher l'image si l'URL est non vide
            if (photourl.value.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(photourl.value),
                    contentDescription = "User Photo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 8.dp)
                )
            } else {
                // Afficher un message si aucune photo n'est enregistrée
                Text(
                    text = "No photo registered",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Modify",
                modifier = Modifier
                    .clickable { navigateToModifyActivity(email) }
                    .border(
                        width = 1.dp,
                        color = Color.LightGray
                    )
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Access to my activities historic",
                modifier = Modifier
                    .clickable { navigateToHistoricActivity(email) }
                    .border(
                        width = 1.dp,
                        color = Color.LightGray
                    )
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


