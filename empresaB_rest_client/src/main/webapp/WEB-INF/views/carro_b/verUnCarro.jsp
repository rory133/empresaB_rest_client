<%@ page session="true"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %> 
<%@ taglib prefix='sec' uri='http://www.springframework.org/security/tags' %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
 
<%-- <link type="text/css" rel="stylesheet"  href="${pageContext.request.contextPath}/resources/css/master.css" /> --%>
<!-- <link type="text/css" rel="stylesheet"  href="../resources/css/master.css" /> -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LISTA DE PRODUCTOS EN EL CARRO</title>

<style type="text/css">
h1 {color:#3B0B0B;}
p {color:blue;}
h4 {color:#B40431;}
</style>





</head>



<!-- <style type="text/css">body{background-color:red;}</style> -->
<h4> ${errorCarroVacio}	</h4>

<h1> Lista de productos en el carro</h1>





<c:if  test="${!empty productosSeleccionados}">

<c:set var="total" value="${0}" scope="page" />
<c:set var="fila" value="${0}" scope="page" />


<table class="table">
<tr>
	<th>FILA</th>
	<th>IMAGEN</th>
    <th>CODIGO PRODUCTO</th>
    <th>CANTIDAD PEDIDA</th>
     <th>PRECIO</th>
     <th>SUBTOTAL</th>

  
</tr>

<c:forEach items="${productosSeleccionados}" var="productosSeleccionados">
    <tr>
   
        <td>  
               ${fila+1} 
   		</td>

        <td>
           
             


              <c:set var="variable" value="${pageContext.request.contextPath}/imagen/${productosSeleccionados.idproducto_b}.jpg" />              
              <img  src="${variable}"width="50" height="50" /> 
              

              




        </td>

        <td>
                ${productosSeleccionados.nombreProducto}
        </td>
        <td>
                ${productosSeleccionados.cantidad}
        </td>
        
         <td>
                 ${productosSeleccionados.precio_b}
         
         </td>

                
       <td>
                ${productosSeleccionados.subTotal}
                 <c:set var="total" value="${total+ productosSeleccionados.subTotal}" scope="page" />
        </td>
        

		
		


	<c:set var="fila" value="${fila+1}" scope="page" />
    </tr>
</c:forEach>
        <sec:authorize access="hasRole('ROLE_CLIENTE')">

       </sec:authorize> 
        

</table>
 <h4>total a pagar:  <c:out value="${total}" /></h4>
</c:if>
<c:if  test="${empty productosSeleccionados}">
		<h4>el carro esta vacio</h4>	

</c:if>

</body>
</html>