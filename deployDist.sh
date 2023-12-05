rm salida.log
rm -rf target
rm -rf tv2017-0.0.1-SNAPSHOT
sbt clean dist
unzip target/universal/tv2017-0.0.1-SNAPSHOT.zip
cd tv2017-0.0.1-SNAPSHOT
#Detiene servicio
sudo kill -9 $(sudo fuser 8089/tcp)
#Elimina archivo generado
rm -f RUNNING_PID
#pwd
bin/tv2017 -Dhttp.port=8089 > salida.log &



