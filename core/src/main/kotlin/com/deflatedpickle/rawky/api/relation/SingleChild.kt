package com.deflatedpickle.rawky.api.relation

interface SingleChild<T> : Relationship, Child {
    // Children can be rehomed
    var parent: T
}