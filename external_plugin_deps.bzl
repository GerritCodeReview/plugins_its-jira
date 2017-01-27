load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
  maven_jar(
    name = 'xmlrpc-client',
    artifact = 'org.apache.xmlrpc:xmlrpc-client:3.1.3',
    sha1 = 'e486ad917028b52265610206fb5a1e2b5914b94b',
    deps = [':xmlrpc-common'],
  )

  maven_jar(
    name = 'xmlrpc-common',
    artifact = 'org.apache.xmlrpc:xmlrpc-common:3.1.3',
    sha1 = '415daf1f1473a947452588906dc9f5b3575fb44d',
    license = 'Apache2.0',
    deps = [':ws-commons-util'],
    visibility = [],
  )

  maven_jar(
    name = 'ws-commons-util',
    artifact = 'org.apache.ws.commons.util:ws-commons-util:1.0.2',
    sha1 = '3f478e6def772c19d1053f61198fa1f6a6119238',
    license = 'Apache2.0',
    deps = [':xml-apis'],
    visibility = [],
  )

  maven_jar(
    name = 'xml-apis',
    artifact = 'xml-apis:xml-apis:1.0.b2',
    sha1 = '3136ca936f64c9d68529f048c2618bd356bf85c9',
    license = 'Apache2.0',
  )

  maven_jar(
    name = 'wsdl4j',
    artifact = 'wsdl4j:wsdl4j:1.5.1',
    sha1 = 'bd804633b9c2cf06258641febc31a8ff3b0906bc',
    license = 'CPL1.0',
    local_license = True,
  )

  maven_jar(
    name = 'jira-rest-java-client-core',
    artifact = 'com.atlassian.jira:jira-rest-java-client-core:3.0.0',
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
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-apache-httpcomponents:0.13.2',
    sha1 = '0afe44baeab6c7d937a3702c27869080e07d5858',
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
    artifact = 'org.springframework:spring-context:2.5.6',
    sha1 = 'a71d857094cc1a0ec8c5c6d4900d26cf22110c87',
    deps = [
      ':aopalliance',
      ':spring-core',
      ':spring-beans'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'aopalliance',
    artifact = 'aopalliance:aopalliance:1.0',
    sha1 = '0235ba8b489512805ac13a8f9ea77a1ca5ebe3e8',
    license = 'PublicDomain'
  )

  maven_jar(
    name = 'spring-beans',
    artifact = 'org.springframework:spring-beans:2.5.6',
    sha1 = '449ea46b27426eb846611a90b2fb8b4dcf271191',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'spring-core',
    artifact = 'org.springframework:spring-core:2.5.6',
    sha1 = 'c450bc49099430e13d21548d1e3d1a564b7e35e9',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpasyncclient',
    artifact  = 'org.apache.httpcomponents:httpasyncclient:4.0-beta3-atlassian-1',
    sha1 = '41e5d5cf3956de717f970231f6cfb58f2c5db755',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'httpcore',
    artifact = 'org.apache.httpcomponents:httpcore:4.2.2',
    sha1 = 'f09ea7959e73a80f51429a0387dc9e66d9e27cd9'
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpcore-nio',
    artifact = 'org.apache.httpcomponents:httpcore-nio:4.2.2',
    sha1 = '4d3b2dd04fc6d02323470ca78425eddc2fb3d4d0',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpclient-cache',
    artifact = 'org.apache.httpcomponents:httpclient-cache:4.2.1-atlassian-2',
    sha1 = '6bba8a512c0c3127a7b245bd69c85cde44aee817',
    deps = [
      ':httpclient'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'httpclient',
    artifact = 'org.apache.httpcomponents:httpclient:4.2.1-atlassian-2',
    sha1 = '1bf4e0a674c553b318698792c2ddbd787150ac95',
    deps = [
      ':commons-codec'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'commons-codec',
    artifact = 'commons-codec:commons-codec:1.6',
    sha1 = 'b7f0fc8f61ecadeb3695f0b9464755eee44374d4',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpasyncclient-cache',
    artifact = 'org.apache.httpcomponents:httpasyncclient-cache:4.0-beta3-atlassian-1',
    sha1 = '58a6212aa98bec35d47a1916d4971b10b8200b65'
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'slf4j-api',
    artifact = 'org.slf4j:slf4j-api:1.6.4',
    sha1 = '2396d74b12b905f780ed7966738bb78438e8371a',
    license = 'slf4j'
  )

  maven_jar(
    name  ='atlassian-event',
    artifact = 'com.atlassian.event:atlassian-event:2.2.0',
    sha1 = 'c7a1b83e92fdb05e02ba717fbce3b7551ce945f7'
    license = 'BSD',
    repository = "https://maven.atlassian.com/repository/public/",
    local_license = True
  )

  maven_jar(
    name = 'sal-api',
    artifact = 'com.atlassian.sal:sal-api:2.7.0',
    sha1 = 'b0d49ec14b5c823c24821bfbc8cd618525390a1b',
    license = 'BSD',
    repository = "https://maven.atlassian.com/repository/public/",
    local_license = True
  )

  maven_jar(
    name = 'atlassian-httpclient-api',
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-api:0.13.2',
    sha1 = '1d375dbeb33159b72fb85ff992c078712d7ebb92',
    deps = [
      ':atlassian-httpclient-spi'
    ],
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/",
  )

  maven_jar(
    name = 'atlassian-httpclient-spi',
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-spi:0.13.2',
    sha1 = '0794e003b42392501c24ea5b7d78a3a1c405dc2a',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'joda-time',
    artifact = 'joda-time:joda-time:1.6',
    sha1 = '5a18504e34c5cbe9259d6fd0123ccf6f16115a41',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'atlassian-util-concurrent',
    artifact = 'com.atlassian.util.concurrent:atlassian-util-concurrent:2.4.0-M9',
    sha1 = 'e8469a31d1999f5459a2c5a61234755f625fc3f7',
    repository = "https://maven.atlassian.com/repository/public/",
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'httpmime',
    artifact = 'org.apache.httpcomponents:httpmime:4.1.2',
    sha1 = 'c78eaaea68d0f36f73b646f08cdb34f1a784239b',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'guava',
    artifact = 'com.google.guava:guava:10.0.1',
    sha1 = '292c96f9cb18231528cac4b0bf17d28149d14809',
    deps = [
      ':jsr305'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jsr305',
    artifact = 'com.google.code.findbugs:jsr305:1.3.9',
    sha1 = '40719ea6961c0cb6afaeb6a921eaa1f6afd4cfdf',
    license = 'Apache2.0',
    attach_source = False
  )

  maven_jar(
    name = 'jira-rest-java-client-api',
    artifact = 'com.atlassian.jira:jira-rest-java-client-api:3.0.0',
    sha1 = 'dd2dfa40cfbb43fe0e450d359d639c1d9a1fa28e',
    license = 'Apache2.0',
    repository = "https://maven.atlassian.com/repository/public/"
  )

  maven_jar(
    name = 'jersey-client',
    artifact = 'com.sun.jersey:jersey-client:1.5',
    sha1 = '4dc285245a495c2682b743c81cb67a7d1e8b2e7d',
    deps = [
      ':jersey-core'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jersey-core',
    artifact = 'com.sun.jersey:jersey-core:1.5',
    sha1 = '955fa871051acd23841ff75087b190278228920a',
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jersey-json',
    artifact = 'com.sun.jersey:jersey-json:1.5',
    sha1 = '8d77a3d4277016277cb85b98c179a2a7e9ec6dfb',
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
    artifact = 'org.codehaus.jettison:jettison:1.1',
    sha1 = '1a01a2a1218fcf9faa2cc2a6ced025bdea687262',
    deps = [
      ':stax-api-1'
    ],
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'stax-api-1',
    artifact = 'stax:stax-api:1.0.1',
    sha1 = '49c100caf72d658aca8e58bd74a4ba90fa2b0d70',
    license = 'Apache2.0',
    attach_source = False
  )

  maven_jar(
    name = 'jaxb-impl',
    artifact = 'com.sun.xml.bind:jaxb-impl:2.2.3',
    sha1 = '565307a5e9c563666418e70b22c07886104e6ba7',
    deps = [
      ':jaxb-api'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'jaxb-api',
    artifact = 'javax.xml.bind:jaxb-api:2.2.2',
    sha1 = 'aeb3021ca93dde265796d82015beecdcff95bf09',
    deps = [
      ':stax-api-2'
    ],
    license = 'CDDL',
    local_license = True
  )

  maven_jar(
    name = 'stax-api-2',
    artifact = 'javax.xml.stream:stax-api:1.0-2',
    sha1 = 'd6337b0de8b25e53e81b922352fbea9f9f57ba0b',
    license = 'CDDL',
    attach_source = False,
    local_license = True
  )

  maven_jar(
    name = 'jackson-core-asl',
    artifact = 'org.codehaus.jackson:jackson-core-asl:1.5.5',
    sha1 = 'beb1ba1c1b84c2f2c54b8f1ce63224ca0f98334d',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-mapper-asl',
    artifact = 'org.codehaus.jackson:jackson-mapper-asl:1.5.5',
    sha1 = '908a1589e4ef52dc81252bc18d678bd232423c10',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-jaxrs',
    artifact = 'org.codehaus.jackson:jackson-jaxrs:1.5.5',
    sha1 = '2141d77b65d5bb36a2cdafb1010b8704c3fd4585',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'jackson-xc',
    artifact = 'org.codehaus.jackson:jackson-xc:1.5.5',
    sha1 = '58811cfa31b5355d0146aae33b3fc3fee661d5bc',
    license = 'Apache2.0'
  )

  maven_jar(
    name = 'axis',
    artifact = 'axis:axis:1.3',
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
    artifact = 'commons-discovery:commons-discovery:0.5',
    sha1 = '3a8ac816bbe02d2f88523ef22cbf2c4abd71d6a8',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'logging',
    artifact = 'commons-logging:commons-logging:1.2',
    sha1 = '4bfc12adfe4842bf07b657f0369c4cb522955686',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'jaxrpc',
    artifact = 'axis:axis-jaxrpc:1.3',
    sha1 = 'a453e393f01b8fd19c13d30fab66a1f89916e1a8',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'mail',
    artifact = 'javax.mail:mail:1.4',
    sha1 = '1aa1579ae5ecd41920c4f355b0a9ef40b68315dd',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'activation',
    artifact = 'javax.activation:activation:1.1',
    sha1 = 'e6cb541461c2834bdea3eb920f1884d1eb508b50',
    license = 'Apache2.0',
    visibility = [],
  )

  maven_jar(
    name = 'wsdl4j',
    artifact = 'axis:axis-wsdl4j:1.3',
    sha1 = 'bd804633b9c2cf06258641febc31a8ff3b0906bc',
    attach_source = False,
    license = 'Apache2.0',
  )

  maven_jar(
    name = 'saaj',
    artifact = 'axis:axis-saaj:1.3',
    sha1 = '067785efd957974af624394e534d072a70670679',
    license = 'Apache2.0',
  )
