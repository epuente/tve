rm RUNNING_PID
fuser -k 8089/tcp
nohup bin/tve2024 -Dhttp.port=disabled -Dhttps.port=8089 -Dhttps.keyStore=conf/videoteca.jks -Dhttps.keyStoreType=JKS -Dhttps.keyStorePassword=Le4MtTwo3W >> salida.log &



