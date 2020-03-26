package com.ivyxjc.libra.starter.config.usecases.annotation

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(LibraUsecaseConfigBootstrapConfiguration::class)
annotation class EnableLibraUsecaseConfig