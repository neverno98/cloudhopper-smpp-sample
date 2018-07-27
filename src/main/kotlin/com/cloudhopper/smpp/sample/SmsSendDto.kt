package com.cloudhopper.smpp.sample

/**
 * @author DK
 * @since 2018-07-25
 */

data class SmsSendDto(
        var countryCode: Int = 1,
        var toolData: String = "",
        var content: String = "",
        var shortCode: String = "",
        var gateway: String = "",
        var status: Int = 0,
        var ticketId: String = "",
        var codingScheme: Int = 0,
        var concatFlag: Boolean = false) {
}