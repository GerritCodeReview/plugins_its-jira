load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'xmlrpc-client',
    id = 'org.apache.xmlrpc:xmlrpc-client:3.1.3',
    sha1 = 'e486ad917028b52265610206fb5a1e2b5914b94b',
    license = 'Apache2.0',
    deps = [':xmlrpc-common'],
  )

  maven_jar(
    name = 'xmlrpc-common',
    id = 'org.apache.xmlrpc:xmlrpc-common:3.1.3',
    sha1 = '415daf1f1473a947452588906dc9f5b3575fb44d',
    license = 'Apache2.0',
    deps = [':ws-commons-util'],
    visibility = [],
  )

  maven_jar(
    name = 'ws-commons-util',
    id = 'org.apache.ws.commons.util:ws-commons-util:1.0.2',
    sha1 = '3f478e6def772c19d1053f61198fa1f6a6119238',
    license = 'Apache2.0',
    deps = [':xml-apis'],
    visibility = [],
  )

  maven_jar(
    name = 'xml-apis',
    id = 'xml-apis:xml-apis:1.0.b2',
    sha1 = '3136ca936f64c9d68529f048c2618bd356bf85c9',
    license = 'Apache2.0',
  )

  maven_jar(
    name = 'wsdl4j',
    id = 'wsdl4j:wsdl4j:1.5.1',
    sha1 = 'bd804633b9c2cf06258641febc31a8ff3b0906bc',
    license = 'CPL1.0',
    local_license = True,
  )

  maven_jar(
    name = 'jira-rest-java-client-core',
    id = 'com.atlassian.jira:jira-rest-java-client-core:3.0.0',
    sha1 = '6dd9fcb1b6cbf2e266270bd3ab10a10f8c8a5464',
    deps = [
      ':atlassian-httpclient-apache-httpcomponents',
      ':atlassian-util-concurrent',
      ':guava',
      ':httpmime',
      ':jersey-client',
      ':jersey-json',
      ':jira-rest-java-client-api',
      ':joda-time'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'atlassian-httpclient-apache-httpcomponents',
    id = 'com.atlassian.httpclient:atlassian-httpclient-apache-httpcomponents:0.13.2',
    deps = [
      ':atlassian-event',
      ':atlassian-httpclient-api',
      ':httpasyncclient',
      ':httpasyncclient-cache',
      ':httpclient-cache',
      ':httpcore',
      ':httpcore-nio',
      ':sal-api',
      ':slf4j-api',
      ':spring-context'
    ],
    repository = "https://maven.atlassian.com/repository/public/",
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'spring-context',
    id = 'org.springframework:spring-context:2.5.6',
    deps = [
      ':aopalliance',
      ':spring-core',
      ':spring-beans'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'aopalliance',
    id = 'aopalliance:aopalliance:1.0',
    license = 'PublicDomain'
  )

  maven_jar(
    name = 'spring-beans',
    id = 'org.springframework:spring-beans:2.5.6',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'spring-core',
    id = 'org.springframework:spring-core:2.5.6',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpasyncclient',
    id  = 'org.apache.httpcomponents:httpasyncclient:4.0-beta3-atlassian-1',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'httpcore',
    id = 'org.apache.httpcomponents:httpcore:4.2.2',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpcore-nio',
    id = 'org.apache.httpcomponents:httpcore-nio:4.2.2',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpclient-cache',
    id = 'org.apache.httpcomponents:httpclient-cache:4.2.1-atlassian-2',
    deps = [
      ':httpclient'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'httpclient',
    id = 'org.apache.httpcomponents:httpclient:4.2.1-atlassian-2',
    deps = [
      ':commons-codec'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'commons-codec',
    id = 'commons-codec:commons-codec:1.6',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpasyncclient-cache',
    id = 'org.apache.httpcomponents:httpasyncclient-cache:4.0-beta3-atlassian-1',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'slf4j-api',
    id = 'org.slf4j:slf4j-api:1.6.4',
    license = 'slf4j'
  )

  maven_jar(
    name  ='atlassian-event',
    id = 'com.atlassian.event:atlassian-event:2.2.0',
    license = 'BSD',
    repository = "https://maven.atlassian.com/repository/public/",
    local_license = True
  )

  maven_jar(
    name = 'sal-api',
    id = 'com.atlassian.sal:sal-api:2.7.0',
    license = 'BSD',
    repository = "https://maven.atlassian.com/repository/public/",
    local_license = True
  )

  maven_jar(
    name = 'atlassian-httpclient-api',
    id = 'com.atlassian.httpclient:atlassian-httpclient-api:0.13.2',
    sha1 = '1d375dbeb33159b72fb85ff992c078712d7ebb92',
    deps = [
      ':atlassian-httpclient-spi'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/",
  )

  maven_jar(
    name = 'atlassian-httpclient-spi',
    id = 'com.atlassian.httpclient:atlassian-httpclient-spi:0.13.2',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'joda-time',
    id = 'joda-time:joda-time:1.6',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'atlassian-util-concurrent',
    id = 'com.atlassian.util.concurrent:atlassian-util-concurrent:2.4.0-M9',
    repository = "https://maven.atlassian.com/repository/public/",
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpmime',
    id = 'org.apache.httpcomponents:httpmime:4.1.2',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'guava',
    id = 'com.google.guava:guava:10.0.1',
    deps = [
      ':jsr305'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jsr305',
    id = 'com.google.code.findbugs:jsr305:1.3.9',
    license = 'Apache2.0',
    attach_source = False
  )

  maven_jar(
    name = 'jira-rest-java-client-api',
    id = 'com.atlassian.jira:jira-rest-java-client-api:3.0.0',
    sha1 = 'dd2dfa40cfbb43fe0e450d359d639c1d9a1fa28e',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'jersey-client',
    id = 'com.sun.jersey:jersey-client:1.5',
    deps = [
      ':jersey-core'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jersey-core',
    id = 'com.sun.jersey:jersey-core:1.5',
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jersey-json',
    id = 'com.sun.jersey:jersey-json:1.5',
    deps = [
      ':jackson-core-asl',
      ':jackson-mapper-asl',
      ':jackson-xc',
      ':jaxb-impl',
      ':jettison'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jettison',
    id = 'org.codehaus.jettison:jettison:1.1',
    deps = [
      ':stax-api-1'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'stax-api-1',
    id='stax:stax-api:1.0.1',
    license = 'Apache2.0',
    attach_source = False
  )

  maven_jar(
    name = 'jaxb-impl',
    id = 'com.sun.xml.bind:jaxb-impl:2.2.3',
    deps = [
      ':jaxb-api'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jaxb-api',
    id = 'javax.xml.bind:jaxb-api:2.2.2',
    deps = [
      ':stax-api-2'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'stax-api-2',
    id = 'javax.xml.stream:stax-api:1.0-2',
    license = 'CDDL',
    attach_source = False,
    local_license = True
  )

  maven_jar(
    name = 'jackson-core-asl',
    id = 'org.codehaus.jackson:jackson-core-asl:1.5.5',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-mapper-asl',
    id = 'org.codehaus.jackson:jackson-mapper-asl:1.5.5',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-jaxrs',
    id = 'org.codehaus.jackson:jackson-jaxrs:1.5.5',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-xc',
    id = 'org.codehaus.jackson:jackson-xc:1.5.5',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'axis',
    id = 'axis:axis:1.3',
    sha1 = '5292fcc2fd41fb22601db8fa92aed7a4223b4470',
    license = 'Apache2.0',
    exported_deps = [
      ':activation',
      ':discovery',
      ':jaxrpc',
      ':logging',
      ':mail',
      ':saaj',
    ],
  )

  maven_jar(
    name = 'discovery',
    id = 'commons-discovery:commons-discovery:0.5',
    sha1 = '3a8ac816bbe02d2f88523ef22cbf2c4abd71d6a8',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'logging',
    id = 'commons-logging:commons-logging:1.2',
    sha1 = '4bfc12adfe4842bf07b657f0369c4cb522955686',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'jaxrpc',
    id = 'axis:axis-jaxrpc:1.3',
    sha1 = 'a453e393f01b8fd19c13d30fab66a1f89916e1a8',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'mail',
    id = 'javax.mail:mail:1.4',
    sha1 = '1aa1579ae5ecd41920c4f355b0a9ef40b68315dd',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'activation',
    id = 'javax.activation:activation:1.1',
    sha1 = 'e6cb541461c2834bdea3eb920f1884d1eb508b50',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'wsdl4j',
    id = 'axis:axis-wsdl4j:1.3',
    sha1 = 'bd804633b9c2cf06258641febc31a8ff3b0906bc',
    attach_source = False,
    license = 'Apache2.0',
  )

  maven_jar(
    name = 'saaj',
    id = 'axis:axis-saaj:1.3',
    sha1 = '067785efd957974af624394e534d072a70670679',
    license = 'Apache2.0',
  )
