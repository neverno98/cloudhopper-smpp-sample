package com.cloudhopper.smpp.sample.iconectiv

import com.cloudhopper.smpp.sample.SmppChDto

/**
 * @author DK
 * @since 2018-07-29
 */

class ConectivDto {

    private var mode = 1

    fun init(smppChDto: SmppChDto) {

        changeMode(smppChDto, System.currentTimeMillis().toInt() % 2)
        smppChDto.smppPwd = "conectivPwd"
        smppChDto.smppPort = 0
    }

    fun change(smppBind: SmppChDto) {

        val localMode = if (mode == 1) 2 else 1
        changeMode(smppBind, localMode)
    }

    private fun changeMode(smppChDto: SmppChDto, mode: Int) {

        this.mode = mode
        if (mode == 1) {
            smppChDto.smppUrl = "conectivUrl1"
            smppChDto.smppId = "conectivId1"
        } else {
            smppChDto.smppUrl = "conectivUrl2"
            smppChDto.smppId = "conectivId2"
        }
    }

}