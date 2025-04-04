package com.developers.sprintsync.data.user_parameters.converter

import com.developers.sprintsync.domain.user_parameters.model.Gender

class GenderStorageConverter {
    companion object {
        fun toGender(gender: String): Gender = Gender.valueOf(gender)

        fun fromGender(gender: Gender): String = gender.name
    }
}