package cajero.modelo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cajero.modelo.entity.Cuenta;
import cajero.repository.CuentaRepository;

@Repository
public class CuentaDaoImplMy8Jpa implements CuentaDao{
	
	@Autowired
	private CuentaRepository cuentaRepository;

	@Override
	public int modificarCuenta(Cuenta cuenta) {
		if(findById(cuenta.getIdCuenta()) != null) {
			cuentaRepository.save(cuenta);
			return 1;
		}
		return 0;
	}

	@Override
	public Cuenta findById(int idCuenta) {
		return cuentaRepository.findById(idCuenta).orElse(null);
	}

	@Override
	public List<Cuenta> findAll() {
		return cuentaRepository.findAll();
	}
	
	
	

}
