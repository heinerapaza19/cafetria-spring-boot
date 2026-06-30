CREATE TABLE pedidos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  fecha_creacion DATETIME NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE pedido_detalles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  pedido_id BIGINT NOT NULL,
  id_producto BIGINT NOT NULL,
  nombre_producto VARCHAR(120) NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);
