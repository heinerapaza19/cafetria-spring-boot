export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  roles: string[];
}

export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
  imagen: string;
}

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  imagen: string;
  idCategoria: number;
  precio: number;
  stock: number;
}

export interface CarritoDetalle {
  id: number;
  idProducto: number;
  nombreProducto: string;
  imagenUrl: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Carrito {
  id: number;
  idUsuario: number;
  estado: 'ACTIVO' | 'CONFIRMADO';
  total: number;
  detalles: CarritoDetalle[];
}

export interface AgregarProductoRequest {
  idProducto: number;
  cantidad: number;
}

export interface PedidoDetalle {
  id: number;
  idProducto: number;
  nombreProducto: string;
  imagenProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

/* 🔥 ESTADOS CORREGIDOS (CLAVE PARA TU ERROR CANCELADO) */
export type EstadoPedido =
  | 'PENDIENTE'
  | 'CONFIRMADO'
  | 'EN_PROCESO'
  | 'LISTO'
  | 'ENTREGADO'
  | 'CANCELADO';

export interface Pedido {
  id: number;
  idUsuario: number;
  total: number;
  estado: EstadoPedido;
  fechaCreacion: string;
  detalles: PedidoDetalle[];
}

export interface Pago {
  id?: number;
  idPago?: number;
  pagoId?: number;
  pedidoId?: number;
  monto: number;
  metodo: 'EFECTIVO' | 'YAPE';
  estado: 'PENDIENTE' | 'PAGADO' | 'PROCESANDO' | 'CONFIRMADO' | 'RECHAZADO';
  fecha: string;
}

export interface PagoRequest {
  pedidoId: number;
  monto: number;
  metodo: 'EFECTIVO' | 'YAPE';
}

export interface CrearPedidoRequest {
  detalles: {
    idProducto: number;
    cantidad: number;
  }[];
}

export interface DashboardPowerBI {
  ventasTotales: number;
  totalPedidos: number;
  clientesActivos: number;
  productosActivos: number;

  ventasHoy: number;
  ventasSemana: number;
  ventasMes: number;
  ventasAnio: number;

  pedidosHoy: number;

  productoMasVendido: string;
  productoMenosVendido: string;

  pagosYape: number;
  pagosEfectivo: number;
}
