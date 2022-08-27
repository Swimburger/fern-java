configPath=$1

cd /client-generator/client-generator-"$VERSION"
java -cp cli-"$VERSION".jar:lib/* \
  com.fern.java.client.ClientGeneratorCli "$configPath"
