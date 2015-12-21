package com.ptby.dynamicreturntypeplugin.json

import com.intellij.openapi.vfs.*
import java.util.*

public class FileSytemConfigChangeListener : VirtualFileListener {
    private val projectConfigChangeListeners = ArrayList<ProjectConfigChangeListener>()

    init {
        val instance = LocalFileSystem.getInstance()
        instance.addVirtualFileListener(this)
    }


    override fun propertyChanged(event: VirtualFilePropertyEvent) {
    }

    public fun registerProjectConfigChangeListener(projectConfigChangeListener: ProjectConfigChangeListener) {
        projectConfigChangeListeners.add(projectConfigChangeListener)
    }


    override fun contentsChanged(event: VirtualFileEvent) {
        refreshIfCorrectFileEvent(event)
    }


    private fun refreshIfCorrectFileEvent(virtualFileEvent: VirtualFileEvent) {
        val currentFileName = virtualFileEvent.getFileName()

        if (currentFileName == expectedConfigFileName) {
            refreshConfigs()
        }
    }


    private fun refreshConfigs() {
        for (projectConfigChangeListener in projectConfigChangeListeners) {
            projectConfigChangeListener.refreshAllConfigs()
        }
    }


    override fun fileCreated(event: VirtualFileEvent) {
        refreshIfCorrectFileEvent(event)
    }

    override fun fileDeleted(event: VirtualFileEvent) {
        refreshIfCorrectFileEvent(event)
    }

    override fun fileMoved(event: VirtualFileMoveEvent) {
        refreshIfCorrectFileEvent(event)
    }

    override fun fileCopied(event: VirtualFileCopyEvent) {
        refreshIfCorrectFileEvent(event)
    }

    override fun beforePropertyChange(event: VirtualFilePropertyEvent) {
    }

    override fun beforeContentsChange(event: VirtualFileEvent) {
    }

    override fun beforeFileDeletion(event: VirtualFileEvent) {
    }

    override fun beforeFileMovement(event: VirtualFileMoveEvent) {
    }

    companion object {
        private val expectedConfigFileName = "dynamicReturnTypeMeta.json"
    }
}
