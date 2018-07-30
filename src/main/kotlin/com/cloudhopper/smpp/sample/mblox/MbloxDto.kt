package com.cloudhopper.smpp.sample.mblox

import com.cloudhopper.smpp.SmppBindType
import com.cloudhopper.smpp.sample.SmppChDto

/**
 * @author DK
 * @since 2018-07-29
 */


class MbloxDto {

    var mbloxConnectionType: MbloxConnectionType? = null

    private var transceiverCount = 0
    private var transmitterCount = 0

    fun initMbloxModel(smppChDto: SmppChDto, smppBindType: SmppBindType, mbloxConnectionType: MbloxConnectionType) {

        this.mbloxConnectionType = mbloxConnectionType

        smppChDto.smppBindType = smppBindType

        if (this.mbloxConnectionType === MbloxConnectionType.Transceiver) {

            smppChDto.smppUrl = "TransceiverUrl"
            smppChDto.smppId = "TransceiverId"
            smppChDto.smppPwd = "TransceiverPwd"
            smppChDto.smppPort = 0
            smppChDto.systemType = "TransceiverSystemType"
            smppChDto.serviceType = "TransceiverServiceType"
            transceiverCount++
        } else {

            smppChDto.smppUrl = "TransmitterUrl"
            smppChDto.smppId = "TransmitterId"
            smppChDto.smppPwd = "TransmitterPwd"
            smppChDto.smppPort = 0
            transmitterCount++
        }
    }
}