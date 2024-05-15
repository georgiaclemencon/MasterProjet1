package com.example.masterprojet1

//import com.google.firebase.appcheck.internal.util.Logger.TAG
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


enum class AuthState {
    LOGIN, REGISTER
}

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
//        auth.signOut()
        database =
            FirebaseDatabase.getInstance("https://master-42ff9-default-rtdb.europe-west1.firebasedatabase.app/")


        setContent {
            Masterprojet1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    LoginRegisterComponent()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            StartActivity()
        }
    }

    private fun register(email: String, password: String, username: String, photoUrl: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                Toast.makeText(
                    baseContext,
                    "Authentication success.",
                    Toast.LENGTH_SHORT,
                ).show()
                writeNewUser(auth.currentUser!!.uid, username, email, photoUrl)
                Toast.makeText(
                    baseContext,
                    "User created.\nGo to Login.",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun writeNewUser(userId: String, username: String, email: String, photoUrl: String) {
        val usersRef = database.getReference("users").child(userId)
        usersRef.child("username").setValue(username)
        usersRef.child("email").setValue(email)
        usersRef.child("photoUrl").setValue(photoUrl)
        usersRef.child("biographie").setValue("")
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                Toast.makeText(
                    baseContext,
                    "Authentication success.",
                    Toast.LENGTH_SHORT,
                ).show()
                StartActivity()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

        fun StartActivity() {
//        val intent = Intent(this, ScanActivity::class.java)
//        startActivity(intent)

            val intent = Intent(this, ActivityMenu::class.java)
startActivity(intent)
finish()
    }



    @Composable
    fun LoginRegisterComponent() {
        val authState = remember { mutableStateOf(AuthState.LOGIN) }
        when (authState.value) {
            AuthState.LOGIN -> LoginComponent(::login, authState)
            AuthState.REGISTER -> RegisterComponent(::register, authState)
        }
    }

}


@Composable
fun LoginComponent(login: (String, String) -> Unit) {
    Text(text = "Hello, World!")
}

@Composable
fun RegisterComponent(
    register: (String, String, String, String) -> Unit, authState: MutableState<AuthState>
) {
    var email by remember { mutableStateOf("georgia@gmail.com") }
    var password by remember { mutableStateOf("georgia1234") }
    var username by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register")
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))


//            Button(onClick = { pickImageLauncher.launch("image/*") }) {
//                Text("Pick Image")
//            }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                errorMessage = "Veuillez remplir les champs."
                Toast.makeText(
                    context, errorMessage, Toast.LENGTH_SHORT
                ).show()
            } else {
                register(email, password, username, photoUrl)
            }
        }) {
            Text("Create account")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { authState.value = AuthState.LOGIN }) {
            Text("Already have an account ?")

        }

    }
}


@Composable
fun LoginComponent(
    logIn: (String, String) -> Unit, authState: MutableState<AuthState>
) {
    var email by remember { mutableStateOf("georgia@gmail.com") }
    var password by remember { mutableStateOf("georgia1234") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome",
            fontSize = 24.sp,
            color = Color.Green,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Card {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },

                )


            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },

                )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { logIn(email, password) }) {
            Text("Log in")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { authState.value = AuthState.REGISTER }) {
            Text("Create an account")
        }
    }
}