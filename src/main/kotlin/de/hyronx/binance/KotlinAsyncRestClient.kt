package de.hyronx.binance

import com.binance.api.client.BinanceApiAsyncRestClient
import com.binance.api.client.BinanceApiCallback
import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.constant.BinanceApiConstants
import com.binance.api.client.domain.account.NewOrder
import com.binance.api.client.domain.account.request.AllOrdersRequest
import com.binance.api.client.domain.account.request.CancelOrderRequest
import com.binance.api.client.domain.account.request.OrderRequest
import com.binance.api.client.domain.account.request.OrderStatusRequest
import com.binance.api.client.domain.market.CandlestickInterval
import com.binance.api.client.impl.BinanceApiService

import com.binance.api.client.impl.BinanceApiServiceGenerator.createService
import com.binance.api.client.impl.BinanceApiServiceGenerator.executeSync

import kotlinx.coroutines.experimental.async

fun BinanceApiClientFactory.newKotlinAsyncRestClient(): KotlinAsyncRestClient {
    val apiKeyField = BinanceApiClientFactory::class.java.getDeclaredField("apiKey")
    apiKeyField.isAccessible = true

    val secretField = BinanceApiClientFactory::class.java.getDeclaredField("apiKey")
    secretField.isAccessible = true

    return KotlinAsyncRestClient(apiKeyField.get(this) as? String, secretField.get(this) as? String)
}

class KotlinAsyncRestClient(apiKey: String?, apiSecret: String?) {
    private val service: BinanceApiService = createService(BinanceApiService::class.java, apiKey, apiSecret)

    fun ping() = async { executeSync(service.ping()) }

    fun getServerTime() = async { executeSync(service.serverTime).serverTime }

    fun getOrderBook(symbol: String, limit: Int) = async { executeSync(service.getOrderBook(symbol, limit)) }

    fun getAggTrades(
            symbol: String,
            fromId: String? = null,
            limit: Int? = null,
            startTime: Long? = null,
            endTime: Long? = null) =
            async { executeSync(service.getAggTrades(symbol, fromId, limit, startTime, endTime)) }

    fun getCandlestickBars(
            symbol: String,
            interval: CandlestickInterval,
            limit: Int? = null,
            startTime: Long? = null,
            endTime: Long? = null) =
            async { executeSync(service.getCandlestickBars(symbol, interval.intervalId, limit, startTime, endTime)) }

    fun get24HrPriceStatistics(symbol: String) = async { executeSync(service.get24HrPriceStatistics(symbol)) }

    fun getAll24HrPriceStatistics() = async { executeSync(service.all24HrPriceStatistics) }

    fun getPrice(symbol: String) = async { executeSync(service.getLatestPrice(symbol)) }

    fun getAllPrices() = async { executeSync(service.latestPrices) }

    fun getBookTickers() = async { executeSync(service.bookTickers) }

    fun newOrder(order: NewOrder) = async {
        executeSync(service.newOrder(
                order.symbol, order.side, order.type,
                order.timeInForce, order.quantity, order.price, order.newClientOrderId, order.stopPrice,
                order.icebergQty, order.newOrderRespType, order.recvWindow, order.timestamp))
    }

    fun newOrderTest(order: NewOrder) = async {
        executeSync(service.newOrderTest(
                order.symbol, order.side, order.type,
                order.timeInForce, order.quantity, order.price, order.newClientOrderId, order.stopPrice,
                order.icebergQty, order.newOrderRespType, order.recvWindow, order.timestamp)
        )
    }

    fun getOrderStatus(orderStatusRequest: OrderStatusRequest) = async {
        executeSync(service.getOrderStatus(
                orderStatusRequest.symbol,
                orderStatusRequest.orderId, orderStatusRequest.origClientOrderId,
                orderStatusRequest.recvWindow, orderStatusRequest.timestamp))
    }

    fun cancelOrder(cancelOrderRequest: CancelOrderRequest) = async {
        executeSync(service.cancelOrder(
                cancelOrderRequest.symbol,
                cancelOrderRequest.orderId, cancelOrderRequest.origClientOrderId,
                cancelOrderRequest.newClientOrderId,
                cancelOrderRequest.recvWindow, cancelOrderRequest.timestamp))
    }

    fun getOpenOrders(orderRequest: OrderRequest) = async {
        executeSync(service.getOpenOrders(orderRequest.symbol, orderRequest.recvWindow, orderRequest.timestamp))
    }

    fun getAllOrders(orderRequest: AllOrdersRequest) = async {
        executeSync(service.getAllOrders(orderRequest.symbol,
                orderRequest.orderId, orderRequest.limit,
                orderRequest.recvWindow, orderRequest.timestamp))
    }

    fun getAccount(recvWindow: Long, timestamp: Long) = async { executeSync(service.getAccount(recvWindow, timestamp)) }

    fun getAccount() = getAccount(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())

    fun getMyTrades(symbol: String, limit: Int?, fromId: Long?, recvWindow: Long, timestamp: Long) = async {
        executeSync(service.getMyTrades(symbol, limit, fromId, recvWindow, timestamp))
    }

    fun getMyTrades(symbol: String, limit: Int? = null) = getMyTrades(
            symbol,
            limit,
            null,
            BinanceApiConstants.DEFAULT_RECEIVING_WINDOW,
            System.currentTimeMillis())

    fun withdraw(asset: String, address: String, amount: String, name: String, addressTag: String) = async {
        executeSync(service.withdraw(
                asset,
                address,
                amount,
                name,
                addressTag,
                BinanceApiConstants.DEFAULT_RECEIVING_WINDOW,
                System.currentTimeMillis()))
    }

    fun getDepositHistory(asset: String) = async {
        executeSync(service.getDepositHistory(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()))
    }

    fun getWithdrawHistory(asset: String) = async {
        executeSync(service.getWithdrawHistory(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()))
    }

    fun getDepositAddress(asset: String) = async {
        executeSync(service.getDepositAddress(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()))
    }

    fun startUserDataStream() = async {
        executeSync(service.startUserDataStream()).toString()
    }

    fun keepAliveUserDataStream(listenKey: String) = async {
        executeSync(service.keepAliveUserDataStream(listenKey))
    }

    fun closeUserDataStream(listenKey: String) = async {
        executeSync(service.closeAliveUserDataStream(listenKey))
    }
}