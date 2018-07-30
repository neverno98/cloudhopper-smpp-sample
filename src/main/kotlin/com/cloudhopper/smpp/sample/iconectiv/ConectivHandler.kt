package com.cloudhopper.smpp.sample.iconectiv

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.sample.SmppChHandler
import com.cloudhopper.smpp.sample.ISmppChGateway

/**
 * @author DK
 * @since 2018-07-29
 */
class ConectivHandler(gateway: ISmppChGateway) : SmppChHandler(gateway) {

    override val defaultEncoding: Charset = CharsetUtil.CHARSET_GSM
}