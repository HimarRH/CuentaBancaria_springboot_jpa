package cajero.modelo.dao;

import java.util.List;

import cajero.modelo.entity.Movimiento;

public interface MovimientoDao {
	
	Movimiento insertMovimiento (Movimiento movimiento);
	List<Movimiento> movimientoCuenta(int idCuenta);

}
