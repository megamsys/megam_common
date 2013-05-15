
resolvers += Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("Typesafe Snapshots", url("http://repo.typesafe.com/typesafe/snapshots/"))

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.7")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.2.0")

