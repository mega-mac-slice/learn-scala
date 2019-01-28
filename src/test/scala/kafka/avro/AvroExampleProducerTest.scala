package kafka.avro

import org.scalatest.FlatSpec

class AvroExampleProducerTest extends FlatSpec {
	"default ctor" should "not explode" in {
		val result = new AvroExampleProducer()
		assert(result != null)
	}
}
