/*
 * Copyright (c) 2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package org.zowe.cobol

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import org.zowe.cobol.state.CobolPluginState
import org.zowe.cobol.state.InitializationOnly
import org.zowe.cobol.state.LanguageSupportStateService

/** COBOL project manager listener. Listens to projects changes and react to them respectively */
class CobolProjectManagerListener : ProjectManagerListener {

  /**
   * Delete TextMate bundle if the last opened project is being closed
   * (the only possible way to handle plug-in's TextMate bundle to be deleted when the plug-in is uninstalled)
   */
  @OptIn(InitializationOnly::class)
  override fun projectClosing(project: Project) {
    val lsStateService = LanguageSupportStateService.getService()
    val pluginState = lsStateService.getPluginState(project) { CobolPluginState(project) }

    if (isLastProjectClosing() && (pluginState.isLSPClientReady() || pluginState.isLSPServerConnectionReady())) {
      pluginState.unloadLSPClient {}
      pluginState.finishDeinitialization {}
    }
  }

  /** Check if the project being closed is the last one that was opened */
  private fun isLastProjectClosing(): Boolean {
    return ProjectManager.getInstance().openProjects.size == 1
  }

}