package com.developers.sprintsync.user_parameters.data.util.converter

import com.developers.sprintsync.user_parameters.components.model.Gender

class GenderStorageConverter {
    companion object {
        fun toGender(gender: String): Gender = Gender.valueOf(gender)

        fun fromGender(gender: Gender): String = gender.name
    }
}