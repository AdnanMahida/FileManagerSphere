package com.etb.filemanager.files.util

/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

open class MapSet<K, V> : AbstractMutableSet<V> {
    private val keyExtractor: (V) -> K

    private val map: MutableMap<K, V>

    constructor(keyExtractor: (V) -> K) : this(keyExtractor, false)

    protected constructor(keyExtractor: (V) -> K, isLinked: Boolean) : super() {
        this.keyExtractor = keyExtractor
        this.map = if (isLinked) linkedMapOf() else hashMapOf()
    }

    override fun iterator(): MutableIterator<V> = map.values.iterator()

    override val size: Int
        get() = map.size

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun contains(element: V): Boolean = map.containsKey(keyExtractor(element))

    override fun add(element: V): Boolean = map.put(keyExtractor(element), element) == null

    override fun remove(element: V): Boolean = map.remove(keyExtractor(element)) != null

    override fun clear() {
        map.clear()
    }
}

open class LinkedMapSet<K, V>(keyExtractor: (V) -> K) : MapSet<K, V>(keyExtractor, true)