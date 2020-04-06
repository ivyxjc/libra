package com.ivyxjc.libra.common

object BeansConstants {
    const val LIBRA_JMS_LISTENER_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalLibraJmsListenerAnnotationProcessor"

    const val LIBRA_SOURCE_CONFIG_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalSourceConfigAnnotationProcessor"

    const val LIBRA_USECASE_CONFIG_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalUsecaseConfigAnnotationProcessor"

    const val LIBRA_TRANSMISSION_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalTransmissionAnnotationProcessor"

    const val LIBRA_TRANSFORMATION_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalTransformationAnnotationProcessor"

    const val LIBRA_REMEDIATION_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalRemediationAnnotationProcessor"


}

object ErrorConstants {
    const val SOURCE_CONFIG = "source-config"
    const val USECASE_CONFIG = "usecase-config"
    const val JMS_CONFIG = "jms-config"
}

object DtoInConstants {
    const val sourceId = "SourceId"
}
