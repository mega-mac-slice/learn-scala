package kafka.simple

import java.util.Properties

import de.huxhorn.sulky.ulid.ULID
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

class SimpleProducer {
	private val BootstrapServer = "0.0.0.0:2181"
	private val Topic = "simple-2019.01.20.001"
	private val KeyGenerator = new ULID()

	private[this] val props = {
		val props = new Properties()
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
		props
	}
	private[this] val producer = new KafkaProducer[String, String](props)

	def send(message: String): Unit = {
		val key = KeyGenerator.nextULID()
		val record = new ProducerRecord(Topic, key, message)
		producer.send(record)
	}
}
