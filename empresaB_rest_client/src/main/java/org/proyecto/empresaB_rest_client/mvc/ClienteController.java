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

	
	protected static Logger logger = Logger.getLogger("*en Cliente_B_BController*");
	
	
	
	
	@RequestMapping(method = RequestMethod.GET, params="new")
	public ModelAndView addContact() {
		logger.info("metodo get --new-- ");
		return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()  );

	  }
	
	
	//enviamos un nuevo cliente al servidor desde aplicacion cliente
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addCliente_B_form(@Valid @ModelAttribute("cliente_b")Cliente_B cliente_b, BindingResult  result)throws Exception {

		
		logger.info("inicio de addCliente_B_form en aplicacion cliente@@@@@@@");
		
		
		//aaaaaPARA CAMBIAR MAS TARDE CON CONSULTA A SERVIDOR
		if (null !=cliente_BServiceImpl.findByCliente_B_login_usuario_b(cliente_b.getLogin_usuario_b()))
		result.addError(new ObjectError("loginInvalido", "Este usuario ya existe"));
		
		
		
		
		//	result.addError(new ObjectError(result.getObjectName(), "este usuario ya existe!"));
		if(result.hasErrors()) {
		logger.info("addCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
	
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}
	
		// Preparamos acceptable media type
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
		
		logger.info("tama�o lista en listado: "+lista.size());
		

	   return new ModelAndView("cliente_b/listaClientes","clientes", lista);
	}
	*/
	
	
	@RequestMapping(value="/admin/listado",method=RequestMethod.GET)
	public ModelAndView listadoClientes_B(){
		logger.info("en listadoClientes_B2 en client*");
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Cliente_B> entity = new HttpEntity<Cliente_B>(headers);
		
		// Send the request as GET
		ModelAndView mav=new ModelAndView("cliente_b/listaClientes");
		try {
			ResponseEntity<ListaClientes_B> result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/clientes", HttpMethod.GET, entity, ListaClientes_B.class);
			logger.info("en listadoClien rult tama�o result: "+result.getBody().getDataCliente().size());	
			mav.addObject("clientes", result.getBody().getDataCliente());
			
					
			} catch (Exception e) {
					logger.error(e);
			}

		return mav;
	}
	
	
	
	@RequestMapping(value="/cliente/edit",method=RequestMethod.GET)
	public ModelAndView editCliente_B_form(String id){


		logger.info("id cliente pasado a edit-modificar: "+id);
		Cliente_B cliente_b= new Cliente_B();
		cliente_b= cliente_BServiceImpl.findByCliente_BIdCliente_b(id);
					
		logger.info("cliente pasado a edit-modificar: "+cliente_b.getNombre_b());
		
		
		//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		//return new ModelAndView("producto_b/listaProductos","productos", lista);
		return new ModelAndView("cliente_b/modificar", "cliente_b",cliente_b);
	
}
	@RequestMapping(value="/cliente/modificarCliente_B", method = RequestMethod.POST)
	public ModelAndView modCliente_B_form(@Valid @ModelAttribute("cliente_b")Cliente_B cliente_b, BindingResult  result) throws Exception{

		
		logger.info("inicio de modCliente_B_form");
		Usuario_B usuarioBuscado=cliente_BServiceImpl.findByCliente_B_login_usuario_b(cliente_b.getLogin_usuario_b());
		Integer idusuarioBuscado=null;
		if (null!=usuarioBuscado){
		idusuarioBuscado=usuarioBuscado.getIdusuarios_b();
		}
		Integer idcliente_b=cliente_b.getIdusuarios_b();

				
		logger.info("inicio de modCliente_B_form idusuarioBuscado "+idusuarioBuscado);
		logger.info("inicio de modCliente_B_form idcliente_b "+idcliente_b);
		
		
		logger.info("inicio de modCliente_B_form id usuarioBuscado "+usuarioBuscado.getIdusuarios_b());
		logger.info("inicio de modCliente_B_form id cliente_B "+cliente_b.getIdusuarios_b());
		if ((null !=usuarioBuscado)&& (idusuarioBuscado==idcliente_b)){
			result.addError(new ObjectError("loginInvalido", "Este usuario ya existe"));
			logger.info("null !=usuarioBuscado"+(null !=usuarioBuscado));
			logger.info("((usuarioBuscado.getIdusuarios_b())!=(cliente_b.getIdusuarios_b()))"+((usuarioBuscado.getIdusuarios_b())!=(cliente_b.getIdusuarios_b())));
			
		}
		if(result.hasErrors()) {
		logger.info("modCliente_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("cliente_b/edit", "cliente_b",new Cliente_B()).addAllObjects(result.getModel());

		}
	

	/*		CODIGO DE CUANDO NO MOSTRABA �ss
		logger.info("modificarProducto_B_form ------NO tiene errores----");
		logger.info("nombre producto a a�adir "+ producto_b.getNombre_productoB());
		//productos_BServiceImpl.save(producto_b);
		logger.info("modificarProducto_B_form ");
		String nombre =producto_b.getNombre_productoB();
		//String nombre =new String();
		try {
		logger.info("el nombre insertado en try antes de cambio: "+nombre);
		nombre =new String (producto_b.getNombre_productoB().getBytes("ISO-8859-1"),"UTF-8");
		//nombre =new String (producto_b.getNombre_productoB().getBytes("UTF-8"),"ISO-8859-1");
		//nombre =new String (nombre1.getBytes("ISO-8859-1"),"UTF-8");
		
		logger.info("el nombre insertado en try despue de cambio: "+nombre);
		} catch(UnsupportedEncodingException uee) {
		    uee.printStackTrace();
		}
		
		
		logger.info("el nombre modificado-update fuera try: "+nombre);
		producto_b.setNombre_productoB(nombre);
		
		*/

		logger.info("modCliente_B_form ");
		cliente_b.setFecha_alta_b(new Date());
		cliente_b.setAUTHORITY("ROLE_CLIENTE");
		cliente_b.setENABLED(true);
		cliente_BServiceImpl.update(cliente_b);

		//return new ModelAndView("redirect:../admin/listado");
		return new ModelAndView("redirect:../../productos/listado");
	
	
	}
	
	
	@RequestMapping(value="/cliente/modificarMiCuenta_B/", method = RequestMethod.GET)
	public ModelAndView modMiCuenta_B_form(@RequestParam(value="login")String  login) throws Exception{
		
		
		Integer id=cliente_BServiceImpl.findByCliente_B_login_usuario_b(login).getIdusuarios_b();
		
		//http://localhost:8080/empresaB/clientes/cliente/edit?id=id

		logger.info(" en modMiCuenta_B_form " +String.valueOf(id));

		return new ModelAndView("redirect:../edit?id="+String.valueOf(id));
		//return new ModelAndView("redirect:../edit?"+id);
	
	
	}
	
	
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
	

}
