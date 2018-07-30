package com.cloudhopper.smpp.sample.iconectiv

import com.cloudhopper.smpp.pdu.SubmitSm
import com.cloudhopper.smpp.sample.SmppGateway
import com.cloudhopper.smpp.sample.SmppRegisteredDeliveryCode
import com.cloudhopper.smpp.sample.SmsSendDto
import com.cloudhopper.smpp.tlv.Tlv
import com.cloudhopper.smpp.type.Address
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-29
 */

class ConectivGateway : SmppGateway() {

    private val conectivChDto = ConectivDto()

    private val logger = LoggerFactory.getLogger("ConectivGateway")

    init {

        try {

            smppChDto.registeredDelivery = SmppRegisteredDeliveryCode.TERMINAL_DELIVERY.code
            conectivChDto.init(smppChDto)
        } catch (th: Exception) {

            logger.error("ConectivChGateway() Exception - ", th)
        }
    }

    override fun changeBindUrl() {

        conectivChDto.change(smppChDto)
    }

    override fun setSessionHandler() {

        smppChDto.sessionHandler = smppChDto.sessionHandler ?: ConectivHandler(this)
    }

    override fun makeSourceAddr(smsSendDto: SmsSendDto): Address {

        return Address(0x03.toByte(), 0x00.toByte(), smsSendDto.shortCode)
    }

    override fun addTlvOption(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

        val campainId = "compaiId"
        val tlvServiceId = Tlv(0x1605.toShort(), campainId.toByteArray())
        submitSm.addOptionalParameter(tlvServiceId)
    }
}