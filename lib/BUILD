package(default_visibility = ["//visibility:public"])

java_library(
  name = 'xmlrpc-client',
  runtime_deps = [':xmlrpc-common'],
  exports = ["@xmlrpc-client//jar"],
)

java_library(
  name = 'xmlrpc-common',
  runtime_deps = [':ws-commons-util'],
  exports = ["@xmlrpc-common//jar"],
  visibility = [],
)

java_library(
  name = 'ws-commons-util',
  runtime_deps = [':xml-apis'],
  exports = ["@ws-commons-util//jar"],
  visibility = [],
)

java_library(
  name = 'xml-apis',
  exports = ["@xml-apis//jar"],
)

java_library(
  name = 'wsdl4j',
  exports = ["@wsdl4j//jar"],
)

java_library(
  name = 'jira-rest-java-client-core',
  runtime_deps = [
    ':atlassian-httpclient-apache-httpcomponents',
    ':atlassian-util-concurrent',
    ':guava',
    ':httpmime',
    ':jersey-client',
    ':jersey-json',
    ':jira-rest-java-client-api',
    ':joda-time'
  ],
  exports = ["@jira-rest-java-client-core//jar"],
)

java_library(
  name = 'atlassian-httpclient-apache-httpcomponents',
  runtime_deps = [
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
  exports = ["@atlassian-httpclient-apache-httpcomponents//jar"],
)

java_library(
  name = 'spring-context',
  runtime_deps = [
    ':aopalliance',
    ':spring-core',
    ':spring-beans'
  ],
  exports = ["@spring-context//jar"],
)

java_library(
  name = 'aopalliance',
  exports = ["@aopalliance//jar"],
)

java_library(
  name = 'spring-beans',
  exports = ["@spring-beans//jar"],
)

java_library(
  name = 'spring-core',
  exports = ["@spring-core//jar"],
)

java_library(
  name = 'httpasyncclient',
  exports = ["@httpasyncclient//jar"],
)

java_library(
  name = 'httpcore',
  exports = ["@httpcore//jar"],
)

java_library(
  name = 'httpcore-nio',
  exports = ["@httpcore-nio//jar"],
)

java_library(
  name = 'httpclient-cache',
  runtime_deps = [
    ':httpclient'
  ],
  exports = ["@httpclient-cache//jar"],
)

java_library(
  name = 'httpclient',
  runtime_deps = [
    ':commons-codec'
  ],
  exports = ["@httpclient//jar"],
)

java_library(
  name = 'commons-codec',
  exports = ["@commons-codec//jar"],
)

java_library(
  name = 'httpasyncclient-cache',
  exports = ["@httpasyncclient-cache//jar"],
)

java_library(
  name = 'slf4j-api',
  exports = ["@slf4j-api//jar"],
)

java_library(
  name  ='atlassian-event',
  exports = ["@atlassian-event//jar"],
)

java_library(
  name = 'sal-api',
  exports = ["@sal-api//jar"],
)

java_library(
  name = 'atlassian-httpclient-api',
  runtime_deps = [
    ':atlassian-httpclient-spi'
  ],
  exports = ["@atlassian-httpclient-api//jar"],
)

java_library(
  name = 'atlassian-httpclient-spi',
  exports = ["@atlassian-httpclient-spi//jar"],
)

java_library(
  name = 'joda-time',
  exports = ["@joda-time//jar"],
)

java_library(
  name = 'atlassian-util-concurrent',
  exports = ["@atlassian-util-concurrent//jar"],
)

java_library(
  name = 'httpmime',
  exports = ["@httpmime//jar"],
)

java_library(
  name = 'guava',
  runtime_deps = [
    ':jsr305'
  ],
  exports = ["@guava//jar"],
)

java_library(
  name = 'jsr305',
  exports = ["@jsr305//jar"],
)

java_library(
  name = 'jira-rest-java-client-api',
  exports = ["@jira-rest-java-client-api//jar"],
)

java_library(
  name = 'jersey-client',
  runtime_deps = [
    ':jersey-core'
  ],
  exports = ["@jersey-client//jar"],
)

java_library(
  name = 'jersey-core',
  exports = ["@jersey-core//jar"],
)

java_library(
  name = 'jersey-json',
  runtime_deps = [
    ':jackson-core-asl',
    ':jackson-mapper-asl',
    ':jackson-xc',
    ':jaxb-impl',
    ':jettison'
  ],
  exports = ["@jersey-json//jar"],
)

java_library(
  name = 'jettison',
  runtime_deps = [
    ':stax-api-1'
  ],
  exports = ["@jettison//jar"],
)

java_library(
  name = 'stax-api-1',
  exports = ["@stax-api-1//jar"],
)

java_library(
  name = 'jaxb-impl',
  runtime_deps = [
    ':jaxb-api'
  ],
  exports = ["@jaxb-impl//jar"],
)

java_library(
  name = 'jaxb-api',
  runtime_deps = [
    ':stax-api-2'
  ],
  exports = ["@jaxb-api//jar"],
)

java_library(
  name = 'stax-api-2',
  exports = ["@stax-api-2//jar"],
)

java_library(
  name = 'jackson-core-asl',
  exports = ["@jackson-core-asl//jar"],
)

java_library(
  name = 'jackson-mapper-asl',
  exports = ["@jackson-mapper-asl//jar"],
)

java_library(
  name = 'jackson-jaxrs',
  exports = ["@jackson-jaxrs//jar"],
)

java_library(
  name = 'jackson-xc',
  exports = ["@jackson-xc//jar"],
)
