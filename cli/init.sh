configPath=$1

cd gradle-resources && ./gradlew
cd ../cli
tar -xvf cli.tar
rm -rf cli.tar
cd ./cli-"$VERSION"
java -cp cli-"$VERSION".jar:lib/* \
  com.fern.java.client.cli.ClientGeneratorCli "$configPath"
