package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.project.Project
import java.util.*

public class OpenProjects {

    private val projects = ArrayList<Project>()

    private var isAccessing = false


    public fun addProject(project: Project) {
        waitUntilAvailable()
        isAccessing = true
        projects.add(project)
        isAccessing = false
    }


    private fun waitUntilAvailable() {
        val maxIterations = 100
        var currentIteration = 0

        while (isAccessing) {
            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }


            if (++currentIteration == maxIterations) {
                throw RuntimeException("Exceeding lock time")
            }
        }
    }


    public fun getOpenProjectsAsArray(): Array<Project> {
        waitUntilAvailable()
        isAccessing = true
        val projectsArray = projects.toArray<Project>(arrayOfNulls<Project>(projects.size()))
        isAccessing = false

        return projectsArray
    }


    public fun removeProject(project: Project) {
        waitUntilAvailable()
        isAccessing = true
        projects.remove(project)
        isAccessing = false
    }
}
