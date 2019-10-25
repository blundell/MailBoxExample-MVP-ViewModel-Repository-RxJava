package com.blundell.tut

class IdGenerator(idStart: Int) {

    private var id: Int = idStart

    fun nextId(): Int {
        return id++
    }
}
