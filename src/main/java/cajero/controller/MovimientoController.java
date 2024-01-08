package cajero.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cajero.modelo.dao.CuentaDao;
import cajero.modelo.dao.MovimientoDao;
import cajero.modelo.entity.Cuenta;
import cajero.modelo.entity.Movimiento;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping ("/cuenta")
public class MovimientoController {
	
	@Autowired
	private CuentaDao cuentaDao;
	
	@Autowired 
	private MovimientoDao movimientoDao;
	
	/**
	 * Enlazamos a la vista del formulario ingresar.
	 * @return ingresar
	 */
	@GetMapping("/ingresar")
	public String irIngresarCuenta() {
		
		return "ingresar";
	}
	
	/**
	 * Recibimos los siguientes parámetros
	 * @param RedirectAttributes para guardarlo hasta dos pasos y poder dar luego el mensaje del ingreso en la home
	 * @param HttpSession para seguir utilizando nuestra usuario sesison y a partir de ahí meterselo al objeto cuenta
	 * @param RequestParam cantida para recibir el parámaetro cantidad y agregarselo al saldo de la cuenta y a la cantidad del movimiento.
	 * A partir de aquí la cuenta seguirá con la sesión, que será modificada con el nuevo salod. Mientras el nuevo objeto creado Movimiento
	 * será creado a mano donde se le introducirá la cuenta de la sesión, la cantidad recibida, el date con la fecha y hora del momento
	 * y el tipo de operación. Una vez construido a través del método insertar, insertaremos el nuevo movimiento en la BDD
	 * @return nos llevará a la home con el nuevo saldo de la cuenta gracias al ingreso realizado.
	 */
	@PostMapping("/ingresar")
	public String ingresarACeunta(RedirectAttributes ratt, HttpSession sesion, @RequestParam double cantidad) {
		Cuenta cuenta = (Cuenta)sesion.getAttribute("cuenta");
		Movimiento movimiento = new Movimiento();
		movimiento.setCantidad(cantidad);
		cuenta.setSaldo(cuenta.getSaldo()+movimiento.getCantidad());
		cuentaDao.modificarCuenta(cuenta);
		movimiento.setCuenta(cuenta);
		movimiento.setFecha(new Date());
		movimiento.setOperacion("Ingreso a cuenta");
		movimientoDao.insertMovimiento(movimiento);
		ratt.addFlashAttribute("mensaje", "Ingreso por importe de "+ movimiento.getCantidad() );
		return "redirect:/home";
	}
	/**
	 * Nos lleva a la vista con el formulario de extraer dinero
	 * @return extraer.
	 */
	
	@GetMapping ("/extraer")
	public String irExtraer () {
		return "extraer";
	}
	
	/**
	 * Recibimos los siguiente parámtros a través del postmapping
	 * @param RedirectAttributes para guardarlo y utilizarlo posteriores vistas.
	 * @param HttpSession para utilizar la sesión iniciada de la cuenta que tenemos y que sera introudicda en el objeto cuenta
	 * @param RequestParam cantidad para crear el nuevo saldo al restarle esta cantida al anterior y anotarlo en el nuevo Movimeinto.
	 * En la cuenta introducimos la cuenta con la que tenemos sesión y creamos un nuevo objeto movimiento al que pasaremos la cantidad.
	 * UNa vez hecho esto comprobamos sí el saldo actual de la cuenta es suficiente al restarle la cantida extraida. si es así, actualizamos 
	 * la cuenta en la BBDD a través del método modificarCuenta con su nuevo saldo, y creamos el nuevo movimiento con la cuenta que tenemos por 
	 * sesión, su cantidad, fecha y hora y tipo de procedimiento. Una vez hecho esto se crea en la BBDD el nuevo movimiento con el método insertMovimiento.
	 * @return home si se puede realizar la extracción o si no es posible se volvera a la página extraer con el mensa de que no se ha podido realizar.
	 */
	
	@PostMapping ("/extraer")
	public String extraerACuenta (RedirectAttributes ratt, HttpSession sesion, @RequestParam double cantidad) {
		Cuenta cuenta = (Cuenta)sesion.getAttribute("cuenta");
		Movimiento movimiento = new Movimiento();
		movimiento.setCantidad(cantidad);
		if ((cuenta.getSaldo()-movimiento.getCantidad())>0) {
			cuenta.setSaldo(cuenta.getSaldo()-movimiento.getCantidad());
			cuentaDao.modificarCuenta(cuenta);
			movimiento.setCuenta(cuenta);
			movimiento.setFecha(new Date());
			movimiento.setOperacion("Extraer de cuenta");
			movimientoDao.insertMovimiento(movimiento);
			ratt.addFlashAttribute("mensaje", "Extracción por importe de "+movimiento.getCantidad());
			return "redirect:/home";
		}else {
			ratt.addFlashAttribute("mensaje", "No se ha podido realizar la retirada. El dinero solicitado es menor al restante de la cuenta");
			return "redirect:/cuenta/extraer";
		}
	}
	
	/**
	 * Recibimos
	 * @param idCuenta para saber de que cuenta se quieren consultar los movimientos
	 * @param model para poder guardarlo y ser utilizado en la nueva vista
	 * Para ello en una nueva vista recibiremos todos los movimeintos de una cuenta determinada, gracias al
	 * método movimientoCuenta, que al recibir el idCuenta nos muestra todos ellos
	 * @return verMovimientos, la vista donde aparecen.
	 */
	@GetMapping("/movimientos/{id}")
	public String verMovimientos(@PathVariable("id") int idCuenta, Model model) {
		model.addAttribute("movimiento", movimientoDao.movimientoCuenta(idCuenta));
		return "verMovimientos";
	}
	
	/**
	 * Método para ir a la vista transferencia
	 * @return tansferencia
	 */
	@GetMapping("/transferencia")
	public String irTransferencia() {
		return "transferencia";
	}
	
	/**
	 * A través de este método de postmapping, recibimos los siguientes parámetros
	 * @param RedirectAttributes para guardarlo y poder, o bien mostrar los errores o ir a la home 
	 * una vez realizada la transferencia
	 * @param HttpSession para utilizar la sesion iniciada de la cuenta que realizará la transferencia.
	 * @param RequestParam cantidad que será transferida, restada del saldo de la cuenta de la cuenta que transfiere
	 * y sumada a la cuenta que recibe el importe, además de crear movimeintos en ambas cuentas.
	 * @param RequestParam idCuenta de la cuenta que recibirá la cantida y será sumada a sus saldo.
	 * Para ello primero comprobamos que la cuenta a la que se quiere realizar la transferencia existe.
	 * Una vez hecho esto se crea el objeto cuentaTransf que serás la cuenta que transfiere el diner y que recibe
	 * la sesión de la cuenta iniciada y por otro lado creamos la cuentaRecibe a partir del paramete idCuenta,
	 * que será la cuenta que recibe la contraseña
	 * Una vez hecho esto comprobamos que la cuenta que realiza la transferencia, no es la misma que la que lo recibirá.
	 * Por último, comprobamos que la cuenta que realiza la transferencia dispone del suficiente saldo para realizarla.
	 * Si todo es positivo, ambas cueuntas serás actualizadas en las BBDD con sus nuevos saldos. Así mismo se incorporaran 
	 * dos nuevos movimeintos, cada uno en la cuenta correspondiente.
	 * @return home, si todo es positivo o a la vista transferencia si falla alguno de los pasos, además de acompñarlo el mensaje correspondiente 
	 * al error que provoca.
	 */
	@PostMapping("/transferencia")
	public String hacerTransferencia(RedirectAttributes ratt,HttpSession sesion,@RequestParam double cantidad, @RequestParam int idCuenta) {
		if (cuentaDao.findById(idCuenta)!= null) {
			Cuenta cuentaTransf = (Cuenta)sesion.getAttribute("cuenta");
			Cuenta cuentaRecibe = cuentaDao.findById(idCuenta);
			if (cuentaTransf.getIdCuenta() != cuentaRecibe.getIdCuenta()) {
				Movimiento movimientoTransf = new Movimiento();
				movimientoTransf.setCantidad(cantidad);
				if((cuentaTransf.getSaldo()-movimientoTransf.getCantidad())>0) {
					cuentaTransf.setSaldo(cuentaTransf.getSaldo()-movimientoTransf.getCantidad());
					cuentaDao.modificarCuenta(cuentaTransf);
					cuentaDao.modificarCuenta(cuentaRecibe);
					movimientoTransf.setCuenta(cuentaTransf);
					movimientoTransf.setFecha(new Date());
					movimientoTransf.setOperacion("Transferencia Realizada");
					Movimiento movimientoRecibido = new Movimiento();
					movimientoRecibido.setCuenta(cuentaRecibe);
					movimientoRecibido.setCantidad(cantidad);
					movimientoRecibido.setFecha(new Date());
					movimientoRecibido.setOperacion("Transferencia Recibida");
					movimientoDao.insertMovimiento(movimientoTransf);
					movimientoDao.insertMovimiento(movimientoRecibido);
					ratt.addFlashAttribute("mensaje", "Transferencia realizada");
					return "redirect:/home";
				}else {
					ratt.addFlashAttribute("mensaje", "El dinero solicitado es menor al restante de la cuenta");
					return "redirect:/cuenta/transferencia";
				}
			}else {
				ratt.addFlashAttribute("mensaje", "No se puede realizar una transferencia a la misma cuenta de origen");
				return "redirect:/cuenta/transferencia";
			}
		}else {
			ratt.addFlashAttribute("mensaje", "La cuenta no existe");
			return "redirect:/cuenta/transferencia";
		}
	}
}
