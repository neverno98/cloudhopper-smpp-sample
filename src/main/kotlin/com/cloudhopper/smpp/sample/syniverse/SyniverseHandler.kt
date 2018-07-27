package com.cloudhopper.smpp.sample.syniverse

import com.cloudhopper.smpp.PduAsyncResponse
import com.cloudhopper.smpp.SmppConstants
import com.cloudhopper.smpp.sample.ISmppChGateway
import com.cloudhopper.smpp.sample.SmppChHandler
import org.slf4j.LoggerFactory

/**
 * @author DK
 * @since 2018-07-27
 */


class SyniverseHandler(gateway: ISmppChGateway) : SmppChHandler(gateway) {

    private val logger = LoggerFactory.getLogger("SyniverseHandler")

    override fun fireExpectedPduResponseReceived(pduAsyncResponse: PduAsyncResponse) {

        try {

            if (pduAsyncResponse.response.commandId == SmppConstants.CMD_ID_SUBMIT_SM_RESP) {
                logger.debug("fireExpectedPduResponseReceived() CMD_ID_SUBMIT_SM_RESP, pduAsyncResponse $pduAsyncResponse")
            } else if (pduAsyncResponse.response.commandId != SmppConstants.CMD_ID_ENQUIRE_LINK_RESP) {
                logger.debug("fireExpectedPduResponseReceived() CMD_ID_ENQUIRE_LINK_RESP, pduAsyncResponse $pduAsyncResponse")
            }
        } catch (th: Exception) {
            logger.error("fireExpectedPduResponseReceived() pduAsyncResponse error $pduAsyncResponse", th)
        }
    }
}