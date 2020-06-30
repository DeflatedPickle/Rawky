package com.deflatedpickle.rawky.util

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.URLClassLoader

object ClassGraphUtil {
    private val logger = LogManager.getLogger(this::class.simpleName)

    lateinit var scanResults: ScanResult
        private set

    private val jars = File("plugins").listFiles { _, name ->
        name.endsWith(".jar")
    }?.map { it.toURI().toURL() } ?: listOf()

    private val classLoader = URLClassLoader(jars.toTypedArray())

    init {
        if (!GeneralUtil.isInDev) {
            if (jars.isEmpty()) {
                this.logger.warn("Found no plugin JARs")
            } else {
                this.logger.info("Found the plugin JARs: ${
                jars.map { it.file.split("/", "\\").last() }
                }")
            }
        }
    }

    fun refresh() {
        this.scanResults = if (GeneralUtil.isInDev) {
            ClassGraph().enableAllInfo().scan(4)
        } else {
            ClassGraph()
                .enableAllInfo()
                .overrideClassLoaders(this.classLoader)
                .scan()
        }
    }
}