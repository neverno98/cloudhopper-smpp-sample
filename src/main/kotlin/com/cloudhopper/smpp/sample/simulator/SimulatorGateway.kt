package com.cloudhopper.smpp.sample.simulator

import com.cloudhopper.smpp.SmppSessionConfiguration
import com.cloudhopper.smpp.sample.SmppGateway
import com.cloudhopper.smpp.sample.SmppRegisteredDeliveryCode
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-27
 */

class SimulatorGateway : SmppGateway() {

    companion object {

        private val MONITOR_INTERVAL: Long = 15000
        private val HOST = ""
        private val PORT = 2776
        private val WINDOW_SIZE = 1000
        private val ID = "id"
        private val NAME = "SimulatorGateway"
        private val PASSWORD = "passwd"
    }

    private val logger = LoggerFactory.getLogger("SimulatorChGateway")

    init {

        try {

            smppChDto.registeredDelivery = SmppRegisteredDeliveryCode.TERMINAL_DELIVERY.code
            smppChDto.smppId = ID
            smppChDto.smppUrl = HOST
            smppChDto.smppPort = PORT
            smppChDto.smppPwd = PASSWORD
            bind()
        } catch (th: Exception) {
            logger.error("SimulatorGateway() {}, Exception - ", th)
        }
    }

    override fun setSessionHandler() {

        smppChDto.sessionHandler = SimulatorHandler(this)
    }

    override fun setSessionConfig() {

        val sessionConfig = SmppSessionConfiguration()
        sessionConfig.windowSize = WINDOW_SIZE
        sessionConfig.windowMonitorInterval = MONITOR_INTERVAL
        sessionConfig.name = NAME
        sessionConfig.type = smppChDto.smppBindType
        sessionConfig.host = smppChDto.smppUrl
        sessionConfig.port = smppChDto.smppPort
        setConnectionTimeout(sessionConfig)
        sessionConfig.systemId = smppChDto.smppId
        sessionConfig.password = smppChDto.smppPwd
        sessionConfig.systemType = smppChDto.systemType
        sessionConfig.loggingOptions.setLogBytes(true)
        sessionConfig.loggingOptions.setLogPdu(true)
        smppChDto.sessionConfig = sessionConfig
    }
}
