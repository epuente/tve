#Detiene servicio
#sudo kill -9 $(sudo fuser 8089/tcp)
sudo fuser -k 8089/tcp
#Elimina archivo generado
rm RUNNING_PID

#Genera JKS
sudo openssl pkcs12 -export -passout pass:Le4MtTwo3W -in /etc/letsencrypt/live/videoteca.dev.ipn.mx/fullchain.pem -inkey /etc/letsencrypt/live/videoteca.dev.ipn.mx/privkey.pem -CAfile /etc/letsencrypt/live/videoteca.dev.ipn.mx/chain.pem -out conf/fullchain_and_key.p12 -name playVTK
sudo chmod a+r conf/fullchain_and_key.p12
keytool -importkeystore -deststorepass Le4MtTwo3W -destkeypass Le4MtTwo3W -destkeystore conf/videoteca.jks -srckeystore conf/fullchain_and_key.p12 -srcstoretype PKCS12 -srcstorepass  Le4MtTwo3W -alias playVTK

#Ejecuta
nohup bin/tve2024 -Dhttp.port=disabled -Dhttps.port=8089 -Dhttps.keyStore=conf/videoteca.jks -Dhttps.keyStoreType=JKS -Dhttps.keyStorePassword=Le4MtTwo3W >> salida.log &

