package com.developers.sprintsync.domain.user_profile.model

enum class Sex(val code: String) {
    MALE("male"),
    FEMALE("female"),
    OTHER("other"),
    UNSPECIFIED("unspecified");

    companion object {
        fun fromCode(code: String?): Sex =
            entries.firstOrNull { it.code == code } ?: UNSPECIFIED
    }
}
