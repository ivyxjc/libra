package com.ivyxjc.libra.starter.common.model

import org.apache.commons.lang3.builder.ToStringBuilder

internal class LibraJmsListenerYaml {
    lateinit var destination: String
    lateinit var messageListener: String
    lateinit var dispatcher: String

    var sourceIds: String = "ALL"
    var id: String = ""
    var containerFactory: String = ""
    var subscription: String = ""
    var selector: String = ""
    var concurrency: String = ""

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}