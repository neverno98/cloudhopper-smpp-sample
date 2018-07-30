package com.cloudhopper.smpp.sample.iconectiv

import com.cloudhopper.smpp.sample.SmppDto

/**
 * @author DK
 * @since 2018-07-29
 */

class ConectivDto {

    private var mode = 1

    fun init(smppDto: SmppDto) {

        changeMode(smppDto, System.currentTimeMillis().toInt() % 2)
        smppDto.smppPwd = "conectivPwd"
        smppDto.smppPort = 0
    }

    fun change(smppBind: SmppDto) {

        val localMode = if (mode == 1) 2 else 1
        changeMode(smppBind, localMode)
    }

    private fun changeMode(smppDto: SmppDto, mode: Int) {

        this.mode = mode
        if (mode == 1) {
            smppDto.smppUrl = "conectivUrl1"
            smppDto.smppId = "conectivId1"
        } else {
            smppDto.smppUrl = "conectivUrl2"
            smppDto.smppId = "conectivId2"
        }
    }

}