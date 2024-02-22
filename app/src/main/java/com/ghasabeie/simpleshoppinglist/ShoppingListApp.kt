package com.ghasabeie.simpleshoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp() {
    var shoppingListItem by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { showDialog = true }) {
            Text(text = "Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shoppingListItem) { eachItem ->
                if (eachItem.isEditing) {
                    ShoppingItemEditor(item = eachItem,
                        onEditComplete = { savedString, savedQty ->
                            val newList = shoppingListItem.map {
                                if (it.id == eachItem.id) {
                                    it.copy(
                                        isEditing = false,
                                        name = savedString,
                                        quantity = savedQty
                                    )
                                } else {
                                    it
                                }
                            }
                            shoppingListItem = newList

                        })
                } else {
                    ShoppingListItem(
                        item = eachItem,
                        onEditClick = {
                            shoppingListItem =
                                shoppingListItem.map { it.copy(isEditing = it.id == eachItem.id) }

                        }, onDeleteClick = {
                            shoppingListItem = shoppingListItem - eachItem
                        })
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (itemName.isNotBlank()) {
                                val newItem = ShoppingItem(
                                    id = shoppingListItem.size + 1,
                                    name = itemName,
                                    quantity = itemQuantity.toInt()
                                )
                                shoppingListItem = shoppingListItem + newItem
                                showDialog = false
                                itemName = ""
                                itemQuantity = ""
                            }
                        }) {
                            Text(text = "Add")
                        }
                        Button(onClick = {
                            showDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }) {
                            Text(text = "Cancel")
                        }
                    }
                },
                title = { Text("Add Shopping Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            value = itemName,
                            placeholder = {
                                Text(text = "Item Name")
                            },
                            singleLine = true,
                            onValueChange = {
                                itemName = it
                            })
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            value = itemQuantity,
                            placeholder = {
                                Text(text = "Item Quantity")
                            },
                            singleLine = true,
                            onValueChange = {
                                itemQuantity = it
                            })
                    }
                }
            )

        }
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(2.dp, Color.Gray),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editorName by remember { mutableStateOf(item.name) }
    var editorQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editorName,
                onValueChange = { editorName = it },
                singleLine = true,
                modifier = Modifier
                    .padding(8.dp)
            )
            BasicTextField(
                value = editorQuantity,
                onValueChange = { editorQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        Button(onClick = {
            isEditing = false
            onEditComplete(editorName, editorQuantity.toIntOrNull() ?: 1)
        }) {
            Text(text = "save")
        }
    }
}
