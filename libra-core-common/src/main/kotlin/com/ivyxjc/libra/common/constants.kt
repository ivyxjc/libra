package com.ivyxjc.libra.common

object BeansConstants {
    const val LIBRA_JMS_LISTENER_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalLibraJmsListenerAnnotationProcessor"

    const val LIBRA_SOURCE_CONFIG_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalSourceConfigAnnotationProcessor"

    const val LIBRA_USECASE_CONFIG_PROCESSOR_BEAN_NAME =
        "com.ivyxjc.libra.jms.config.internalUsecaseConfigAnnotationProcessor"
}

object ErrorConstants {
    const val SOURCE_CONFIG = "source-config"
    const val USECASE_CONFIG = "usecase-config"
    const val JMS_CONFIG = "jms-config"
}
