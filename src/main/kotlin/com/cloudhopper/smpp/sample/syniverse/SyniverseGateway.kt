package com.cloudhopper.smpp.sample.syniverse

import com.cloudhopper.smpp.SmppBindType
import com.cloudhopper.smpp.sample.SmppChGateway
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-27
 */

class SyniverseGateway : SmppChGateway {

    private val logger = LoggerFactory.getLogger("SyniverseGateway")

    protected constructor() {

    }

    constructor(smppBindType: SmppBindType) : super() {

        try {

            smppChDto.smppUrl = ""
            smppChDto.smppId = ""
            smppChDto.smppPwd = ""
            smppChDto.smppPort = 0
            smppChDto.smppBindType = smppBindType
            bind()
        } catch (ex: Exception) {
            logger.error("SyniverseChGateway() Exception - ", ex)
        }
    }

    override fun setSessionHandler() {

        smppChDto.sessionHandler = SyniverseHandler(this)
    }
}