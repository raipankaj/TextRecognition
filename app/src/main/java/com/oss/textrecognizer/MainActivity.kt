package com.oss.textrecognizer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lib.recognizer.TextRecognizer
import com.oss.textrecognizer.ui.theme.TextRecognizerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextRecognizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    TextRecognizer(
                        onSuccess = {
                            Log.e("DATA", "${it.text}")
                        },
                        onException = {
                            Log.e("DATA", "Exception ${it.localizedMessage}")
                        }
                    )
                }
            }
        }
    }
}
