package com.example.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class Jackson {
    companion object {
        fun toJson(any: Any?): String {
            val jacksonMapper = jacksonObjectMapper()
            return jacksonMapper.writeValueAsString(any)
        }
    }
}