package com.example.a279project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a279project.ui.theme._279projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _279projectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(name = "Android")
                        Spacer(modifier = Modifier)
                        DeleteButton(onClick = { deleteDatabase() })
                    }
                }
            }
        }
    }

    /**
     * Function to delete the database named "listings.db"
     */
    private fun deleteDatabase() {
        val dbName = "listings.db"
        val isDeleted = deleteDatabase(dbName)

        if (isDeleted) {
            println("Database $dbName deleted successfully.")
        } else {
            println("Failed to delete database $dbName. It may not exist.")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun DeleteButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Delete Database")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _279projectTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting("Android")
            Spacer(modifier = Modifier)
            DeleteButton(onClick = {})
        }
    }
}
