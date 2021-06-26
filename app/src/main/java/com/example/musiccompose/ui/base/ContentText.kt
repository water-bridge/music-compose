package com.example.musiccompose.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContentText(
    text: String
) {
    Text(
        text = text,
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 20.dp),
        textAlign = TextAlign.Center
    )
    Divider(
        thickness = 2.dp,
        modifier = Modifier.padding(5.dp)
    )
}