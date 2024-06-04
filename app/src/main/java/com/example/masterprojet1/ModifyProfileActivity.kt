package com.example.masterprojet1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.ExecutorService

class ModifyProfileActivity : ComponentActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentUsername = mutableStateOf("")
        val photoUrl = mutableStateOf("")

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
                                    label = { Text(text = "Start a training") },
                                    selected = false,
                                    onClick = { val intent = Intent(context, ScanActivity::class.java)
                                        startActivity(intent) }
                                )
                                NavigationDrawerItem(
                                    label = { Text(text = "Historique") },
                                    selected = false,
                                    onClick = {
                                        val intent = Intent(context, DeviceActivity::class.java)
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
                                        val intent =
                                            Intent(context, DeviceActivity_LEDs::class.java)
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

                        val database =
                            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
                        val reference = database.getReference("RegisterUser")

                        val (userInput1, setUserInput1) = remember { mutableStateOf(currentUsername.value) }
                        val (userInput3, setUserInput3) = remember {
                            mutableStateOf(
                                if (photoUrl.value.isEmpty()) {
                                    "No photo"
                                } else {
                                    photoUrl.value
                                }
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.soundpacerunnerslogo),
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(180.dp) // Définir la taille de l'image
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                            }

                            item {
                                Text(
                                    text = "Username",
                                    textAlign = TextAlign.Start,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                            item {
                                TextField(
                                    value = currentUsername.value,
                                    onValueChange = {
                                        setUserInput1(it)
                                        currentUsername.value = it
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                            item {
                                Button(
                                    onClick = {
                                        Log.d("UserInput", "Texte saisi : $userInput1")
                                        postmodifydata_user(uidUser ?: "", userInput1)
                                        Toast.makeText(
                                            context,
                                            "Validate username changes done with success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text("Validate username changes")
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                            item {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 14.dp)
                                        .fillMaxWidth(),
                                    thickness = 2.dp,
                                    color = Color.Blue
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            item {
                                Text(
                                    text = "User Photo",
                                    textAlign = TextAlign.Start,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                            item {
                                TextField(
                                    value = photoUrl.value,
                                    onValueChange = {
                                        setUserInput3(it)
                                        photoUrl.value = it
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                            item {
                                Button(
                                    onClick = {
                                        Log.d("UserInput", "Texte saisi : $userInput3")
                                        postmodifydata_photo(uidUser ?: "", userInput3)
                                        Toast.makeText(
                                            context,
                                            "Validate photoUrl changes done with success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text("Validate photo url changes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findUsernameByEmail(email: String, callback: (String?) -> Unit) {
        val usersRef =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("RegisterUser")
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
        val usersRef =
            FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("RegisterUser")
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

    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}


private fun postmodifydata_user(userId: String, newdata: String) {

    val database =
        FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("RegisterUser").child(userId)

    val updates = hashMapOf<String, Any>(
        "username" to newdata
    )

    userRef.updateChildren(updates)
        .addOnSuccessListener {
            Log.d("UpdateUsername", "Username updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.e("UpdateUsername", "Error updating username: ${e.message}")
        }

}

private fun postmodifydata_photo(userId: String, newdata: String) {

    val database =
        FirebaseDatabase.getInstance("https://master2-20e46-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("RegisterUser").child(userId)

    val updates = hashMapOf<String, Any>(
        "photoUrl" to newdata
    )

    userRef.updateChildren(updates)
        .addOnSuccessListener {
            Log.d("UpdatephotoUrl", "photoUrl updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.e("UpdatephotoUrl", "Error updating photoUrl: ${e.message}")
        }

}