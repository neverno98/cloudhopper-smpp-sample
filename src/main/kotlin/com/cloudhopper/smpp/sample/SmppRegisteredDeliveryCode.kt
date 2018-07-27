package com.cloudhopper.smpp.sample

/**
 * @author DK
 * @since 2018-07-24
 */
enum class SmppRegisteredDeliveryCode (val code: Int) {

    NO_DELIVERY(0), TERMINAL_DELIVERY(1), FAILED(2), ALL_DR_DELIVERFY(17)
}