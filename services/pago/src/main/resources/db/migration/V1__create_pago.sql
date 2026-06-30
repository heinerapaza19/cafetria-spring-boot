CREATE TABLE pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT,
    monto DECIMAL(10,2),
    metodo VARCHAR(20)
);