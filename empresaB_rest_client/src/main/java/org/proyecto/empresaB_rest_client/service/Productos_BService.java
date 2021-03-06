package org.proyecto.empresaB_rest_client.service;

import java.util.List;

import org.proyecto.empresaB_rest_client.model.Producto_B;
import org.springframework.stereotype.Service;


public interface Productos_BService {
	
	public List<Producto_B> getProductos_B();
	public void save(Producto_B producto_B);
	public Producto_B findByProducto_BIdProducto_b(String Producto_BIdProducto_b);
	public void update(Producto_B producto_B);
	void delete(Producto_B producto_B);

}
