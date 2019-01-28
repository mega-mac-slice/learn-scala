package kafka.avro

import java.util.Properties

import com.sksamuel.avro4s.{FromRecord, FromValue}
import de.huxhorn.sulky.ulid.ULID
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.ByteArraySerializer

class AvroExampleProducer {
	private val BootstrapServer = "0.0.0.0:2181"
	private val SchemaUrl = "http://localhost:8081"
	private val Topic = "avro-2019.01.23.001"
	private val KeyGenerator = new ULID()

	private[this] val props = {
		val props = new Properties()
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[ByteArraySerializer].getName)
		props
	}

  implicit val AvroExamplePayloadFromRecord: FromRecord[AvroExamplePayload] = FromRecord[AvroExamplePayload]

	private[this] val valueSerde = {
		new AvroSerde[AvroExamplePayload](SchemaUrl, false)
	}

	private[this] val producer = new KafkaProducer[String, Array[Byte]](props)

	def send(payload: AvroExamplePayload): Unit = {
		val key = KeyGenerator.nextULID()
		val value = valueSerde.serializer().serialize(Topic, payload)
		val record = new ProducerRecord(Topic, key, value)
		producer.send(record)
	}
}
