package com.cloudhopper.smpp.sample.mblox

import com.cloudhopper.smpp.SmppBindType
import com.cloudhopper.smpp.sample.SmppDto

/**
 * @author DK
 * @since 2018-07-29
 */


class MbloxDto {

    var mbloxConnectionType: MbloxConnectionType? = null

    private var transceiverCount = 0
    private var transmitterCount = 0

    fun initMbloxModel(smppDto: SmppDto, smppBindType: SmppBindType, mbloxConnectionType: MbloxConnectionType) {

        this.mbloxConnectionType = mbloxConnectionType

        smppDto.smppBindType = smppBindType

        if (this.mbloxConnectionType === MbloxConnectionType.Transceiver) {

            smppDto.smppUrl = "TransceiverUrl"
            smppDto.smppId = "TransceiverId"
            smppDto.smppPwd = "TransceiverPwd"
            smppDto.smppPort = 0
            smppDto.systemType = "TransceiverSystemType"
            smppDto.serviceType = "TransceiverServiceType"
            transceiverCount++
        } else {

            smppDto.smppUrl = "TransmitterUrl"
            smppDto.smppId = "TransmitterId"
            smppDto.smppPwd = "TransmitterPwd"
            smppDto.smppPort = 0
            transmitterCount++
        }
    }
}