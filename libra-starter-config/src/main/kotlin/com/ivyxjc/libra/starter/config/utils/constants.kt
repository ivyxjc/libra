package com.ivyxjc.libra.starter.config.utils

internal object ConfigConstants {
    const val USECASE_TYPE_SIMPLE = "Simple"
    const val USECASE_TYPE_STATUS = "Status"

    const val DEFAULT_CONCURRENCY = "8-8"

    const val Q_PEFIX_DEFAULT = "default"

    const val TRANSMISSION_JMS_NAME = "transmission"
    const val TRANSFORMATION_JMS_NAME = "transformation"
    const val REMEDIATION_JMS_NAME = "remediation"
    const val PAYLOAD_JMS_NAME = "payload"

    const val TRANSMISSION_PLATFORM = "transmissionPlatform"
    const val TRANSFORMATION_PLATFORM = "transformationPlatform"
    const val REMEDIATION_PLATFORM = "remediationPlatform"
    const val PAYLOAD_PLATFORM = "payloadPlatform"

    const val BLANK_RAW_TRANS_DISPATCHER = "blankRawTransDispatcher"
    const val BLANK_USE_CASE_DISPATCHER = "blankUcTxnDispatcher"

    const val TRANSMISSION_LISTENER = "transmissionListener"
    const val TRANSFORMATION_LISTENER = "transformationListener"
    const val REMEDIATION_LISTENER = "remediationListener"
}


