# --- Sample dataset

# --- !Ups

INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(1, now(), now(), 'MOV');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(2, now(), now(), 'MP4');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(3, now(), now(), 'AVI');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(4, now(), now(), 'MKV');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(5, now(), now(), '3GP');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(6, now(), now(), 'WMP');
INSERT INTO vtk_formato (id, audit_insert, audit_update, descripcion) VALUES(7, now(), now(), 'Sin registro');

INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(1, now(), now(), 'Difusión');
INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(2, now(), now(), 'Consulta');
INSERT INTO disponibilidad (id, audit_insert, audit_update, descripcion) VALUES(3, now(), now(), 'OnDemand');

INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(1, now(), now(), 'Físico-Matemáticas');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(2, now(), now(), 'Ciencias Médico-Biológicas');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(3, now(), now(), 'Ciencias Sociales y Administrativas');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(4, now(), now(), 'Humanidades');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(5, now(), now(), 'Comunicación-Lenguas');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(6, now(), now(), 'Tecnología y Cómputo');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(7, now(), now(), 'Cultura-Arte');
INSERT INTO areatematica (id, audit_insert, audit_update, descripcion) VALUES(999, now(), now(), 'Otra');


INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(1, now(), now(), 'Productores', 'producción');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(3, now(), now(), 'Ingeniería', 'aspecto técnico');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(2, now(), now(), 'Camarógrafos', 'grabación de video / levantamiento de imágen');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(6, now(), now(), 'Postproductores', 'postproducción de audio y/o video');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(7, now(), now(), 'Conductores / locutores', 'conducción y/o locución');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(5, now(), now(), 'Transmisión', 'transmisión');
INSERT INTO tipo_credito (id, audit_insert, audit_update, descripcion, accion) VALUES(4, now(), now(), 'Guionistas', 'guión');

INSERT INTO produccion (id, audit_insert, audit_update, descripcion) VALUES(1, '2024-01-09 00:00:00.000', '2024-01-09 00:00:00.000', 'Departamento de Televisión Educativa');
INSERT INTO produccion (id, audit_insert, audit_update, descripcion) VALUES(2, '2024-01-09 00:00:00.000', '2024-01-09 00:00:00.000', 'Externo');

INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Medio Superior');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Superior');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Posgrado');
INSERT INTO nivel (id, audit_insert, audit_update, descripcion) VALUES(4, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Área Central');

INSERT INTO tipo_grabacion (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Material propio');
INSERT INTO tipo_grabacion (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-10-23 00:00:00.000', '2023-10-23 00:00:00.000', 'Adquirido');

INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(1, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC -64kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(2, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC - 32 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(3, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC - 48 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(4, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC- 88.2 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(5, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC -96 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(6, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC - 176 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(7, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Stereo ACC - 192 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(8, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC -64kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(10, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC - 32 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(11, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC - 48 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(12, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC- 88.2 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(13, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC -96 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(14, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC - 176 kbps – 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(15, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Mono ACC - 192 kbps – 44,1 32/44,1/48  kHz   -16 Bits');
INSERT INTO tipo_audio (id, audit_insert, audit_update, descripcion) VALUES(16, '2023-11-23 00:00:00.000', '2023-11-23 00:00:00.000', 'Sin registro');

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
INSERT INTO tipo_video (id, audit_insert, audit_update, descripcion) VALUES(16, '2023-11-15 00:00:00.000', '2023-11-15 00:00:00.000', 'Sin registro');

INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(1, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'folio', 'Folio DP', 'Escriba aquí folio del oficio de solicitud');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(2, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'clave', 'ID', 'Identificador Videoteca');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(3, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'URDescripcion', 'Instancia solicitante', 'Escriba el nombre completo de la Instancia solicitante y se realizará automáticamente la búsqueda.<br><br>Si la Instancia que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la Instancia es nueva, oprima el botón <strong>Nueva Instancia</strong> que esta al final de la lista.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(4, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'eventos_0_id', 'Evento', 'Indique cual es el tipo de evento<br>Puede seleccionar más de una opción');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(5, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'nivel1', 'Nivel', 'Indique cual es el Nivel<br>Puede seleccionar más de una opción');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(6, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'folioDEV', 'Folio DEV', 'Escriba aqui el número de folio que asigna la DEV cuando se recibe el oficio de solicitud');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(7, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'serieDescripcion', 'Serie', 'Escriba la descripción de la serie y se realizará automáticamente la búsqueda.<br><br>Si la serie que desea agregar aparece en la lista de coincidencias, elijala de la lista.<br><br>Si la serie es nueva, oprima el botón <strong>Nueva serie</strong> que esta al final de la lista.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(8, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'creditos', 'Creditos', 'Escriba los nombres de las personas que intevinieron en la producción. Distribuyalos en las pestañas según corresponda.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(9, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'titulo', 'Título', 'Escriba el nombre o título del material grabado');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(10, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'obra', 'Número de obra', 'Escriba el número de la obra de acuerdo al siguiente formato:<br><br>xx/xx<br><br>Donde cada x es dígito. Por ejemplo si se trata del segundo volumen de un total de 12, escriba 02/12');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(11, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'duracionStr', 'Duración', 'Escriba la duración del material grabado de acuerdo al siguiente formato:<br><br>hh:mm:ss<br><br>Donde h es para hora, m es para los minutos y s para los segundos<br><br>Ejemplo: para representar 7 horas 42 minutos y 3 segundos, se debe escribir 07:42:03');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(12, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'palabrasClaveStr', 'Palabras clave', 'Escriba las palabas clave que serviran para identificar los tópicos de los que trata el material.<br>Pueden escribirse palabras y frases cortas.<br><br>Mientras escriba oprima enter para hacer la separación entre palabras clave.<br><br>Por ejemplo: para agregar las palabras clave ''autoestima'' y ''depresión juvenil'', escriba <i>autoestima</i> oprima enter y despues <i>depresión juvenil</i> oprima enter.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(13, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'produccion_id', 'Producción', 'Seleccione de la lista la producción correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(14, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'idioma_id', 'Idioma', 'Seleccione de la lista el idioma correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(140, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'accesibilidad_audio', 'Accesibilidad', 'Si el material incorpora apoyo de accesibilidad, indique cual.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(15, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'tipograbacion_id', 'Grabación', 'Seleccione de la lista el tipo de grabación correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(16, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'fechaProduccion', 'Fecha de producción', 'Navege en el calendario y seleccione la fecha de producción. Puede usar las flechas izquierda y derecha para retroceder o avanzar entre años y meses. <b>No se admiten fechas anteriores al año 1995.</b>');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(17, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'fechaPublicacion', 'Fecha de publicación', 'Escriba la fecha de publicación en uno de los siguientes formatos:<br><br>dd/mm/yyyy<br>mm/yyyy<br>yyyy<br><br>Donde dd es para el día con dos dígitos, mm es para el mes con 2 dígitos, yyyy es para el año con 4 dígitos.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(18, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'disponibilidad_id', 'Disponibilidad', 'Seleccione de la lista la disponibilidad correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(19, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'areatematica_id', 'Área temática', 'Seleccione el área temática. Puede seleccionar más de una opción.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(20, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'formato_id', 'Formato', 'Seleccione de la lista el tipo de formato correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(21, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'sinopsis', 'Sinópsis', 'Escriba la sinópsis del material grabado');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(22, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxIntervencion', 'Intervención de personajes', 'Detalle de las intervenciones de los personajes que aparecen en cada una de las partes del material grabado.<br><br>Se pueden agregar varios renglones según se requiera.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(23, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxTiempo', 'Tiempo de intervención', 'El tiempo se divide en <i>inicio</i> y </>término</i>.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(24, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxInicio', 'Inicio de la intervención del personaje', 'Inicio de la intervención del personaje sobre un tema.<br>Registre el inicio de acuerdo al siguiente formato:<br><br>hh:mm:ss<br><br>Donde h es para hora, m es para los minutos y s para los segundos<br><br>Ejemplo: para representar 7 horas 42 muntos y 3 segundos, se debe escribir 07:42:03 ');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(25, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxTermino', 'Término de la intervención del personaje', 'Término de la intervención del personaje sobre un tema.<br>Registre el inicio de acuerdo al siguiente formato:<br><br>hh:mm:ss<br><br>Donde h es para hora, m es para los minutos y s para los segundos<br><br>Ejemplo: para representar 7 horas 42 muntos y 3 segundos, se debe escribir 07:42:03 ');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(26, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxNombre', 'Nombre', 'Nombre completo del personaje que aparece en el video.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(27, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxGrado', 'Grado', 'Grado académico del personaje que aparece en el video.<br><br>Por ejemplo: Lic');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(28, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxCargo', 'Cargo', 'Cargo del personaje que aparece en el video.<br><br>Por ejemplo: Director de la ESIME Azcapotzalco');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(29, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'auxTema', 'Título o tema', 'Escriba el título o tema que desarrollan dentro de los tiempos <i>inicio</i> y <i>término</i>.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(30, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'audio_id', 'Audio', 'Seleccione de la lista las caracteristicas del audio');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(300, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'calidad_audio_B', 'Calidad audio', 'Seleccione la calidad del audio');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(31, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'video_id', 'Video', 'Seleccione de la lista el tipo de video correspondiente.');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(310, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'calidad_video_B', 'Calidad video', 'Seleccione la calidad de video');
INSERT INTO vtk_campo (id, audit_insert, audit_update, "label", nombre, indicaciones) VALUES(32, '2023-11-16 00:00:00.000', '2023-11-16 00:00:00.000', 'observaciones', 'Observaciones', 'Observaciones que resaltar referentes al material grabado, escríbalas aqui.');

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
drop table if exists tipo_audio cascade;
drop table if exists tipo_video cascade;
drop table if exists vtk_campo cascade;
drop table if exists vtk_formato cascade;

drop sequence if exists areatematica_seq;
drop sequence if exists disponibilidad_seq;
drop sequence if exists serie_seq;
drop sequence if exists nivel_seq;
drop sequence if exists produccion_seq;
drop sequence if exists tipo_credito_seq;
drop sequence if exists tipo_grabacion_seq;
drop sequence if exists tipo_audio_seq;
drop sequence if exists tipo_video_seq;
drop sequence if exists vtk_campo_seq;
