package com.example.daterangeexporter.core.utils

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

inline fun <K, V> LazyListScope.itemsIndexed(
    items: Map<K, V>,
    noinline key: ((index: Int, item: Map.Entry<K, V>) -> Any)? = null,
    noinline contentType: (index: Int, item: Map.Entry<K, V>) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: Map.Entry<K, V>) -> Unit
) {
    val entries = items.entries.toList()

    items(
        count = items.size,
        key = if (key != null) { index: Int -> key(index, entries[index]) } else null,
        contentType = { index: Int -> contentType(index, entries[index]) }
    ) {index ->
        itemContent(index, entries[index])
    }
}
