package com.cloudhopper.smpp.sample

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.SmppConstants
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler
import com.cloudhopper.smpp.pdu.DeliverSm
import com.cloudhopper.smpp.pdu.PduRequest
import com.cloudhopper.smpp.pdu.PduResponse
import com.cloudhopper.smpp.type.RecoverablePduException
import com.cloudhopper.smpp.type.UnrecoverablePduException
import org.slf4j.LoggerFactory
import java.nio.channels.ClosedChannelException


/**
 * @author DK
 * @since 2018-07-24
 */


abstract class SmppHandler(protected var smppGateway: ISmppGateway) : DefaultSmppSessionHandler(), ISmppHandler {

    private val logger = LoggerFactory.getLogger("SmppHandler")

    protected open val defaultEncoding: Charset = CharsetUtil.CHARSET_ISO_8859_1

    protected open fun fireDeliverSmReceived(deliverSm: DeliverSm) {

        val recordSm = StringBuilder()
        deliverSm.appendBodyToString(recordSm)
        val message = decodeShortMessage(deliverSm)
        logger.info("fireDeliverSmReceived() message=$message, org=$recordSm")

        if (isDr(message)) {
            catchDr(message)
        } else {
            catchMo(deliverSm, message)
        }
    }

    protected open fun decodeShortMessage(deliverSm: DeliverSm): String {

        when (deliverSm.dataCoding) {

            SmppConstants.DATA_CODING_DEFAULT -> return CharsetUtil.decode(deliverSm.shortMessage, defaultEncoding)
            SmppConstants.DATA_CODING_IA5 -> return CharsetUtil.decode(deliverSm.shortMessage, CharsetUtil.CHARSET_GSM)
            SmppConstants.DATA_CODING_LATIN1 -> return CharsetUtil.decode(deliverSm.shortMessage, CharsetUtil.CHARSET_ISO_8859_1)
            SmppConstants.DATA_CODING_UCS2 -> return CharsetUtil.decode(deliverSm.shortMessage, CharsetUtil.CHARSET_UCS_2)
            else -> throw IllegalArgumentException("Unsupported codingScheme=" + deliverSm.dataCoding)
        }
    }

    protected open fun catchDr(message: String) {

        val smppDr = SmppDr()
        smppDr.parse(message)
        if ("" != smppDr.ticketId) {

            insertDr(smppDr);
        }
    }

    protected open fun insertDr(smppDr: SmppDr) {

    }

    protected open fun isDr(message: String): Boolean {

        return !(message.startsWith("id:") || message.startsWith("sm:"))
    }

    protected open fun catchMo(deliverSm: DeliverSm, message: String) {

        if (deliverSm.esmClass != SmppConstants.ESM_CLASS_UDHI_MASK) {
            insertMo(deliverSm, message)
        } else {
            logger.debug("catchMo() SmppConstants.ESM_CLASS_UDHI_MASK")
        }
    }

    protected open fun insertMo(deliverSm: DeliverSm, message: String) {

    }

    override fun firePduRequestReceived(pduRequest: PduRequest<*>): PduResponse? {

        val response = pduRequest.createResponse()
        if (pduRequest.commandId == SmppConstants.CMD_ID_DELIVER_SM) {

            val deliverSm = pduRequest as DeliverSm
            fireDeliverSmReceived(deliverSm)
        } else if (pduRequest.commandId == SmppConstants.CMD_ID_ENQUIRE_LINK || pduRequest.commandId == SmppConstants.CMD_ID_ENQUIRE_LINK_RESP) {
        } else {
        }
        return response
    }

    override fun fireChannelUnexpectedlyClosed() {

        logger.warn("fireChannelUnexpectedlyClosed()")
        smppGateway.asyncWaitAndBind()
    }

    override fun fireUnexpectedPduResponseReceived(pduResponse: PduResponse) {

        logger.warn("fireUnexpectedPduResponseReceived() pduResponse=${pduResponse.toString()}")
        smppGateway.asyncWaitAndBind()
    }

    override fun fireUnrecoverablePduException(e: UnrecoverablePduException) {

        logger.warn("fireUnrecoverablePduException() pduResponse=${e.toString()}")
        smppGateway.asyncWaitAndBind()
    }

    override fun fireRecoverablePduException(e: RecoverablePduException) {

        logger.warn("fireRecoverablePduException() pduResponse=${e.toString()}")
        smppGateway.asyncWaitAndBind()
    }

    override fun fireUnknownThrowable(t: Throwable) {

        if (t is ClosedChannelException) {
            fireChannelUnexpectedlyClosed()
        }
    }
}