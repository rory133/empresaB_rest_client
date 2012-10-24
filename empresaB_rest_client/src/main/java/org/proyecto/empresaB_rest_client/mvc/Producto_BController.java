package org.proyecto.empresaB_rest_client.mvc;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import org.proyecto.empresaB_rest_client.exception.GenericException;
import org.proyecto.empresaB_rest_client.model.Carro_B;
import org.proyecto.empresaB_rest_client.model.ListaProductos_B;
import org.proyecto.empresaB_rest_client.model.ListaProductos_BSeleccionados;
import org.proyecto.empresaB_rest_client.model.Producto_B;
import org.proyecto.empresaB_rest_client.model.Producto_BSeleccionado;
import org.proyecto.empresaB_rest_client.util.ListaProductosSeleccionados;





@Controller
@RequestMapping("/productos")
public class Producto_BController {


	

	
	

	
	@Autowired
	ServletContext context;
	
	
	private RestTemplate restTemplate = new RestTemplate();
	

	
	protected static Logger logger = Logger.getLogger("*en Producto_BController*");
		
	
	
	//pedimo al servidor el listado de todos los productos
	@RequestMapping(value="/listado",method=RequestMethod.GET)
	public ModelAndView listadoProductos_B(HttpSession session){
		
		//si hay session indicamos en la lista de productos los seleccionados
		if (session.getAttribute("carro_b")!=null){
			Carro_B carro_b =new Carro_B();
			carro_b=(Carro_B)session.getAttribute("carro_b");
		
		//para poder indicar la cantidad seleccionada de cada uno en la vista
			  //obtenemos la lista de los productos seleccionados hasta ahora
			//para poder indicar la cantidad seleccionada de cada uno en la vista
					// Preparamos acceptable media type
					List<MediaType> acceptableMediaTypes3 = new ArrayList<MediaType>();
					acceptableMediaTypes3.add(MediaType.APPLICATION_XML);
					//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
					
					// preparamos el header
					HttpHeaders headers3 = new HttpHeaders();
					headers3.setAccept(acceptableMediaTypes3);
					HttpEntity<ListaProductos_BSeleccionados> entity3 = new HttpEntity<ListaProductos_BSeleccionados>(headers3);
					
					// enviamos el request como GET
					
					ResponseEntity<ListaProductos_BSeleccionados> result3=null;
					try {
						//realizamos consulta a servidor para que nos envie todos los productos del carro
						
					 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIdCarro/{idCarro}", 
							 HttpMethod.GET, entity3, ListaProductos_BSeleccionados.class,String.valueOf( carro_b.getIdcarro_b()));
					
						
						
								
						} catch (Exception e) {
								logger.error(e);
						}
					List<Producto_BSeleccionado> listaProductosRecibida = null;
					listaProductosRecibida = result3.getBody().getDataProductoBSeleccionado();
					//logger.info("ACABAMOS DE OBTENER LA LISTA DE LOS PRODUCTOS SELECCIONADOS HASTA AHORA (DEL CARRO):::::::: tamaño "+listaProductosRecibida.size());
					
					//List<Producto_BSeleccionado> listaProductosRecibida=producto_BSeleccionadoService.findByProducto_BSeleccionadoPorIdcarro_b(String.valueOf( carro_b.getIdcarro_b()));
    
		//para que no de problemas cuando tenemos un carro pero lo hemos vaciado			
		if(null!=listaProductosRecibida){			
					
			Set<ListaProductosSeleccionados> listaProductos=new HashSet<ListaProductosSeleccionados>(0);
			Iterator<Producto_BSeleccionado> itr =listaProductosRecibida.iterator();
			while (itr.hasNext()) {
				Producto_BSeleccionado element = itr.next();
				ListaProductosSeleccionados lista = new ListaProductosSeleccionados();
				lista.setCantidad(element.getCantidad());
				lista.setIdCarro(element.getCarro_b().getIdcarro_b());
				lista.setIdproducto_b(element.getProducto_b().getIdproductob());
				lista.setIdProductoSeleccionado(element.getIdproductoSeleccionado());
				lista.setNombreProducto(element.getProducto_b().getNombre_productoB());
				lista.setPrecio_b(element.getProducto_b().getPrecio_b());
				lista.setSubTotal(element.getSubTotal());
				listaProductos.add(lista);
				
			}
			//obtenemos la lista de productos para enviarla a la vista
			// Preparamos acceptable media type
			List<MediaType> acceptableMediaTypes5 = new ArrayList<MediaType>();
			acceptableMediaTypes5.add(MediaType.APPLICATION_XML);
			//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
			
			// preparamos el header
			HttpHeaders headers5 = new HttpHeaders();
			headers5.setAccept(acceptableMediaTypes5);
			HttpEntity<ListaProductos_B> entity5 = new HttpEntity<ListaProductos_B>(headers5);
			
			// enviamos el request como GET
			
			ResponseEntity<ListaProductos_B> result5=null;
			try {
				//realizamos consulta a servidor para que nos envie todos los productos
			 result5 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity5, ListaProductos_B.class);
			
				
				
						
				} catch (Exception e) {
						logger.error(e);
				}
			
			List<Producto_B> lista = result5.getBody().getDataProducto();		
			//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
			
			
			
			ModelAndView mav= new ModelAndView("producto_b/listaProductos");
			mav.addObject("productos", lista);
			//añadimos la lista de los seleccionados hasta ahora
			mav.addObject("productosSeleccionados",listaProductos);
			return mav;
			
		}else{
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
			
	}else{
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
		
	}
		

	
	
	
	//cuando un administrador pide añadir productos le enviamos al formulario
	@RequestMapping(value="/admin/" ,method = RequestMethod.GET, params="new")
	public ModelAndView addProducto() {
		logger.info("metodo get --new-- ");
		return new ModelAndView("producto_b/edit", "producto_b",new Producto_B());
	  }
	

	
	
	//public ModelAndView modProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b,  BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image, HttpServletRequest request){
	@RequestMapping(value="/admin/modificarProductoB", method = RequestMethod.POST)
	public ModelAndView modProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b,  BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image)throws Exception{

		//si tiene errores lo devolvemos a la pagina de modificar Producto_B
		if(result.hasErrors()) {
		logger.info("modificarProducto_B_form ------tiene errores----"+result.toString());
			return new ModelAndView("producto_b/modificar", "producto_b",producto_b).addAllObjects(result.getModel());
		}

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

			//enviamos el resquest como PUT
			
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
	
	
	@RequestMapping(value="/admin/crearProductoB",method = RequestMethod.POST)
	public ModelAndView addProducto_B_form(@Valid @ModelAttribute("producto_b")Producto_B producto_b, BindingResult  result,@RequestParam(value="image",required=false)MultipartFile image) throws Exception{


		logger.info("inicio de addProducto_B_form");
		if(result.hasErrors()) {
		logger.info("addProducto_B_form ------tiene errores----"+result.toString());
		logger.info("errores: "+result.toString());
		 return new ModelAndView("producto_b/edit", "producto_b",new Producto_B()).addAllObjects(result.getModel());

		}

		
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