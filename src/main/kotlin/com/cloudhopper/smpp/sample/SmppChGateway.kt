package com.cloudhopper.smpp.sample

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil
import com.cloudhopper.smpp.SmppConstants
import com.cloudhopper.smpp.SmppSessionConfiguration
import com.cloudhopper.smpp.impl.DefaultSmppClient
import com.cloudhopper.smpp.pdu.EnquireLink
import com.cloudhopper.smpp.pdu.SubmitSm
import com.cloudhopper.smpp.pdu.SubmitSmResp
import com.cloudhopper.smpp.type.Address
import com.cloudhopper.smpp.type.SmppChannelException
import com.cloudhopper.smpp.type.SmppInvalidArgumentException
import com.cloudhopper.smpp.type.SmppTimeoutException
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author DK
 * @since 2018-07-25
 */


abstract class SmppChGateway : ISmppChGateway {

    protected val BIND_AYCN_TIMEOUT = 3 * 1000L
    protected val SMPP_LIMIT_BYTE = 160

    private val logger = LoggerFactory.getLogger("SmppChGateway")

    protected val smppChDto = SmppChDto()
    protected val scheduledExecutor: ScheduledExecutorService
    protected open var defaultEncoding: Charset = CharsetUtil.CHARSET_GSM
    protected val isAsyncMode: Boolean = false

    init {

        scheduledExecutor = Executors.newSingleThreadScheduledExecutor()

        var min = 60L * 1000L
        val timer = Timer()
        val smppChJob = SmppChJob(this)
        timer.scheduleAtFixedRate(smppChJob, min, min)

        smppChDto.timer = timer
        smppChDto.smppChJob = smppChJob
    }

    protected open fun setSessionHandler() {

    }

    @Throws(Exception::class)
    override fun enquireLink() {

        try {

            smppChDto.session?.sendRequestPdu(EnquireLink(), smppChDto.submitTimeout, false)
        } catch (th: Exception) {

            logger.warn("enquireLink() Exception - ", th)
            asyncWaitAndBind()
        }
    }

    protected open fun initBind() {

        try {
            smppChDto.session = null
            smppChDto.executor = Executors.newCachedThreadPool() as ThreadPoolExecutor

            val monitorExecutor = Executors.newScheduledThreadPool(smppChDto.sessionCount, object : ThreadFactory {

                private val sequence = AtomicInteger(0)
                override fun newThread(r: Runnable): Thread {

                    val t = Thread(r)
                    t.name = "SmppClientSessionWindowMonitorPool-" + sequence.getAndIncrement()
                    return t
                }
            }) as ScheduledThreadPoolExecutor

            smppChDto.monitorExecutor = monitorExecutor
            smppChDto.clientBootstrap = DefaultSmppClient(Executors.newCachedThreadPool(), smppChDto.sessionCount, monitorExecutor)

            setSessionHandler()
            setSessionConfig()
            setSsl()
        } catch (th: Exception) {

            logger.warn("enquireLink() initBind() , Exception - ", th)
        }
    }

    @Throws(Exception::class)
    protected open fun bind() {

        try {

            initBind()
            smppChDto.session = smppChDto.clientBootstrap?.bind(smppChDto.sessionConfig, smppChDto.sessionHandler) ?: throw Exception("session.null")
        } catch (e: Exception) {

            logger.warn("bind() ${smppChDto}, Exception - ", e)
            failedDelay()
            changeBindUrl()
            asyncWaitAndBind()
        }
    }

    override fun asyncWaitAndBind() {

        scheduledExecutor.schedule({
            try {
                rebind()
            } catch (ex: Exception) {
                logger.error("asyncWaitAndBind() ${smppChDto}, Exception - ", ex)
            }
        }, BIND_AYCN_TIMEOUT, TimeUnit.MILLISECONDS)
    }

    protected open fun rebind() {

        try {
            close()
        } catch (th: Exception) {
            logger.warn("rebind() Exception - ", th)
        }

        try {

            unbind()
            bind()
        } catch (th: Exception) {
            logger.warn("rebind() Exception - ", th)
        }
    }

    @Throws(Exception::class)
    protected open fun close() {

        smppChDto.session?.destroy()
        smppChDto.clientBootstrap?.destroy()

        smppChDto.executor?.shutdown()
        smppChDto.executor?.awaitTermination(10000, TimeUnit.MILLISECONDS)

        smppChDto.monitorExecutor?.shutdownNow()
        smppChDto.monitorExecutor?.awaitTermination(10000, TimeUnit.MILLISECONDS)
    }

    @Throws(Exception::class)
    protected open fun unbind() {

        smppChDto.session?.unbind(5000)
    }

    protected open fun failedDelay() {

        try {
            Thread.sleep(60000)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    protected open fun changeBindUrl() {

    }

    protected open fun setSessionConfig() {

        val sessionConfig = SmppSessionConfiguration()
        sessionConfig.windowSize = smppChDto.windowSize
        sessionConfig.name = "Tester.Session.0"
        sessionConfig.type = smppChDto.smppBindType
        sessionConfig.host = smppChDto.smppUrl
        sessionConfig.port = smppChDto.smppPort
        sessionConfig.systemId = smppChDto.smppId
        sessionConfig.password = smppChDto.smppPwd
        sessionConfig.systemType = smppChDto.systemType
        sessionConfig.loggingOptions.setLogBytes(true)
        sessionConfig.loggingOptions.setLogPdu(true)

        setConnectionTimeout(sessionConfig)
        smppChDto.sessionConfig = sessionConfig
    }

    protected open fun setConnectionTimeout(sessionConfig: SmppSessionConfiguration) {

        sessionConfig.connectTimeout = 40000
    }

    protected open fun setSsl() {

    }

    @Throws(Exception::class)
    protected open fun makeEncode(smsSendDto: SmsSendDto): ByteArray {

        val content = smsSendDto.content
        when (smsSendDto.codingScheme?.toByte()) {

            SmppConstants.DATA_CODING_DEFAULT -> return CharsetUtil.encode(content, defaultEncoding)
            SmppConstants.DATA_CODING_IA5 -> return CharsetUtil.encode(content, CharsetUtil.CHARSET_GSM)
            SmppConstants.DATA_CODING_LATIN1 -> return CharsetUtil.encode(content, CharsetUtil.CHARSET_ISO_8859_1)
            SmppConstants.DATA_CODING_UCS2 -> return CharsetUtil.encode(content, CharsetUtil.CHARSET_UCS_2)
            else -> throw IllegalArgumentException("Unsupported codingScheme=$content")
        }
    }

    protected open fun makeToolAddr(smsSendDto: SmsSendDto): Address {

        return Address(0x01.toByte(), 0x01.toByte(), smsSendDto.toolData)
    }

    protected open fun makeSourceAddr(smsSendDto: SmsSendDto): Address {

        return Address(0x05.toByte(), 0x00.toByte(), smsSendDto.shortCode)
    }

    protected open fun addTlvOption(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

    }

    protected open fun setDcs(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

        submitSm.dataCoding = smsSendDto.codingScheme?.toByte() ?: return
    }

    protected open fun setEsmClass(smsSendDto: SmsSendDto, submitSm: SubmitSm) {

        if (smsSendDto.concatFlag) {
            submitSm.esmClass = SmppConstants.ESM_CLASS_UDHI_MASK
        }
    }

    @Throws(Exception::class)
    protected open fun makeSubmitSm(smsSendDto: SmsSendDto, textBytes: ByteArray): SubmitSm {

        val submitSm = SubmitSm()
        submitSm.sourceAddress = makeSourceAddr(smsSendDto)
        submitSm.destAddress = makeToolAddr(smsSendDto)
        submitSm.shortMessage = textBytes
        submitSm.registeredDelivery = smppChDto.registeredDelivery.toByte()
        setDcs(smsSendDto, submitSm)
        setEsmClass(smsSendDto, submitSm)
        addTlvOption(smsSendDto, submitSm)
        return submitSm
    }

    @Throws(Exception::class)
    protected open fun cutLimitLength(textBytes: ByteArray): ByteArray {

        if (textBytes.size <= SMPP_LIMIT_BYTE) {
            return textBytes
        }

        val textBytes2 = ByteArray(SMPP_LIMIT_BYTE)
        for (i in 0 until SMPP_LIMIT_BYTE) {
            textBytes2[i] = textBytes[i]
        }
        return textBytes2
    }

    fun sendSms(smsSendDto: SmsSendDto) {

        try {

            addSubmitLog()
            var textBytes = makeEncode(smsSendDto)
            textBytes = cutLimitLength(textBytes)

            checkSession()
            smppChDto.session ?: return

            val submitSm = makeSubmitSm(smsSendDto, textBytes)
            val pre = System.currentTimeMillis()
            val submitResp: SubmitSmResp? = smppChDto.session?.submit(submitSm, smppChDto.submitTimeout)
            sleepSubmit(System.currentTimeMillis() - pre)
            postSubmitResp(submitResp, smsSendDto)
        } catch (th: SmppChannelException) {

            smsSendDto.status = SmppStatus.RETRY.code
            asyncWaitAndBind()
        } catch (th: SmppInvalidArgumentException) {

            smsSendDto.status = SmppStatus.FAILED.code
            asyncWaitAndBind()
        } catch (th: SmppTimeoutException) {

            smsSendDto.status = SmppStatus.RETRY.code
            asyncWaitAndBind()
        } catch (th: Exception) {

            smsSendDto.status = SmppStatus.FAILED.code
            asyncWaitAndBind()
        }
    }

    private fun checkSession() {

        while (smppChDto.session == null) {
            sleepSubmit(1000L)
        }
    }

    @Throws(Exception::class)
    protected open fun addSubmitLog() {

    }

    open fun postSubmitResp(submitResp: SubmitSmResp?, smsSendDto: SmsSendDto) {

        if (submitResp?.commandStatus == SmppConstants.STATUS_OK) {
            smsSendDto.status = SmppStatus.SUCCESS.code
            smsSendDto.ticketId = submitResp?.messageId ?: ""
        } else {
            smsSendDto.status = SmppStatus.FAILED.code
        }
    }

    @Throws(Exception::class)
    protected fun sleepSubmit(sendTime: Long) {

    }

    protected open fun getStatusByCommandStatus(commandStatus: Int): Int {

        val status: Int
        when (commandStatus) {
            SmppConstants.STATUS_INVMSGLEN -> status = SmppStatus.FAILED.code
            SmppConstants.STATUS_INVSRCADR -> status = SmppStatus.FAILED.code
            SmppConstants.STATUS_INVDSTADR -> status = SmppStatus.FAILED.code
            SmppConstants.STATUS_X_R_APPN -> status = SmppStatus.FAILED.code
            else -> status = SmppStatus.RETRY.code
        }

        return status
    }
}