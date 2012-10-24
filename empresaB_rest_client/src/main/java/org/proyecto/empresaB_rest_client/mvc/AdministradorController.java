package org.proyecto.empresaB_rest_client.mvc;


import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.proyecto.empresaB_rest_client.model.Administrador_B;
import org.proyecto.empresaB_rest_client.model.ListaAdministradores_B;
import org.proyecto.empresaB_rest_client.model.Usuario_B;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;




@Controller
@RequestMapping("/administradores")
public class AdministradorController {
	

	
	
	@Autowired
	ServletContext context;
	
	
	private RestTemplate restTemplate = new RestTemplate();

	
	protected static Logger logger = Logger.getLogger("*en Administrador_B_BController en cliente @@@@@@*");
	
	
	
	
	@RequestMapping(method = RequestMethod.GET, params="new")
	public ModelAndView addContact() {
		logger.info("metodo get --new-- ");
		return new ModelAndView("administrador_b/edit", "administrador_b",new Administrador_B()  );

	  }
	
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addAdministrador_B_form(@Valid @ModelAttribute("administrador_b")Administrador_B administrador_b, BindingResult  result)throws Exception {

		
		logger.info("inicio de addAdministrador_B_form n cliente @@@@@@ ");
		if(result.hasErrors()) {
		logger.info("addAdministrador_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("administrador_b/edit", "administrador_b",new Administrador_B()).addAllObjects(result.getModel());

		}
	
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Administrador_B> entity = new HttpEntity<Administrador_B>(administrador_b,headers);

		//enviamos el resquest como POST
		
		try {
			//ResponseEntity<Cliente_B> clienteDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores/administrador",
							HttpMethod.POST, entity, Administrador_B.class);
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}

		return new ModelAndView("redirect:listado");
}
	
	
	
	

	
	
	@RequestMapping(value="/listado",method=RequestMethod.GET)
	public ModelAndView listadoAdministradores_B(){
		logger.info("en listadoAdministradores_B en Cliente @@@@@@*");
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Administrador_B> entity = new HttpEntity<Administrador_B>(headers);
		
		// enviamos el request como GET
		ModelAndView mav=new ModelAndView("administrador_b/listaAdministradores");
		try {
			//realizamos consulta a servidor para que nos envie todos los administradores
			ResponseEntity<ListaAdministradores_B> result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores", HttpMethod.GET, entity, ListaAdministradores_B.class);
				
			mav.addObject("administradores", result.getBody().getDataAdministrador());
			
					
			} catch (Exception e) {
					logger.error(e);
			}

		return mav;
	}
	
	

	
	
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView editAdministrador_B_form(String id){


	
		Administrador_B administrador_b= new Administrador_B();
		
		//obtenemos el administrador correspondiente a ese id
		// Preparamos acceptable media type
						List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
						acceptableMediaTypes.add(MediaType.APPLICATION_XML);
						
						// preparamos el header
						HttpHeaders headers = new HttpHeaders();
						headers.setAccept(acceptableMediaTypes);
						HttpEntity<Administrador_B> entity = new HttpEntity<Administrador_B>(headers);

						//enviamos el resquest como POST
						ResponseEntity<Administrador_B> result=null;
						
						try {
							
							 result=
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores/administrador/{id}",
											HttpMethod.GET, entity, Administrador_B.class,id);
					
							
									
							} catch (Exception e) {
									logger.error(e);
							} 

		
						administrador_b= result.getBody();
		
		
		
		
		
		
		
		
		//administrador_b= administrador_BServiceImpl.findByAdministrador_BIdAdministrador_b(id);
					
		logger.info("Administrador  editAdministrador_B_form  en Cliente @@@@@@*");
		
		
		//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		//return new ModelAndView("producto_b/listaProductos","productos", lista);
		return new ModelAndView("administrador_b/modificar", "administrador_b",administrador_b);
	
}

	
	
	@RequestMapping(value="/modificarAdministrador_B", method = RequestMethod.POST)
	public ModelAndView modAdministrador_B_form(@Valid @ModelAttribute("administrador_b")Administrador_B administrador_b, BindingResult  result) throws Exception{
		
		if(result.hasErrors()) {
		logger.info("modAdministrador_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("administrador_b/edit", "administrador_b",new Administrador_B()).addAllObjects(result.getModel());

		}
		
		logger.info("inicio de modCliente_B_form en cliente@@@@@@@");
		//comprobamos que no exista este login
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Administrador_B> entity = new HttpEntity<Administrador_B>(headers);

		//enviamos el resquest como GET
		ResponseEntity<Administrador_B> resultado=null;
		
		try {
			
			 resultado=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores/administradorLogin/{login}",
							HttpMethod.GET, entity, Administrador_B.class,administrador_b.getLogin_usuario_b());
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}
			
		//buscamos un usuario por el login enviado
		Usuario_B usuarioBuscado=resultado.getBody();
	
		
		
		Integer idusuarioBuscado=null;
		if (null!=usuarioBuscado){
		idusuarioBuscado=usuarioBuscado.getIdusuarios_b();
		}
		Integer idadministrador_b=administrador_b.getIdusuarios_b();
		
	
		//if (null !=usuarioBuscado){
			
		if ((null !=usuarioBuscado)&& (idusuarioBuscado==idadministrador_b)){
			result.addError(new ObjectError("loginInvalido", "Este usuario ya existe"));
			logger.info("null !=usuarioBuscado"+(null !=usuarioBuscado));

			
		}
		
		
		logger.info("inicio de modAdministrador_B_form");
		if(result.hasErrors()) {
		logger.info("modAdministrador_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("administrador_b/edit", "administrador_b",new Administrador_B()).addAllObjects(result.getModel());

		}
	

	
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes2 = new ArrayList<MediaType>();
		acceptableMediaTypes2.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setAccept(acceptableMediaTypes2);
		HttpEntity<Administrador_B> entity2 = new HttpEntity<Administrador_B>(administrador_b, headers2);

		//enviamos el resquest como PUT

		
		try {
			
		
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores/administrador/{id}",
							HttpMethod.PUT, entity2, Administrador_B.class,administrador_b.getIdusuarios_b());
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		
		
		
		
		

		return new ModelAndView("redirect:listado");
		
	
	
	}
	
	
	

	
	
	@RequestMapping(value="/borrar",method=RequestMethod.GET)
	public ModelAndView delAdministrador_B_form(String id){

		logger.info("en borrarADMINISTRADOR con ide con id @@@@@@ : "+id);		
		
		//obtenemos el cliente correspondiente a ese id		
		
		
		
		// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Administrador_B> entity = new HttpEntity<Administrador_B>(headers);

				//enviamos el resquest como POST
				
				//ResponseEntity<Cliente_B> result=null;
				
				try {
					
					// result=
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/administradores/administrador/{id}",
									HttpMethod.DELETE, entity, Administrador_B.class,id);
							
			
					
							
					} catch (Exception e) {
							logger.error(e);
					} 
			
		return new ModelAndView("redirect:listado");

}
	

	

}