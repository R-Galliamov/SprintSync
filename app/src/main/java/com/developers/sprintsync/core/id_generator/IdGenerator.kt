package com.developers.sprintsync.core.id_generator

import com.github.f4b6a3.ulid.UlidCreator

interface IdGenerator {
    fun generate(): String
}

class ULIIdGenerator : IdGenerator {
    override fun generate(): String {
        return UlidCreator.getUlid().toString()
    }
}