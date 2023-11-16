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

INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(1, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'folio', 'Folio', 'Escriba aquí folio del oficio de solicitud');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(2, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'clave', 'ID', 'Identificador Videoteca');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(3, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'URDescripcion', 'Instancia solicitante', 'Escriba el nombre completo de la Instancia solicitante y se realizará automáticamente la búsqueda.<br><br>Si la Instancia que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la Instancia es nueva, oprima el botón <strong>Nueva UR</strong>');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(4, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'eventos_0_id', 'Evento', 'Indique cual es el tipo de evento<br>Puede seleccionar más de una opción');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(5, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'nivel1', 'Nivel', 'Indique cual es el Nivel<br>Puede seleccionar más de una opción');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(6, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'folioDEV', 'Folio DEV', 'E$scriba aqui el número de folio que asigna la DEV cuando se recibe el oficio de solicitud');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(7, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'serieDescripcion', 'Serie', 'Escriba la descripción de la serie y se realizará automáticamente la búsqueda.<br><br>Si la serie que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la serie es nueva, oprima el botón <strong>Nueva serie</strong>');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(8, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'creditos', 'Creditos', 'PENDIENTE');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(9, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'titulo', 'Título', 'Escriba el nombre o título del material grabado');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(10, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'obra', 'Número de obra', 'Escriba el número de la obra de acuerdo al siguiente formato:<br><br>xx/xx<br><br>Donde cada x es dígito');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(11, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'duracionStr', 'Duración', 'Escriba la duración del material grabado de acuerdo al siguiente formato:<br><br>hhhh:mm:ss<br><br>Donde h es para hora, m es para los minutos y s para los segundos<br><br>Ejemplo: para representar 7 horas 42 muntos y 3 segundos, se debe escribir 007:42:03');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(12, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'palabrasClaveStr', 'Palabras clave', 'Escriba las palabas clave que serviran para identificar los tópicos de los que trata el material.<br>Pueden escribirse palabras y frases cortas.<br><br>Mientrs escriba oprima enter para hacer la separación entre palabras clave.<br><br>Por ejemplo: para agregar las palabras clave ''autoestima'' y ''depresión juvenil'', escriba <i>autoestima</i> oprima enter y despues <i>depresión juvenil</i> oprima enter.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(13, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'produccionDescripcion', 'Producción', 'Escriba la producción y se realizará automáticamente la búsqueda.<br><br>Si la producción que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la producción es nueva, oprima el botón <strong>Nueva producción</strong>');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(14, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'idioma_id', 'Idioma', 'Seleccione de la lista el idioma correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(15, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'tipograbacion_id', 'Grabación', 'Seleccione de la lista el tipo de grabación correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(16, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'fechaProduccion', 'Fecha de producción', 'Escriba la fecha de producción en uno de los siguientes formatos:<br><br>dd/mm/yyyy<br>mm/yyyy<br>yyyy');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(17, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'fechaPublicacion', 'Fecha de publicación', 'Escriba la fecha de publicación en uno de los siguientes formatos:<br><br>dd/mm/yyyy<br>mm/yyyy<br>yyyy');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(18, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'disponibilidad_id', 'Disponibilidad', 'Seleccione de la lista la disponibilidad correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(19, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'areatematicaDescripcion', 'Área temática', 'Escriba la descripción del área temática y se realizará automáticamente la búsqueda.<br><br>Si el área temática que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si el área temática es nueva, oprima el botón <strong>Nueva área</strong>');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(20, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'formato_id', 'Formato', 'Seleccione de la lista el tipo de formato correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(21, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'sinopsis', 'Sinópsis', 'Escriba la sinópsis del material grabado');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(30, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'audio', 'Audio', 'Escriba las especificaciones del audio(bits, estado, calidad, etc)');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(31, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'video_id', 'Video', 'Seleccione de la lista el tipo de video correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(32, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'observaciones', 'Observaciones', 'Si tiene observaciones que resaltar referentes al material grabado, escríbalas aqui.');

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
drop table if exists vtk_campo cascade;

drop sequence if exists areatematica_seq;
drop sequence if exists disponibilidad_seq;
drop sequence if exists serie_seq;
drop sequence if exists nivel_seq;
drop sequence if exists produccion_seq;
drop sequence if exists tipo_credito_seq;
drop sequence if exists tipo_grabacion_seq;
drop sequence if exists tipo_video_seq;
drop sequence if exists vtk_campo_seq;
