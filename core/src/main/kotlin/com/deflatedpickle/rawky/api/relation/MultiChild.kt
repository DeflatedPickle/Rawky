package com.deflatedpickle.rawky.api.relation

interface MultiChild<T> : Relationship, Child {
    val parents: MutableList<T>
}