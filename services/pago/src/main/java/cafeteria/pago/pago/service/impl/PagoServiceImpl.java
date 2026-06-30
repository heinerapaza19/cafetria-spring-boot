package cafeteria.pago.pago.service.impl;


import cafeteria.pago.pago.dto.PagoDTO;
import cafeteria.pago.pago.entity.Pago;
import cafeteria.pago.pago.mapper.PagoMapper;
import cafeteria.pago.pago.repository.PagoRepository;
import cafeteria.pago.pago.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;
    private final RestTemplate restTemplate;

    public PagoServiceImpl(PagoRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public PagoDTO crear(PagoDTO dto) {

        validar(dto);

        Pago pago = PagoMapper.toEntity(dto);
        Pago guardado = repository.save(pago);

        // conectar con pedido-service
      //  cambiarEstadoPedido(dto.getPedidoId());

        return PagoMapper.toDTO(guardado);
    }

    @Override
    public List<PagoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(PagoMapper::toDTO)
                .toList();
    }

    @Override
    public PagoDTO obtenerPorId(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return PagoMapper.toDTO(pago);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // ---------------- PRO LOGIC ----------------

    private void validar(PagoDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().doubleValue() <= 0) {
            throw new RuntimeException("Monto inválido");
        }

        if (!dto.getMetodo().equalsIgnoreCase("EFECTIVO")
                && !dto.getMetodo().equalsIgnoreCase("YAPE")) {
            throw new RuntimeException("Método inválido");
        }
    }

    //private void cambiarEstadoPedido(Long pedidoId) {
        //String url = "http://localhost:8081/pedido/" + pedidoId + "/pagar";
      //  restTemplate.put(url, null);
   // }
}