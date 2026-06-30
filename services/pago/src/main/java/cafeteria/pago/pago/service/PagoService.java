package cafeteria.pago.pago.service;


import cafeteria.pago.pago.dto.PagoDTO;

import java.util.List;

public interface PagoService {

    PagoDTO crear(PagoDTO dto);

    List<PagoDTO> listar();

    PagoDTO obtenerPorId(Long id);

    void eliminar(Long id);
}