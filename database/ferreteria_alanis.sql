
CREATE DATABASE IF NOT EXISTS ferreteria_alanis
    CHARACTER SET  utf8mb4
    COLLATE        utf8mb4_unicode_ci;

USE ferreteria_alanis;

CREATE TABLE DEPARTAMENTO (
    id_departamento          INT          NOT NULL AUTO_INCREMENT,
    nombre_departamento      VARCHAR(100) NOT NULL,
    descripcion_departamento VARCHAR(200)          DEFAULT NULL,
    CONSTRAINT PK_DEPARTAMENTO PRIMARY KEY (id_departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PROVEEDOR (
    id_proveedor     INT          NOT NULL AUTO_INCREMENT,
    nombre_proveedor VARCHAR(200) NOT NULL,
    telefono         VARCHAR(20)          DEFAULT NULL,
    correo           VARCHAR(100)         DEFAULT NULL,
    direccion        VARCHAR(300)         DEFAULT NULL,
    notas            TEXT                 DEFAULT NULL,
    CONSTRAINT PK_PROVEEDOR PRIMARY KEY (id_proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PRODUCTO (
    codigo_barras        VARCHAR(50)   NOT NULL,
    descripcion          VARCHAR(200)  NOT NULL,
    precio_costo         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    porcentaje_ganancia  DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    precio_venta_lista   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    existencia           DECIMAL(10,3) NOT NULL DEFAULT 0.000,
    inv_minimo           DECIMAL(10,3)          DEFAULT NULL,
    inv_maximo           DECIMAL(10,3)          DEFAULT NULL,
    unidad               VARCHAR(10)   NOT NULL DEFAULT 'Pza',
    usa_inventario       BOOLEAN       NOT NULL DEFAULT FALSE,
    cfdi_clave_producto  VARCHAR(20)            DEFAULT NULL,
    cfdi_unidad_medida   VARCHAR(20)            DEFAULT NULL,
    id_departamento      INT                    DEFAULT NULL,
    id_proveedor         INT                    DEFAULT NULL,
    CONSTRAINT PK_PRODUCTO
        PRIMARY KEY (codigo_barras),
    CONSTRAINT FK_PRODUCTO_DEPARTAMENTO
        FOREIGN KEY (id_departamento)
        REFERENCES DEPARTAMENTO(id_departamento)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT FK_PRODUCTO_PROVEEDOR
        FOREIGN KEY (id_proveedor)
        REFERENCES PROVEEDOR(id_proveedor)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT CHK_UNIDAD
        CHECK (unidad IN ('Pza','Granel'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE USUARIO (
    id_usuario       INT          NOT NULL AUTO_INCREMENT,
    nombre_usuario   VARCHAR(100) NOT NULL,
    contrasena_hash  VARCHAR(255) NOT NULL,
    CONSTRAINT PK_USUARIO        PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_USUARIO_NOMBRE UNIQUE      (nombre_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MODULO (
    id_modulo     INT         NOT NULL AUTO_INCREMENT,
    nombre_modulo VARCHAR(50) NOT NULL,
    CONSTRAINT PK_MODULO        PRIMARY KEY (id_modulo),
    CONSTRAINT UQ_MODULO_NOMBRE UNIQUE      (nombre_modulo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Módulos fijos del sistema (no se agregan ni eliminan desde la app)
INSERT INTO MODULO (nombre_modulo) VALUES
    ('Ventas'),
    ('Créditos'),
    ('Clientes'),
    ('Productos'),
    ('Inventario'),
    ('Recepción de materiales'),
    ('Retiros y Salidas'),
    ('Corte'),
    ('Reportes'),
    ('Configuración');

CREATE TABLE USUARIO_MODULO (
    id_usuario INT NOT NULL,
    id_modulo  INT NOT NULL,
    CONSTRAINT PK_USUARIO_MODULO PRIMARY KEY (id_usuario, id_modulo),
    CONSTRAINT FK_UM_USUARIO
        FOREIGN KEY (id_usuario)
        REFERENCES USUARIO(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FK_UM_MODULO
        FOREIGN KEY (id_modulo)
        REFERENCES MODULO(id_modulo)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CLIENTE (
    id_cliente       INT           NOT NULL AUTO_INCREMENT,
    nombres          VARCHAR(100)  NOT NULL,
    apellido_paterno VARCHAR(100)           DEFAULT NULL,
    apellido_materno VARCHAR(100)           DEFAULT NULL,
    telefono         VARCHAR(20)            DEFAULT NULL,
    correo           VARCHAR(100)           DEFAULT NULL,
    domicilio        VARCHAR(200)           DEFAULT NULL,
    colonia          VARCHAR(100)           DEFAULT NULL,
    municipio_estado VARCHAR(100)           DEFAULT NULL,
    codigo_postal    VARCHAR(10)            DEFAULT NULL,
    notas            TEXT                   DEFAULT NULL,
    tiene_credito    BOOLEAN       NOT NULL DEFAULT FALSE,
    limite_credito   DECIMAL(10,2)          DEFAULT NULL,
    saldo_credito    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT PK_CLIENTE PRIMARY KEY (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE TICKET (
    folio_ticket         INT           NOT NULL AUTO_INCREMENT,
    tipo_documento       VARCHAR(20)   NOT NULL DEFAULT 'Ticket',
    estado_documento     VARCHAR(15)   NOT NULL DEFAULT 'Activo',
    fecha_transaccion    DATE          NOT NULL,
    hora_transaccion     TIME          NOT NULL,
    folio_referencia     INT                    DEFAULT NULL,
    total_bruto          DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    porcentaje_descuento DECIMAL(5,3)  NOT NULL DEFAULT 0.000,
    total_descuento      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_neto           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    id_cliente           INT                    DEFAULT NULL,
    id_usuario           INT           NOT NULL,
    CONSTRAINT PK_TICKET
        PRIMARY KEY (folio_ticket),
    CONSTRAINT FK_TICKET_REFERENCIA
        FOREIGN KEY (folio_referencia)
        REFERENCES TICKET(folio_ticket)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT FK_TICKET_CLIENTE
        FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE(id_cliente)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT FK_TICKET_USUARIO
        FOREIGN KEY (id_usuario)
        REFERENCES USUARIO(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT CHK_TIPO_DOC
        CHECK (tipo_documento   IN ('Ticket','Cotización','Devolución','Re-Ticket')),
    CONSTRAINT CHK_ESTADO_DOC
        CHECK (estado_documento IN ('Activo','Cancelado','Pagado'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PAGO (
    id_pago                  INT           NOT NULL AUTO_INCREMENT,
    folio_ticket             INT           NOT NULL,
    metodo_pago              VARCHAR(20)   NOT NULL,
    monto_efectivo           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    pago_con                 DECIMAL(10,2)          DEFAULT NULL,
    cambio                   DECIMAL(10,2)          DEFAULT NULL,
    monto_tarjeta            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    referencia_tarjeta       VARCHAR(100)           DEFAULT NULL,
    voucher_tarjeta          BOOLEAN                DEFAULT FALSE,
    monto_transferencia      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    referencia_transferencia VARCHAR(100)           DEFAULT NULL,
    voucher_transferencia    BOOLEAN                DEFAULT FALSE,
    monto_cheque             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    referencia_cheque        VARCHAR(100)           DEFAULT NULL,
    monto_credito            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT PK_PAGO
        PRIMARY KEY (id_pago),
    CONSTRAINT UQ_PAGO_TICKET
        UNIQUE (folio_ticket),
    CONSTRAINT FK_PAGO_TICKET
        FOREIGN KEY (folio_ticket)
        REFERENCES TICKET(folio_ticket)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT CHK_METODO_PAGO
        CHECK (metodo_pago IN ('Efectivo','Crédito','Tarjeta','Mixto','Transferencia','Cheque'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE DETALLE_TICKET (
    folio_ticket          INT           NOT NULL,
    codigo_barras         VARCHAR(50)   NOT NULL,
    cantidad              DECIMAL(10,3) NOT NULL,
    precio_unitario_venta DECIMAL(10,2) NOT NULL,
    importe               DECIMAL(10,2) NOT NULL,
    descuento_producto    DECIMAL(5,3)  NOT NULL DEFAULT 0.000,
    CONSTRAINT PK_DETALLE
        PRIMARY KEY (folio_ticket, codigo_barras),
    CONSTRAINT FK_DETALLE_TICKET
        FOREIGN KEY (folio_ticket)
        REFERENCES TICKET(folio_ticket)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FK_DETALLE_PRODUCTO
        FOREIGN KEY (codigo_barras)
        REFERENCES PRODUCTO(codigo_barras)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE RETIRO (
    id_retiro          INT           NOT NULL AUTO_INCREMENT,
    fecha_retiro       DATE          NOT NULL,
    hora_retiro        TIME          NOT NULL,
    monto_retiro       DECIMAL(10,2) NOT NULL,
    descripcion_retiro VARCHAR(300)           DEFAULT NULL,
    id_usuario         INT           NOT NULL,
    CONSTRAINT PK_RETIRO
        PRIMARY KEY (id_retiro),
    CONSTRAINT FK_RETIRO_USUARIO
        FOREIGN KEY (id_usuario)
        REFERENCES USUARIO(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CORTE (
    id_corte                INT           NOT NULL AUTO_INCREMENT,
    fecha_corte             DATE          NOT NULL,
    fondo_inicial           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_ventas_dia        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_ventas_con_dcto   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_ventas_sin_dcto   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    venta_neta              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_efectivo_dia      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_tarjeta_dia       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_transferencia_dia DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_credito_dia       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_cheque_dia        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_gastos_dia        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fondo_final             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_entregado         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    diferencia              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    billetes_1000           INT           NOT NULL DEFAULT 0,
    billetes_500            INT           NOT NULL DEFAULT 0,
    billetes_200            INT           NOT NULL DEFAULT 0,
    billetes_100            INT           NOT NULL DEFAULT 0,
    billetes_50             INT           NOT NULL DEFAULT 0,
    billetes_20             INT           NOT NULL DEFAULT 0,
    monedas                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    dolares                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    venta_sin_ticket        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    entrada_caja            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    id_usuario              INT           NOT NULL,
    CONSTRAINT PK_CORTE
        PRIMARY KEY (id_corte),
    CONSTRAINT FK_CORTE_USUARIO
        FOREIGN KEY (id_usuario)
        REFERENCES USUARIO(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO USUARIO (nombre_usuario, contrasena_hash)
VALUES ('admin', SHA2('Admin2025!', 256));

-- El administrador (id=1) tiene acceso a todos los módulos
INSERT INTO USUARIO_MODULO (id_usuario, id_modulo)
SELECT 1, id_modulo FROM MODULO;