load("//tools/bzl:maven_jar.bzl", "maven_jar", "MAVEN_LOCAL")

ATLASSIAN_REPO = 'https://maven.atlassian.com/repository/public/'

def external_plugin_deps():
  maven_jar(
    name = 'xmlrpc_client',
    artifact = 'org.apache.xmlrpc:xmlrpc-client:3.1.3',
    deps = [
      '@xmlrpc_common//jar',
    ],
    sha1 = 'e486ad917028b52265610206fb5a1e2b5914b94b',
  )

  maven_jar(
    name = 'xmlrpc_common',
    artifact = 'org.apache.xmlrpc:xmlrpc-common:3.1.3',
    deps = [
      '@ws_commons_util//jar',
    ],
    sha1 = '415daf1f1473a947452588906dc9f5b3575fb44d',
  )

  maven_jar(
    name = 'ws_commons_util',
    artifact = 'org.apache.ws.commons.util:ws-commons-util:1.0.2',
    deps = [
      '@xml_apis//jar',
    ],
    sha1 = '3f478e6def772c19d1053f61198fa1f6a6119238',
  )

  maven_jar(
    name = 'xml_apis',
    artifact = 'xml-apis:xml-apis:1.0.b2',
    sha1 = '3136ca936f64c9d68529f048c2618bd356bf85c9',
  )

  maven_jar(
    name = 'wsdl4j',
    artifact = 'wsdl4j:wsdl4j:1.5.1',
    sha1 = 'bd804633b9c2cf06258641febc31a8ff3b0906bc',
  )

  maven_jar(
    name = 'jira_rest_java_client_core',
    artifact = 'com.atlassian.jira:jira-rest-java-client-core:3.0.1-SNAPSHOT',
    deps = [
      '@atlassian_httpclient_apache_httpcomponents//jar',
      '@atlassian_util_concurrent//jar',
      '@guava//jar:neverlink',
      '@httpmime//jar',
      '@jersey_client//jar',
      '@jersey_json//jar',
      '@jira_rest_java_client_api//jar',
      '@joda_time//jar',
    ],
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'atlassian_httpclient_apache_httpcomponents',
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-apache-httpcomponents:0.13.9-m2-SNAPSHOT',
    deps = [
      '@atlassian_event//jar',
      '@atlassian_httpclient_api//jar',
      '@httpasyncclient//jar',
      '@httpasyncclient_cache//jar',
      '@httpclient_cache//jar',
      '@httpcore//jar',
      '@httpcore_nio//jar',
      '@sal_api//jar',
      '@slf4j_api//jar',
      '@spring_context//jar',
    ],
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'spring_context',
    artifact = 'org.springframework:spring-context:2.5.6',
    deps = [
      '@aopalliance//jar',
      '@spring_core//jar',
      '@spring_beans//jar',
    ],
    sha1 = '983416e612875bdcf877dad4c9d5d77ae37e06ee',
  )

  maven_jar(
    name = 'aopalliance',
    artifact = 'aopalliance:aopalliance:1.0',
    sha1 = '0235ba8b489512805ac13a8f9ea77a1ca5ebe3e8',
  )

  maven_jar(
    name = 'spring_beans',
    artifact = 'org.springframework:spring-beans:2.5.6',
    sha1 = '449ea46b27426eb846611a90b2fb8b4dcf271191',
  )

  maven_jar(
    name = 'spring_core',
    artifact = 'org.springframework:spring-core:2.5.6',
    sha1 = 'c450bc49099430e13d21548d1e3d1a564b7e35e9',
  )

  maven_jar(
    name = 'httpclient_cache',
    artifact = 'org.apache.httpcomponents:httpclient-cache:4.4.1',
    sha1 = '6c9ba9c38bca8742d5745bb27bcd4b9c7542ea24',
    deps = [
      '@httpclient//jar:neverlink',
    ],
    repository = ATLASSIAN_REPO,
  )

  maven_jar(
    name = 'commons_codec',
    artifact = 'commons-codec:commons-codec:1.6',
    sha1 = 'b7f0fc8f61ecadeb3695f0b9464755eee44374d4',
  )

  maven_jar(
    name = 'httpasyncclient_cache',
    artifact = 'org.apache.httpcomponents:httpasyncclient-cache:4.1.1',
    sha1 = '93601cc6fe8f4b042260c6cda9d52fffb62ae31a',
    repository = ATLASSIAN_REPO,
  )

  maven_jar(
    name = 'slf4j_api',
    artifact = 'org.slf4j:slf4j-api:1.6.4',
    sha1 = '2396d74b12b905f780ed7966738bb78438e8371a',
  )

  maven_jar(
    name  ='atlassian_event',
    artifact = 'com.atlassian.event:atlassian-event:2.2.0',
    sha1 = 'c7a1b83e92fdb05e02ba717fbce3b7551ce945f7',
    repository = ATLASSIAN_REPO,
  )

  maven_jar(
    name = 'sal_api',
    artifact = 'com.atlassian.sal:sal-api:2.7.0',
    sha1 = 'b0d49ec14b5c823c24821bfbc8cd618525390a1b',
    repository = ATLASSIAN_REPO,
  )

  maven_jar(
    name = 'atlassian_httpclient_api',
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-api:0.13.9-m2-SNAPSHOT',
    deps = [
      '@atlassian_httpclient_spi//jar',
    ],
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'atlassian_httpclient_spi',
    artifact = 'com.atlassian.httpclient:atlassian-httpclient-spi:0.13.9-m2-SNAPSHOT',
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'joda_time',
    artifact = 'joda-time:joda-time:1.6',
    sha1 = '5a18504e34c5cbe9259d6fd0123ccf6f16115a41',
  )

  maven_jar(
    name = 'atlassian_util_concurrent',
    artifact = 'com.atlassian.util.concurrent:atlassian-util-concurrent:2.4.0-M9',
    sha1 = 'e8469a31d1999f5459a2c5a61234755f625fc3f7',
    repository = ATLASSIAN_REPO,
  )

  maven_jar(
    name = 'httpmime',
    artifact = 'org.apache.httpcomponents:httpmime:4.1.2',
    sha1 = 'c78eaaea68d0f36f73b646f08cdb34f1a784239b',
  )

  maven_jar(
    name = 'jsr305',
    artifact = 'com.google.code.findbugs:jsr305:1.3.9',
    sha1 = '40719ea6961c0cb6afaeb6a921eaa1f6afd4cfdf',
    attach_source = False
  )

  maven_jar(
    name = 'jira_rest_java_client_api',
    artifact = 'com.atlassian.jira:jira-rest-java-client-api:3.0.1-SNAPSHOT',
    repository = MAVEN_LOCAL,
  )

  maven_jar(
    name = 'jersey_client',
    artifact = 'com.sun.jersey:jersey-client:1.5',
    deps = [
      '@jersey_core//jar',
    ],
    sha1 = '4dc285245a495c2682b743c81cb67a7d1e8b2e7d',
  )

  maven_jar(
    name = 'jersey_core',
    artifact = 'com.sun.jersey:jersey-core:1.5',
    sha1 = '955fa871051acd23841ff75087b190278228920a',
  )

  maven_jar(
    name = 'jersey_json',
    artifact = 'com.sun.jersey:jersey-json:1.5',
    deps = [
      '@jackson_core_asl//jar',
      '@jackson_mapper_asl//jar',
      '@jackson_xc//jar',
      '@jaxb_impl//jar',
      '@jettison//jar',
    ],
    sha1 = '8d77a3d4277016277cb85b98c179a2a7e9ec6dfb',
  )

  maven_jar(
    name = 'jettison',
    artifact = 'org.codehaus.jettison:jettison:1.1',
    deps = [
      '@stax_api_1//jar',
    ],
    sha1 = '1a01a2a1218fcf9faa2cc2a6ced025bdea687262',
  )

  maven_jar(
    name = 'stax_api_1',
    artifact = 'stax:stax-api:1.0.1',
    sha1 = '49c100caf72d658aca8e58bd74a4ba90fa2b0d70',
    attach_source = False,
  )

  maven_jar(
    name = 'jaxb_impl',
    artifact = 'com.sun.xml.bind:jaxb-impl:2.2.3',
    deps = [
      '@jaxb_api//jar',
    ],
    sha1 = '565307a5e9c563666418e70b22c07886104e6ba7',
  )

  maven_jar(
    name = 'jaxb_api',
    artifact = 'javax.xml.bind:jaxb-api:2.2.2',
    deps = [
      '@stax_api_2//jar',
    ],
    sha1 = 'aeb3021ca93dde265796d82015beecdcff95bf09',
  )

  maven_jar(
    name = 'stax_api_2',
    artifact = 'javax.xml.stream:stax-api:1.0-2',
    sha1 = 'd6337b0de8b25e53e81b922352fbea9f9f57ba0b',
    attach_source = False,
  )

  maven_jar(
    name = 'jackson_core_asl',
    artifact = 'org.codehaus.jackson:jackson-core-asl:1.5.5',
    sha1 = 'beb1ba1c1b84c2f2c54b8f1ce63224ca0f98334d',
  )

  maven_jar(
    name = 'jackson_mapper_asl',
    artifact = 'org.codehaus.jackson:jackson-mapper-asl:1.5.5',
    sha1 = '908a1589e4ef52dc81252bc18d678bd232423c10',
  )

  maven_jar(
    name = 'jackson_jaxrs',
    artifact = 'org.codehaus.jackson:jackson-jaxrs:1.5.5',
    sha1 = '2141d77b65d5bb36a2cdafb1010b8704c3fd4585',
  )

  maven_jar(
    name = 'jackson_xc',
    artifact = 'org.codehaus.jackson:jackson-xc:1.5.5',
    sha1 = '58811cfa31b5355d0146aae33b3fc3fee661d5bc',
  )
