rm salida.log
rm -rf target
rm -rf tv2017-0.0.1-SNAPSHOT
sbt clean
sbt reload
sbt dist
unzip target/universal/tv2017-0.0.1-SNAPSHOT.zip
cd tv2017-0.0.1-SNAPSHOT
#Detiene servicio
sudo kill -9 $(sudo fuser 8087/tcp)
#Elimina archivo generado
rm -f RUNNING_PID
#pwd
bin/tv2017 -Dhttp.port=8087 > salida.log &



