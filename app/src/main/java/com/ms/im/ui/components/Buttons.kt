package com.ms.im.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.ms.im.OrderDirection
import com.ms.im.SortField


class Buttons {

    // General Use Button
    @Composable
    fun Generic(onClick: () -> Unit, placeholder: String, enabled: Boolean = true) {
        Button(onClick = { onClick() },
            modifier = Modifier
                .height(70.dp),
            shape = RoundedCornerShape(0.dp),
            enabled = enabled
            )
        {
            Text(placeholder, fontSize = 20.sp)
        }
    }

    @Composable
    fun Generic(onClick: () -> Unit, placeholder: String, enabled: Boolean = true, modifier: Modifier) {
        Button(onClick = { onClick() },
            modifier = modifier
                .height(70.dp),
            shape = RoundedCornerShape(0.dp),
            enabled = enabled
        )
        {
            Text(placeholder, fontSize = 20.sp)
        }
    }

    // Activity change button
    @Composable
    fun Activity(
        context: Context,
        activityClass: Class<out Activity>,
        placeholder: String) {
        Button(onClick = {
            val intent = Intent(context, activityClass)
            context.startActivity(intent)
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp, 0.dp, 10.dp, 20.dp))
        {
            Text(placeholder, fontSize = 20.sp)
        }
    }

    @Composable
    fun Icon(onClick: () -> Unit, imageVector: ImageVector) {
        Button(onClick = { onClick() },
            modifier = Modifier
                .height(70.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Icon(imageVector = imageVector, contentDescription = "Icon")
        }
    }

    @Composable
    fun <T> Cycle(
        options: List<T>,
        selected: T,
        onOptionChange: (T) -> Unit,
        labelMapper: (T) -> String = { it.toString() }
    ) {
        val currentIndex = options.indexOf(selected)
        val nextIndex = (currentIndex + 1) % options.size

        Generic({ onOptionChange(options[nextIndex]) },
            labelMapper(selected),
            modifier = Modifier.width(150.dp))
    }

    @Composable
    fun ItemInteractable(
        onClick: () -> Unit,
        onDoubleClick: () -> Unit,
        content: @Composable () -> Unit,
        modifier: Modifier = Modifier
    ) {
        var lastClickTime by remember { mutableLongStateOf(0L) }
        val doubleClickThreshold = 300L // milliseconds

        Button(
            onClick = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime <= doubleClickThreshold) {
                    onDoubleClick()
                    lastClickTime = 0L // reset to avoid triple-clicks
                } else {
                    onClick()
                    lastClickTime = currentTime
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }

    @Composable
    fun SortFieldRadio(
        current: SortField,
        field: SortField,
        label: String,
        onSelect: (SortField) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = current == field,
                onClick = { onSelect(field) }
            )
            Text(label)
        }
    }

    @Composable
    fun SortDirectionRadio(
        current: OrderDirection,
        direction: OrderDirection,
        label: String,
        onSelect: (OrderDirection) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = current == direction,
                onClick = { onSelect(direction) }
            )
            Text(label)
        }
    }
}