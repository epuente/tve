if [ -d "salida.log" ]
then
  rm salida.log
fi
rm -r target
sbt clean dist
dir="dist"
rcp target/universal/tve2024-0.0.6-SNAPSHOT.zip eduardo@148.204.111.26:/home/eduardo/tve/$dir
ssh eduardo@148.204.111.26 '
cd tve/dist;
pwd;
chmod a+x tve2024-0.0.6-SNAPSHOT.zip;
unzip tve2024-0.0.6-SNAPSHOT.zip;
chmod a+x tve2024-0.0.6-SNAPSHOT/bin/tve2024';
#rcp iniciar.sh eduardo@148.204.111.26:/home/eduardo/tve/$dir/tve2024-0.0.6-SNAPSHOT
#rcp actualizarCertificado.sh eduardo@148.204.111.26:/home/eduardo/tve/$dir/tve2024-0.0.3-SNAPSHOT
#ssh -t eduardo@148.204.111.26 'cd tve/dist3/tve2023-0.0.1-SNAPSHOT/; sh actualizarCertificado.sh;'

