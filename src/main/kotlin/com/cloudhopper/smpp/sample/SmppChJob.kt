package com.cloudhopper.smpp.sample

import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author DK
 * @since 2018-07-24
 */

class SmppChJob(private val gateway: ISmppChGateway) : TimerTask() {

    private val logger = LoggerFactory.getLogger("SmppChJob")

    override fun run() {

        try {
            gateway.enquireLink()
        } catch (ex: Exception) {

            logger.warn("run() Exception - ", ex)
        }
    }
}