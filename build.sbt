name := "storj-ml"

version := "1.0"

lazy val `predictive_analytics_service` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val sparkVersion = "1.6.0"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq( jdbc,
	cache,
	ws,
	specs2 % Test,
	"com.typesafe.akka" %% "akka-testkit" % "2.3.14" % "test",
	"org.scalanlp" %% "breeze" % "0.12",
	"org.scalanlp" %% "breeze-natives" % "0.12",
	"org.scalaz" %% "scalaz-core" % "7.2.4",
	"org.scalaz" %% "scalaz-concurrent" % "7.2.4",
	"org.apache.spark" %% "spark-core" % sparkVersion,
	"org.apache.spark" %% "spark-sql" % sparkVersion,
	"org.apache.spark" %% "spark-mllib" % sparkVersion,
	"org.apache.spark" %% "spark-streaming" % sparkVersion,
	"org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
	"com.typesafe" % "config" % "1.3.0",
	"org.elasticsearch" % "elasticsearch-spark_2.11" % "2.3.2",
	"com.github.nscala-time" %% "nscala-time" % "2.14.0",
  "ch.qos.logback"     % "logback-classic" % "1.1.3"
)

pipelineStages := Seq(rjs)
