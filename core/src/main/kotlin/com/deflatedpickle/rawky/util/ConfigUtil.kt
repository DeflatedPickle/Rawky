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

    fun createConfigFolder(): File = File("config")

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

        for (id in PluginUtil.discoveredPlugins) {
            list.add(this.createConfigFile(id.value))
        }

        return list
    }

    @ImplicitReflectionSerializer
    fun serializeConfigToInstance(file: File, instance: Any): File? {
        @Suppress("UNCHECKED_CAST")
        val serializer = instance::class.serializer() as KSerializer<Any>

        val json = Json(JsonConfiguration.Stable)
        val jsonData = json.stringify(serializer, instance)

        val out = FileOutputStream(file, false)

        out.write(U.formatJson(jsonData).toByteArray())
        out.flush()
        out.close()

        return file
    }

    fun serializeConfig(id: String, file: File): File? {
        if (PluginUtil.slugToPlugin[id]!!.settings == Nothing::class) return null

        val settings = PluginUtil.slugToPlugin[id]!!.settings

        this.idToSettings.getOrPut(file.nameWithoutExtension, { settings.createInstance() })
        val instance = this.idToSettings[file.nameWithoutExtension]!!

        this.serializeConfigToInstance(file, instance)

        return file
    }

    fun deserializeConfig(file: File): Boolean {
        val obj = PluginUtil.slugToPlugin[file.nameWithoutExtension] ?: return false
        val settings = obj.settings

        val instance = settings.createInstance()

        this.deserializeConfigToInstance(file, instance)

        return true
    }

    fun deserializeConfigToInstance(file: File, instance: Any): Any {
        @Suppress("UNCHECKED_CAST")
        val serializer = instance::class.serializer() as KSerializer<Any>

        val json = Json(JsonConfiguration.Stable)
        val jsonObj = json.parse(serializer, file.readText())

        this.idToSettings[file.nameWithoutExtension] = jsonObj

        return jsonObj
    }

    fun serializeAllConfigs(): List<File> {
        val list = mutableListOf<File>()

        for (plugin in PluginUtil.discoveredPlugins) {
            val id = PluginUtil.pluginToSlug(plugin)
            val file = File("config/$id.json")

            this.serializeConfig(id, file)
        }

        return list
    }
}