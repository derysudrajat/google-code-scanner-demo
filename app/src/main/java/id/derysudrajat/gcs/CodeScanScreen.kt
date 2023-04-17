package id.derysudrajat.gcs

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import id.derysudrajat.gcs.ui.theme.GoogleCodeScannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeScanScreen() {
    val context = LocalContext.current
    val scanner = getScanner(context)
    var result by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Google Code Scanner Demo", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                result = Resource.TEXT_BLANK
                checkModule(context, scanner) { isSuccess, message ->
                    if (isSuccess) scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            result = barcode.rawValue.toString()
                        }
                        .addOnFailureListener { error ->
                            result = error.message.toString()
                        }
                    else result = message
                }
            }) {
                Text(text = Resource.TEXT_SCAN_HERE)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(visible = result.isNotBlank()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (result == Resource.TEXT_FAILED_SCAN) Resource.TEXT_RESULT_FAILED
                        else Resource.TEXT_RESULT_SUCCESS
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                        border = BorderStroke(2.dp, Color.Black.copy(0.8f))
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = result
                        )
                    }
                }
            }

        }
    }
}

private object Resource {
    const val TEXT_SCAN_HERE = "Scan Here"
    const val TEXT_BLANK = ""
    const val TEXT_FAILED_SCAN = "Failed to scan code."
    const val TEXT_RESULT_FAILED = "Failed:"
    const val TEXT_RESULT_SUCCESS = "Result:"
}

fun getScanner(context: Context): GmsBarcodeScanner = GmsBarcodeScanning.getClient(
    context, GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_PDF417,
            Barcode.TYPE_ISBN
        )
        .build()
)