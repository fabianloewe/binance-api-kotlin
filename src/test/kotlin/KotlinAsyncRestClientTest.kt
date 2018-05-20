package de.hyronx.binance

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.domain.market.OrderBookEntry
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.future.completed
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class KotlinAsyncRestClientTest : StringSpec() {
    private val client: KotlinAsyncRestClient

    init {
        client = factory.newKotlinAsyncRestClient()

        "it should ping asynchronously" {
            runBlocking {
                try {
                    client.ping()
                    completed<Unit>()
                } catch (e: Exception) {
                    fail(e)
                }
            }
        }

        "it should return the server time asynchronously" {
            runBlocking {
                try {
                    val serverTime = client.getServerTime().await()
                    serverTime shouldNotBe 0
                } catch (e: Exception) {
                    fail(e)
                }
            }
        }

        "it should return the latest order book of XVG/ETH asynchronously" {
            runBlocking {
                try {
                    val book = client.getOrderBook("XVGETH", 5).await()
                    book.asks shouldNot beEmpty<OrderBookEntry>()
                    book.bids shouldNot beEmpty<OrderBookEntry>()
                } catch (e: Exception) {
                    fail(e)
                }
            }
        }
    }

    companion object {
        private val factory: BinanceApiClientFactory

        init {
            val props = Properties()
            props.load(Files.newBufferedReader(Paths.get("src/test/resources/test.properties")))

            factory = BinanceApiClientFactory.newInstance(
                    props.get("binance.apiKey") as? String,
                    props.get("binance.apiSecret") as? String)
        }
    }
}