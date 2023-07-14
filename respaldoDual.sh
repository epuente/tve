#!/bin/sh
if [ -z "$1" ]; then
    echo "No se ha suminstrado el primer argumento 'branch' (para la rama principal se usa 'main') ";
else
      echo "branch = $1";
      if [ -z "$2" ]; then
          echo "No se ha suminstrado el segundo argumento 'comentario del push' ";
      else
          echo "comentario = $2";
          #Borrar compilado
          sbt clean
          if [ -d "bin" ]; then rm -Rf bin; fi

          #rm -r bin/
          este=$(pwd | sed 's:.*/::')
          fecha=`date +%Y%m%d_%H%M`

          #Comprimir en 7z y guardarlo en carpeta local ( ~/playFramework/respaldos)
          7z a -mx=9 -mmt12 -xr!target -xr!.git ~/playFramework/respaldos/$fecha-resp-$este.7z ${PWD}
          echo "Se ha respaldado en la ruta ~/playFramework/respaldos con el nombre $fecha-resp-$este.7z"

          #El segmento siguiente ya no se usa porque webDav de box.com ya no esta soportado en las cuentas gratuitas
          #guardar el comprimido en carpeta para respaldar en la nube (~/respaldosRemotos)
          #cp -r ~/playFramework/respaldos/$fecha-resp-$este.7z ~/respaldosRemotos/
          #echo "Se ha respaldado en la ruta ~/respaldosRemotos con el nombre $fecha-resp-$este.7z"
          #Esta programado un respaldo diario a todo lo que esté en la carpeta respaldosRemotos, con Deja Dump se respalda via webDav en box.com

          #Dado lo anterior, se optó por usar Git con GitHub



          git init
          git add *
          git branch -M $1
          git commit -m $2
          git remote add origin https://github.com/epuente/tve.git
          git push https://github.com/epuente/tve.git
          echo "Se ha respaldado el proyecto en el repositorio https://github.com/epuente/tve.git"
      fi
fi

