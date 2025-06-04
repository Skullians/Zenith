package net.skullian.zenith

abstract class ZenithExtension {
    internal val dependencies: MutableList<String> = mutableListOf()

    fun dependencies(vararg modules: String) {
        this.dependencies.addAll(modules)
    }
}