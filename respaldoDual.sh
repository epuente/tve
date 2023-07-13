#!/bin/sh

#Borrar compilado
sbt clean
if [ -d "bin" ]; then rm -Rf bin; fi

#rm -r bin/
este=$(pwd | sed 's:.*/::')
fecha=`date +%Y%m%d_%H%M`

#Comprimir en 7z y guardarlo en carpeta local ( ~/playFramework/respaldos)
7z a -mx=9 -mmt12 -xr!target ~/playFramework/respaldos/$fecha-resp-$este.7z ${PWD}
echo "Se ha respaldado en la ruta ~/playFramework/respaldos con el nombre $fecha-resp-$este.7z"

#guardar el comprimido en carpeta para respaldar en la nube (~/respaldosRemotos)
cp -r ~/playFramework/respaldos/$fecha-resp-$este.7z ~/respaldosRemotos/
echo "Se ha respaldado en la ruta ~/respaldosRemotos con el nombre $fecha-resp-$este.7z"

#Esta programado un respaldo diario a todo lo que esté en la carpeta respaldosRemotos, con Deja Dump se respalda via webDav en box.com
