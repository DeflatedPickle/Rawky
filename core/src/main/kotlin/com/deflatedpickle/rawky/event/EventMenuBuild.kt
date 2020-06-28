package com.deflatedpickle.rawky.event

import javax.swing.JMenu

/**
 * Called after a menu has been created but before it has been added to the bar
 */
object EventMenuBuild : AbstractEvent<JMenu>()