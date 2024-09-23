package com.developers.sprintsync.parameters.dataStorage.converter

import com.developers.sprintsync.parameters.model.Gender

class GenderStorageConverter {
    companion object {
        fun toGender(gender: String): Gender = Gender.valueOf(gender)

        fun fromGender(gender: Gender): String = gender.name
    }
}