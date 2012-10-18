package org.proyecto.empresaB_rest_client.mvc;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.proyecto.empresaB_rest_client.bo.Producto_BBo;
import org.proyecto.empresaB_rest_client.bo.impl.Producto_BBoImpl;
import org.proyecto.empresaB_rest_client.exception.GenericException;
import org.proyecto.empresaB_rest_client.model.Cliente_B;
import org.proyecto.empresaB_rest_client.model.ListaClientes_B;
import org.proyecto.empresaB_rest_client.model.ListaProductos_B;
import org.proyecto.empresaB_rest_client.model.Producto_B;
import org.proyecto.empresaB_rest_client.service.Productos_BService;
import org.proyecto.empresaB_rest_client.service.impl.Productos_BServiceImpl;





@Controller
@RequestMapping("/productos")
public class Producto_BController {

	@Autowired
	private Productos_BServiceImpl productos_BServiceImpl;
	

	
	

	
	@Autowired
	ServletContext context;
	
	
	private RestTemplate restTemplate = new RestTemplate();
	

	
	protected static Logger logger = Logger.getLogger("*en Producto_BController*");
		
	
	
	//pedimo al servidor el listado de todos los productos
	@RequestMapping(value="/listado",method=RequestMethod.GET)
	public ModelAndView listadoProductos_B(){
		
		
		
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<ListaProductos_B> entity = new HttpEntity<ListaProductos_B>(headers);
		
		// enviamos el request como GET
		ModelAndView mav=new ModelAndView("producto_b/listaProductos");
		try {
			//realizamos consulta a servidor para que nos envie todos los clientes
			ResponseEntity<ListaProductos_B> result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity, ListaProductos_B.class);
		
			mav.addObject("productos", result.getBody().getDataProducto());
			
					
			} catch (Exception e) {
					logger.error(e);
			}

		return mav;
	}
		

	
	
	
	
	/*
	@RequestMapping(value="/listado",method=RequestMethod.GET)
	public ModelAndView listadoProductos_B(){
		List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		logger.info("en listadoProductos_B2*");
		
		logger.info("tama�o lista en listado: "+lista.size());
		

	   return new ModelAndView("producto_b/listaProductos","productos", lista);
	}
	
	*/
	
	
	
	
	
/*	
	
	@RequestMapping(value="/listado2",method=RequestMethod.GET)
	public ModelAndView listado2Productos_B(){
		List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		logger.info("en listadoProductos_B2*");
		
		logger.info("tama�o lista en listado: "+lista.size());
		
		ModelAndView mav= new ModelAndView("producto_b/listaProductos");
		String bienvenida1="Bienvenido a la pagina de empreaB ";
		String bienvenida2="para realizar compras deberias estar logado ";
		String bienvenida3="estos son los productos de que disponemos:";
		mav.addObject("bienvenida1", bienvenida1);
		mav.addObject("bienvenida2", bienvenida2);
		mav.addObject("bienvenida3", bienvenida3);
		mav.addObject("productos", lista);
		return mav;
	   //return new ModelAndView("producto_b/listaProductos","productos", lista);
	}
	
	*/
	
	
	
	
	//cuando un administrador pide a�adir productos le enviamos al formulario
	@RequestMapping(value="/admin/" ,method = RequestMethod.GET, params="new")
	public ModelAndView addProducto() {
		logger.info("metodo get --new-- ");
		return new ModelAndView("producto_b/edit", "producto_b",new Producto_B());
	  }
	
	
	

	//@RequestMapping(method = RequestMethod.POST)
	
	
	//public ModelAndView modProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b,  BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image, HttpServletRequest request){
	@RequestMapping(value="/admin/modificarProductoB", method = RequestMethod.POST)
	public ModelAndView modProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b,  BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image)throws Exception{

		//si tiene errores lo devolvemos a la pagina de modificar Producto_B
		if(result.hasErrors()) {
		logger.info("modificarProducto_B_form ------tiene errores----"+result.toString());
			return new ModelAndView("producto_b/modificar", "producto_b",producto_b).addAllObjects(result.getModel());
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
		if(image.isEmpty()){
			logger.info("en SIN IMAGEN ");
			//Actualizamos producto
			// ----Preparamos acceptable media type----
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_XML);
			
			// preparamos el header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(producto_b,headers);

			//enviamos el resquest como POST
			
			try {
				//ResponseEntity<Cliente_B> clienteDevuelto = 
						restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
								HttpMethod.PUT, entity, Producto_B.class);
		
				
						
				} catch (Exception e) {
						logger.error(e);
			}
		
		

		}
			
			
		try{
			if(!image.isEmpty()){
				
				//byte[] bFile = new byte[image.getBytes().length];
				logger.info("en try antes de validar imagen en modificar ");
				validarImagen (image);
				logger.info("en try despues de validar imagen en modificar ");
				saveImage(producto_b.getIdproductob()+".jpg",image);
				//Actualizamos producto
				// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(producto_b,headers);

				//enviamos el resquest como POST
				
				try {
					//ResponseEntity<Cliente_B> clienteDevuelto = 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
									HttpMethod.PUT, entity, Producto_B.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
				
				logger.info("salvando imagen "+ producto_b.getIdproductob() +"en try ");
			}
			
		}catch (Exception e){
			result.reject(e.getMessage());
		   return new ModelAndView("producto_b/modificar", "producto_b", producto_b).addAllObjects(result.getModel());
		}
		

		
		logger.info("udpdateProducto_B_form ");
		/*
		List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		return new ModelAndView("producto_b/listaProductos","productos", lista);
		*/

		return new ModelAndView("redirect:../listado");
	
	
}
	
	
	@RequestMapping(value="/admin/edit",method=RequestMethod.GET)
	public ModelAndView editProducto_B_form(String id){


		Producto_B productob=new Producto_B();
		//productob=	productos_BServiceImpl.findByProducto_BIdProducto_b(id);
		
		//obtenemos el producto correspondiente a ese id
				// Preparamos acceptable media type
						List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
						acceptableMediaTypes.add(MediaType.APPLICATION_XML);
						
						// preparamos el header
						HttpHeaders headers = new HttpHeaders();
						headers.setAccept(acceptableMediaTypes);
						HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(headers);

						//enviamos el resquest como POST
						ResponseEntity<Producto_B> result=null;
						
						try {
							
							 result=
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/producto/{id}",
											HttpMethod.GET, entity, Producto_B.class,id);
					
							
									
							} catch (Exception e) {
									logger.error(e);
							} 
		
		
						productob=result.getBody();
		
		
		logger.info("producto pasado a edit-modificar: "+productob.getNombre_productoB());

		return new ModelAndView("producto_b/modificar", "producto_b",productob);
	
}
	
	
	//@RequestMapping(value="/crearProductoB", method = RequestMethod.POST)
	
	//public ModelAndView addProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b, BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image, HttpServletRequest request){
	@RequestMapping(value="/admin/crearProductoB",method = RequestMethod.POST)
	public ModelAndView addProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b, BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image) throws Exception{


		logger.info("inicio de addProducto_B_form");
		if(result.hasErrors()) {
		logger.info("addProducto_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("producto_b/edit", "producto_b",new Producto_B()).addAllObjects(result.getModel());

		}

		/*				CODIGO DE CUANDO NO MOSTRABA LAS �s---
		logger.info("addProducto_B_form ------NO tiene errores----");
		logger.info("nombre producto a a�adir "+ producto_b.getNombre_productoB());
		//productos_BServiceImpl.save(producto_b);
		logger.info("addProducto_B_form ");
		
		
		String nombre =producto_b.getNombre_productoB();
		try {
		logger.info("el nombre insertado en try antes de cambio"+nombre);
		nombre =new String (producto_b.getNombre_productoB().getBytes("ISO-8859-1"),"UTF-8");
		
		logger.info("el nombre insertado en try despue de cambio"+nombre);
		} catch(UnsupportedEncodingException uee) {
		    uee.printStackTrace();
		}
		logger.info("el nombre insertado en add-save fuera try"+nombre);
		producto_b.setNombre_productoB(nombre);
		*/
		
		if(image.isEmpty()){
			logger.info("en SIN IMAGEN ");
			//salvamos producto
			// ----Preparamos acceptable media type----
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_XML);
			
			// preparamos el header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(producto_b,headers);

			//enviamos el resquest como POST
			
			try {
				//ResponseEntity<Cliente_B> clienteDevuelto = 
						restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
								HttpMethod.POST, entity, Producto_B.class);
		
				
						
				} catch (Exception e) {
						logger.error(e);
			}
		
		

		}
		try{
			if(!image.isEmpty()){
				//salvamos producto
				// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(producto_b,headers);
				String idProductoCreado=null;

				//enviamos el resquest como POST
				
				try {
					
					ResponseEntity<String> resultado = 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
									//HttpMethod.POST, entity, Producto_B.class);
									HttpMethod.POST, entity, String.class);
										
					
					
					idProductoCreado=resultado.getBody();
					logger.info("en CLIENTE ID DEL PRODUCTO DEVUELTO: "+resultado.getBody());
							
					} catch (Exception e) {
							logger.error(e);
				}
				
				logger.info("antes de validar imagen en addProducto_B_form");
				validarImagen (image);
				
				logger.info("salvando imagen "+ idProductoCreado +"en try ");
				
				saveImage(idProductoCreado+".jpg",image);

				
			}
			
		}catch (Exception e){
			result.reject(e.getMessage()+"aqui");
			return new ModelAndView("producto_b/edit", "producto_b",new Producto_B()).addAllObjects(result.getModel());

		}
		

		
		logger.info("addProducto_B_form ");
		

		return new ModelAndView("redirect:../listado");
		
		
	
}
	//borramos un pruducto por id
	@RequestMapping(value="/admin/borrar",method=RequestMethod.GET)
	public ModelAndView delProducto_B_form(String id){

		logger.info("en borrar producto con id: "+id);

		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(headers);


		try {
			
			// result=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/producto/{id}",
							HttpMethod.DELETE, entity, Producto_B.class,id);
					
	
			
					
			} catch (Exception e) {
					logger.error(e);
			} 
		

		

		return new ModelAndView("redirect:../listado");

	
}
	

   //private void saveImage(String filename, MultipartFile image)throws RuntimeException{
	private void saveImage(String filename, MultipartFile image)throws GenericException{
	
	try{
		
		//File file = new File(context.getRealPath("/")+"WEB-INF\\resources\\imagenes\\"+ filename);
		
		File file = new File("C:\\imagenes\\empresaB_rest_client\\"+ filename);
		logger.info("context.getRealPath(/)+/resources/imagenes/::::::::::::::::"+file);
		
		
		
		
		FileUtils.writeByteArrayToFile(file,image.getBytes());
		logger.info("salvando imagen en trye de saveimage "+ file.getName() +"en try ");
	}catch (IOException e){
		//throw new RuntimeException ("no se puede cargar la imagen");
		throw new GenericException("Oppss...System error, please visit it later");
		}
	}
   
	
	
	
   private void validarImagen (MultipartFile image){
	   if((image.getContentType().equals("image/jpeg"))||(image.getContentType().equals("image/pjpeg"))
			   ||(image.getContentType().equals("image/jpg"))||(image.getContentType().equals("image/x-png"))){
		   logger.info("tipo imagen :"+ image.getContentType());
	   }
	   else{
		   logger.info("tipo imagen :"+ image.getContentType());
		   throw new GenericException("la imagen no es jpg");
		   
	   }
	   
	 }

	
}