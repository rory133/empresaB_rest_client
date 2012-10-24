package org.proyecto.empresaB_rest_client.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="producto_bSeleccionado")
@Entity
@Table(name = "producto_bseleccionado")
public class Producto_BSeleccionado implements Serializable {
	
	private static final long serialVersionUID = -5527566206302296042L;
	
	  private Integer idproductoSeleccionado;
	  private Integer cantidad;
	  private Producto_B producto_b;
	  private Carro_B carro_b;
	  private BigDecimal subTotal;
	  
	  
	  
	  public Producto_BSeleccionado() {

		}
	  
		
	@Column(name = "CANTIDAD")
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}



	@Column(name = "SUBTOTAL")
	  public BigDecimal getSubTotal() {
		return subTotal;
	}


	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	@Id
	  @Column(name = "IDPRODUCTOSELECCIONADO", unique = true, nullable = false)	 
	  @GeneratedValue
	public Integer getIdproductoSeleccionado() {
		return idproductoSeleccionado;
	}


	public void setIdproductoSeleccionado(Integer idproductoSeleccionado) {
		this.idproductoSeleccionado = idproductoSeleccionado;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idproductoB", nullable = false)
	public Producto_B getProducto_b() {
		return producto_b;
	}

	public void setProducto_b(Producto_B producto_b) {
		this.producto_b = producto_b;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IDCARRO_B", nullable = false)
	public Carro_B getCarro_b() {
		return carro_b;
	}


	public void setCarro_b(Carro_B carro_b) {
		this.carro_b = carro_b;
	}
  
	  
	 
}

