package com.cloudhopper.smpp.sample.openmarket

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.pdu.SubmitSm
import com.cloudhopper.smpp.sample.SmppChGateway
import com.cloudhopper.smpp.sample.SmppRegisteredDeliveryCode
import com.cloudhopper.smpp.sample.SmppStatus
import com.cloudhopper.smpp.sample.SmsSendDto
import com.cloudhopper.smpp.ssl.SslConfiguration
import com.cloudhopper.smpp.tlv.Tlv
import com.cloudhopper.smpp.type.Address
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-27
 */


class OpenmarketGateway : SmppChGateway() {

    private val logger = LoggerFactory.getLogger("OpenmarketGateway")

    override var defaultEncoding: Charset= CharsetUtil.CHARSET_ISO_8859_1

    init {

        try {

            smppChDto.registeredDelivery = SmppRegisteredDeliveryCode.TERMINAL_DELIVERY.code
            smppChDto.smppUrl = ""
            smppChDto.smppPort = 0
            smppChDto.smppId = ""
            smppChDto.smppPwd = ""
            bind()
        } catch (th: Exception) {

            logger.error("OpenmarketGateway() Exception - ", th)
        }
    }

    override fun setSessionHandler() {

        smppChDto.sessionHandler = OpenmarketHandler(this)
    }

    override fun setSsl() {

        val sslConfig = SslConfiguration()
        sslConfig.addExcludeProtocols("TLSv1.0", "TLSv1.1", "SSLv2Hello")
        sslConfig.isValidateCerts = true
        sslConfig.isValidatePeerCerts = true

        val sessionConfig = smppChDto.sessionConfig ?: return
        sessionConfig.setSslConfiguration(sslConfig)
        sessionConfig.setUseSsl(true)
    }

    override fun addTlvOption(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

        val programId = Tlv(0x2157.toShort(), smsSendDto.shortCode.toByteArray())
        submitSm.addOptionalParameter(programId)
    }

    override fun makeSourceAddr(smsSendDto: SmsSendDto): Address {

        return Address(0x03.toByte(), 0x00.toByte(), smsSendDto.shortCode)
    }

    override fun getStatusByCommandStatus(commandStatus: Int): Int {

        return SmppStatus.FAILED.code
    }
}