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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masterprojet1.model.User
import com.example.masterprojet1.ui.theme.MasterProjet1Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.concurrent.ExecutorService

class ModifyProfileActivity : ComponentActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentUsername = mutableStateOf("")
        val authEmail = FirebaseAuth.getInstance().currentUser?.email
        if (authEmail != null) {
            Log.d("EMAIL", "Email: $authEmail")
            GetUserfromEmail(authEmail, currentUsername)
        }

        val uidUser = FirebaseAuth.getInstance().currentUser?.uid
        if (uidUser != null) {
            Log.d("UID", "Uid: $uidUser")
            //GetUserfromEmail(authEmail, currentUsername)
        }

        var biography = mutableStateOf("")
        getBiography(uidUser ?: "", currentUsername, authEmail ?: "", biography)

        var photourl = mutableStateOf("")
        getPhotoUrl(uidUser ?: "", currentUsername, authEmail ?: "", photourl)

        setContent {
            MasterProjet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database =
                        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
                    val reference = database.getReference("user")
                    val (userInput1, setUserInput1) = remember { mutableStateOf(currentUsername.value) }
                    val (userInput2, setUserInput2) = remember {
                        mutableStateOf(
                            if (biography.value.isEmpty()) {
                                "No biography"
                            } else {
                                biography.value
                            }
                        )
                    }
                    val (userInput3, setUserInput3) = remember {
                        mutableStateOf(
                            if (photourl.value.isEmpty()) {
                                "No photo"
                            } else {
                                photourl.value
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

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        //verticalArrangement = Arrangement.Center,
                        //horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(240.dp)) // Espacement vertical de 16 dp

                        Text(
                            text = "Username",
                            textAlign = TextAlign.Start, // Aligner le texte vers la gauche
                            fontSize = 20.sp, // Taille du texte
                            modifier = Modifier.padding(start = 16.dp) // Espacement à gauche

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = userInput1,
                            onValueChange = {
                                setUserInput1(it)
                                // Mettez à jour currentUsername avec la nouvelle valeur entrée
                                currentUsername.value = it
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = {
                                // Utilisez la valeur de userInput comme bon vous semble
                                Log.d("UserInput", "Texte saisi : $userInput1")
                                postmodifydata_user(uidUser ?: "", userInput1)
                                //postdata(uidUser ?: "", currentUsername.value)
                                // Afficher un toast pour indiquer que les données ont été validées
                                Toast.makeText(
                                    applicationContext,
                                    "Validate username changes done with success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .padding(16.dp) // Ajoutez un padding de 16 dp
                                .widthIn(max = 200.dp) // Limitez la largeur maximale à 200 dp
                        ) {
                            Text("Validate username changes")
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Divider(
                            color = Color.Blue, // Couleur de la ligne
                            thickness = 2.dp, // Épaisseur de la ligne en pixels
                            modifier = Modifier
                                .padding(horizontal = 14.dp) // Espacement horizontal
                                .fillMaxWidth() // Remplit toute la largeur disponible
                                .align(Alignment.CenterHorizontally) // Alignement horizontal au centre
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "User description",
                            textAlign = TextAlign.Start, // Aligner le texte vers la gauche
                            fontSize = 20.sp, // Taille du texte
                            modifier = Modifier.padding(start = 16.dp) // Espacement à gauche

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = userInput2,
                            onValueChange = {
                                setUserInput2(it)
                                biography.value = it
                            },
                            //label = { Text("Text Field 1") }
                            modifier = Modifier.padding(horizontal = 16.dp) // Espacement à gauche et à droite

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = {
                                // Utilisez la valeur de userInput comme bon vous semble
                                Log.d("UserInput", "Texte saisi : $userInput2")
                                postmodifydata_bio(uidUser ?: "", userInput2)
                                //postmodifydata_bio(uidUser ?: "", biography.value)
                                // Afficher un toast pour indiquer que les données ont été validées
                                Toast.makeText(
                                    applicationContext,
                                    "Validate biography changes done with success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .padding(16.dp) // Ajoutez un padding de 16 dp
                                .widthIn(max = 200.dp) // Limitez la largeur maximale à 200 dp
                        ) {
                            Text("Validate biography changes")
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Divider(
                            color = Color.Blue, // Couleur de la ligne
                            thickness = 2.dp, // Épaisseur de la ligne en pixels
                            modifier = Modifier
                                .padding(horizontal = 14.dp) // Espacement horizontal
                                .fillMaxWidth() // Remplit toute la largeur disponible
                                .align(Alignment.CenterHorizontally) // Alignement horizontal au centre
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "User Photo",
                            textAlign = TextAlign.Start, // Aligner le texte vers la gauche
                            fontSize = 20.sp, // Taille du texte
                            modifier = Modifier.padding(start = 16.dp) // Espacement à gauche

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = userInput3,
                            onValueChange = {
                                setUserInput3(it)
                                photourl.value = it
                            },
                            //label = { Text("Text Field 1") }
                            modifier = Modifier.padding(horizontal = 16.dp) // Espacement à gauche et à droite

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = {
                                // Utilisez la valeur de userInput comme bon vous semble
                                Log.d("UserInput", "Texte saisi : $userInput3")
                                postmodifydata_photo(uidUser ?: "", userInput3)
                                //postmodifydata_photo(uidUser ?: "", photourl.value)
                                // Afficher un toast pour indiquer que les données ont été validées
                                Toast.makeText(
                                    applicationContext,
                                    "Validate photoUrl changes done with success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .padding(16.dp) // Ajoutez un padding de 16 dp
                                .widthIn(max = 200.dp) // Limitez la largeur maximale à 200 dp
                        ) {
                            Text("Validate photo url changes")
                        }

                    }
                    //ActionBarModifyProfile(navigateFunction = ::startActivity)
                }
            }
        }
    }
    fun getBiography(uid: String, username: MutableState<String>, email: String, biography: MutableState<String>) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val userRef = database.getReference("user").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    biography.value = user.age
                    Log.d("UserBiography", "Biography: $biography")
                    // Utilisez la variable biography comme vous le souhaitez
                } else {
                    Log.d("UserBiography", "User not found.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserBiography", "Database error: ${databaseError.message}")
            }
        })
    }

    fun getPhotoUrl(uid: String, username: MutableState<String>, email: String, photourl: MutableState<String>) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val userRef = database.getReference("user").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    photourl.value = user.photoUrl
                    Log.d("UserPhotoUrl", "PhotoUrl: $photourl")
                    // Utilisez la variable biography comme vous le souhaitez
                } else {
                    Log.d("UserPhotoUrl", "User not found.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserPhotoUrl", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun GetUserfromEmail(email: String, returnUsername: MutableState<String>) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("user")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.email == email) {
                            returnUsername.value = user.username
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(FirebaseRemoteConfig.TAG, "Failed to read value.", error.toException())
            }
        })
    }
}


private fun postmodifydata_user(key: String, newdata : String) {

    //val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    //val myRef = database.getReference("user")

// Supposez que vous avez la clé de l'élément que vous souhaitez mettre à jour
    //val key = "clé_de_l'élément"

// Obtenez une référence à l'élément que vous souhaitez mettre à jour en utilisant sa clé
    //val elementRef = myRef.child(key)

// Mettez à jour les données de l'élément
    //val newData =
    //elementRef.setValue(newData) // Pour remplacer complètement les données de l'élément

    val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("user").child(key)

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

private fun postmodifydata_bio(key: String, newdata : String) {

    //val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    //val myRef = database.getReference("user")

// Supposez que vous avez la clé de l'élément que vous souhaitez mettre à jour
    //val key = "clé_de_l'élément"

// Obtenez une référence à l'élément que vous souhaitez mettre à jour en utilisant sa clé
    //val elementRef = myRef.child(key)

// Mettez à jour les données de l'élément
    //val newData =
    //elementRef.setValue(newData) // Pour remplacer complètement les données de l'élément

    val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("user").child(key)

    val updates = hashMapOf<String, Any>(
        "biographie" to newdata
    )

    userRef.updateChildren(updates)
        .addOnSuccessListener {
            Log.d("UpdateBiography", "Biography updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.e("UpdateBiography", "Error updating Biography: ${e.message}")
        }

}

private fun postmodifydata_photo(key: String, newdata : String) {

    //val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    //val myRef = database.getReference("user")

// Supposez que vous avez la clé de l'élément que vous souhaitez mettre à jour
    //val key = "clé_de_l'élément"

// Obtenez une référence à l'élément que vous souhaitez mettre à jour en utilisant sa clé
    //val elementRef = myRef.child(key)

// Mettez à jour les données de l'élément
    //val newData =
    //elementRef.setValue(newData) // Pour remplacer complètement les données de l'élément

    val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("user").child(key)

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


/*
@Composable
fun ReceivedData(receivedValue: MutableState<String>) {
    // Créez un MutableState pour stocker la valeur reçue
    //val receivedValue = remember { mutableStateOf("") }

    // Récupérez la valeur depuis Firebase et mettez à jour le MutableState
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("tmp")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mettre à jour le MutableState avec la valeur récupérée
                receivedValue.value = dataSnapshot.getValue(String::class.java) ?: ""
                Log.d(TAG, "Value is: ${receivedValue.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    // Affichez la valeur dans votre interface utilisateur
    Text(
        text = receivedValue.value,
        //style = TextStyle(fontSize = 20.sp)
    )
}

@Composable
fun DisplayReceivedValue(receivedValue: String) {
    Text(
        text = receivedValue,
        //style = TextStyle(fontSize = 20.sp)
    )
}
*/
/*
@Composable
fun ActionBarModifyProfile(navigateFunction: (Class<*>) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = {
                navigateFunction(MainActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = { FindActivity::class.java },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.find),
                contentDescription = "Find",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(NewPublicationActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(MapActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.map),
                contentDescription = "Map",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(UserProfileActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
*/

