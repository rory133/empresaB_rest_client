package org.proyecto.empresaB_rest_client.mvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.proyecto.empresaB_rest_client.model.Carro_B;
import org.proyecto.empresaB_rest_client.model.Cliente_B;
import org.proyecto.empresaB_rest_client.model.ListaCarros_B;
import org.proyecto.empresaB_rest_client.model.ListaProductos_B;
import org.proyecto.empresaB_rest_client.model.ListaProductos_BSeleccionados;
import org.proyecto.empresaB_rest_client.model.Producto_B;
import org.proyecto.empresaB_rest_client.model.Producto_BSeleccionado;
import org.proyecto.empresaB_rest_client.model.TarjetaCredito;
import org.proyecto.empresaB_rest_client.model.Usuario_B;
import org.proyecto.empresaB_rest_client.service.impl.Carro_BServiceImpl;
import org.proyecto.empresaB_rest_client.service.impl.Cliente_BServiceImpl;
import org.proyecto.empresaB_rest_client.service.impl.Producto_BSeleccionadoServiceImpl;
import org.proyecto.empresaB_rest_client.service.impl.Productos_BServiceImpl;
import org.proyecto.empresaB_rest_client.util.ListaPedidos;
import org.proyecto.empresaB_rest_client.util.ListaProductosSeleccionados;
import org.proyecto.empresaB_rest_client.util.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope("session")
@RequestMapping("/carro")
@SessionAttributes("carro_B")
public class CarroController {

	
	@Autowired
	private Carro_B carro_b;
	
	@Autowired
	private Productos_BServiceImpl productos_BServiceImpl;
	
	@Autowired
	private Producto_BSeleccionadoServiceImpl producto_BSeleccionadoService;
	
	@Autowired
	private Cliente_BServiceImpl cliente_BServiceImpl;
	
	@Autowired
	Carro_BServiceImpl carro_BService;
	
	@Autowired
	private TarjetaCredito tarjetaCredito;
	
	
	@Autowired
	private Mail mail;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	
	protected static Logger logger = Logger.getLogger("*en CarroController*en CLIENTE @@@@@@@@@@");
	
	@RequestMapping(value="/sumaProducto", method = RequestMethod.GET)
	public ModelAndView sumaProducto(@RequestParam(value="cantidad")String cantidad, @RequestParam(value="idProducto")String  idProducto, HttpSession session) throws Exception{
	//	carro_BService.save(carro_b);
		logger.info("en /sumaProducto en CLIENTE @@@@@@@@@@");
		logger.info("session.getAttribute('carro_b')-al entrar: " + session.getAttribute("carro_b"));


		
		if (session.getAttribute("carro_b")==null){
			logger.info("if (carro_b.getIdcarro_b()==null)");
			
			//creamos un carro nuevo
			Carro_B carro_b =new Carro_B();
			
			//obtenemos usuario de la sesion
			User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			logger.info("usuario user user.getUsername() : "+user.getUsername());
			
			Usuario_B usuario= new Usuario_B();
			
			usuario= cliente_BServiceImpl.findByCliente_B_login_usuario_b(user.getUsername());
			
			logger.info("login usuario obtenido:"+usuario.getLogin_usuario_b());
			
			
			
			
			Cliente_B cliente= new Cliente_B();
			cliente=(Cliente_B)usuario;
			
			//sumamos los datos del carro
			carro_b.setCliente_b(cliente);
			carro_b.setFecha_b(new Date());
			carro_b.setEnviado(false);
			carro_b.setPagado(false);
			//ponemos a cero el total
			carro_b.setTotal(new BigDecimal(0));
			logger.info("en /sumaProducto ççççç pongo a cero el carro: "+carro_b.getTotal());
			
			
			//carro_BService.save(carro_b);
			
			//Salvamos el Carro en servidor
			// ----Preparamos acceptable media type----
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_XML);
			
			// preparamos el header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<Carro_B> entity = new HttpEntity<Carro_B>(carro_b,headers);

			//enviamos el resquest como POST
			ResponseEntity<Carro_B> result = null;
			try {
				//ResponseEntity<Cliente_B> clienteDevuelto = 
				result	=restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b",
								HttpMethod.POST, entity, Carro_B.class);
		
				
						
				} catch (Exception e) {
						logger.error(e);
			}
			
			
			carro_b=result.getBody();
			
			session.setAttribute("carro_b", carro_b);
			logger.info("if (carro_b.getIdcarro_b()==null) despues    :  " +carro_b.getIdcarro_b() );
		}
		
		carro_b=(Carro_B)session.getAttribute("carro_b");
		
		
		
		
		logger.info("imprimo el id del carro: "+carro_b.getIdcarro_b());
		logger.info("imprimo la fecha del carro: "+carro_b.getFecha_b());
		//Carro_B carro_b=new Carro_B(new Date(),)
		logger.info("session.getAttributeNames().toString()"+session.getAttributeNames().toString());
		logger.info("session.getAttribute('carro_b'): " + session.getAttribute("carro_b"));
		logger.info("session Id:"+session.getId());
		logger.info("cantidad Recibida"+ cantidad);
		
		logger.info("idproducto Recibido"+ idProducto);
		//logger.info("usuario de la sesion : "+session.getAttribute("user"));
		logger.info("carro de la sesion : "+session.getAttribute("carro_b"));
		
		
		logger.info("datos carro id: "+carro_b.getIdcarro_b());
		
		
		
		

		
	
		
		Producto_B producto=new Producto_B();
		//producto=productos_BServiceImpl.findByProducto_BIdProducto_b(idProducto);
		
		//obtenemos el producto correspondiente al id del producto que se añade al carro
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
									HttpMethod.GET, entity, Producto_B.class,idProducto);
			
					
							
					} catch (Exception e) {
							logger.error(e);
					} 
					
				

				producto=result.getBody();
		
				logger.info("en /sumaProducto nombre del producto_b obtenido de servidor: "+producto.getNombre_productoB());
		
		
		
		
		
		
		
				
		
		Producto_BSeleccionado producto_BSeleccionado=new Producto_BSeleccionado();
		
		producto_BSeleccionado.setProducto_b(producto);
		logger.info(" ide carro_b antes de ponerlo en producto seleccionado: "+carro_b.getIdcarro_b());
		producto_BSeleccionado.setCarro_b(carro_b);	
		//producto_BSeleccionado.setCantidad(Integer.parseInt(cantidad));
		
		
		
		logger.info("carro_b.getIdcarro_b():"+carro_b.getIdcarro_b());
		Producto_BSeleccionado producto_BSeleccionado_test=new Producto_BSeleccionado();
		logger.info("Producto_BSeleccionado producto_BSeleccionado_test=new Producto_BSeleccionado();");
		
		//producto_BSeleccionado_test=producto_BSeleccionadoService.findByProducto_BSeleccionadoIdProducto_b_y_carro_b(String.valueOf(producto.getIdproductob()),String.valueOf( carro_b.getIdcarro_b()));
		
		
		
		
		      //obtenemos el productoSelccionado por el id del producto y el id del carro si en un modificacion de un producto previamente elegido se trata de forma distinta
				// Preparamos acceptable media type
						List<MediaType> acceptableMediaTypes2 = new ArrayList<MediaType>();
						acceptableMediaTypes2.add(MediaType.APPLICATION_XML);
						
						// preparamos el header
						HttpHeaders headers2 = new HttpHeaders();
						headers2.setAccept(acceptableMediaTypes2);
						HttpEntity<Producto_BSeleccionado> entity2 = new HttpEntity<Producto_BSeleccionado>(headers2);
						
						logger.info("IMPRIMO EL IDCARRO  String.valueOf( carro_b.getIdcarro_b())"+String.valueOf( carro_b.getIdcarro_b()));
						logger.info("IMPRIMO EL IDCARRO  "+ carro_b.getIdcarro_b());

						//enviamos el resquest como POST
						ResponseEntity<Producto_BSeleccionado> result2=null;
						
						/*
						Map<String, String> params = new HashMap<String, String>();
					    params.put("id", idProducto);
					    params.put("idCarro", String.valueOf( carro_b.getIdcarro_b()));
						*/
						try {
							
							 result2=
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIDProductoIdCarro/{id}/{idCarro}",
											HttpMethod.GET, entity2, Producto_BSeleccionado.class,idProducto,String.valueOf( carro_b.getIdcarro_b()));
									//HttpMethod.GET, entity2, Producto_BSeleccionado.class,params);
							
									
							} catch (Exception e) {
									logger.error(e);
							} 

						producto_BSeleccionado_test=result2.getBody();
		
		
		logger.info("DESPUES DE OBTENER producto_BSeleccionado_test=producto_BSeleccionadoService.findByProducto_BSeleccionadoIdProducto_b_y_carro_b.....");
		
		//comprobamos si ese producto ya estaba en el carro si es asi se trata de forma distinta la actualizacion de la cantidad
		if (null!=producto_BSeleccionado_test){
			//controlamos que el pedido no exceda la cantidad de existencias. aqui se tiene en cuenta los que se habian pedido antes
			if ((producto_BSeleccionado_test.getCantidad()+producto.getCantidad_existencias())<(Integer.parseInt(cantidad))){
				//si excede devolmemos la lista de productos con el mensaje
				//primero obtenemos la lista de productos
				// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes3 = new ArrayList<MediaType>();
				acceptableMediaTypes3.add(MediaType.APPLICATION_XML);
				//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
				
				// preparamos el header
				HttpHeaders headers3 = new HttpHeaders();
				headers3.setAccept(acceptableMediaTypes3);
				HttpEntity<ListaProductos_B> entity3 = new HttpEntity<ListaProductos_B>(headers3);
				
				// enviamos el request como GET
				
				ResponseEntity<ListaProductos_B> result3=null;
				try {
					//realizamos consulta a servidor para que nos envie todos los clientes
				 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity3, ListaProductos_B.class);
				
					
					
							
					} catch (Exception e) {
							logger.error(e);
					}
				
				List<Producto_B> lista = result3.getBody().getDataProducto();
				//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		     	
		     	
		     	//devolvemos la vista
		     	
				ModelAndView mav= new ModelAndView("producto_b/listaProductos");
				mav.addObject("productos", lista);
				mav.addObject("errordeCantidad","no puede pedir mas cantidad que las existencias");
				mav.addObject("productoPedido",idProducto);
				return mav;
				}
			
			
		//logger.info("producto_BSeleccionado_test idproducto="+producto_BSeleccionado_test.getIdproductob());
		logger.info("producto_BSeleccionado_test idcarro="+producto_BSeleccionado_test.getCarro_b().getIdcarro_b());
		logger.info("producto_BSeleccionado cantidad" +producto_BSeleccionado.getCantidad());
		logger.info("producto_BSeleccionado id producto" +producto_BSeleccionado.getProducto_b().getIdproductob());
		logger.info("producto_BSeleccionado id " +producto_BSeleccionado.getIdproductoSeleccionado());
		logger.info("producto_BSeleccionado id " +producto_BSeleccionado.getCarro_b().getIdcarro_b());
		logger.info("producto_BSeleccionado_test id " +producto_BSeleccionado_test.getIdproductoSeleccionado());
		
		
		//actualizacomos el valor de existencia
		producto.setCantidad_existencias(producto.getCantidad_existencias()+producto_BSeleccionado_test.getCantidad()-Integer.parseInt(cantidad));
		
		//actualizamos la cantidad de producto_BSeleccionado
		producto_BSeleccionado.setCantidad(Integer.parseInt(cantidad));
		
		//actualizamos producto con el nuevo valor de cantidad
		
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
		acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers4 = new HttpHeaders();
		headers4.setAccept(acceptableMediaTypes4);
		HttpEntity<Producto_B> entity4 = new HttpEntity<Producto_B>(producto,headers4);

		//enviamos el resquest como POST
		
		try {
			//ResponseEntity<Cliente_B> clienteDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
							HttpMethod.PUT, entity4, Producto_B.class);
	
			
					
			} catch (Exception e) {
					logger.error(e);
		}
		//productos_BServiceImpl.update(producto);
		
		//actualizamos el total del carro
		
		
		logger.info("en /sumaProducto ççççç valor carroTotal antes de restar el valor anterior: "+carro_b.getTotal());
		//restamos el subtotal del producto seleccionado al total del carro		producto_BSeleccionado_test
		carro_b.setTotal(carro_b.getTotal().subtract(producto_BSeleccionado_test.getSubTotal()));
		
		logger.info("en /sumaProducto ççççç valor carroTotal DESPUES de restar el valor anterior: "+carro_b.getTotal());
		
		//añadidmo valor al subtotal del producto seleccionado
		BigDecimal temp = new BigDecimal(Integer.parseInt(cantidad));
		producto_BSeleccionado.setSubTotal(producto.getPrecio_b().multiply(temp));
		logger.info("en /sumaProducto ççççç VALOR DE PRODUCTOBSELECCIONADO CUANDO SE ACTUALIZA VALOR YA QUE SE HABIA PEDIDO ANTES: "+producto_BSeleccionado.getSubTotal());
		
		//acutalizamos el nuevo valor del total en carro
		carro_b.setTotal(carro_b.getTotal().add(producto_BSeleccionado.getSubTotal()));
		//actualizacomos producto_BSeleccionado
		
		producto_BSeleccionado.setIdproductoSeleccionado(producto_BSeleccionado_test.getIdproductoSeleccionado());
		
		
		// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes5 = new ArrayList<MediaType>();
				acceptableMediaTypes5.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers5 = new HttpHeaders();
				headers5.setAccept(acceptableMediaTypes5);
				HttpEntity<Producto_BSeleccionado> entity5 = new HttpEntity<Producto_BSeleccionado>(producto_BSeleccionado,headers5);

				//enviamos el resquest como POST
				
				try {
				
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionado",
					//restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado",
									HttpMethod.PUT, entity5, Producto_BSeleccionado.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
		
				//producto_BSeleccionadoService.update(producto_BSeleccionado);

		
		
		}else{
			//El producto se seleccion por primera vez para este carro, controlamos que el pedido no exceda la cantidad de existencias.
			
			logger.info("producto_cantidad de existencias::::::::"+producto.getCantidad_existencias());
			logger.info("producto_cantidad pedidas::::::::"+Integer.parseInt(cantidad));
			if (producto.getCantidad_existencias()<(Integer.parseInt(cantidad))){
				//la cantidad pedida sobrepasa la cantidad de existencias, devolvemos la vista de la lista de productos con el mensaje 
				
				logger.info("la cantidad pedida supera las existencias::::::::");
				//si excede devolmemos la lista de productos con el mensaje
				//primero obtenemos la lista de productos
				// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes3 = new ArrayList<MediaType>();
				acceptableMediaTypes3.add(MediaType.APPLICATION_XML);
				//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
				
				// preparamos el header
				HttpHeaders headers3 = new HttpHeaders();
				headers3.setAccept(acceptableMediaTypes3);
				HttpEntity<ListaProductos_B> entity3 = new HttpEntity<ListaProductos_B>(headers3);
				
				// enviamos el request como GET
				
				ResponseEntity<ListaProductos_B> result3=null;
				try {
					//realizamos consulta a servidor para que nos envie todos los productos
				 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity3, ListaProductos_B.class);
				
					
					
							
					} catch (Exception e) {
							logger.error(e);
					}
				
				List<Producto_B> lista = result3.getBody().getDataProducto();
				//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
				
				ModelAndView mav= new ModelAndView("producto_b/listaProductos");
				mav.addObject("productos", lista);
				mav.addObject("errordeCantidad","no puede pedir mas cantidad que las existencias");
				mav.addObject("productoPedido",idProducto);
				return mav;
				}
			

			
			
			logger.info("la cantidad pedida NO supera las existencias LAS ACTUALIZAMOS EN PRODUCTO::::::::");
			//actualizacomos el valor de existencias en el producto
			//añadimos el carro al productoSeleccionado
			producto_BSeleccionado.setCarro_b(carro_b);	
			producto.setCantidad_existencias(producto.getCantidad_existencias()-Integer.parseInt(cantidad));
			//y lo salvamos
			
			// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
				acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers4 = new HttpHeaders();
				headers4.setAccept(acceptableMediaTypes4);
				HttpEntity<Producto_B> entity4 = new HttpEntity<Producto_B>(producto,headers4);

				//enviamos el resquest como POST
				
				try {
					//ResponseEntity<Cliente_B> clienteDevuelto = 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
									HttpMethod.PUT, entity4, Producto_B.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
				
				//productos_BServiceImpl.update(producto);	
				
				//actualizamos la cantidad de producto_BSeleccionado
				producto_BSeleccionado.setCantidad(Integer.parseInt(cantidad));
				
				//añadidmo valor al subtotal del producto seleccionado
				BigDecimal temp = new BigDecimal(Integer.parseInt(cantidad));
				producto_BSeleccionado.setSubTotal(producto.getPrecio_b().multiply(temp));
				logger.info("en /sumaProducto ççççç VALOR DE SUTTOTAL DE PRODUCTO_BSELECCIONADO DESPUED DE AÑADIRLE SUBTOTAL POR PRIMERA VEZ: "+producto_BSeleccionado.getSubTotal());
				
				
				logger.info("SALVAMOS PRUDUCTO SELECCIONADO::::::::");
				//salvamos producto_BSeleccionado
				List<MediaType> acceptableMediaTypes5 = new ArrayList<MediaType>();
				acceptableMediaTypes5.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers5 = new HttpHeaders();
				headers5.setAccept(acceptableMediaTypes5);
				HttpEntity<Producto_BSeleccionado> entity5 = new HttpEntity<Producto_BSeleccionado>(producto_BSeleccionado,headers5);

				//enviamos el resquest como POST
				
				try {
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionado",
							//restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado",
									HttpMethod.POST, entity5, Producto_BSeleccionado.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
				
				logger.info("HEMOS SALVADO PRUDUCTO SELECCIONADO::::::::");
				//producto_BSeleccionadoService.save(producto_BSeleccionado);
				//producto_BSeleccionadoService.update(producto_BSeleccionado);
				
				
				logger.info("ACTUALIZAMOS CARRO::::::::");
				carro_b.getProducto_BSeleccionado().add(producto_BSeleccionado);
				
				//sumamos el subtotal del producto seleccionado al total del carro		
				carro_b.setTotal(carro_b.getTotal().add(producto_BSeleccionado.getSubTotal()));
		
		}
		/*logger.info("ACTUALIZAMOS CARRO::::::::");
		carro_b.getProducto_BSeleccionado().add(producto_BSeleccionado);*/
		
		

		
		//Actualizamos el carro
		
		// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
				acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers4 = new HttpHeaders();
				headers4.setAccept(acceptableMediaTypes4);
				HttpEntity<Carro_B> entity4 = new HttpEntity<Carro_B>(carro_b,headers4);

				//enviamos el resquest como PUT
				
				try {
					//ResponseEntity<Cliente_B> clienteDevuelto = 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b",
									HttpMethod.PUT, entity4, Carro_B.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
				//carro_BService.update(carro_b); NO ESTABA
		
		
				logger.info("OBTENEMOS LA LISTA DE LOS PRODUCTOS SELECCIONADOS HASTA AHORA:::::::: ID IDCARRO ES: "+String.valueOf( carro_b.getIdcarro_b()));
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
				logger.info("ACABAMOS DE OBTENER LA LISTA DE LOS PRODUCTOS SELECCIONADOS HASTA AHORA (DEL CARRO):::::::: tamaño "+listaProductosRecibida.size());
				
				//List<Producto_BSeleccionado> listaProductosRecibida=producto_BSeleccionadoService.findByProducto_BSeleccionadoPorIdcarro_b(String.valueOf( carro_b.getIdcarro_b()));
		
				
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
			//realizamos consulta a servidor para que nos envie todos los clientes
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
		//return new ModelAndView("redirect:../../productos/listado");
	}
	
	
	
	
	
	
	@RequestMapping(value="/verCarro", method = RequestMethod.GET)
	public ModelAndView verCarro( HttpSession session) throws Exception{
		logger.info("en /verCarro en CLIENTE @@@@@@@@@@");
		logger.info("en /verCarro en CLIENTE @@@@@@@@@@ id Carro "+String.valueOf( carro_b.getIdcarro_b()));
		//Si aun no hay ningun carro se envia un mensaje en la vista
		if (session.getAttribute("carro_b")==null){
			logger.info("en ver Carro, e carro esta vacio");
			ModelAndView mav= new ModelAndView("producto_b/listaProductos");
			List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
			mav.addObject("errorCarroVacio","¡¡¡el carro esta vacio, aun no ha seleccionado ningun producto!!!");
			mav.addObject("productos", lista);		
			return mav;

		}
		//si hay carro se crea la vista con los productos que contiene
		//se obtienen los productos del carro
		
		
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
			//realizamos consulta a servidor para que nos envie todos los clientes
		 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIdCarro/{idCarro}", 
				 HttpMethod.GET, entity3, ListaProductos_BSeleccionados.class,String.valueOf( carro_b.getIdcarro_b()));
		
			
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		List<Producto_BSeleccionado> listaProductosRecibida = result3.getBody().getDataProductoBSeleccionado();
		
		//List<Producto_BSeleccionado> listaProductosRecibida=producto_BSeleccionadoService.findByProducto_BSeleccionadoPorIdcarro_b(String.valueOf( carro_b.getIdcarro_b()));
		
		Set<ListaProductosSeleccionados> listaProductos=new HashSet<ListaProductosSeleccionados>(0);
		
		
		if (null!=listaProductosRecibida){
		//if (!listaProductosRecibida.isEmpty()){888888888
		//logger.info("tamaño lista productosSeleccionados en esteCarro"+listaProductosRecibida.size());
		
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
		
		}else listaProductos=null;
		
		
		
		//preparamos la vista
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<ListaProductos_B> entity = new HttpEntity<ListaProductos_B>(headers);
		
		// enviamos el request como GET
		
		ResponseEntity<ListaProductos_B> result=null;
		try {
			//realizamos consulta a servidor para que nos envie todos los clientes
		 result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity, ListaProductos_B.class);
		
			
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		List<Producto_B> lista = result.getBody().getDataProducto();		
		//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		ModelAndView mav= new ModelAndView("carro_b/verCarroActual");
		mav.addObject("productos", lista);
		mav.addObject("idCarro", String.valueOf( carro_b.getIdcarro_b()));
		mav.addObject("productosSeleccionados",listaProductos);
		return mav;
	}
	 
	
	
	
	
	
	@RequestMapping(value="/eliminarProductoCarro", method = RequestMethod.GET)
	public ModelAndView eliminarProductoCarro(@RequestParam(value="idProductoSeleccionado")String  idProductoSeleccionado,@RequestParam(value="idProducto")String  idProducto, @RequestParam(value="cantidad")String cantidad,  @RequestParam(value="idCarro")String idCarro, HttpSession session) throws Exception{
		logger.info("en /eliminarProductoCarro en CLIENTE @@@@@@@@@@");
		
	
		
		
		
		//eliminar el producto
		Producto_BSeleccionado producto_BSeleccionado=new Producto_BSeleccionado();
		
		//lo buscamos por el id del producto y del carro
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes2 = new ArrayList<MediaType>();
		acceptableMediaTypes2.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setAccept(acceptableMediaTypes2);
		HttpEntity<Producto_BSeleccionado> entity2 = new HttpEntity<Producto_BSeleccionado>(headers2);

		//enviamos el resquest como POST
		ResponseEntity<Producto_BSeleccionado> result2=null;
		
		try {
			
			 result2=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIDProductoIdCarro/{id}/{idCarro}",
						//	HttpMethod.GET, entity2, Producto_BSeleccionado.class,idProducto,String.valueOf( carro_b.getIdcarro_b()));
					HttpMethod.GET, entity2, Producto_BSeleccionado.class,idProducto,idCarro);
			
					
			} catch (Exception e) {
					logger.error(e);
			} 
		
		producto_BSeleccionado=result2.getBody();
		//producto_BSeleccionado=producto_BSeleccionadoService.findByProducto_BSeleccionadoIdProducto_b_y_carro_b(idProducto, String.valueOf(carro_b.getIdcarro_b()));
		
		
		
		//obtenemos el carro
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes1 = new ArrayList<MediaType>();
		acceptableMediaTypes1.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers1 = new HttpHeaders();
		headers1.setAccept(acceptableMediaTypes1);
		HttpEntity<Carro_B> entity1 = new HttpEntity<Carro_B>(headers1);

		//enviamos el resquest como GET
		ResponseEntity<Carro_B> carroDevuelto=null;
		try {
			 carroDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b/{id}",
							HttpMethod.GET, entity1, Carro_B.class,idCarro);
	
			
					
			} catch (Exception e) {
					logger.error(e);
		}

		Carro_B carroTemp=carroDevuelto.getBody();
		//reducimos el total del carro el valor del producto seleccionado
		carroTemp.setTotal(carroTemp.getTotal().subtract(producto_BSeleccionado.getSubTotal()));


		//eliminamos el productoSeleccionado
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Producto_B> entity = new HttpEntity<Producto_B>(headers);


		try {
			
			// result=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionado/{id}",
							HttpMethod.DELETE, entity, Producto_B.class,idProducto,String.valueOf(producto_BSeleccionado.getIdproductoSeleccionado()));
					
	
			
					
			} catch (Exception e) {
					logger.error(e);
			} 
		 	//producto_BSeleccionadoService.delete(producto_BSeleccionado);
		
		//sumamos cantidad que tenia el producto seleccionado a la cantidad de existencias del producto
		
		
		Producto_B producto=new Producto_B();
		//encontramos el producto
		
		//obtenemos el producto correspondiente a ese id
		// Preparamos acceptable media type
				List<MediaType> acceptableMediaTypes3 = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers3 = new HttpHeaders();
				headers3.setAccept(acceptableMediaTypes3);
				HttpEntity<Producto_B> entity3 = new HttpEntity<Producto_B>(headers3);

				//enviamos el resquest como POST
				ResponseEntity<Producto_B> result3=null;
				
				try {
					
					 result3=
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/producto/{id}",
									HttpMethod.GET, entity3, Producto_B.class,idProducto);
			
					
							
					} catch (Exception e) {
							logger.error(e);
					}
		
		
				producto=result3.getBody();		
				//producto=productos_BServiceImpl.findByProducto_BIdProducto_b(idProducto);
				
				
		
		//actualizamos cantidad
		producto.setCantidad_existencias(producto.getCantidad_existencias()+Integer.parseInt(cantidad));
		
		//Actualizamos producto
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
		acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers4 = new HttpHeaders();
		headers4.setAccept(acceptableMediaTypes4);
		HttpEntity<Producto_B> entity4 = new HttpEntity<Producto_B>(producto,headers4);

		//enviamos el resquest como PUT
		
		try {
			 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos/admin/producto",
							HttpMethod.PUT, entity4, Producto_B.class);
	
			
					
			} catch (Exception e) {
					logger.error(e);
		}
		
		//productos_BServiceImpl.update(producto);
		
		//Actualizamos el carro
		
				// ----Preparamos acceptable media type----
						List<MediaType> acceptableMediaTypes6 = new ArrayList<MediaType>();
						acceptableMediaTypes6.add(MediaType.APPLICATION_XML);
						
						// preparamos el header
						HttpHeaders headers6 = new HttpHeaders();
						headers6.setAccept(acceptableMediaTypes6);
						HttpEntity<Carro_B> entity6 = new HttpEntity<Carro_B>(carroTemp,headers6);

						//enviamos el resquest como PUT
						
						try {
							//ResponseEntity<Cliente_B> clienteDevuelto = 
									restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b",
											HttpMethod.PUT, entity6, Carro_B.class);
					
							
									
							} catch (Exception e) {
									logger.error(e);
						}
		
		
		
		
		
		
		
		
		
		//devolvemos vista a verCarroActual
		return new ModelAndView("redirect: verCarro");
	}
	
	@RequestMapping(value="/verTodosLosPedidos", method = RequestMethod.GET)
	public ModelAndView verTodosLosPedidos( HttpSession session) throws Exception{
		
		logger.info("en /carro/verTodos/LosPedidos en CLIENTE @@@@@@@@@@");
		//buscamos todos los carros
		//que sea una lista ordenada
		Set<ListaPedidos> listaCarrosAMostrar=new TreeSet<ListaPedidos>();
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes3 = new ArrayList<MediaType>();
		acceptableMediaTypes3.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers3 = new HttpHeaders();
		headers3.setAccept(acceptableMediaTypes3);
		HttpEntity<ListaCarros_B> entity3 = new HttpEntity<ListaCarros_B>(headers3);
		
		// enviamos el request como GET
		
		ResponseEntity<ListaCarros_B> result3=null;
		try {
			//realizamos consulta a servidor para que nos envie todos los carros
		 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carros",
				 HttpMethod.GET, entity3, ListaCarros_B.class);
		
			
			
					
			} catch (Exception e) {
					logger.error(e);
			}
	
		List<Carro_B> listaCarros=result3.getBody().getDataCarro();
		//List<Carro_B> listaCarros =carro_BService.findAll();
		
		
		
		//buscamos los datos de cada carro
		Iterator<Carro_B> iterCarro =listaCarros.iterator();
		
		
		
		while (iterCarro.hasNext()) {
			ListaPedidos listaCarrosPedidos = new ListaPedidos();
			Carro_B elementoCarro = iterCarro.next();
			listaCarrosPedidos.setIdCliente(elementoCarro.getCliente_b().getIdusuarios_b());
			listaCarrosPedidos.setLoginCliente(elementoCarro.getCliente_b().getLogin_usuario_b());
			listaCarrosPedidos.setPagado(elementoCarro.getPagado());
			listaCarrosPedidos.setEnviado(elementoCarro.getEnviado());
			listaCarrosPedidos.setIdCarro(elementoCarro.getIdcarro_b());
			listaCarrosPedidos.setFechaPedido(elementoCarro.getFecha_b());
			listaCarrosPedidos.setTotal(elementoCarro.getTotal());
			//logger.info("imprimo el login del usuario desde listaCarrospedidos: "+listaCarrosPedidos.getLoginCliente());
			
			//le añadimos tambien los productos seleccionados de cada carro
			// Preparamos acceptable media type
			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(MediaType.APPLICATION_XML);
			//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
			
			// preparamos el header
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			HttpEntity<ListaProductos_BSeleccionados> entity = new HttpEntity<ListaProductos_BSeleccionados>(headers);
			
			// enviamos el request como GET
			
			ResponseEntity<ListaProductos_BSeleccionados> result=null;
			try {
				//realizamos consulta a servidor para que nos envie todos los clientes
			 result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIdCarro/{idCarro}", 
					 HttpMethod.GET, entity, ListaProductos_BSeleccionados.class,String.valueOf(elementoCarro.getIdcarro_b()));
			
				
				
						
				} catch (Exception e) {
						logger.error(e);
				}
			
			List<Producto_BSeleccionado> listaProductosCarro= null;
			listaProductosCarro=result.getBody().getDataProductoBSeleccionado();			
			//List<Producto_BSeleccionado> listaProductosCarro=producto_BSeleccionadoService.findByProducto_BSeleccionadoPorIdcarro_b(String.valueOf(elementoCarro.getIdcarro_b()));
			
			
			if(null!=listaProductosCarro){
			
			Iterator<Producto_BSeleccionado> itr =listaProductosCarro.iterator();
			Set<ListaProductosSeleccionados> listaProductos=new HashSet<ListaProductosSeleccionados>(0);
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
			listaCarrosPedidos.setListaProductosSeleccionados(listaProductos);
			listaCarrosAMostrar.add(listaCarrosPedidos);
			
			}else{
				listaCarrosPedidos.setListaProductosSeleccionados(null);
				listaCarrosAMostrar.add(listaCarrosPedidos);
			}
			
		}
		
		
		ModelAndView mav= new ModelAndView("carro_b/verPedidos");
		mav.addObject("TodosLosPedidos", listaCarrosAMostrar);
		return mav;
		
	}
	
	
	
	@RequestMapping(value="/verDetallesCarro", method = RequestMethod.GET)
	public ModelAndView verDetallesCarro( @RequestParam(value="idCarro")String  idCarro) throws Exception{
		
		Set<ListaProductosSeleccionados> listaProductos=new HashSet<ListaProductosSeleccionados>(0);
		
		//obtenemos los productos seleccionados del carro
		
		
		
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
			//realizamos consulta a servidor para que nos envie todos los clientes
		 result3 = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productoBSeleccionado/producto_b_seleccionadoIdCarro/{idCarro}", 
				 HttpMethod.GET, entity3, ListaProductos_BSeleccionados.class,idCarro);
		
			
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		List<Producto_BSeleccionado> listaProductosRecibida = result3.getBody().getDataProductoBSeleccionado();
		//List<Producto_BSeleccionado> listaProductosRecibida=producto_BSeleccionadoService.findByProducto_BSeleccionadoPorIdcarro_b(idCarro);
		if (!listaProductosRecibida.isEmpty()){
		//logger.info("tamaño lista productosSeleccionados en esteCarro"+listaProductosRecibida.size());
		
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
		
		}else listaProductos=null;
		
		//obtenemos la lista de productos para enviar a la vista
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		//acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<ListaProductos_B> entity = new HttpEntity<ListaProductos_B>(headers);
		
		// enviamos el request como GET
		
		ResponseEntity<ListaProductos_B> result=null;
		try {
			//realizamos consulta a servidor para que nos envie todos los clientes
		 result = restTemplate.exchange("http://localhost:8080/empresaB_rest_server/productos", HttpMethod.GET, entity, ListaProductos_B.class);
		
			
			
					
			} catch (Exception e) {
					logger.error(e);
			}
		
		List<Producto_B> lista = result.getBody().getDataProducto();		
		//List<Producto_B> lista =productos_BServiceImpl.getProductos_B();
		
		
		ModelAndView mav= new ModelAndView("carro_b/verDetallesCarro");
		mav.addObject("idCarro", idCarro);
		mav.addObject("productos", lista);
		mav.addObject("productosSeleccionados",listaProductos);
		return mav;
	}
	
	
	
	
	
	
	@RequestMapping(value="/borrarCarro", method = RequestMethod.GET)
	public ModelAndView borrarCarro( @RequestParam(value="idCarro")String  idCarro) throws Exception{
		
		
		
		// Preparamos acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Carro_B> entity = new HttpEntity<Carro_B>(headers);


		try {
			
			// result=
			restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b/{id}",
							HttpMethod.DELETE, entity, Carro_B.class,idCarro);
					
	
			
					
			} catch (Exception e) {
					logger.error(e);
			} 
		
		
		
		
		//carro_b=carro_BService.findByCarro_BIdCarro_b(idCarro);
		//carro_BService.delete(carro_b);

		ModelAndView mav= new ModelAndView("redirect: verTodosLosPedidos");
		return mav;
	}
	
	
	
	
	
	
	@RequestMapping(value="/cambioEstadoCarroPagado", method = RequestMethod.GET)
	public ModelAndView cambioEstadoCarroPagado( @RequestParam(value="idCarro")String  idCarro) throws Exception{
		//BUSCAMOS EL CARRO
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
		acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers4 = new HttpHeaders();
		headers4.setAccept(acceptableMediaTypes4);
		HttpEntity<Carro_B> entity4 = new HttpEntity<Carro_B>(headers4);

		//enviamos el resquest como GET
		ResponseEntity<Carro_B> carroDevuelto=null;
		try {
			 carroDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b/{id}",
							HttpMethod.GET, entity4, Carro_B.class,idCarro);
	
			
					
			} catch (Exception e) {
					logger.error(e);
		}
		
		
		
		Carro_B carro=carroDevuelto.getBody();		
		//carro_b=carro_BService.findByCarro_BIdCarro_b(idCarro);
		if(carro.getPagado())
		  carro.setPagado(false);
		else carro.setPagado(true);
		//actualizamos el carro
		
		
		// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Carro_B> entity = new HttpEntity<Carro_B>(carro,headers);

				//enviamos el resquest como PUT
				
				try {
				 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b",
									HttpMethod.PUT, entity, Carro_B.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
		//carro_BService.update(carro_b);


		ModelAndView mav= new ModelAndView("redirect: verTodosLosPedidos");
	

		return mav;
	}
	@RequestMapping(value="/cambioEstadoCarroEnviado", method = RequestMethod.GET)
	public ModelAndView cambioEstadoCarroEnviado( @RequestParam(value="idCarro")String  idCarro) throws Exception{
		//BUSCAMOS EL CARRO
		// ----Preparamos acceptable media type----
		List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
		acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
		
		// preparamos el header
		HttpHeaders headers4 = new HttpHeaders();
		headers4.setAccept(acceptableMediaTypes4);
		HttpEntity<Carro_B> entity4 = new HttpEntity<Carro_B>(headers4);

		//enviamos el resquest como GET
		ResponseEntity<Carro_B> carroDevuelto=null;
		try {
			 carroDevuelto = 
					restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b/{id}",
							HttpMethod.GET, entity4, Carro_B.class,idCarro);
	
			
					
			} catch (Exception e) {
					logger.error(e);
		}
		Carro_B carro=carroDevuelto.getBody();	
		//carro_b=carro_BService.findByCarro_BIdCarro_b(idCarro);
		
		
		
		if(carro.getEnviado())
		  carro.setEnviado(false);
		else{ carro.setEnviado(true);
		String content="apreciado usuario le informamos que el pago de su pedido numero "+idCarro+" ha sido enviado, en breve recibirá información de la agencia de transportes";
		String subject="pedido: "+idCarro;		
		mail.sendMail( carro.getCliente_b().getLogin_usuario_b(), content, carro.getCliente_b().getEmail_b(), subject);
		
		}
		
		//actualizamos el carro
		
		
		// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
						
				// preparamos el header
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(acceptableMediaTypes);
				HttpEntity<Carro_B> entity = new HttpEntity<Carro_B>(carro,headers);

				//enviamos el resquest como PUT
						
				try {
					
						restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b",
											HttpMethod.PUT, entity, Carro_B.class);
					
							
									
							} catch (Exception e) {
									logger.error(e);
						}		
		
		
		
		
		
		//carro_BService.update(carro_b);


		ModelAndView mav= new ModelAndView("redirect: verTodosLosPedidos");
	

		return mav;
	}
	@RequestMapping(value="/pagarCarro", method = RequestMethod.GET)
	public ModelAndView pagarCarro( @RequestParam(value="idCarro")String  idCarro,@RequestParam(value="total")String  total) throws Exception{
		
		//BUSCAMOS EL CARRO
				// ----Preparamos acceptable media type----
				List<MediaType> acceptableMediaTypes4 = new ArrayList<MediaType>();
				acceptableMediaTypes4.add(MediaType.APPLICATION_XML);
				
				// preparamos el header
				HttpHeaders headers4 = new HttpHeaders();
				headers4.setAccept(acceptableMediaTypes4);
				HttpEntity<Carro_B> entity4 = new HttpEntity<Carro_B>(carro_b,headers4);

				//enviamos el resquest como GET
				ResponseEntity<Carro_B> carroDevuelto=null;
				try {
					 carroDevuelto = 
							restTemplate.exchange("http://localhost:8080/empresaB_rest_server/carro/carro_b/{id}",
									HttpMethod.GET, entity4, Carro_B.class);
			
					
							
					} catch (Exception e) {
							logger.error(e);
				}
				carro_b=carroDevuelto.getBody();
		
		//carro_b=carro_BService.findByCarro_BIdCarro_b(idCarro);



		ModelAndView mav= new ModelAndView("carro_b/datosTarjeta");
		mav.addObject("tarjetaCredito", tarjetaCredito);
		mav.addObject("idCarro", carro_b);
		mav.addObject("total", total);
		return mav;
	}
}

	

