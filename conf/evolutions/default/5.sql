# --- Sample dataset

# --- !Ups

INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'MOV');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'MP4');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'AVI');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(4, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'MKV');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(5, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', '3GP');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(6, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'WMP');

INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'Difusión');
INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'Consulta');
INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'OnDemand');

INSERT INTO areatematica (id, audit_insert, audit_update, descripcion, sigla) VALUES(1, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'Ciencias Físico Matemáticas', 'CFM');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion, sigla) VALUES(2, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'Ciencias Sociales y Administrativas', 'CSyA');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion, sigla) VALUES(3, '2023-08-24 14:23:42.691', '2023-08-24 14:23:42.691', 'Ciencias Médico Biológicas', 'CMB');

INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(1, '2023-09-12 11:38:06.354', '2023-09-12 11:38:06.354', 'Productores', 'producción');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(3, '2023-09-12 11:38:06.375', '2023-09-12 11:38:06.375', 'Ingeniería', 'aspecto técnico');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(2, '2023-09-12 11:38:06.382', '2023-09-12 11:38:06.382', 'Camarógrafos', 'grabación de video / levantamiento de imágen');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(6, '2023-09-12 11:38:06.388', '2023-09-12 11:38:06.388', 'Postproductores', 'postproducción de audio y/o video');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(5, '2023-09-12 11:38:06.390', '2023-09-12 11:38:06.390', 'Transmisión', 'transmisión');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(4, '2023-09-12 11:38:06.393', '2023-09-12 11:38:06.393', 'Guionistas', 'guión');

INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(1, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Coordinación de Televisión Educativa', 'Cord. TVE');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(2, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Coordinación de Tecnología Educativa', 'Cord. TecEduc');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(3, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Coproducción División de Televisión Educativa - ANUIES', 'CoProd DivTVE');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(4, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Departamento de Televisión Educativa - ANUIES', 'Depto TVE-ANUIES');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(5, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Televisión Educativa SEP', 'TVE SEP');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion, sigla) VALUES(8, '2023-09-12 00:00:00.000', '2023-09-12 00:00:00.000', 'Departamento de Televisión Educativa', 'DeptoTVE');

INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Medio Superior');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Superior');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Posgrado');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(4, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Área Central');

INSERT INTO tipo_grabacion (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Master');
INSERT INTO tipo_grabacion (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Copia');

INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 1920x1080 (FHD) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 1280x720 (HD) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 854x480 (SD) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(4, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 640x360 (SD) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(5, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 426x240 (SD) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(6, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 2560x1440 (2K) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(7, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 3840x2160 (4K) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(8, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 7680x4320 (8K) H.264 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(9, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 1920x1080 (FHD) H.265 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(10, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 2560x1440 (2K) H.265 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(11, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 3840x2160 (4K) H.265 16:9');
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(12, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'NTSC 7680x4320 (8K) H.265 16:9');

select setval( 'serie_seq', (select max(id)+1 from serie), true);
select setval( 'vtk_catalogo_seq', (select max(id)+1 from vtk_catalogo), true);
select setval( 'vtk_evento_seq', (select max(id)+1 from vtk_evento), true);
select setval( 'vtk_nivel_seq', (select max(id)+1 from vtk_nivel), true);
select setval( 'produccion_seq', (select max(id)+1 from produccion), true);
select setval( 'areatematica_seq', (select max(id)+1 from areatematica), true);

 # --- !Downs

drop table if exists areatematica cascade;
drop table if exists disponibilidad cascade;
drop table if exists serie cascade;
drop table if exists sistema cascade;
drop table if exists nivel cascade;
drop table if exists produccion cascade;
drop table if exists tipo_credito cascade;
drop table if exists tipo_grabacion cascade;
drop table if exists tipo_grabacion cascade;

drop sequence if exists areatematica_seq;
drop sequence if exists disponibilidad_seq;
drop sequence if exists serie_seq;
drop sequence if exists nivel_seq;
drop sequence if exists produccion_seq;
drop sequence if exists tipo_credito_seq;
drop sequence if exists tipo_grabacion_seq;
drop sequence if exists tipo_video_seq;
