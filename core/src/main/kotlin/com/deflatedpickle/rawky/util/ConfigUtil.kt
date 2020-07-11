package com.deflatedpickle.rawky.util

import com.github.underscore.lodash.U
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.full.createInstance

@Suppress("MemberVisibilityCanBePrivate", "unused")
@OptIn(ImplicitReflectionSerializer::class)
object ConfigUtil {
    private val logger = LogManager.getLogger(this::class.simpleName)

    /**
     * A map to get config settings for a plugin ID
     */
    private val idToSettings = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getSettings(id: String): T = idToSettings[id] as T

    val configFolder = File("config")

    fun createConfigFolder(): File =
        this.configFolder.apply {
            if (mkdir()) {
                this@ConfigUtil.logger.info("Created the config folder at ${this.absolutePath}")
            }
        }

    fun createConfigFile(id: String): File =
        File("config/$id.json").apply {
            if (createNewFile()) {
                this@ConfigUtil.logger.info("Created a config file for $id at ${this.absolutePath}")
            }
        }

    fun hasConfigFile(id: String): Boolean =
        File("config/$id.json").exists()

    fun createAllConfigFiles(): List<File> {
        val list = mutableListOf<File>()

        for (id in PluginUtil.pluginLoadOrder) {
            list.add(this.createConfigFile(id.value))
        }

        return list
    }

    @ImplicitReflectionSerializer
    fun serializeConfig(id: String): File? {
        if (PluginUtil.slugToPlugin[id]!!.settings == Nothing::class) return null

        val file = File("config/$id.json")

        val settings = PluginUtil.slugToPlugin[id]!!.settings

        this.idToSettings.getOrPut(file.nameWithoutExtension, { settings.createInstance() })
        val instance = this.idToSettings[file.nameWithoutExtension]!!

        @Suppress("UNCHECKED_CAST")
        val serializer = instance::class.serializer() as KSerializer<Any>

        val json = Json(JsonConfiguration.Stable)
        val jsonData = json.stringify(serializer, instance)

        val out = FileOutputStream(file, false)

        out.write(U.formatJson(jsonData).toByteArray())
        out.flush()
        out.close()

        this.logger.info("Serialized the config for $id to ${file.absolutePath}")

        return file
    }

    fun deserializeConfig(file: File) {
        val settings = PluginUtil.slugToPlugin[file.nameWithoutExtension]!!.settings

        val instance = settings.createInstance()
        @Suppress("UNCHECKED_CAST")
        val serializer = instance::class.serializer() as KSerializer<Any>

        val json = Json(JsonConfiguration.Stable)
        val jsonObj = json.parse(serializer, file.readText())

        this.idToSettings[file.nameWithoutExtension] = jsonObj

        this.logger.info("Deserialized the config for $file from ${file.absolutePath}")
    }

    fun serializeAllConfigs(): List<File> {
        val list = mutableListOf<File>()

        for (plugin in PluginUtil.pluginLoadOrder) {
            this.serializeConfig(
                PluginUtil.pluginToSlug(plugin)
            )
        }

        return list
    }

    fun createAndSerializeNewConfigFiles() {
        for (plugin in PluginUtil.pluginLoadOrder) {
            if (!this.hasConfigFile(
                    PluginUtil.pluginToSlug(plugin))
            ) {
                this.serializeConfig(
                    PluginUtil.pluginToSlug(plugin)
                )
            }
        }
    }

    fun deserializeOldConfigFiles() {
        val files = this.configFolder.listFiles()

        if (files != null) {
            for (file in files) {
                this.deserializeConfig(file)
            }
        }
    }
}