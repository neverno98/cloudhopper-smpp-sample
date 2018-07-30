package com.cloudhopper.smpp.sample.mblox

/**
 * @author DK
 * @since 2018-07-29
 */
enum class MbloxConnectionType(val code: Int) {

    Transceiver(1), Transmitter(2);

    override fun toString(): String {

        return if (this == Transceiver) {
            "Transceiver"
        } else {
            "Transmitter"
        }
    }

}