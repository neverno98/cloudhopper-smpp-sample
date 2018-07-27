package com.cloudhopper.smpp.sample

/**
 * @author DK
 * @since 2018-07-25
 */

enum class SmppStatus(val code: Int) {

    SUCCESS(2), FAILED(3), RETRY(4)
}