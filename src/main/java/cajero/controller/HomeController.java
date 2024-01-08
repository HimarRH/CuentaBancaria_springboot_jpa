package cajero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cajero.modelo.dao.CuentaDao;
import cajero.modelo.entity.Cuenta;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private CuentaDao cuentaDao;
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	/**
	 * Enlazamos con la vista de login de la cuenta. Se podrá llegar a ella a traves de diferentes paths.
	 * @return login.
	 */
	@GetMapping({"","/","/login"})
	public String accesoCuenta() {
		
		return "login";
	}
	
	/**
	 * Este método del postMapping recibe los siguientes parámetros.
	 * @param RedirectAttributes para mantener la información guardada
	 * @param HttpSession para generar una nueva sesión asocida a una determinada cuenta
	 * @param idCuenta para identificar la cuenta con la que queremos iniciar sesión.
	 * Con la idCuenta recibida por parámetro creamos un objeto cuenta gracias al método findById.
	 * Tras esto, comprobamos su esa cuenta existe, si es así, a través del méotodp propio de HttpSession 
	 * llamado setAttribute generamos la nueva sesión cuenta que mantendremos mientras no cerremos sesión
	 * @return home si la cuenta existe o volveremos al login si la cuenta no existe, acompañado del mensaje cuenta no encontrada.
	 */
	@PostMapping("/login")
	public String procAcceso(RedirectAttributes ratt,  HttpSession sesion, @RequestParam int idCuenta) {
		
		Cuenta cuenta = cuentaDao.findById(idCuenta);
		if(cuenta !=null ) {
			sesion.setAttribute("cuenta", cuenta);
			return "redirect:/home";
		}
		ratt.addFlashAttribute("mensaje", "Cuenta no encontrada");
		return "redirect:/login";
	}
	
	/**
	 * Este método de getmapping lo utilizaremos para cerrar la sesión. Recibimos
	 * @param HttpSession con la sesion que queremos cerrar.
	 * Para ello nos valemos de dos metrodos propios, rel removeatributte pasandele la sesión de la cuenta que
	 * queremos sea borrada y, por otro lado, invalidate para invalidar el objeto de la sesión.
	 * @return login.
	 */
	@GetMapping("/logout")
	public String logout(HttpSession sesion) {
		sesion.removeAttribute("cuenta");
		sesion.invalidate();
		return "login";
	}


}
