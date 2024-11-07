#!/bin/bash
export PGPASSWORD='tve';
pg_dump -U tve -F c -b -h localhost -d tveservicios > /home/eduardo/tve/dumps/tveservicios_produccion_$(date +'%Y%m%d_%H%M').dmp
