package com.example.daterangeexporter.core.utils

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

inline fun <K, V> LazyListScope.items(
    items: Map<K, V>,
    noinline key: ((item: Map.Entry<K, V>) -> Any)? = null,
    noinline contentType: (item: Map.Entry<K, V>) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: Map.Entry<K, V>) -> Unit
) {
    val entries = items.entries.toList()

    items(
        count = items.size,
        key = if (key != null) { index: Int -> key(entries[index]) } else null,
        contentType = { index: Int -> contentType(entries[index]) }
    ) {
        itemContent(entries[it])
    }
}
