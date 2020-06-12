package com.deflatedpickle.rawky.util

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult

object ClassGraphUtil {
    lateinit var scanResults: ScanResult
        private set

    fun refresh() {
        this.scanResults = ClassGraph().enableAllInfo().scan()
    }
}