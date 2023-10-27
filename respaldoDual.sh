#!/bin/sh
if [ -z "$1" ]; then
    echo "No se ha suministrado el argumento \"comentario del commit\"   (ejemplo: respaldoDual.sh \"Actualización del controlador UsuarioController.java\" )    ";
else
    echo "comentario = $1";
    #Borrar compilado
    sbt clean
    if [ -d "bin" ]; then rm -Rf bin; fi

    #rm -r bin/
    este=$(pwd | sed 's:.*/::')
    fecha=`date +%Y%m%d_%H%M`

    #Comprimir en 7z y guardarlo en carpeta local ( ~/playFramework/respaldos)
    7z a -mx=9 -mmt12 -xr!target -xr!.git ~/playFramework/respaldos/$fecha-resp-$este.7z ${PWD}
    echo "Se ha respaldado en la ruta ~/playFramework/respaldos con el nombre $fecha-resp-$este.7z"
    echo ""
    #El segmento siguiente ya no se usa porque webDav de box.com ya no esta soportado en las cuentas gratuitas
    #guardar el comprimido en carpeta para respaldar en la nube (~/respaldosRemotos)
    #cp -r ~/playFramework/respaldos/$fecha-resp-$este.7z ~/respaldosRemotos/
    #echo "Se ha respaldado en la ruta ~/respaldosRemotos con el nombre $fecha-resp-$este.7z"
    #Esta programado un respaldo diario a todo lo que esté en la carpeta respaldosRemotos, con Deja Dump se respalda via webDav en box.com

    #Dado lo anterior, se optó por usar Git con GitHub

    # SINO ES POSIBLE HACER EL LOGIN REMOTO EN GITHUB,EJECUTAR gh auth login
    if [ -d !".git" ]; then
        git init
    fi
    git add *
    #git branch -M $1
    git commit -m "$1"
    if  [[ -z $(git remote) ]]; then
      git remote add origin https://github.com/epuente/tve.git
    fi
    git push
    git push https://github.com/epuente/tve.git
    echo "Se ha respaldado el proyecto en el repositorio https://github.com/epuente/tve.git"
fi
