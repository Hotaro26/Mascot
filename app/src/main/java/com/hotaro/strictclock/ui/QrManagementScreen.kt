package com.hotaro.strictclock.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hotaro.strictclock.ui.challenges.QRScannerView
import com.hotaro.strictclock.ui.theme.*

data class QrCodeModel(val name: String, val data: String)

fun loadQrs(context: Context): List<QrCodeModel> {
    val prefs = context.getSharedPreferences("qr_prefs", Context.MODE_PRIVATE)
    val jsonString = prefs.getString("saved_qrs", "[]") ?: "[]"
    return try {
        val array = org.json.JSONArray(jsonString)
        val list = mutableListOf<QrCodeModel>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(QrCodeModel(obj.getString("name"), obj.getString("data")))
        }
        list
    } catch(e: Exception) { emptyList() }
}

fun saveQrs(context: Context, list: List<QrCodeModel>) {
    val array = org.json.JSONArray()
    list.forEach { 
        val obj = org.json.JSONObject()
        obj.put("name", it.name)
        obj.put("data", it.data)
        array.put(obj)
    }
    val prefs = context.getSharedPreferences("qr_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("saved_qrs", array.toString()).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrManagementScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var qrList by remember { mutableStateOf(loadQrs(context)) }
    
    var showScanner by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf<String?>(null) }
    var editQr by remember { mutableStateOf<QrCodeModel?>(null) }
    
    var showNameDialog by remember { mutableStateOf(false) }
    var currentNameInput by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage QR Codes", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = primaryContainerDark,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp).size(40.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onPrimaryContainerDark, modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundDark, titleContentColor = onSurfaceDark, navigationIconContentColor = onSurfaceDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScanner = true },
                containerColor = primaryDark,
                contentColor = onPrimaryDark
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add QR")
            }
        },
        containerColor = backgroundDark
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            if (qrList.isEmpty()) {
                item {
                    Text("No QR Codes saved yet. Click + to add one.", color = onSurfaceVariantDark, modifier = Modifier.padding(16.dp))
                }
            }
            items(qrList) { qr ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { 
                        editQr = qr
                        currentNameInput = qr.name
                        showNameDialog = true
                    },
                    colors = CardDefaults.cardColors(containerColor = surfaceContainerHighDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.QrCode, contentDescription = null, tint = primaryDark, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(qr.name, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = onSurfaceDark)
                            Text("Click to edit name", fontSize = 14.sp, color = onSurfaceVariantDark)
                        }
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = onSurfaceVariantDark)
                    }
                }
            }
        }
        
        if (showScanner) {
            Dialog(onDismissRequest = { showScanner = false; scannedData = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        QRScannerView(
                            onQrDetected = { data ->
                                scannedData = data
                                showScanner = false
                                currentNameInput = "qr_code${qrList.size + 1}"
                                showNameDialog = true
                            }
                        )
                        IconButton(onClick = { showScanner = false }, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                }
            }
        }
        
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showNameDialog = false
                    scannedData = null
                    editQr = null
                },
                title = { Text(if (editQr != null) "Edit QR Name" else "Save QR Code") },
                text = {
                    OutlinedTextField(
                        value = currentNameInput,
                        onValueChange = { currentNameInput = it },
                        label = { Text("Tag Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = onSurfaceDark,
                            unfocusedTextColor = onSurfaceDark,
                            focusedBorderColor = primaryDark,
                            focusedLabelColor = primaryDark
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (currentNameInput.isNotBlank()) {
                            val newList = qrList.toMutableList()
                            if (editQr != null) {
                                val idx = newList.indexOf(editQr)
                                if (idx != -1) {
                                    newList[idx] = newList[idx].copy(name = currentNameInput)
                                }
                            } else if (scannedData != null) {
                                newList.add(QrCodeModel(currentNameInput, scannedData!!))
                            }
                            qrList = newList
                            saveQrs(context, newList)
                        }
                        showNameDialog = false
                        scannedData = null
                        editQr = null
                    }) { Text("Save", color = primaryDark) }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showNameDialog = false
                        scannedData = null
                        editQr = null
                    }) { Text("Cancel", color = onSurfaceVariantDark) }
                },
                containerColor = surfaceContainerHighDark,
                titleContentColor = onSurfaceDark,
                textContentColor = onSurfaceVariantDark
            )
        }
    }
}
