package com.cloudhopper.smpp.sample

import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author DK
 * @since 2018-07-24
 */

class SmppDr {

    private val DR_ID = "id:"
    private val DR_ERR = "err:"
    private val DR_SUBMIT = "submit"
    private val DR_DONE = "done"
    private val DR_DATE = "date:"

    private val logger = LoggerFactory.getLogger("SmppDr")

    var ticketId: String? = null
    var drCode: String? = null

    fun parse(message: String) {

        val st = StringTokenizer(message)
        while (st.hasMoreTokens()) {

            val token = st.nextToken()
            if (token.startsWith(DR_ID)) {
                ticketId = token.substring(DR_ID.length)
            } else if (token.startsWith(DR_ERR)) {
                drCode = token.substring(DR_ERR.length)
            }
        }
        logger.info("parse() $this")
    }

    override fun toString(): String {

        return "SmppDr(ticketId=$ticketId, drCode=$drCode)"
    }
}