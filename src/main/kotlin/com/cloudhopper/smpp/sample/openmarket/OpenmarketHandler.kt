package com.cloudhopper.smpp.sample.openmarket

import com.cloudhopper.smpp.SmppConstants
import com.cloudhopper.smpp.pdu.DeliverSm
import com.cloudhopper.smpp.sample.ISmppChGateway
import com.cloudhopper.smpp.sample.SmppChHandler
import com.cloudhopper.smpp.tlv.TlvConvertException
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-27
 */


class OpenmarketHandler(smppGateway: ISmppChGateway) : SmppChHandler(smppGateway) {

    private val logger = LoggerFactory.getLogger("OpenmarketHandler")

    override fun fireDeliverSmReceived(deliverSm: DeliverSm) {

        deliverSm.appendBodyToString(StringBuilder())

        val drCodeTlv = deliverSm.getOptionalParameter(0x2153.toShort())
        val ticketIdTlv = deliverSm.getOptionalParameter(SmppConstants.TAG_RECEIPTED_MSG_ID)
        if (drCodeTlv != null && ticketIdTlv != null) {
            try {
                insertDr(ticketIdTlv.valueAsString, drCodeTlv.valueAsString)
            } catch (ex: TlvConvertException) {
                logger.info("setMessage() TlvConvertException - {$deliverSm}")
            }
        } else {

            val message = decodeShortMessage(deliverSm)
            catchMo(deliverSm, message)
        }
    }

    private fun insertDr(ticketId: String, drCode: String) {
    }
}