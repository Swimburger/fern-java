unzip fern-client-cli.zip
rm -rf fern-client-cli.zip
cd fern-client-cli
java -cp fern-client-cli.jar:lib/* \
  com.fern.java.client.cli.ClientGeneratorCli ../../api/generated/ir.json ../medplum-java-client com.medplum