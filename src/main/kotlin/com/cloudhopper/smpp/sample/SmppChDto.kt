package com.cloudhopper.smpp.sample

import com.cloudhopper.smpp.SmppBindType
import com.cloudhopper.smpp.SmppSession
import com.cloudhopper.smpp.SmppSessionConfiguration
import com.cloudhopper.smpp.impl.DefaultSmppClient
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor

/**
 * @author DK
 * @since 2018-07-24
 */

data class SmppChDto(

        var monitorExecutor: ScheduledThreadPoolExecutor? = null,
        var sessionConfig: SmppSessionConfiguration? = null,
        var executor: ThreadPoolExecutor? = null,
        var clientBootstrap: DefaultSmppClient? = null,
        var sessionHandler: SmppChHandler? = null,
        var session: SmppSession? = null,
        var smppChJob: SmppChJob? = null,
        var timer: Timer? = null,
        var smppBindType: SmppBindType = SmppBindType.TRANSMITTER,
        var smppUrl: String? = null,
        var smppId: String? = null,
        var smppPwd: String? = null,
        var smppPort: Int = 0,
        var serviceType: String? = null,
        var systemType: String? = null,
        var windowSize: Int = 1000,
        var sessionCount: Int = 1,
        var submitTimeout: Long = 1000,
        var registeredDelivery: Int = SmppRegisteredDeliveryCode.ALL_DR_DELIVERFY.code) {

}
