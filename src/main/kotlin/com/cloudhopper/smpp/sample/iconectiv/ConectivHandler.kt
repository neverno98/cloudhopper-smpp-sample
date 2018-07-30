package com.cloudhopper.smpp.sample.iconectiv

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.sample.SmppHandler
import com.cloudhopper.smpp.sample.ISmppGateway

/**
 * @author DK
 * @since 2018-07-29
 */
class ConectivHandler(gateway: ISmppGateway) : SmppHandler(gateway) {

    override val defaultEncoding: Charset = CharsetUtil.CHARSET_GSM
}