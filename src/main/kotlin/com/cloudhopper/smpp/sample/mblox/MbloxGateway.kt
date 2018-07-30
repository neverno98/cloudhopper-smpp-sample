package com.cloudhopper.smpp.sample.mblox

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.SmppBindType
import com.cloudhopper.smpp.SmppConstants
import com.cloudhopper.smpp.pdu.SubmitSm
import com.cloudhopper.smpp.pdu.SubmitSmResp
import com.cloudhopper.smpp.sample.SmppGateway
import com.cloudhopper.smpp.sample.SmppStatus
import com.cloudhopper.smpp.sample.SmsSendDto
import com.cloudhopper.smpp.ssl.SslConfiguration
import com.cloudhopper.smpp.tlv.Tlv
import org.slf4j.LoggerFactory
import java.math.BigInteger

/**
 * @author DK
 * @since 2018-07-29
 */


class MbloxGateway(smppBindType: SmppBindType, mbloxConnectionType: MbloxConnectionType) : SmppGateway() {

    private val logger = LoggerFactory.getLogger("MbloxGateway")

    private val sslPort = 0;
    private val mbloxChDto = MbloxDto()
    override var defaultEncoding: Charset = CharsetUtil.CHARSET_ISO_8859_1

    init {

        try {

            mbloxChDto.initMbloxModel(smppChDto, smppBindType, mbloxConnectionType)
            bind()
        } catch (th: Exception) {

            logger.error("MbloxGateway() Exception - ", th)
        }
    }

    override fun setSessionHandler() {

        smppChDto.sessionHandler = smppChDto.sessionHandler ?: MbloxHandler(this)
    }

    override fun setSsl() {

        smppChDto.sessionConfig?.setPort(smppChDto.smppPort + sslPort)
        val sslConfig = SslConfiguration()
        sslConfig.keyStorePath = "sslFile"
        sslConfig.keyStorePassword = "sslPwd"
        sslConfig.keyManagerPassword = "sslPwd"
        sslConfig.trustStorePath = "sslFile"
        sslConfig.trustStorePassword = "sslPwd"
        sslConfig.needClientAuth = true
        sslConfig.isValidateCerts = true
        sslConfig.isValidatePeerCerts = true
        smppChDto.sessionConfig?.setSslConfiguration(sslConfig)
        smppChDto.sessionConfig?.setUseSsl(true)
    }

    override fun addTlvOption(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

        if (mbloxChDto.mbloxConnectionType === MbloxConnectionType.Transceiver) {

            submitSm.serviceType = smppChDto.serviceType
            val operatorId = Tlv(0x1402.toShort(), "0".toByteArray())
            submitSm.addOptionalParameter(operatorId)

            val tariff = Tlv(0x1403.toShort(), "0".toByteArray())
            submitSm.addOptionalParameter(tariff)

        }
    }

    override fun postSubmitResp(submitResp: SubmitSmResp?, smsSendDto: SmsSendDto) {

        submitResp ?: return

        if (submitResp.commandStatus == SmppConstants.STATUS_OK) {

            smsSendDto.status = SmppStatus.SUCCESS.code
            smsSendDto.ticketId = getTicketId(submitResp)
        } else if (submitResp.commandStatus  > 1025) {

            smsSendDto.status = SmppStatus.FAILED.code
        } else {

            smsSendDto.status = getStatusByCommandStatus(submitResp.commandStatus)
        }
    }

    private fun getTicketId(submitResp: SubmitSmResp?): String {

        submitResp ?: return ""

        val bi = BigInteger(submitResp.messageId, 16)
        return bi.toLong().toString()
    }
}