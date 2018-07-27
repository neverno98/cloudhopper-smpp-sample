package com.cloudhopper.smpp.sample

/**
 * @author DK
 * @since 2018-07-24
 */

interface ISmppChGateway {

    @Throws(Exception::class)
    fun enquireLink()

    fun asyncWaitAndBind()
}
