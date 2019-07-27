package com.deflatedpickle.rawky.components

import de.javagl.obj.Obj
import de.javagl.obj.ObjData
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.AWTGLCanvas
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import java.awt.event.ComponentEvent
import java.nio.IntBuffer
import javax.swing.Timer
import kotlin.math.PI
import kotlin.math.sin


class ModelView : AWTGLCanvas() {
    var initialized = false
    var tick = 0

    var textureID = 0

    val model = ObjUtils.convertToRenderable(ObjReader.read(ClassLoader.getSystemResourceAsStream("models/cube.obj")))

    init {
        Timer(1000 / 60) {
            tick++
            repaint()
        }.start()
    }

    fun updateTexture() {
        val buffer = BufferUtils.createByteBuffer(16 * 16 * 4)

        for (row in Components.pixelGrid.pixelMatrix) {
            for (column in row) {
                buffer.put(25)
                buffer.put(25)
                buffer.put(25)
                buffer.put(25)
            }
        }
        buffer.flip()

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 16, 16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    override fun initGL() {
        val buffer = BufferUtils.createByteBuffer(16 * 16 * 4)
        for (i in 0 until (16 * 16 * 4)) {
            buffer.put(0)
        }
        buffer.flip()

        textureID = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 16, 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    override fun paintGL() {
        if (!initialized) {
            GL11.glViewport(0, 0, width, height)
            GL11.glEnable(GL11.GL_DEPTH_TEST)

            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, -100.0, 100.0)
            GL11.glMatrixMode(GL11.GL_MODELVIEW)

            initialized = true
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()

        GL11.glClearColor(64.0f / 255.0f, 64.0f / 255.0f, 64.0f / 255.0f, 1.0f)

        updateTexture()

        // GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID)

        GL11.glScalef(0.4f, 0.4f, 0.4f)
        GL11.glRotatef(25f, 1f, 0f, 0f)
        GL11.glRotatef(360 * sin(tick * 0.02 / (PI * 2)).toFloat(), 0f, 1f, 0f)

        GL11.glBegin(GL11.GL_TRIANGLES)
        val indicesArray = ObjData.getFaceVertexIndicesArray(model)

        for (index in indicesArray) {
            val texture = model.getTexCoord(index)
            GL11.glTexCoord2f(texture.x, texture.y)

            val vertex = model.getVertex(index)
            GL11.glVertex3f(vertex.x, vertex.y, vertex.z)
        }

        GL11.glEnd()

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        swapBuffers()
    }
}