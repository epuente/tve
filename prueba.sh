#!/bin/shsh p
if [ -z "$1" ]; then
    echo "No se ha suminstrado el primer argumento 'branch' ";
else
    echo "branch = $1";
    if [ -z "$2" ]; then
        echo "No se ha suminstrado el segundo argumento 'comentario del push' ";
    else
        echo "comentario = $2";
        git init
        git add *
        git branch -M $1
        git commit -m $2
        git remote add origin https://github.com/epuente/tve.git
        git push https://github.com/epuente/tve.git
    fi
fi
