/**
 *     Copyright 2017-2018 Guillaume Tâche
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.github.gtache.lsp.requests

import com.github.gtache.lsp.PluginMain
import com.github.gtache.lsp.editor.EditorEventManager
import com.github.gtache.lsp.utils.{ApplicationUtils, FileUtils}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.{FileEditorManager, OpenFileDescriptor}
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile

/**
  * Object handling reformat events
  */
object ReformatHandler {

  /**
    * Unused
    * Reformats all the files in the project
    *
    * @param project The project
    * @return True if all the files were supported by the language servers, false otherwise
    */
  def reformatAllFiles(project: Project): Boolean = {
    var allFilesSupported = true
    ProjectFileIndex.getInstance(project).iterateContent((fileOrDir: VirtualFile) => {
      if (fileOrDir.isDirectory) {
        true
      } else {
        if (PluginMain.isExtensionSupported(fileOrDir.getExtension)) {
          reformatFile(fileOrDir, project)
          true
        } else {
          allFilesSupported = false
          true
        }
      }
    })
    allFilesSupported
  }

  /**
    * Reformat a file given a VirtualFile and a Project
    *
    * @param file    The file
    * @param project The project
    */
  def reformatFile(file: VirtualFile, project: Project): Unit = {
    if (PluginMain.isExtensionSupported(file.getExtension)) {
      val uri = FileUtils.VFSToURI(file)
      EditorEventManager.forUri(uri) match {
        case Some(manager) =>
          manager.reformat()
        case None =>
          ApplicationUtils.invokeLater(() => {
            val fileEditorManager = FileEditorManager.getInstance(project)
            val descriptor = new OpenFileDescriptor(project, file)
            val editor = ApplicationUtils.computableWriteAction(() => {
              fileEditorManager.openTextEditor(descriptor, false)
            })
            EditorEventManager.forEditor(editor).get.reformat(closeAfter = true)
          })
      }
    }
  }

  /**
    * Reformat a file given its editor
    *
    * @param editor The editor
    */
  def reformatFile(editor: Editor): Unit =
    EditorEventManager.forEditor(editor).foreach(manager => manager.reformat())


  /**
    * Reformat a selection in a file given its editor
    *
    * @param editor The editor
    */
  def reformatSelection(editor: Editor): Unit = {
    EditorEventManager.forEditor(editor).foreach(manager => manager.reformatSelection())
  }

}
