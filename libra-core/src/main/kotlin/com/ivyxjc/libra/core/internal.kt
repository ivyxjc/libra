package com.ivyxjc.libra.core

import org.slf4j.MarkerFactory


/**
 * libra error, it means that something wrong in project libra
 */
internal val libraErrorMarker = MarkerFactory.getMarker("Libra-Error")


/**
 * higher than warn but lower than error
 */
internal val warnPlusMarker = MarkerFactory.getMarker("WarnPlus")

