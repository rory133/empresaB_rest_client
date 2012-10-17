package org.proyecto.empresaB_rest_client.mvc;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;
import org.proyecto.empresaB_rest_client.model.Cliente_B;
import org.proyecto.empresaB_rest_client.model.ListaClientes_B;
import org.proyecto.empresaB_rest_client.model.Producto_B;
import org.proyecto.empresaB_rest_client.model.Usuario_B;
import org.proyecto.empresaB_rest_client.service.impl.Cliente_BServiceImpl;
import org.proyecto.empresaB_rest_client.service.impl.Productos_BServiceImpl;
import org.proyecto.empresaB_rest_client.util.ListaProvincias;
import org.proyecto.empresaB_rest_client.util.Mail;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;




@Controller
@RequestMapping("/clientes")
public class ClienteController {
	
	@Autowired
	private Cliente_BServiceImpl cliente_BServiceImpl;
	
	@Autowired
	private Productos_BServiceImpl productos_BServiceImpl;
	
	
	@Autowired
	ServletContext context;
	
	@Autowired
	private Mail mail;
	
	private RestTemplate restTemplate = new RestTemplate();

	
	protected static Logger logger = Logger.getLogger("*en Cliente_B_BController*cliente@@@@@@@");
	
	
	
	
	@RequestMapping(method = RequestMethod.GET, params="new")
	public ModelAndView addContact() {
		logger.info("metodo get --new  addContact--cliente@@@@@@@ ");
		return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()  );

	  }
	
	
	//enviamos un nuevo cliente al servidor desde aplicacion cliente	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addCliente_B_form(@Valid @ModelAttribute("cliente_b")Cliente_B cliente_b, BindingResult  result)throws Exception {

		
		logger.info("inicio de addCliente_B_form en aplicacion cliente@@@@@@@");
		
		if(result.hasErrors()) {
		logger.info("addCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
	
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}

		
		//comprobamos que no exista este login
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes2 = new ArrayList<MediaType>();
		acceptableMediaTypes2.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setAccept(acceptableMediaTypes2);
		HttpEntity<Cliente_B> entity2 = new HttpEntity<Cliente_B>(headers2);

		//enviamos el resquest como POST
		ResponseEntity<Cliente_B> resultado=null;
		
		try {
			
			 resultado=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/clienteLogin/{login}",
							HttpMethod.GET, entity2, Cliente_B.class,cliente_b.getLogin_usuario_b());
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}
			
		//buscamos un usuario por el login enviado
		Usuario_B usuarioBuscado=resultado.getBody();
		

		
		
		
		
		
		
		if (null !=usuarioBuscado)
		//if (null !=cliente_BServiceImpl.findByCliente_B_login_usuario_b(cliente_b.getLogin_usuario_b()))
		result.addError(new ObjectError("loginInvalido", "Este usuario ya existe"));
		
		
		
		
		//	result.addError(new ObjectError(result.getObjectName(), "este usuario ya existe!"));
		if(result.hasErrors()) {
		logger.info("addCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
	
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}
	
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(cliente_b,headers);

		//enviamos el resquest como POST
		
		try {
			//ResponseEntity<Cliente_B> clienteDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/cliente",
							HttpMethod.POST, entity, Cliente_B.class);
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		
		
		//devolvemos pagina inicial
		List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		return new ModelAndView("producto_b/listaProductos","productos", lista);
		
}
	
	
	
	
	
	/*
	@RequestMapping(value="/admin/listado",method=RequestMethod.GET)
	public ModelAndView listadoClientes_B(){
		List<Cliente_B> lista =cliente_BServiceImpl.findAll();
		logger.info("en listadoProductos_B2*");
		
		logger.info("tamaño lista en listado: "+lista.size());
		

	   return new ModelAndView("cliente_b/listaClientes","clientes", lista);
	}
	*/
	
	//---listamos todos los clientes----
	@RequestMapping(value="/admin/listado",method=RequestMethod.GET)
	public ModelAndView listadoClientes_B(){
		logger.info("en listadoClientes_B2 en client en Cliente @@@@@@*");
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);
		
		// enviamos el request como GET
		ModelAndView mav=new ModelAndView("cliente_b/listaClientes");
		try {
			//realizamos consulta a servidor para que nos envie todos los clientes
			ResponseEntity<ListaClientes_B> result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes", HttpMethod.GET, entity, ListaClientes_B.class);
			logger.info("en listadoClien rult tamaño result: "+result.getBody().getDataCliente().size());	
			mav.addObject("clientes", result.getBody().getDataCliente());
			
					
			} catch (Exception e) {
					logger.error(e);
			}

		return mav;
	}
	
	
	
	@RequestMapping(value="/cliente/edit",method=RequestMethod.GET)
	public ModelAndView editCliente_B_form(String id){


		logger.info("id cliente pasado a edit-modificar en Cliente @@@@@@ : "+id);
		
		Cliente_B cliente_b= new Cliente_B();
		
		//obtenemos el cliente correspondiente a ese id
		// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);

				//enviamos el resquest como POST
				ResponseEntity<Cliente_B> result=null;
				
				try {
					
					 result=
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/cliente/{id}",
									HttpMethod.GET, entity, Cliente_B.class,id);
			
					
							
					} catch (Exception e) {
							logger.error(e);
					} 

		
	//	return new ModelAndView("cliente_b/modificar", "cliente_b",result.getBody().getIdusuarios_b());
				return new ModelAndView("cliente_b/modificar", "cliente_b",result.getBody());
	
}
	@RequestMapping(value="/cliente/modificarCliente_B", method = RequestMethod.POST)
	public ModelAndView modCliente_B_form(@Valid @ModelAttribute("cliente_b")Cliente_B cliente_b, BindingResult  result) throws Exception{

		if(result.hasErrors()) {
		logger.info("modCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}
		
		logger.info("inicio de modCliente_B_form en cliente@@@@@@@");
		//comprobamos que no exista este login
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);

		//enviamos el resquest como GET
		ResponseEntity<Cliente_B> resultado=null;
		
		try {
			
			 resultado=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/clienteLogin/{login}",
							HttpMethod.GET, entity, Cliente_B.class,cliente_b.getLogin_usuario_b());
	
			
					
			} catch (Exception e) {
					logger.error(e);
			}
			
		//buscamos un usuario por el login enviado
		Usuario_B usuarioBuscado=resultado.getBody();
		
		Integer idusuarioBuscado=null;
		
		if (null!=usuarioBuscado){
			//ya existia ese login asignamos si id a idusuarioBuscado
		idusuarioBuscado=usuarioBuscado.getIdusuarios_b();
		}
		//asignamos el id del usuario pasado a idcliente
		Integer idcliente_b=cliente_b.getIdusuarios_b();
		//si los dos ids coinciden y no son nulos ha usado el login de otro usuario, añadimos un error con el aviso
		if ((null !=usuarioBuscado)&& (idusuarioBuscado==idcliente_b)){
			result.addError(new ObjectError("loginInvalido", "Este usuario ya existe"));
			logger.info("null !=usuarioBuscado"+(null !=usuarioBuscado));
			logger.info("((usuarioBuscado.getIdusuarios_b())!=(cliente_b.getIdusuarios_b()))"+((usuarioBuscado.getIdusuarios_b())!=(cliente_b.getIdusuarios_b())));
			
		}
		//como hemos añadido el error se devuelve a la vista para volver a editarlo
		if(result.hasErrors()) {
		logger.info("modCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}
		//si llegamos aqui podemos actualizar el cliente.
		logger.info("modCliente_B_form ");
		cliente_b.setFecha_alta_b(new Date());
		cliente_b.setAUTHORITY("ROLE_CLIENTE");
		cliente_b.setENABLED(true);
		
		
		
		
		// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes2 = new ArrayList<MediaType>();
				acceptableMediaTypes2.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers2 = new HttpHeaders();
				headers2.setAccept(acceptableMediaTypes2);
				HttpEntity<Cliente_B> entity2 = new HttpEntity<Cliente_B>(cliente_b, headers2);

				//enviamos el resquest como PUT
		
				
				try {
					
				
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/cliente/{id}",
									HttpMethod.PUT, entity2, Cliente_B.class,cliente_b.getIdusuarios_b());
			
					
							
					} catch (Exception e) {
							logger.error(e);
					}
					
		
		//volvemos a la vista principal
		return new ModelAndView("redirect:../../productos/listado");
	
	
	}
	
	
	@RequestMapping(value="/cliente/modificarMiCuenta_B/", method = RequestMethod.GET)
	public ModelAndView modMiCuenta_B_form(@RequestParam(value="login")String  login) throws Exception{
		
		logger.info(" en cliente /cliente/modificarMiCuenta_B/@@@@@@ con login: " + login);
		
		//obtenemos el idusuario del login pasado
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);

		//enviamos el resquest como GET
		ResponseEntity<Cliente_B> result=null;
		
		try {
			
			 result=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/clienteLogin/{login}",
							HttpMethod.GET, entity,Cliente_B.class, login);
			 logger.info(" result 	restTemplate.exchange(http://localhost:8080/empresaB_rest_server/clientes/cliente/{login} result.getBody().getLogin_usuario_b() :"+result.getBody().getLogin_usuario_b());
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		String valorId=String.valueOf( result.getBody().getIdusuarios_b());
		
		return new ModelAndView("redirect:../edit?id="+valorId);
	
	}
	
	
	@RequestMapping(value="/admin/borrar",method=RequestMethod.GET)
	public ModelAndView delCliente_B_form(String id){
		
		logger.info("en borrar con ide con id @@@@@@ : "+id);		
		
		//obtenemos el cliente correspondiente a ese id		
		
		
		
		// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);

				//enviamos el resquest como POST
				
				//ResponseEntity<Cliente_B> result=null;
				
				try {
					
					// result=
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes/cliente/{id}",
									HttpMethod.DELETE, entity, Cliente_B.class,id);
							
			
					
							
					} catch (Exception e) {
							logger.error(e);
					} 
				logger.info("Despues de borrar cliente borrar con ide con id @@@@@@ : ");	
		return new ModelAndView("redirect:listado");

}
	
	
/*	
	@RequestMapping(value="/admin/borrar",method=RequestMethod.GET)
	public ModelAndView delCliente_B_form(String id){
		logger.info(" en borrrar cliente ");
		logger.info("en borrar con ide con id: "+id);
		Cliente_B cliente_b= new Cliente_B();
		cliente_b= cliente_BServiceImpl.findByCliente_BIdCliente_b(id);
		logger.info(" con cliente : "+cliente_b.getNombre_b());
		cliente_BServiceImpl.delete(cliente_b);
		
		logger.info("borrando cliente : "+cliente_b.getNombre_b());

		return new ModelAndView("redirect:listado");

}
	*/

}
