package com.deflatedpickle.rawky.api.relation

// A matrix can belong to a parent as well as have children
interface Matrix<P, C> : SingleChild<P>, MultiParent<C> {
    val rows: Int
    val columns: Int
}