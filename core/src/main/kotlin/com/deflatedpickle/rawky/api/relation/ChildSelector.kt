package com.deflatedpickle.rawky.api.relation

interface ChildSelector<T> {
    var currentChild: T
}