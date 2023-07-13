#Agrega personal para turno M y V para la Sala de Ingesta y Digitalización, sus horarios y una solicitud del folio 152020 de Vanessa
UPDATE cuenta_rol SET rol_id=16 WHERE id=69;


INSERT INTO personal (id, num_empleado, paterno, materno, nombre, tipo_id, tipocontrato_id, turno, activo, audit_insert, audit_update) VALUES(84, '20210002', 'De Sala', 'Ingesta', 'Operador 2', 1, 1, 'V', 'S', '2021-04-21 12:25:09.000', '2021-04-21 12:37:22.000');
INSERT INTO personal (id, num_empleado, paterno, materno, nombre, tipo_id, tipocontrato_id, turno, activo, audit_insert, audit_update) VALUES(83, '202104001', 'De Sala', 'Ingesta', 'Operador 1', 1, 1, 'M', 'S', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');

INSERT INTO operador_sala (id, personal_id, sala_id, turno, audit_insert, audit_update) VALUES(16, 83, 1, 'M', '2021-04-21 12:37:05.000', '2021-04-21 12:37:05.000');
INSERT INTO operador_sala (id, personal_id, sala_id, turno, audit_insert, audit_update) VALUES(17, 84, 1, 'V', '2021-04-21 12:37:05.000', '2021-04-21 12:37:05.000');

INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(455, 84, 2, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2021-04-21 12:37:22.000', '2021-04-21 12:37:22.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(456, 84, 3, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2021-04-21 12:37:22.000', '2021-04-21 12:37:22.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(457, 84, 4, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2021-04-21 12:37:22.000', '2021-04-21 12:37:22.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(458, 84, 5, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2021-04-21 12:37:22.000', '2021-04-21 12:37:22.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(459, 84, 6, '1970-01-01 15:00:00.000', '1970-01-01 21:00:00.000', '2021-04-21 12:37:22.000', '2021-04-21 12:37:22.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(445, 83, 2, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(446, 83, 3, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(447, 83, 4, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(448, 83, 5, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');
INSERT INTO personal_horario (id, personal_id, diasemana, desde, hasta, audit_insert, audit_update) VALUES(449, 83, 6, '1970-01-01 08:00:00.000', '1970-01-01 15:00:00.000', '2021-04-21 12:23:38.000', '2021-04-21 12:23:38.000');

INSERT INTO cuenta (id, personal_id, username, password, audit_insert, audit_update) VALUES(66, 83, 'opera1', 'opera1', '2021-04-21 13:40:18.000', '2021-04-21 13:40:18.000');
INSERT INTO cuenta (id, personal_id, username, password, audit_insert, audit_update) VALUES(67, 84, 'opera2', 'opera2', '2021-04-21 13:40:18.000', '2021-04-21 13:40:18.000');

INSERT INTO cuenta_rol (id, cuenta_id, rol_id, audit_insert, audit_update) VALUES(68, 66

, 16, '2021-04-21 13:56:57.000', '2021-04-21 13:56:57.000');


UPDATE folio SET fechaentrega='2022-02-28 00:00:00.000' WHERE id=1017;
