package cajero.modelo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cajero.modelo.entity.Movimiento;
import cajero.repository.MovimientoRepository;

@Repository
public class MovimientoDaoImplMy8Jpa implements MovimientoDao {
	
	@Autowired
	private MovimientoRepository movimientoRepository;

	@Override
	public Movimiento insertMovimiento(Movimiento movimiento) {
		try {
			return movimientoRepository.save(movimiento);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Movimiento> movimientoCuenta(int idCuenta) {
		return movimientoRepository.movimientoCuenta(idCuenta);
	}

}
