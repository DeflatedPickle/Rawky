package com.deflatedpickle.rawky.api.relation

// my wife left me
interface SingleParent<T> : Relationship, Parent {
    // Fun fact; the programmer can swap your kid at any point!
    var child: SingleChild<T>
}