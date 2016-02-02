
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

resolvers += "bigtoast-github" at "http://bigtoast.github.com/repo/"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
