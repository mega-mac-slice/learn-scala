name := "learn-scala"
version := "0.1"
scalaVersion := "2.12.8"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

libraryDependencies ++= Seq(
	// kafka
	"org.apache.kafka" %% "kafka" % "2.1.0",
	"org.apache.kafka" % "kafka-clients" % "2.1.0",
	//avro
	"com.sksamuel.avro4s" %% "avro4s-core" % "1.9.0",
	// confluent
	"io.confluent" % "kafka-avro-serializer" % "4.1.0",
	"io.confluent" % "kafka-streams-avro-serde" % "4.1.0",
	"io.confluent" % "kafka-schema-registry-client" % "4.1.0",
	// other
	"de.huxhorn.sulky" % "de.huxhorn.sulky.ulid" % "8.2.0",

	// test
	"org.scalatest" %% "scalatest" % "3.0.5" % Test
)
