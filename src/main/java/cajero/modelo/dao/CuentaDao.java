package cajero.modelo.dao;

import java.util.List;

import cajero.modelo.entity.Cuenta;

public interface CuentaDao {
	
	int modificarCuenta (Cuenta cuenta);
	Cuenta findById (int idCuenta);
	List<Cuenta> findAll();

}
