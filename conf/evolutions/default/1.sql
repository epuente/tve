# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table accesorio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  tipo_id                   bigint,
  descripcion               varchar(100) not null,
  estado_id                 bigint,
  serie                     varchar(255),
  modelo                    varchar(255),
  observacion               varchar(600),
  constraint pk_accesorio primary key (id))
;

create table agenda (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  folioproductorasignado_id bigint,
  fase_id                   bigint not null,
  inicio                    timestamp not null,
  fin                       timestamp not null,
  estado_id                 bigint not null,
  observacion               varchar(600),
  version                   timestamp not null,
  constraint pk_agenda primary key (id))
;

create table agenda_accesorio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  accesorio_id              bigint not null,
  autorizo_id               bigint,
  constraint pk_agenda_accesorio primary key (id))
;

create table agenda_cancelacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  estado_anterior_id        bigint,
  motivo_id                 bigint,
  observacion               varchar(600),
  constraint pk_agenda_cancelacion primary key (id))
;

create table agenda_cuenta_rol (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint not null,
  cuentarol_id              bigint,
  constraint pk_agenda_cuenta_rol primary key (id))
;

create table agenda_equipo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  equipo_id                 bigint not null,
  autorizo_id               bigint,
  constraint pk_agenda_equipo primary key (id))
;

create table agenda_formato_entrega (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  formato_id                bigint not null,
  constraint pk_agenda_formato_entrega primary key (id))
;

create table agenda_ing_admon_eqpo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint not null,
  ingeniero_id              bigint not null,
  constraint pk_agenda_ing_admon_eqpo primary key (id))
;

create table agenda_ingesta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  estado_id                 bigint,
  calificado_previo         boolean,
  cambio_formato            boolean,
  permanencia_productor     boolean,
  concluido                 boolean,
  autorizo_id               bigint not null,
  constraint pk_agenda_ingesta primary key (id))
;

create table agenda_ingesta_almacenamiento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  medioalmacenamiento_id    bigint,
  constraint pk_agenda_ingesta_almacenamiento primary key (id))
;

create table agenda_ingesta_det_audio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  detalle                   varchar(300),
  constraint pk_agenda_ingesta_det_audio primary key (id))
;

create table agenda_ingesta_det_video (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  detalle                   varchar(300),
  constraint pk_agenda_ingesta_det_video primary key (id))
;

create table agenda_ingesta_fto (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  formatoingesta_id         bigint,
  cantidadtarjetas          integer,
  constraint pk_agenda_ingesta_fto primary key (id))
;

create table agenda_ingesta_fto_o (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint not null,
  descripcion               varchar(255) not null,
  cantidadtarjetas          integer,
  constraint pk_agenda_ingesta_fto_o primary key (id))
;

create table agenda_ingesta_fto_s (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  formatoingesta_id         bigint,
  constraint pk_agenda_ingesta_fto_s primary key (id))
;

create table agenda_ingesta_idioma (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint not null,
  idioma_id                 bigint not null,
  constraint pk_agenda_ingesta_idioma primary key (id))
;

create table agenda_ingesta_idioma_otro (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint not null,
  descripcion               varchar(100) not null,
  constraint pk_agenda_ingesta_idioma_otro primary key (id))
;

create table agenda_ingesta_observacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  observacion               varchar(500),
  constraint pk_agenda_ingesta_observacion primary key (id))
;

create table agenda_ingesta_problema (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendaingesta_id          bigint,
  descripcion               varchar(500),
  constraint pk_agenda_ingesta_problema primary key (id))
;

create table agenda_junta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  constraint pk_agenda_junta primary key (id))
;

create table agenda_locacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  locacion                  varchar(255),
  constraint pk_agenda_locacion primary key (id))
;

create table agenda_operador_sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  personal_id               bigint,
  constraint pk_agenda_operador_sala primary key (id))
;

create table agenda_previo_autoriza_accesorio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  preagendaaccesorio_id     bigint,
  constraint pk_agenda_previo_autoriza_acceso primary key (id))
;

create table agenda_previo_autoriza_equipo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  preagendaequipo_id        bigint,
  constraint pk_agenda_previo_autoriza_equipo primary key (id))
;

create table agenda_sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  sala_id                   bigint not null,
  constraint pk_agenda_sala primary key (id))
;

create table agenda_sala_uso_cabina (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agendasala_id             bigint,
  usocabina                 varchar(1),
  constraint pk_agenda_sala_uso_cabina primary key (id))
;

create table agenda_salida (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  salida                    timestamp not null,
  constraint pk_agenda_salida primary key (id))
;

create table agenda_vehiculo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  agenda_id                 bigint,
  vehiculo_id               bigint not null,
  version                   timestamp not null,
  constraint pk_agenda_vehiculo primary key (id))
;

create table ambito (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(255),
  constraint pk_ambito primary key (id))
;

create table areatematica (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  sigla                     varchar(255),
  catalogador_id            bigint,
  constraint pk_areatematica primary key (id))
;

create table catalogador (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  paterno                   varchar(25) not null,
  materno                   varchar(25) not null,
  nombre                    varchar(25) not null,
  password                  varchar(25) not null,
  constraint pk_catalogador primary key (id))
;

create table cola_correos (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  correo                    varchar(255) not null,
  estado                    smallint not null,
  numintentos               smallint,
  asunto                    varchar(255),
  folio                     varchar(255),
  oficio_descripcion        varchar(255),
  servicio_descripcion      varchar(255),
  contenido                 TEXT,
  constraint pk_cola_correos primary key (id))
;

create table credito (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogo_id               bigint,
  tipo_credito_id           bigint,
  personas                  varchar(75),
  catalogador_id            bigint not null,
  constraint pk_credito primary key (id))
;

create table ctacorreo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  hostname                  varchar(255) not null,
  puerto                    varchar(255) not null,
  cuenta                    varchar(255),
  contrasenia               varchar(255),
  activa                    boolean,
  constraint pk_ctacorreo primary key (id))
;

create table cuenta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  personal_id               bigint,
  username                  varchar(15) not null,
  password                  varchar(15) not null,
  constraint pk_cuenta primary key (id))
;

create table cuenta_rol (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  cuenta_id                 bigint,
  rol_id                    bigint,
  constraint pk_cuenta_rol primary key (id))
;

create table disponibilidad (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  usuario_id                bigint,
  constraint pk_disponibilidad primary key (id))
;

create table equipo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  estado_id                 bigint,
  serie                     varchar(50),
  marca                     varchar(100),
  modelo                    varchar(100),
  observacion               varchar(600),
  constraint pk_equipo primary key (id))
;

create table estado (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_estado primary key (id))
;

create table estado_equipo_accesorio_vehiculo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(50),
  constraint pk_estado_equipo_accesorio_vehic primary key (id))
;

create table estado_material (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_estado_material primary key (id))
;

create table fase (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(50),
  constraint pk_fase primary key (id))
;

create table folio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  numfolio                  bigint,
  oficio_id                 bigint,
  estado_id                 bigint,
  fechaentrega              timestamp,
  numeroresguardo           varchar(20),
  observacion               varchar(600),
  foliocancelacion_id       bigint,
  constraint uq_folio_numfolio unique (numfolio),
  constraint uq_folio_numeroresguardo unique (numeroresguardo),
  constraint pk_folio primary key (id))
;

create table folio_cancelacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  motivo_id                 bigint,
  estadoanterior_id         bigint,
  constraint pk_folio_cancelacion primary key (id))
;

create table folio_productor_asignado (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  folio_id                  bigint not null,
  personal_id               bigint not null,
  constraint pk_folio_productor_asignado primary key (id))
;

create table formato (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_formato primary key (id))
;

create table formato_ingesta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_formato_ingesta primary key (id))
;

create table his_folio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  folio_id                  bigint,
  estado_id                 bigint,
  usuario_id                bigint,
  rol_id                    bigint,
  constraint pk_his_folio primary key (id))
;

create table idioma (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_idioma primary key (id))
;

create table medio_almacenamiento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_medio_almacenamiento primary key (id))
;

create table motivo_cancelacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_motivo_cancelacion primary key (id))
;

create table nivel (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_nivel primary key (id))
;

create table nivel_academico (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_nivel_academico primary key (id))
;

create table oficio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio                    varchar(25),
  urremitente_id            bigint not null,
  descripcion               varchar(600) not null,
  titulo                    varchar(150),
  fecharemitente            timestamp,
  fecharecibidodtve         timestamp,
  fecharecibidoupev         timestamp,
  observacion               varchar(600),
  constraint pk_oficio primary key (id))
;

create table oficio_bitacora (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_bitacora primary key (id))
;

create table oficio_contacto (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_id                 bigint,
  nombre                    varchar(150),
  constraint pk_oficio_contacto primary key (id))
;

create table oficio_contacto_correo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_contactos_id       bigint,
  correo                    varchar(50),
  constraint pk_oficio_contacto_correo primary key (id))
;

create table oficio_contacto_telefono (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_contactos_id       bigint,
  telefono                  varchar(25),
  constraint pk_oficio_contacto_telefono primary key (id))
;

create table oficio_encuesta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_encuesta primary key (id))
;

create table oficio_entrada_salida (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_entrada_salida primary key (id))
;

create table oficio_evidencia_entrega (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_evidencia_entrega primary key (id))
;

create table oficio_fecha_grabacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_id                 bigint,
  inicio                    timestamp not null,
  fin                       timestamp,
  constraint pk_oficio_fecha_grabacion primary key (id))
;

create table oficio_guion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_guion primary key (id))
;

create table oficio_imagen (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_imagen primary key (id))
;

create table oficio_minuta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  constraint pk_oficio_minuta primary key (id))
;

create table oficio_productor_solicitado (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_id                 bigint,
  personal_id               bigint,
  constraint pk_oficio_productor_solicitado primary key (id))
;

create table oficio_respuesta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  oficio_id                 bigint,
  fecharespuesta            timestamp,
  foliorespuesta            timestamp,
  constraint pk_oficio_respuesta primary key (id))
;

create table oficio_servicio_solicitado (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  oficio_id                 bigint,
  servicio_id               bigint not null,
  constraint pk_oficio_servicio_solicitado primary key (id))
;

create table operador_sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  personal_id               bigint,
  sala_id                   bigint not null,
  turno                     varchar(1) not null,
  version                   timestamp not null,
  constraint pk_operador_sala primary key (id))
;

create table palabra_clave (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogador_id            bigint not null,
  descripcion               varchar(30) not null,
  catalogo_id               bigint,
  constraint pk_palabra_clave primary key (id))
;

create table personal (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  num_empleado              varchar(13) not null,
  paterno                   varchar(25),
  materno                   varchar(25),
  nombre                    varchar(25),
  tipocontrato_id           bigint,
  turno                     varchar(1),
  activo                    varchar(1) not null,
  constraint uq_personal_num_empleado unique (num_empleado),
  constraint pk_personal primary key (id))
;

create table personal_avatar (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  nombrearchivo             varchar(255),
  contenttype               varchar(255),
  contenido                 bytea,
  personal_id               bigint,
  constraint pk_personal_avatar primary key (id))
;

create table personal_cambio_horario (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  personal_id               bigint,
  fechadesde                timestamp not null,
  fechahasta                timestamp not null,
  horariodesde              timestamp not null,
  horariohasta              timestamp not null,
  excluyente                boolean not null,
  constraint pk_personal_cambio_horario primary key (id))
;

create table personal_correo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  personal_id               bigint not null,
  email                     varchar(255) not null,
  constraint pk_personal_correo primary key (id))
;

create table personal_horario (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  personal_id               bigint not null,
  diasemana                 integer not null,
  desde                     timestamp,
  hasta                     timestamp,
  constraint pk_personal_horario primary key (id))
;

create table pre_agenda (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  folioproductorasignado_id bigint,
  fase_id                   bigint not null,
  inicio                    timestamp not null,
  fin                       timestamp not null,
  estado_id                 bigint not null,
  observacion               varchar(600),
  constraint pk_pre_agenda primary key (id))
;

create table pre_agenda_accesorio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint not null,
  accesorio_id              bigint not null,
  estado_id                 bigint,
  constraint pk_pre_agenda_accesorio primary key (id))
;

create table pre_agenda_cancelacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  estado_anterior_id        bigint,
  motivo_id                 bigint,
  observacion               varchar(600),
  constraint pk_pre_agenda_cancelacion primary key (id))
;

create table pre_agenda_equipo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint not null,
  equipo_id                 bigint not null,
  estado_id                 bigint,
  constraint pk_pre_agenda_equipo primary key (id))
;

create table pre_agenda_formato_entrega (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  formato_id                bigint not null,
  cantidad                  integer not null,
  constraint pk_pre_agenda_formato_entrega primary key (id))
;

create table pre_agenda_ing_admon_eqpo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint not null,
  ingeniero_id              bigint not null,
  constraint pk_pre_agenda_ing_admon_eqpo primary key (id))
;

create table pre_agenda_ingesta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  estado_id                 bigint,
  calificado_previo         boolean,
  cambio_formato            boolean,
  datos_video               varchar(255),
  datos_audio               varchar(255),
  constraint pk_pre_agenda_ingesta primary key (id))
;

create table pre_agenda_ingesta_almacenamiento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagendaingesta_id       bigint,
  medioalmacenamiento_id    bigint,
  constraint pk_pre_agenda_ingesta_almacenami primary key (id))
;

create table pre_agenda_ingesta_fmto_salida (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagendaingesta_id       bigint,
  formatoingesta_id         bigint,
  constraint pk_pre_agenda_ingesta_fmto_salid primary key (id))
;

create table pre_agenda_junta (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  constraint pk_pre_agenda_junta primary key (id))
;

create table pre_agenda_locacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  locacion                  varchar(255),
  constraint pk_pre_agenda_locacion primary key (id))
;

create table pre_agenda_locutor (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  personal_id               bigint not null,
  constraint pk_pre_agenda_locutor primary key (id))
;

create table pre_agenda_operador_sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  personal_id               bigint,
  constraint pk_pre_agenda_operador_sala primary key (id))
;

create table pre_agenda_rol (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  rol_id                    bigint not null,
  cantidad                  integer not null,
  constraint pk_pre_agenda_rol primary key (id))
;

create table pre_agenda_sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  sala_id                   bigint not null,
  constraint pk_pre_agenda_sala primary key (id))
;

create table pre_agenda_sala_uso_cabina (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagendasala_id          bigint,
  usocabina                 varchar(1),
  constraint pk_pre_agenda_sala_uso_cabina primary key (id))
;

create table pre_agenda_salida (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  salida                    timestamp not null,
  constraint pk_pre_agenda_salida primary key (id))
;

create table pre_agenda_vehiculo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  preagenda_id              bigint,
  constraint pk_pre_agenda_vehiculo primary key (id))
;

create table produccion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  sigla                     varchar(255),
  catalogador_id            bigint,
  constraint pk_produccion primary key (id))
;

create table registro_acceso (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  usuario_id                bigint,
  rol_id                    bigint,
  ip                        varchar(255),
  ruta                      varchar(255),
  constraint pk_registro_acceso primary key (id))
;

create table rol (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_rol primary key (id))
;

create table rol_derecho (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  rol_id                    bigint not null,
  ambito_id                 bigint,
  lectura                   boolean,
  escritura                 boolean,
  ejecucion                 boolean,
  constraint pk_rol_derecho primary key (id))
;

create table rol_fase (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  fase_id                   bigint,
  rol_id                    bigint,
  constraint pk_rol_fase primary key (id))
;

create table sala (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_sala primary key (id))
;

create table sala_mantenimiento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  sala_id                   bigint not null,
  tipo_id                   bigint not null,
  desde                     timestamp not null,
  hasta                     timestamp not null,
  motivo                    varchar(800),
  version                   timestamp not null,
  constraint pk_sala_mantenimiento primary key (id))
;

create table serie (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(500) not null,
  catalogador_id            bigint,
  constraint pk_serie primary key (id))
;

create table servicio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_servicio primary key (id))
;

create table tipo_contrato (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_tipo_contrato primary key (id))
;

create table tipo_credito (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  accion                    varchar(255),
  constraint pk_tipo_credito primary key (id))
;

create table tipo_equipo_accesorio (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_tipo_equipo_accesorio primary key (id))
;

create table tipo_grabacion (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_tipo_grabacion primary key (id))
;

create table tipo_mantenimiento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_tipo_mantenimiento primary key (id))
;

create table tipo_video (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  constraint pk_tipo_video primary key (id))
;

create table unidad_responsable (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  clave                     varchar(6),
  nombre_largo              varchar(150) not null,
  nombre_corto              varchar(50),
  tipo                      varchar(2),
  nivel                     varchar(1),
  area_conocimiento         varchar(1),
  catalogador_id            bigint,
  constraint pk_unidad_responsable primary key (id))
;

create table vehiculo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  placa                     varchar(10) not null,
  descripcion               varchar(100),
  estado_id                 bigint,
  activo                    varchar(255),
  modelo                    varchar(4),
  constraint pk_vehiculo primary key (id))
;

create table video_personaje (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogo_id               bigint,
  nombre                    varchar(75),
  constraint pk_video_personaje primary key (id))
;

create table vtk_catalogo (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  folio                     varchar(30),
  unidadresponsable_id      bigint,
  folio_dev                 varchar(30),
  titulo                    varchar(200),
  sinopsis                  varchar(3000) not null,
  serie_id                  bigint,
  clave                     varchar(30) not null,
  obra                      varchar(5),
  formato_id                bigint not null,
  idioma_id                 bigint,
  tipograbacion_id          bigint,
  produccion_id             bigint,
  duracion                  bigint,
  fecha_produccion          varchar(10),
  fecha_publicacion         varchar(10),
  disponibilidad_id         bigint,
  areatematica_id           bigint not null,
  nresguardo                varchar(255),
  liga                      varchar(255),
  catalogador_id            bigint not null,
  audio                     varchar(150),
  video_id                  bigint not null,
  observaciones             varchar(3000) not null,
  version                   bigint not null,
  constraint pk_vtk_catalogo primary key (id))
;

create table vtk_evento (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogo_id               bigint,
  servicio_id               bigint,
  constraint pk_vtk_evento primary key (id))
;

create table vtk_formato (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  descripcion               varchar(100) not null,
  usuario_id                bigint,
  constraint pk_vtk_formato primary key (id))
;

create table vtk_nivel (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogo_id               bigint,
  nivel_id                  bigint,
  constraint pk_vtk_nivel primary key (id))
;

create table vtk_time_line (
  id                        bigint not null,
  audit_insert              timestamp,
  audit_update              timestamp,
  catalogo_id               bigint,
  personaje_id              bigint,
  gradoacademico            varchar(50),
  cargo                     varchar(50),
  desde                     bigint,
  hasta                     bigint,
  tema                      varchar(200),
  constraint pk_vtk_time_line primary key (id))
;

create sequence accesorio_seq;

create sequence agenda_seq;

create sequence agenda_accesorio_seq;

create sequence agenda_cancelacion_seq;

create sequence agenda_cuenta_rol_seq;

create sequence agenda_equipo_seq;

create sequence agenda_formato_entrega_seq;

create sequence agenda_ing_admon_eqpo_seq;

create sequence agenda_ingesta_seq;

create sequence agenda_ingesta_almacenamiento_seq;

create sequence agenda_ingesta_det_audio_seq;

create sequence agenda_ingesta_det_video_seq;

create sequence agenda_ingesta_fto_seq;

create sequence agenda_ingesta_fto_o_seq;

create sequence agenda_ingesta_fto_s_seq;

create sequence agenda_ingesta_idioma_seq;

create sequence agenda_ingesta_idioma_otro_seq;

create sequence agenda_ingesta_observacion_seq;

create sequence agenda_ingesta_problema_seq;

create sequence agenda_junta_seq;

create sequence agenda_locacion_seq;

create sequence agenda_operador_sala_seq;

create sequence agenda_previo_autoriza_accesorio_seq;

create sequence agenda_previo_autoriza_equipo_seq;

create sequence agenda_sala_seq;

create sequence agenda_sala_uso_cabina_seq;

create sequence agenda_salida_seq;

create sequence agenda_vehiculo_seq;

create sequence ambito_seq;

create sequence areatematica_seq;

create sequence catalogador_seq;

create sequence cola_correos_seq;

create sequence credito_seq;

create sequence ctacorreo_seq;

create sequence cuenta_seq;

create sequence cuenta_rol_seq;

create sequence disponibilidad_seq;

create sequence equipo_seq;

create sequence estado_seq;

create sequence estado_equipo_accesorio_vehiculo_seq;

create sequence estado_material_seq;

create sequence fase_seq;

create sequence folio_seq;

create sequence folio_cancelacion_seq;

create sequence folio_productor_asignado_seq;

create sequence formato_seq;

create sequence formato_ingesta_seq;

create sequence his_folio_seq;

create sequence idioma_seq;

create sequence medio_almacenamiento_seq;

create sequence motivo_cancelacion_seq;

create sequence nivel_seq;

create sequence nivel_academico_seq;

create sequence oficio_seq;

create sequence oficio_bitacora_seq;

create sequence oficio_contacto_seq;

create sequence oficio_contacto_correo_seq;

create sequence oficio_contacto_telefono_seq;

create sequence oficio_encuesta_seq;

create sequence oficio_entrada_salida_seq;

create sequence oficio_evidencia_entrega_seq;

create sequence oficio_fecha_grabacion_seq;

create sequence oficio_guion_seq;

create sequence oficio_imagen_seq;

create sequence oficio_minuta_seq;

create sequence oficio_productor_solicitado_seq;

create sequence oficio_respuesta_seq;

create sequence oficio_servicio_solicitado_seq;

create sequence operador_sala_seq;

create sequence palabra_clave_seq;

create sequence personal_seq;

create sequence personal_avatar_seq;

create sequence personal_cambio_horario_seq;

create sequence personal_correo_seq;

create sequence personal_horario_seq;

create sequence pre_agenda_seq;

create sequence pre_agenda_accesorio_seq;

create sequence pre_agenda_cancelacion_seq;

create sequence pre_agenda_equipo_seq;

create sequence pre_agenda_formato_entrega_seq;

create sequence pre_agenda_ing_admon_eqpo_seq;

create sequence pre_agenda_ingesta_seq;

create sequence pre_agenda_ingesta_almacenamiento_seq;

create sequence pre_agenda_ingesta_fmto_salida_seq;

create sequence pre_agenda_junta_seq;

create sequence pre_agenda_locacion_seq;

create sequence pre_agenda_locutor_seq;

create sequence pre_agenda_operador_sala_seq;

create sequence pre_agenda_rol_seq;

create sequence pre_agenda_sala_seq;

create sequence pre_agenda_sala_uso_cabina_seq;

create sequence pre_agenda_salida_seq;

create sequence pre_agenda_vehiculo_seq;

create sequence produccion_seq;

create sequence registro_acceso_seq;

create sequence rol_seq;

create sequence rol_derecho_seq;

create sequence rol_fase_seq;

create sequence sala_seq;

create sequence sala_mantenimiento_seq;

create sequence serie_seq;

create sequence servicio_seq;

create sequence tipo_contrato_seq;

create sequence tipo_credito_seq;

create sequence tipo_equipo_accesorio_seq;

create sequence tipo_grabacion_seq;

create sequence tipo_mantenimiento_seq;

create sequence tipo_video_seq;

create sequence unidad_responsable_seq;

create sequence vehiculo_seq;

create sequence video_personaje_seq;

create sequence vtk_catalogo_seq;

create sequence vtk_evento_seq;

create sequence vtk_formato_seq;

create sequence vtk_nivel_seq;

create sequence vtk_time_line_seq;

alter table accesorio add constraint fk_accesorio_tipo_1 foreign key (tipo_id) references tipo_equipo_accesorio (id);
create index ix_accesorio_tipo_1 on accesorio (tipo_id);
alter table accesorio add constraint fk_accesorio_estado_2 foreign key (estado_id) references estado_equipo_accesorio_vehiculo (id);
create index ix_accesorio_estado_2 on accesorio (estado_id);
alter table agenda add constraint fk_agenda_folioproductorasigna_3 foreign key (folioproductorasignado_id) references folio_productor_asignado (id);
create index ix_agenda_folioproductorasigna_3 on agenda (folioproductorasignado_id);
alter table agenda add constraint fk_agenda_fase_4 foreign key (fase_id) references fase (id);
create index ix_agenda_fase_4 on agenda (fase_id);
alter table agenda add constraint fk_agenda_estado_5 foreign key (estado_id) references estado (id);
create index ix_agenda_estado_5 on agenda (estado_id);
alter table agenda_accesorio add constraint fk_agenda_accesorio_agenda_6 foreign key (agenda_id) references agenda (id);
create index ix_agenda_accesorio_agenda_6 on agenda_accesorio (agenda_id);
alter table agenda_accesorio add constraint fk_agenda_accesorio_accesorio_7 foreign key (accesorio_id) references accesorio (id);
create index ix_agenda_accesorio_accesorio_7 on agenda_accesorio (accesorio_id);
alter table agenda_accesorio add constraint fk_agenda_accesorio_autorizo_8 foreign key (autorizo_id) references personal (id);
create index ix_agenda_accesorio_autorizo_8 on agenda_accesorio (autorizo_id);
alter table agenda_cancelacion add constraint fk_agenda_cancelacion_agenda_9 foreign key (agenda_id) references agenda (id);
create index ix_agenda_cancelacion_agenda_9 on agenda_cancelacion (agenda_id);
alter table agenda_cancelacion add constraint fk_agenda_cancelacion_estadoA_10 foreign key (estado_anterior_id) references estado (id);
create index ix_agenda_cancelacion_estadoA_10 on agenda_cancelacion (estado_anterior_id);
alter table agenda_cancelacion add constraint fk_agenda_cancelacion_motivo_11 foreign key (motivo_id) references motivo_cancelacion (id);
create index ix_agenda_cancelacion_motivo_11 on agenda_cancelacion (motivo_id);
alter table agenda_cuenta_rol add constraint fk_agenda_cuenta_rol_agenda_12 foreign key (agenda_id) references agenda (id);
create index ix_agenda_cuenta_rol_agenda_12 on agenda_cuenta_rol (agenda_id);
alter table agenda_cuenta_rol add constraint fk_agenda_cuenta_rol_cuentaro_13 foreign key (cuentarol_id) references cuenta_rol (id);
create index ix_agenda_cuenta_rol_cuentaro_13 on agenda_cuenta_rol (cuentarol_id);
alter table agenda_equipo add constraint fk_agenda_equipo_agenda_14 foreign key (agenda_id) references agenda (id);
create index ix_agenda_equipo_agenda_14 on agenda_equipo (agenda_id);
alter table agenda_equipo add constraint fk_agenda_equipo_equipo_15 foreign key (equipo_id) references equipo (id);
create index ix_agenda_equipo_equipo_15 on agenda_equipo (equipo_id);
alter table agenda_equipo add constraint fk_agenda_equipo_autorizo_16 foreign key (autorizo_id) references personal (id);
create index ix_agenda_equipo_autorizo_16 on agenda_equipo (autorizo_id);
alter table agenda_formato_entrega add constraint fk_agenda_formato_entrega_age_17 foreign key (agenda_id) references agenda (id);
create index ix_agenda_formato_entrega_age_17 on agenda_formato_entrega (agenda_id);
alter table agenda_formato_entrega add constraint fk_agenda_formato_entrega_for_18 foreign key (formato_id) references formato (id);
create index ix_agenda_formato_entrega_for_18 on agenda_formato_entrega (formato_id);
alter table agenda_ing_admon_eqpo add constraint fk_agenda_ing_admon_eqpo_agen_19 foreign key (agenda_id) references agenda (id);
create index ix_agenda_ing_admon_eqpo_agen_19 on agenda_ing_admon_eqpo (agenda_id);
alter table agenda_ing_admon_eqpo add constraint fk_agenda_ing_admon_eqpo_inge_20 foreign key (ingeniero_id) references personal (id);
create index ix_agenda_ing_admon_eqpo_inge_20 on agenda_ing_admon_eqpo (ingeniero_id);
alter table agenda_ingesta add constraint fk_agenda_ingesta_agenda_21 foreign key (agenda_id) references agenda (id);
create index ix_agenda_ingesta_agenda_21 on agenda_ingesta (agenda_id);
alter table agenda_ingesta add constraint fk_agenda_ingesta_estado_22 foreign key (estado_id) references estado_material (id);
create index ix_agenda_ingesta_estado_22 on agenda_ingesta (estado_id);
alter table agenda_ingesta add constraint fk_agenda_ingesta_autorizo_23 foreign key (autorizo_id) references personal (id);
create index ix_agenda_ingesta_autorizo_23 on agenda_ingesta (autorizo_id);
alter table agenda_ingesta_almacenamiento add constraint fk_agenda_ingesta_almacenamie_24 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_almacenamie_24 on agenda_ingesta_almacenamiento (agendaingesta_id);
alter table agenda_ingesta_almacenamiento add constraint fk_agenda_ingesta_almacenamie_25 foreign key (medioalmacenamiento_id) references medio_almacenamiento (id);
create index ix_agenda_ingesta_almacenamie_25 on agenda_ingesta_almacenamiento (medioalmacenamiento_id);
alter table agenda_ingesta_det_audio add constraint fk_agenda_ingesta_det_audio_a_26 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_det_audio_a_26 on agenda_ingesta_det_audio (agendaingesta_id);
alter table agenda_ingesta_det_video add constraint fk_agenda_ingesta_det_video_a_27 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_det_video_a_27 on agenda_ingesta_det_video (agendaingesta_id);
alter table agenda_ingesta_fto add constraint fk_agenda_ingesta_fto_agendai_28 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_fto_agendai_28 on agenda_ingesta_fto (agendaingesta_id);
alter table agenda_ingesta_fto add constraint fk_agenda_ingesta_fto_formato_29 foreign key (formatoingesta_id) references formato_ingesta (id);
create index ix_agenda_ingesta_fto_formato_29 on agenda_ingesta_fto (formatoingesta_id);
alter table agenda_ingesta_fto_o add constraint fk_agenda_ingesta_fto_o_agend_30 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_fto_o_agend_30 on agenda_ingesta_fto_o (agendaingesta_id);
alter table agenda_ingesta_fto_s add constraint fk_agenda_ingesta_fto_s_agend_31 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_fto_s_agend_31 on agenda_ingesta_fto_s (agendaingesta_id);
alter table agenda_ingesta_fto_s add constraint fk_agenda_ingesta_fto_s_forma_32 foreign key (formatoingesta_id) references formato_ingesta (id);
create index ix_agenda_ingesta_fto_s_forma_32 on agenda_ingesta_fto_s (formatoingesta_id);
alter table agenda_ingesta_idioma add constraint fk_agenda_ingesta_idioma_agen_33 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_idioma_agen_33 on agenda_ingesta_idioma (agendaingesta_id);
alter table agenda_ingesta_idioma add constraint fk_agenda_ingesta_idioma_idio_34 foreign key (idioma_id) references idioma (id);
create index ix_agenda_ingesta_idioma_idio_34 on agenda_ingesta_idioma (idioma_id);
alter table agenda_ingesta_idioma_otro add constraint fk_agenda_ingesta_idioma_otro_35 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_idioma_otro_35 on agenda_ingesta_idioma_otro (agendaingesta_id);
alter table agenda_ingesta_observacion add constraint fk_agenda_ingesta_observacion_36 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_observacion_36 on agenda_ingesta_observacion (agendaingesta_id);
alter table agenda_ingesta_problema add constraint fk_agenda_ingesta_problema_ag_37 foreign key (agendaingesta_id) references agenda_ingesta (id);
create index ix_agenda_ingesta_problema_ag_37 on agenda_ingesta_problema (agendaingesta_id);
alter table agenda_junta add constraint fk_agenda_junta_agenda_38 foreign key (agenda_id) references agenda (id);
create index ix_agenda_junta_agenda_38 on agenda_junta (agenda_id);
alter table agenda_locacion add constraint fk_agenda_locacion_agenda_39 foreign key (agenda_id) references agenda (id);
create index ix_agenda_locacion_agenda_39 on agenda_locacion (agenda_id);
alter table agenda_operador_sala add constraint fk_agenda_operador_sala_agend_40 foreign key (agenda_id) references agenda (id);
create index ix_agenda_operador_sala_agend_40 on agenda_operador_sala (agenda_id);
alter table agenda_operador_sala add constraint fk_agenda_operador_sala_perso_41 foreign key (personal_id) references personal (id);
create index ix_agenda_operador_sala_perso_41 on agenda_operador_sala (personal_id);
alter table agenda_previo_autoriza_accesorio add constraint fk_agenda_previo_autoriza_acc_42 foreign key (agenda_id) references agenda (id);
create index ix_agenda_previo_autoriza_acc_42 on agenda_previo_autoriza_accesorio (agenda_id);
alter table agenda_previo_autoriza_accesorio add constraint fk_agenda_previo_autoriza_acc_43 foreign key (preagendaaccesorio_id) references pre_agenda_accesorio (id);
create index ix_agenda_previo_autoriza_acc_43 on agenda_previo_autoriza_accesorio (preagendaaccesorio_id);
alter table agenda_previo_autoriza_equipo add constraint fk_agenda_previo_autoriza_equ_44 foreign key (agenda_id) references agenda (id);
create index ix_agenda_previo_autoriza_equ_44 on agenda_previo_autoriza_equipo (agenda_id);
alter table agenda_previo_autoriza_equipo add constraint fk_agenda_previo_autoriza_equ_45 foreign key (preagendaequipo_id) references pre_agenda_equipo (id);
create index ix_agenda_previo_autoriza_equ_45 on agenda_previo_autoriza_equipo (preagendaequipo_id);
alter table agenda_sala add constraint fk_agenda_sala_agenda_46 foreign key (agenda_id) references agenda (id);
create index ix_agenda_sala_agenda_46 on agenda_sala (agenda_id);
alter table agenda_sala add constraint fk_agenda_sala_sala_47 foreign key (sala_id) references sala (id);
create index ix_agenda_sala_sala_47 on agenda_sala (sala_id);
alter table agenda_sala_uso_cabina add constraint fk_agenda_sala_uso_cabina_age_48 foreign key (agendasala_id) references agenda_sala (id);
create index ix_agenda_sala_uso_cabina_age_48 on agenda_sala_uso_cabina (agendasala_id);
alter table agenda_salida add constraint fk_agenda_salida_agenda_49 foreign key (agenda_id) references agenda (id);
create index ix_agenda_salida_agenda_49 on agenda_salida (agenda_id);
alter table agenda_vehiculo add constraint fk_agenda_vehiculo_agenda_50 foreign key (agenda_id) references agenda (id);
create index ix_agenda_vehiculo_agenda_50 on agenda_vehiculo (agenda_id);
alter table agenda_vehiculo add constraint fk_agenda_vehiculo_vehiculo_51 foreign key (vehiculo_id) references vehiculo (id);
create index ix_agenda_vehiculo_vehiculo_51 on agenda_vehiculo (vehiculo_id);
alter table areatematica add constraint fk_areatematica_catalogador_52 foreign key (catalogador_id) references personal (id);
create index ix_areatematica_catalogador_52 on areatematica (catalogador_id);
alter table credito add constraint fk_credito_catalogo_53 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_credito_catalogo_53 on credito (catalogo_id);
alter table credito add constraint fk_credito_tipoCredito_54 foreign key (tipo_credito_id) references tipo_credito (id);
create index ix_credito_tipoCredito_54 on credito (tipo_credito_id);
alter table credito add constraint fk_credito_catalogador_55 foreign key (catalogador_id) references personal (id);
create index ix_credito_catalogador_55 on credito (catalogador_id);
alter table cuenta add constraint fk_cuenta_personal_56 foreign key (personal_id) references personal (id);
create index ix_cuenta_personal_56 on cuenta (personal_id);
alter table cuenta_rol add constraint fk_cuenta_rol_cuenta_57 foreign key (cuenta_id) references cuenta (id);
create index ix_cuenta_rol_cuenta_57 on cuenta_rol (cuenta_id);
alter table cuenta_rol add constraint fk_cuenta_rol_rol_58 foreign key (rol_id) references rol (id);
create index ix_cuenta_rol_rol_58 on cuenta_rol (rol_id);
alter table disponibilidad add constraint fk_disponibilidad_usuario_59 foreign key (usuario_id) references personal (id);
create index ix_disponibilidad_usuario_59 on disponibilidad (usuario_id);
alter table equipo add constraint fk_equipo_estado_60 foreign key (estado_id) references estado_equipo_accesorio_vehiculo (id);
create index ix_equipo_estado_60 on equipo (estado_id);
alter table folio add constraint fk_folio_oficio_61 foreign key (oficio_id) references oficio (id);
create index ix_folio_oficio_61 on folio (oficio_id);
alter table folio add constraint fk_folio_estado_62 foreign key (estado_id) references estado (id);
create index ix_folio_estado_62 on folio (estado_id);
alter table folio add constraint fk_folio_foliocancelacion_63 foreign key (foliocancelacion_id) references folio_cancelacion (id);
create index ix_folio_foliocancelacion_63 on folio (foliocancelacion_id);
alter table folio_cancelacion add constraint fk_folio_cancelacion_motivo_64 foreign key (motivo_id) references motivo_cancelacion (id);
create index ix_folio_cancelacion_motivo_64 on folio_cancelacion (motivo_id);
alter table folio_cancelacion add constraint fk_folio_cancelacion_estadoan_65 foreign key (estadoanterior_id) references estado (id);
create index ix_folio_cancelacion_estadoan_65 on folio_cancelacion (estadoanterior_id);
alter table folio_productor_asignado add constraint fk_folio_productor_asignado_f_66 foreign key (folio_id) references folio (id);
create index ix_folio_productor_asignado_f_66 on folio_productor_asignado (folio_id);
alter table folio_productor_asignado add constraint fk_folio_productor_asignado_p_67 foreign key (personal_id) references personal (id);
create index ix_folio_productor_asignado_p_67 on folio_productor_asignado (personal_id);
alter table his_folio add constraint fk_his_folio_folio_68 foreign key (folio_id) references folio (id);
create index ix_his_folio_folio_68 on his_folio (folio_id);
alter table his_folio add constraint fk_his_folio_estado_69 foreign key (estado_id) references estado (id);
create index ix_his_folio_estado_69 on his_folio (estado_id);
alter table his_folio add constraint fk_his_folio_usuario_70 foreign key (usuario_id) references personal (id);
create index ix_his_folio_usuario_70 on his_folio (usuario_id);
alter table his_folio add constraint fk_his_folio_rol_71 foreign key (rol_id) references rol (id);
create index ix_his_folio_rol_71 on his_folio (rol_id);
alter table oficio add constraint fk_oficio_urremitente_72 foreign key (urremitente_id) references unidad_responsable (id);
create index ix_oficio_urremitente_72 on oficio (urremitente_id);
alter table oficio_bitacora add constraint fk_oficio_bitacora_oficio_73 foreign key (oficio_id) references oficio (id);
create index ix_oficio_bitacora_oficio_73 on oficio_bitacora (oficio_id);
alter table oficio_contacto add constraint fk_oficio_contacto_oficio_74 foreign key (oficio_id) references oficio (id);
create index ix_oficio_contacto_oficio_74 on oficio_contacto (oficio_id);
alter table oficio_contacto_correo add constraint fk_oficio_contacto_correo_ofi_75 foreign key (oficio_contactos_id) references oficio_contacto (id);
create index ix_oficio_contacto_correo_ofi_75 on oficio_contacto_correo (oficio_contactos_id);
alter table oficio_contacto_telefono add constraint fk_oficio_contacto_telefono_o_76 foreign key (oficio_contactos_id) references oficio_contacto (id);
create index ix_oficio_contacto_telefono_o_76 on oficio_contacto_telefono (oficio_contactos_id);
alter table oficio_encuesta add constraint fk_oficio_encuesta_oficio_77 foreign key (oficio_id) references oficio (id);
create index ix_oficio_encuesta_oficio_77 on oficio_encuesta (oficio_id);
alter table oficio_entrada_salida add constraint fk_oficio_entrada_salida_ofic_78 foreign key (oficio_id) references oficio (id);
create index ix_oficio_entrada_salida_ofic_78 on oficio_entrada_salida (oficio_id);
alter table oficio_evidencia_entrega add constraint fk_oficio_evidencia_entrega_o_79 foreign key (oficio_id) references oficio (id);
create index ix_oficio_evidencia_entrega_o_79 on oficio_evidencia_entrega (oficio_id);
alter table oficio_fecha_grabacion add constraint fk_oficio_fecha_grabacion_ofi_80 foreign key (oficio_id) references oficio (id);
create index ix_oficio_fecha_grabacion_ofi_80 on oficio_fecha_grabacion (oficio_id);
alter table oficio_guion add constraint fk_oficio_guion_oficio_81 foreign key (oficio_id) references oficio (id);
create index ix_oficio_guion_oficio_81 on oficio_guion (oficio_id);
alter table oficio_imagen add constraint fk_oficio_imagen_oficio_82 foreign key (oficio_id) references oficio (id);
create index ix_oficio_imagen_oficio_82 on oficio_imagen (oficio_id);
alter table oficio_minuta add constraint fk_oficio_minuta_oficio_83 foreign key (oficio_id) references oficio (id);
create index ix_oficio_minuta_oficio_83 on oficio_minuta (oficio_id);
alter table oficio_productor_solicitado add constraint fk_oficio_productor_solicitad_84 foreign key (oficio_id) references oficio (id);
create index ix_oficio_productor_solicitad_84 on oficio_productor_solicitado (oficio_id);
alter table oficio_productor_solicitado add constraint fk_oficio_productor_solicitad_85 foreign key (personal_id) references personal (id);
create index ix_oficio_productor_solicitad_85 on oficio_productor_solicitado (personal_id);
alter table oficio_respuesta add constraint fk_oficio_respuesta_oficio_86 foreign key (oficio_id) references oficio (id);
create index ix_oficio_respuesta_oficio_86 on oficio_respuesta (oficio_id);
alter table oficio_servicio_solicitado add constraint fk_oficio_servicio_solicitado_87 foreign key (oficio_id) references oficio (id);
create index ix_oficio_servicio_solicitado_87 on oficio_servicio_solicitado (oficio_id);
alter table oficio_servicio_solicitado add constraint fk_oficio_servicio_solicitado_88 foreign key (servicio_id) references servicio (id);
create index ix_oficio_servicio_solicitado_88 on oficio_servicio_solicitado (servicio_id);
alter table operador_sala add constraint fk_operador_sala_personal_89 foreign key (personal_id) references personal (id);
create index ix_operador_sala_personal_89 on operador_sala (personal_id);
alter table operador_sala add constraint fk_operador_sala_sala_90 foreign key (sala_id) references sala (id);
create index ix_operador_sala_sala_90 on operador_sala (sala_id);
alter table palabra_clave add constraint fk_palabra_clave_catalogador_91 foreign key (catalogador_id) references personal (id);
create index ix_palabra_clave_catalogador_91 on palabra_clave (catalogador_id);
alter table palabra_clave add constraint fk_palabra_clave_catalogo_92 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_palabra_clave_catalogo_92 on palabra_clave (catalogo_id);
alter table personal add constraint fk_personal_tipocontrato_93 foreign key (tipocontrato_id) references tipo_contrato (id);
create index ix_personal_tipocontrato_93 on personal (tipocontrato_id);
alter table personal_avatar add constraint fk_personal_avatar_personal_94 foreign key (personal_id) references personal (id);
create index ix_personal_avatar_personal_94 on personal_avatar (personal_id);
alter table personal_cambio_horario add constraint fk_personal_cambio_horario_pe_95 foreign key (personal_id) references personal (id);
create index ix_personal_cambio_horario_pe_95 on personal_cambio_horario (personal_id);
alter table personal_correo add constraint fk_personal_correo_personal_96 foreign key (personal_id) references personal (id);
create index ix_personal_correo_personal_96 on personal_correo (personal_id);
alter table personal_horario add constraint fk_personal_horario_personal_97 foreign key (personal_id) references personal (id);
create index ix_personal_horario_personal_97 on personal_horario (personal_id);
alter table pre_agenda add constraint fk_pre_agenda_folioproductora_98 foreign key (folioproductorasignado_id) references folio_productor_asignado (id);
create index ix_pre_agenda_folioproductora_98 on pre_agenda (folioproductorasignado_id);
alter table pre_agenda add constraint fk_pre_agenda_fase_99 foreign key (fase_id) references fase (id);
create index ix_pre_agenda_fase_99 on pre_agenda (fase_id);
alter table pre_agenda add constraint fk_pre_agenda_estado_100 foreign key (estado_id) references estado (id);
create index ix_pre_agenda_estado_100 on pre_agenda (estado_id);
alter table pre_agenda_accesorio add constraint fk_pre_agenda_accesorio_prea_101 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_accesorio_prea_101 on pre_agenda_accesorio (preagenda_id);
alter table pre_agenda_accesorio add constraint fk_pre_agenda_accesorio_acce_102 foreign key (accesorio_id) references accesorio (id);
create index ix_pre_agenda_accesorio_acce_102 on pre_agenda_accesorio (accesorio_id);
alter table pre_agenda_accesorio add constraint fk_pre_agenda_accesorio_esta_103 foreign key (estado_id) references estado (id);
create index ix_pre_agenda_accesorio_esta_103 on pre_agenda_accesorio (estado_id);
alter table pre_agenda_cancelacion add constraint fk_pre_agenda_cancelacion_pr_104 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_cancelacion_pr_104 on pre_agenda_cancelacion (preagenda_id);
alter table pre_agenda_cancelacion add constraint fk_pre_agenda_cancelacion_es_105 foreign key (estado_anterior_id) references estado (id);
create index ix_pre_agenda_cancelacion_es_105 on pre_agenda_cancelacion (estado_anterior_id);
alter table pre_agenda_cancelacion add constraint fk_pre_agenda_cancelacion_mo_106 foreign key (motivo_id) references motivo_cancelacion (id);
create index ix_pre_agenda_cancelacion_mo_106 on pre_agenda_cancelacion (motivo_id);
alter table pre_agenda_equipo add constraint fk_pre_agenda_equipo_preagen_107 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_equipo_preagen_107 on pre_agenda_equipo (preagenda_id);
alter table pre_agenda_equipo add constraint fk_pre_agenda_equipo_equipo_108 foreign key (equipo_id) references equipo (id);
create index ix_pre_agenda_equipo_equipo_108 on pre_agenda_equipo (equipo_id);
alter table pre_agenda_equipo add constraint fk_pre_agenda_equipo_estado_109 foreign key (estado_id) references estado (id);
create index ix_pre_agenda_equipo_estado_109 on pre_agenda_equipo (estado_id);
alter table pre_agenda_formato_entrega add constraint fk_pre_agenda_formato_entreg_110 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_formato_entreg_110 on pre_agenda_formato_entrega (preagenda_id);
alter table pre_agenda_formato_entrega add constraint fk_pre_agenda_formato_entreg_111 foreign key (formato_id) references formato (id);
create index ix_pre_agenda_formato_entreg_111 on pre_agenda_formato_entrega (formato_id);
alter table pre_agenda_ing_admon_eqpo add constraint fk_pre_agenda_ing_admon_eqpo_112 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_ing_admon_eqpo_112 on pre_agenda_ing_admon_eqpo (preagenda_id);
alter table pre_agenda_ing_admon_eqpo add constraint fk_pre_agenda_ing_admon_eqpo_113 foreign key (ingeniero_id) references personal (id);
create index ix_pre_agenda_ing_admon_eqpo_113 on pre_agenda_ing_admon_eqpo (ingeniero_id);
alter table pre_agenda_ingesta add constraint fk_pre_agenda_ingesta_preage_114 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_ingesta_preage_114 on pre_agenda_ingesta (preagenda_id);
alter table pre_agenda_ingesta add constraint fk_pre_agenda_ingesta_estado_115 foreign key (estado_id) references estado_material (id);
create index ix_pre_agenda_ingesta_estado_115 on pre_agenda_ingesta (estado_id);
alter table pre_agenda_ingesta_almacenamiento add constraint fk_pre_agenda_ingesta_almace_116 foreign key (preagendaingesta_id) references pre_agenda_ingesta (id);
create index ix_pre_agenda_ingesta_almace_116 on pre_agenda_ingesta_almacenamiento (preagendaingesta_id);
alter table pre_agenda_ingesta_almacenamiento add constraint fk_pre_agenda_ingesta_almace_117 foreign key (medioalmacenamiento_id) references medio_almacenamiento (id);
create index ix_pre_agenda_ingesta_almace_117 on pre_agenda_ingesta_almacenamiento (medioalmacenamiento_id);
alter table pre_agenda_ingesta_fmto_salida add constraint fk_pre_agenda_ingesta_fmto_s_118 foreign key (preagendaingesta_id) references pre_agenda_ingesta (id);
create index ix_pre_agenda_ingesta_fmto_s_118 on pre_agenda_ingesta_fmto_salida (preagendaingesta_id);
alter table pre_agenda_ingesta_fmto_salida add constraint fk_pre_agenda_ingesta_fmto_s_119 foreign key (formatoingesta_id) references formato_ingesta (id);
create index ix_pre_agenda_ingesta_fmto_s_119 on pre_agenda_ingesta_fmto_salida (formatoingesta_id);
alter table pre_agenda_junta add constraint fk_pre_agenda_junta_preagend_120 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_junta_preagend_120 on pre_agenda_junta (preagenda_id);
alter table pre_agenda_locacion add constraint fk_pre_agenda_locacion_preag_121 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_locacion_preag_121 on pre_agenda_locacion (preagenda_id);
alter table pre_agenda_locutor add constraint fk_pre_agenda_locutor_preage_122 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_locutor_preage_122 on pre_agenda_locutor (preagenda_id);
alter table pre_agenda_locutor add constraint fk_pre_agenda_locutor_person_123 foreign key (personal_id) references personal (id);
create index ix_pre_agenda_locutor_person_123 on pre_agenda_locutor (personal_id);
alter table pre_agenda_operador_sala add constraint fk_pre_agenda_operador_sala__124 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_operador_sala__124 on pre_agenda_operador_sala (preagenda_id);
alter table pre_agenda_operador_sala add constraint fk_pre_agenda_operador_sala__125 foreign key (personal_id) references personal (id);
create index ix_pre_agenda_operador_sala__125 on pre_agenda_operador_sala (personal_id);
alter table pre_agenda_rol add constraint fk_pre_agenda_rol_preagenda_126 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_rol_preagenda_126 on pre_agenda_rol (preagenda_id);
alter table pre_agenda_rol add constraint fk_pre_agenda_rol_rol_127 foreign key (rol_id) references rol (id);
create index ix_pre_agenda_rol_rol_127 on pre_agenda_rol (rol_id);
alter table pre_agenda_sala add constraint fk_pre_agenda_sala_preagenda_128 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_sala_preagenda_128 on pre_agenda_sala (preagenda_id);
alter table pre_agenda_sala add constraint fk_pre_agenda_sala_sala_129 foreign key (sala_id) references sala (id);
create index ix_pre_agenda_sala_sala_129 on pre_agenda_sala (sala_id);
alter table pre_agenda_sala_uso_cabina add constraint fk_pre_agenda_sala_uso_cabin_130 foreign key (preagendasala_id) references pre_agenda_sala (id);
create index ix_pre_agenda_sala_uso_cabin_130 on pre_agenda_sala_uso_cabina (preagendasala_id);
alter table pre_agenda_salida add constraint fk_pre_agenda_salida_preagen_131 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_salida_preagen_131 on pre_agenda_salida (preagenda_id);
alter table pre_agenda_vehiculo add constraint fk_pre_agenda_vehiculo_preag_132 foreign key (preagenda_id) references pre_agenda (id);
create index ix_pre_agenda_vehiculo_preag_132 on pre_agenda_vehiculo (preagenda_id);
alter table produccion add constraint fk_produccion_catalogador_133 foreign key (catalogador_id) references personal (id);
create index ix_produccion_catalogador_133 on produccion (catalogador_id);
alter table registro_acceso add constraint fk_registro_acceso_usuario_134 foreign key (usuario_id) references personal (id);
create index ix_registro_acceso_usuario_134 on registro_acceso (usuario_id);
alter table registro_acceso add constraint fk_registro_acceso_rol_135 foreign key (rol_id) references rol (id);
create index ix_registro_acceso_rol_135 on registro_acceso (rol_id);
alter table rol_derecho add constraint fk_rol_derecho_rol_136 foreign key (rol_id) references rol (id);
create index ix_rol_derecho_rol_136 on rol_derecho (rol_id);
alter table rol_derecho add constraint fk_rol_derecho_ambito_137 foreign key (ambito_id) references ambito (id);
create index ix_rol_derecho_ambito_137 on rol_derecho (ambito_id);
alter table rol_fase add constraint fk_rol_fase_fase_138 foreign key (fase_id) references fase (id);
create index ix_rol_fase_fase_138 on rol_fase (fase_id);
alter table rol_fase add constraint fk_rol_fase_rol_139 foreign key (rol_id) references rol (id);
create index ix_rol_fase_rol_139 on rol_fase (rol_id);
alter table sala_mantenimiento add constraint fk_sala_mantenimiento_sala_140 foreign key (sala_id) references sala (id);
create index ix_sala_mantenimiento_sala_140 on sala_mantenimiento (sala_id);
alter table sala_mantenimiento add constraint fk_sala_mantenimiento_tipo_141 foreign key (tipo_id) references tipo_mantenimiento (id);
create index ix_sala_mantenimiento_tipo_141 on sala_mantenimiento (tipo_id);
alter table serie add constraint fk_serie_catalogador_142 foreign key (catalogador_id) references personal (id);
create index ix_serie_catalogador_142 on serie (catalogador_id);
alter table unidad_responsable add constraint fk_unidad_responsable_catalo_143 foreign key (catalogador_id) references personal (id);
create index ix_unidad_responsable_catalo_143 on unidad_responsable (catalogador_id);
alter table vehiculo add constraint fk_vehiculo_estado_144 foreign key (estado_id) references estado_equipo_accesorio_vehiculo (id);
create index ix_vehiculo_estado_144 on vehiculo (estado_id);
alter table video_personaje add constraint fk_video_personaje_catalogo_145 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_video_personaje_catalogo_145 on video_personaje (catalogo_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_unidadrespon_146 foreign key (unidadresponsable_id) references unidad_responsable (id);
create index ix_vtk_catalogo_unidadrespon_146 on vtk_catalogo (unidadresponsable_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_serie_147 foreign key (serie_id) references serie (id);
create index ix_vtk_catalogo_serie_147 on vtk_catalogo (serie_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_formato_148 foreign key (formato_id) references vtk_formato (id);
create index ix_vtk_catalogo_formato_148 on vtk_catalogo (formato_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_idioma_149 foreign key (idioma_id) references idioma (id);
create index ix_vtk_catalogo_idioma_149 on vtk_catalogo (idioma_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_tipograbacio_150 foreign key (tipograbacion_id) references tipo_grabacion (id);
create index ix_vtk_catalogo_tipograbacio_150 on vtk_catalogo (tipograbacion_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_produccion_151 foreign key (produccion_id) references produccion (id);
create index ix_vtk_catalogo_produccion_151 on vtk_catalogo (produccion_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_disponibilid_152 foreign key (disponibilidad_id) references disponibilidad (id);
create index ix_vtk_catalogo_disponibilid_152 on vtk_catalogo (disponibilidad_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_areatematica_153 foreign key (areatematica_id) references areatematica (id);
create index ix_vtk_catalogo_areatematica_153 on vtk_catalogo (areatematica_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_catalogador_154 foreign key (catalogador_id) references personal (id);
create index ix_vtk_catalogo_catalogador_154 on vtk_catalogo (catalogador_id);
alter table vtk_catalogo add constraint fk_vtk_catalogo_video_155 foreign key (video_id) references tipo_video (id);
create index ix_vtk_catalogo_video_155 on vtk_catalogo (video_id);
alter table vtk_evento add constraint fk_vtk_evento_catalogo_156 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_vtk_evento_catalogo_156 on vtk_evento (catalogo_id);
alter table vtk_evento add constraint fk_vtk_evento_servicio_157 foreign key (servicio_id) references servicio (id);
create index ix_vtk_evento_servicio_157 on vtk_evento (servicio_id);
alter table vtk_formato add constraint fk_vtk_formato_usuario_158 foreign key (usuario_id) references personal (id);
create index ix_vtk_formato_usuario_158 on vtk_formato (usuario_id);
alter table vtk_nivel add constraint fk_vtk_nivel_catalogo_159 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_vtk_nivel_catalogo_159 on vtk_nivel (catalogo_id);
alter table vtk_nivel add constraint fk_vtk_nivel_nivel_160 foreign key (nivel_id) references nivel (id);
create index ix_vtk_nivel_nivel_160 on vtk_nivel (nivel_id);
alter table vtk_time_line add constraint fk_vtk_time_line_catalogo_161 foreign key (catalogo_id) references vtk_catalogo (id);
create index ix_vtk_time_line_catalogo_161 on vtk_time_line (catalogo_id);
alter table vtk_time_line add constraint fk_vtk_time_line_personaje_162 foreign key (personaje_id) references video_personaje (id);
create index ix_vtk_time_line_personaje_162 on vtk_time_line (personaje_id);



# --- !Downs

drop table if exists accesorio cascade;

drop table if exists agenda cascade;

drop table if exists agenda_accesorio cascade;

drop table if exists agenda_cancelacion cascade;

drop table if exists agenda_cuenta_rol cascade;

drop table if exists agenda_equipo cascade;

drop table if exists agenda_formato_entrega cascade;

drop table if exists agenda_ing_admon_eqpo cascade;

drop table if exists agenda_ingesta cascade;

drop table if exists agenda_ingesta_almacenamiento cascade;

drop table if exists agenda_ingesta_det_audio cascade;

drop table if exists agenda_ingesta_det_video cascade;

drop table if exists agenda_ingesta_fto cascade;

drop table if exists agenda_ingesta_fto_o cascade;

drop table if exists agenda_ingesta_fto_s cascade;

drop table if exists agenda_ingesta_idioma cascade;

drop table if exists agenda_ingesta_idioma_otro cascade;

drop table if exists agenda_ingesta_observacion cascade;

drop table if exists agenda_ingesta_problema cascade;

drop table if exists agenda_junta cascade;

drop table if exists agenda_locacion cascade;

drop table if exists agenda_operador_sala cascade;

drop table if exists agenda_previo_autoriza_accesorio cascade;

drop table if exists agenda_previo_autoriza_equipo cascade;

drop table if exists agenda_sala cascade;

drop table if exists agenda_sala_uso_cabina cascade;

drop table if exists agenda_salida cascade;

drop table if exists agenda_vehiculo cascade;

drop table if exists ambito cascade;

drop table if exists areatematica cascade;

drop table if exists catalogador cascade;

drop table if exists cola_correos cascade;

drop table if exists credito cascade;

drop table if exists ctacorreo cascade;

drop table if exists cuenta cascade;

drop table if exists cuenta_rol cascade;

drop table if exists disponibilidad cascade;

drop table if exists equipo cascade;

drop table if exists estado cascade;

drop table if exists estado_equipo_accesorio_vehiculo cascade;

drop table if exists estado_material cascade;

drop table if exists fase cascade;

drop table if exists folio cascade;

drop table if exists folio_cancelacion cascade;

drop table if exists folio_productor_asignado cascade;

drop table if exists formato cascade;

drop table if exists formato_ingesta cascade;

drop table if exists his_folio cascade;

drop table if exists idioma cascade;

drop table if exists medio_almacenamiento cascade;

drop table if exists motivo_cancelacion cascade;

drop table if exists nivel cascade;

drop table if exists nivel_academico cascade;

drop table if exists oficio cascade;

drop table if exists oficio_bitacora cascade;

drop table if exists oficio_contacto cascade;

drop table if exists oficio_contacto_correo cascade;

drop table if exists oficio_contacto_telefono cascade;

drop table if exists oficio_encuesta cascade;

drop table if exists oficio_entrada_salida cascade;

drop table if exists oficio_evidencia_entrega cascade;

drop table if exists oficio_fecha_grabacion cascade;

drop table if exists oficio_guion cascade;

drop table if exists oficio_imagen cascade;

drop table if exists oficio_minuta cascade;

drop table if exists oficio_productor_solicitado cascade;

drop table if exists oficio_respuesta cascade;

drop table if exists oficio_servicio_solicitado cascade;

drop table if exists operador_sala cascade;

drop table if exists palabra_clave cascade;

drop table if exists personal cascade;

drop table if exists personal_avatar cascade;

drop table if exists personal_cambio_horario cascade;

drop table if exists personal_correo cascade;

drop table if exists personal_horario cascade;

drop table if exists pre_agenda cascade;

drop table if exists pre_agenda_accesorio cascade;

drop table if exists pre_agenda_cancelacion cascade;

drop table if exists pre_agenda_equipo cascade;

drop table if exists pre_agenda_formato_entrega cascade;

drop table if exists pre_agenda_ing_admon_eqpo cascade;

drop table if exists pre_agenda_ingesta cascade;

drop table if exists pre_agenda_ingesta_almacenamiento cascade;

drop table if exists pre_agenda_ingesta_fmto_salida cascade;

drop table if exists pre_agenda_junta cascade;

drop table if exists pre_agenda_locacion cascade;

drop table if exists pre_agenda_locutor cascade;

drop table if exists pre_agenda_operador_sala cascade;

drop table if exists pre_agenda_rol cascade;

drop table if exists pre_agenda_sala cascade;

drop table if exists pre_agenda_sala_uso_cabina cascade;

drop table if exists pre_agenda_salida cascade;

drop table if exists pre_agenda_vehiculo cascade;

drop table if exists produccion cascade;

drop table if exists registro_acceso cascade;

drop table if exists rol cascade;

drop table if exists rol_derecho cascade;

drop table if exists rol_fase cascade;

drop table if exists sala cascade;

drop table if exists sala_mantenimiento cascade;

drop table if exists serie cascade;

drop table if exists servicio cascade;

drop table if exists tipo_contrato cascade;

drop table if exists tipo_credito cascade;

drop table if exists tipo_equipo_accesorio cascade;

drop table if exists tipo_grabacion cascade;

drop table if exists tipo_mantenimiento cascade;

drop table if exists tipo_video cascade;

drop table if exists unidad_responsable cascade;

drop table if exists vehiculo cascade;

drop table if exists video_personaje cascade;

drop table if exists vtk_catalogo cascade;

drop table if exists vtk_evento cascade;

drop table if exists vtk_formato cascade;

drop table if exists vtk_nivel cascade;

drop table if exists vtk_time_line cascade;

drop sequence if exists accesorio_seq;

drop sequence if exists agenda_seq;

drop sequence if exists agenda_accesorio_seq;

drop sequence if exists agenda_cancelacion_seq;

drop sequence if exists agenda_cuenta_rol_seq;

drop sequence if exists agenda_equipo_seq;

drop sequence if exists agenda_formato_entrega_seq;

drop sequence if exists agenda_ing_admon_eqpo_seq;

drop sequence if exists agenda_ingesta_seq;

drop sequence if exists agenda_ingesta_almacenamiento_seq;

drop sequence if exists agenda_ingesta_det_audio_seq;

drop sequence if exists agenda_ingesta_det_video_seq;

drop sequence if exists agenda_ingesta_fto_seq;

drop sequence if exists agenda_ingesta_fto_o_seq;

drop sequence if exists agenda_ingesta_fto_s_seq;

drop sequence if exists agenda_ingesta_idioma_seq;

drop sequence if exists agenda_ingesta_idioma_otro_seq;

drop sequence if exists agenda_ingesta_observacion_seq;

drop sequence if exists agenda_ingesta_problema_seq;

drop sequence if exists agenda_junta_seq;

drop sequence if exists agenda_locacion_seq;

drop sequence if exists agenda_operador_sala_seq;

drop sequence if exists agenda_previo_autoriza_accesorio_seq;

drop sequence if exists agenda_previo_autoriza_equipo_seq;

drop sequence if exists agenda_sala_seq;

drop sequence if exists agenda_sala_uso_cabina_seq;

drop sequence if exists agenda_salida_seq;

drop sequence if exists agenda_vehiculo_seq;

drop sequence if exists ambito_seq;

drop sequence if exists areatematica_seq;

drop sequence if exists catalogador_seq;

drop sequence if exists cola_correos_seq;

drop sequence if exists credito_seq;

drop sequence if exists ctacorreo_seq;

drop sequence if exists cuenta_seq;

drop sequence if exists cuenta_rol_seq;

drop sequence if exists disponibilidad_seq;

drop sequence if exists equipo_seq;

drop sequence if exists estado_seq;

drop sequence if exists estado_equipo_accesorio_vehiculo_seq;

drop sequence if exists estado_material_seq;

drop sequence if exists fase_seq;

drop sequence if exists folio_seq;

drop sequence if exists folio_cancelacion_seq;

drop sequence if exists folio_productor_asignado_seq;

drop sequence if exists formato_seq;

drop sequence if exists formato_ingesta_seq;

drop sequence if exists his_folio_seq;

drop sequence if exists idioma_seq;

drop sequence if exists medio_almacenamiento_seq;

drop sequence if exists motivo_cancelacion_seq;

drop sequence if exists nivel_seq;

drop sequence if exists nivel_academico_seq;

drop sequence if exists oficio_seq;

drop sequence if exists oficio_bitacora_seq;

drop sequence if exists oficio_contacto_seq;

drop sequence if exists oficio_contacto_correo_seq;

drop sequence if exists oficio_contacto_telefono_seq;

drop sequence if exists oficio_encuesta_seq;

drop sequence if exists oficio_entrada_salida_seq;

drop sequence if exists oficio_evidencia_entrega_seq;

drop sequence if exists oficio_fecha_grabacion_seq;

drop sequence if exists oficio_guion_seq;

drop sequence if exists oficio_imagen_seq;

drop sequence if exists oficio_minuta_seq;

drop sequence if exists oficio_productor_solicitado_seq;

drop sequence if exists oficio_respuesta_seq;

drop sequence if exists oficio_servicio_solicitado_seq;

drop sequence if exists operador_sala_seq;

drop sequence if exists palabra_clave_seq;

drop sequence if exists personal_seq;

drop sequence if exists personal_avatar_seq;

drop sequence if exists personal_cambio_horario_seq;

drop sequence if exists personal_correo_seq;

drop sequence if exists personal_horario_seq;

drop sequence if exists pre_agenda_seq;

drop sequence if exists pre_agenda_accesorio_seq;

drop sequence if exists pre_agenda_cancelacion_seq;

drop sequence if exists pre_agenda_equipo_seq;

drop sequence if exists pre_agenda_formato_entrega_seq;

drop sequence if exists pre_agenda_ing_admon_eqpo_seq;

drop sequence if exists pre_agenda_ingesta_seq;

drop sequence if exists pre_agenda_ingesta_almacenamiento_seq;

drop sequence if exists pre_agenda_ingesta_fmto_salida_seq;

drop sequence if exists pre_agenda_junta_seq;

drop sequence if exists pre_agenda_locacion_seq;

drop sequence if exists pre_agenda_locutor_seq;

drop sequence if exists pre_agenda_operador_sala_seq;

drop sequence if exists pre_agenda_rol_seq;

drop sequence if exists pre_agenda_sala_seq;

drop sequence if exists pre_agenda_sala_uso_cabina_seq;

drop sequence if exists pre_agenda_salida_seq;

drop sequence if exists pre_agenda_vehiculo_seq;

drop sequence if exists produccion_seq;

drop sequence if exists registro_acceso_seq;

drop sequence if exists rol_seq;

drop sequence if exists rol_derecho_seq;

drop sequence if exists rol_fase_seq;

drop sequence if exists sala_seq;

drop sequence if exists sala_mantenimiento_seq;

drop sequence if exists serie_seq;

drop sequence if exists servicio_seq;

drop sequence if exists tipo_contrato_seq;

drop sequence if exists tipo_credito_seq;

drop sequence if exists tipo_equipo_accesorio_seq;

drop sequence if exists tipo_grabacion_seq;

drop sequence if exists tipo_mantenimiento_seq;

drop sequence if exists tipo_video_seq;

drop sequence if exists unidad_responsable_seq;

drop sequence if exists vehiculo_seq;

drop sequence if exists video_personaje_seq;

drop sequence if exists vtk_catalogo_seq;

drop sequence if exists vtk_evento_seq;

drop sequence if exists vtk_formato_seq;

drop sequence if exists vtk_nivel_seq;

drop sequence if exists vtk_time_line_seq;

