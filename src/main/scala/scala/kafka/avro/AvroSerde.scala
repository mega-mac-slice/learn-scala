package kafka.avro

import com.sksamuel.avro4s.RecordFormat
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

// Adapted from https://github.com/CasperKoning/avro4s-schema-registry-kafka-streams-demo
// implementation

object AvroSerde {
	val subjectNameStrategy = "io.confluent.kafka.serializers.subject.RecordNameStrategy"
}

class AvroSerde[CC](schemaRegistryUrl: String = "http://localhost:8081", isKey: Boolean)(
		implicit format: RecordFormat[CC]
) extends Serde[CC] {

	class AvroDeserializer(schemaRegistryUrl: String = "http://localhost:8081",
	                               isKey: Boolean)(implicit format: RecordFormat[CC])
		extends Deserializer[CC] {
		private val deserializer = {
			val s = new KafkaAvroDeserializer()
			val configs = new java.util.HashMap[String, Any]()
			configs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
			configs.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false")
			configs.put(AbstractKafkaAvroSerDeConfig.KEY_SUBJECT_NAME_STRATEGY,
				AvroSerde.subjectNameStrategy)
			configs.put(AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
				AvroSerde.subjectNameStrategy)
			s.configure(configs, isKey)
			s
		}

		override def configure(configs: java.util.Map[String, _], isKey: Boolean): Unit = {}

		override def close(): Unit = deserializer.close()

		override def deserialize(topic: String, data: Array[Byte]): CC = {
			val record = deserializer.deserialize(topic, data).asInstanceOf[GenericRecord]
			format.from(record)
		}
	}

	class AvroSerializer(schemaRegistryUrl: String = "http://localhost:8081", isKey: Boolean)(
			implicit format: RecordFormat[CC])
		extends Serializer[CC] {
		private val serializer = {
			val s = new KafkaAvroSerializer()
			val configs = new java.util.HashMap[String, Any]()
			configs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
			configs.put(AbstractKafkaAvroSerDeConfig.KEY_SUBJECT_NAME_STRATEGY,
				AvroSerde.subjectNameStrategy)
			configs.put(AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
				AvroSerde.subjectNameStrategy)
			s.configure(configs, isKey)
			s
		}

		override def configure(configs: java.util.Map[String, _], isKey: Boolean): Unit = {}

		override def close(): Unit = serializer.close()

		override def serialize(topic: String, data: CC): Array[Byte] = {
			val record = format.to(data)
			serializer.serialize(topic, record)
		}
	}

	override def deserializer(): Deserializer[CC] =
		new AvroDeserializer(schemaRegistryUrl, isKey)

	override def serializer(): Serializer[CC] = new AvroSerializer(schemaRegistryUrl, isKey)

	override def configure(configs: java.util.Map[String, _], isKey: Boolean): Unit = {}

	override def close(): Unit = {}
}
