if [ -d "salida.log" ]
then
  rm salida.log
fi
rm -r target
sbt clean dist
dir="dist9"
rcp target/universal/tve2023-0.0.1-SNAPSHOT.zip eduardo@148.204.111.26:/home/eduardo/tve/$dir
ssh -t eduardo@148.204.111.26 '
cd tve/dist9;
pwd;
chmod a+x tve2023-0.0.1-SNAPSHOT.zip;
unzip tve2023-0.0.1-SNAPSHOT.zip;
cd tve2023-0.0.1-SNAPSHOT/;
pwd;
sudo openssl pkcs12 -export -passout pass:Le4MtTwo3W -in /etc/letsencrypt/live/videoteca.dev.ipn.mx/fullchain.pem -inkey /etc/letsencrypt/live/videoteca.dev.ipn.mx/privkey.pem -CAfile /etc/letsencrypt/live/videoteca.dev.ipn.mx/chain.pem -out conf/fullchain_and_key.p12 -name playVTK;
sudo chmod a+r conf/fullchain_and_key.p12;
keytool -importkeystore -deststorepass Le4MtTwo3W -destkeypass Le4MtTwo3W -destkeystore conf/videoteca.jks -srckeystore conf/fullchain_and_key.p12 -srcstoretype PKCS12 -srcstorepass  Le4MtTwo3W -alias playVTK;'
rcp iniciar.sh eduardo@148.204.111.26:/home/eduardo/tve/dist9/tve2023-0.0.1-SNAPSHOT
rcp actualizarCertificado.sh eduardo@148.204.111.26:/home/eduardo/tve/dist9/tve2023-0.0.1-SNAPSHOT
ssh -t eduardo@148.204.111.26 'cd tve/dist9/; sh iniciar.sh;'



