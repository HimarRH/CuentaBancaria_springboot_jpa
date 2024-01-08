package cajero.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cajero.modelo.entity.Cuenta;

public interface CuentaRepository extends JpaRepository <Cuenta, Integer> {

}
