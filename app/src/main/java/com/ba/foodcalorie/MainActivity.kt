package com.ba.foodcalorie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ba.foodcalorie.ui.theme.FoodCalorieTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import com.ba.foodcalorie.data.Food
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodCalorieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    var queryState by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            SmallTopAppBarExample(
                queryState = queryState,
                onQueryChange = { newQuery -> queryState = newQuery }, // Lambda ifadesi ile queryState'i güncelle
                onCloseClicked = {
                    keyboardController?.hide()
                    queryState = ""
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FoodList(query = queryState) // Filtrelenmiş liste gösterimi
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample(
    queryState: String,
    onQueryChange: (String) -> Unit, // Callback fonksiyonunu parametre olarak ekleme
    onCloseClicked: () -> Unit
) {
    // Burada, queryState ve onQueryChange kullanılır
    var showSearchBar by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    TopAppBar(title = {
        if (showSearchBar) {
            TextField(
                value = queryState,
                onValueChange = onQueryChange, // TextField değeri değiştiğinde callback'i çağır
                singleLine = true,
                placeholder = { Text(stringResource(id = R.string.search)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    // Burada arama işlemi için gerekli kodu ekleyin.
                    showSearchBar = false
                }),
                // Diğer TextField ayarları...
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

        } else {
            Text(stringResource(id = R.string.topbar_name))
        }

    },
        actions = {
            if (showSearchBar) {
                // Arama çubuğu aktifken kapatma (X) simgesi göster
                IconButton(onClick = {
                    showSearchBar = false
                    onCloseClicked() // Kapatma işlevini çağır
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "Kapat")
                }
            } else {
                // Arama çubuğu pasifken arama simgesi göster
                IconButton(onClick = { showSearchBar = true }) {
                    Icon(Icons.Filled.Search, contentDescription = "Ara")
                }
            }
        })


}

@Composable
fun FoodCard(food: Food, onDeleteConfirm: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ConfirmDeleteDialog(onDismiss = { showDialog = false }, onConfirm = {
            onDeleteConfirm()
            showDialog = false
        })
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${food.calories} ${stringResource(id = R.string.calorie)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete_it))
            }
        }
    }
}


@Composable
fun FoodList(query: String) {
    val allFoods = remember { mutableStateListOf(
        Food(1, "Elma", 95),
        Food(2, "Muz", 105),
        Food(3, "Tavuk Göğsü (100g)", 165),
        Food(4, "Yumurta", 155),
        Food(5, "Pizza (100g)", 245),
        Food(6, "Ayran",68),
        Food(7, "Cacık", 234)
    )}

    val filteredFoods = if (query.isEmpty()) {
        allFoods
    } else {
        allFoods.filter { it.name.contains(query, ignoreCase = true) }
    }

    LazyColumn {
        items(items = filteredFoods, key = { it.id }) { food ->
            FoodCard(food = food) {
                allFoods.remove(food)
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.delete_confirmation)) },
        text = { Text(stringResource(id = R.string.are_you_sure_deleting)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.no))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodCalorieTheme {
        //FoodList()
    }
}