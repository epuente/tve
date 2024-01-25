#sudo kill -9 $(sudo fuser 8089/tcp)
sudo fuser -k 8089/tcp
nohup bin/tve2024 -Dhttp.port=disabled -Dhttp.port=8089 -Dhttps.keyStore=conf/videoteca.jks -Dhttps.keyStoreType=JKS -Dhttps.keyStorePassword=Le4MtTwo3W >> salida.log &