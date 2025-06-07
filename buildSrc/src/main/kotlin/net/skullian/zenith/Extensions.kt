package net.skullian.zenith

abstract class Zenith {
    internal val dependencies: MutableList<String> = mutableListOf()

    fun modules(vararg modules: String) {
        this.dependencies.addAll(modules)
    }
}
