package com.deflatedpickle.rawky.tilepalette

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.*

class ImportTilePaletteDialog : TaskDialog(Haruhi.window, "Import Tile Palette") {
    val tileWidthInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    val tileHeightInput = JSpinner(SpinnerNumberModel(16, 1, null, 8))
    private val sizeSwapper = JButton(MonoIcon.SWAP).apply {
        addActionListener {
            val temp = tileWidthInput.value
            tileWidthInput.value = tileHeightInput.value
            tileHeightInput.value = temp
        }
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            add(JLabel("Tile Size:"), StickEast)
            add(tileWidthInput, FillHorizontal)
            add(JLabel("X"))
            add(tileHeightInput, FillHorizontal)
            add(sizeSwapper, FinishLine)
        }
    }
}