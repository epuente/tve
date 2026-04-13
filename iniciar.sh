JAVA_HOME=/usr/lib/jvm/openlogic-openjdk-8-hotspot-amd64
export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH
export PATH




rm RUNNING_PID
fuser -k 8080/tcp
nohup bin/tve2024 -Dhttp.port=disabled -Dhttps.port=8080 -Dhttps.keyStore=conf/videoteca.jks -Dhttps.keyStoreType=JKS -Dhttps.keyStorePassword=Le4MtTwo3W >> salida.log &



