package com.ms.im.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector

class Buttons {

    // General Use Button
    @Composable
    fun Generic(onClick: () -> Unit, placeholder: String) {
        Button(onClick = { onClick() },
            modifier = Modifier
                .height(70.dp),
            shape = RoundedCornerShape(0.dp)
            )
        {
            Text(placeholder, fontSize = 20.sp)
        }
    }

    @Composable
    fun Generic(onClick: () -> Unit, placeholder: String, modifier: Modifier) {
        Button(onClick = { onClick() },
            modifier = modifier
                .height(70.dp),
            shape = RoundedCornerShape(0.dp)
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
}