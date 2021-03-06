<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<meta name="author" content="Raul Tribaldos"/>
<meta name="description" content="Garceray"/>
<meta name="keywords" content="Garceray, transporte, camiones"/>
<meta name="language" content="spanish"/>

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.digitaldoc.model.*" %>
<%@ page import="com.digitaldoc.dao.DocumentosDAO" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	List<Usuario> listUsuarios = (List<Usuario>) request.getAttribute("listUsuarios");
	List<Documento> listFacturas = (List<Documento>) request.getAttribute("listFacturas");
	Usuario usuario = (Usuario) request.getAttribute("usuario");
		
	Anticipo anticipo = (Anticipo) request.getAttribute("anticipo");
	String urlUpload= blobstoreService.createUploadUrl("/upload");
	
	String styleDivDocs="display: block; visibility: visible;";
	String idAnticipo="0";
	Calendar cal= Calendar.getInstance();
	String fecha= df.format(cal.getTime());
	Usuario acreedor=null;
	String idEmpresa="";
	String habilitado="";
	String tipoAnticipo="nuevoAnticipo";
	String key="";
	String nomFichAnticipo="";
	
	if (anticipo==null){
		styleDivDocs="display: none; visibility: hidden;";
	}else{
		idAnticipo = anticipo.getId();
		tipoAnticipo= "editaAnticipo";
		if(anticipo.getFecha() != null){
			fecha = df.format(anticipo.getFecha());
		}
		habilitado="readonly";
		acreedor = anticipo.getAcreedor();
		idEmpresa= String.valueOf(acreedor.getId());
		if(anticipo.getFichero() != null){
			key=anticipo.getFichero().getDisplayURL(); 
			nomFichAnticipo=anticipo.getFichero().getFilename();
		}
	}
%>

	<title>Garceray</title>

	<meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
    <meta name="viewport" content="width=device-width" />

	<script src="assets/js/jquery-1.10.2.js"></script>
	<script src="assets/js/jquery-ui-1.10.4.custom.js"></script>
	
	<!--   Core JS Files   -->
    
	<script src="assets/js/bootstrap.min.js" type="text/javascript"></script>

	<!--  Checkbox, Radio & Switch Plugins -->
	<script src="assets/js/bootstrap-checkbox-radio-switch.js"></script>

	<!--  Charts Plugin -->
	<script src="assets/js/chartist.min.js"></script>

    <!--  Notifications Plugin    -->
    <script src="assets/js/bootstrap-notify.js"></script>

    <!--  Google Maps Plugin    -->
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>

    <!-- Light Bootstrap Table Core javascript and methods for Demo purpose -->
	<script src="assets/js/light-bootstrap-dashboard.js"></script>

	<!-- Light Bootstrap Table DEMO methods, don't include it in your project! -->
	<script src="assets/js/demo.js"></script>
			
	<script>
		 $.datepicker.regional['es'] = {
			 closeText: 'Cerrar',
			 prevText: '<Ant',
			 nextText: 'Sig>',
			 currentText: 'Hoy',
			 monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
			 monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
			 dayNames: ['Domingo', 'Lunes', 'Martes', 'Mi�rcoles', 'Jueves', 'Viernes', 'S�bado'],
			 dayNamesShort: ['Dom','Lun','Mar','Mi�','Juv','Vie','S�b'],
			 dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','S�'],
			 weekHeader: 'Sm',
			 dateFormat: 'dd/mm/yy',
			 firstDay: 1,
			 isRTL: false,
			 showMonthAfterYear: false,
			 yearSuffix: ''
		};
		$.datepicker.setDefaults($.datepicker.regional['es']);
		$(function () {
			 $( "#datepicker" ).datepicker();
		});
	</script>
	
    <!-- Bootstrap core CSS     -->
    <link href="assets/css/bootstrap.min.css" rel="stylesheet" />

    <!-- Animation library for notifications   -->
    <link href="assets/css/animate.min.css" rel="stylesheet"/>

    <!--  Light Bootstrap Table core CSS    -->
    <link href="assets/css/light-bootstrap-dashboard.css" rel="stylesheet"/>

    <!--  CSS for Demo Purpose, don't include it in your project     -->
    <link href="assets/css/demo.css" rel="stylesheet" />

    <!--     Fonts and icons     -->
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>
    <link href="assets/css/pe-icon-7-stroke.css" rel="stylesheet" />
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
</head>
<body>

<div class="wrapper">
    <div class="sidebar" data-color="green" data-image="assets/img/garceray/logo_camion_color.png">

    <!--   you can change the color of the sidebar using: data-color="blue | azure | green | orange | red | purple" -->


    	<div class="sidebar-wrapper">
            <div class="logo">
                <a href="#" class="simple-text">
                    Administraci&oacute;n 
                </a>
            </div>

             <ul class="nav">
                <li>
                    <a href="/digitalDoc?op=clientes&id=<%=usuario.getId()%>">
                        <i class="pe-7s-user"></i>
                        <p>Usuarios</p>
                    </a>
                </li>
                 <li>
                 			<a href="/digitalDoc?op=facturas&id=<%=usuario.getId()%>">
                     		<i class="pe-7s-news-paper"></i>
                     		<p>Facturas</p>
                	 		</a>
           		</li>
           		<li class="active">
                 		<a href="/digitalDoc?op=anticipos&id=<%=usuario.getId()%>">
                     		<i class="pe-7s-news-paper"></i>
                     		<p>Anticipos</p>
               	 		</a>
           		</li>
           		<li>
                 		<a href="/digitalDoc?op=cancelaciones&id=<%=usuario.getId()%>">
                     		<i class="pe-7s-news-paper"></i>
                     		<p>Cancelaciones</p>
               	 		</a>
           		</li>

            </ul>
    	</div>
    </div>

    <div class="main-panel">
        <nav class="navbar navbar-default navbar-fixed">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#">Nuevo anticipo</a>
                </div>
                
            </div>
        </nav>

        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="header">
                                <h4 class="title">Datos del anticipo</h4>
                            </div>
                            <div class="content">
                            
                              <form action="/digitalDoc" method="post">
                               		<div class="row">
                                        <div class="col-md-9">
                                            <div class="form-group">
                                                <label>Acreedor</label>
                                                <select class="form-control" placeholder="Empresa" required name="empresa" <%=habilitado%>>
                                                	
                                                <%
                                                	if(acreedor != null){
                                                %>
                                                	   <option value=<%=acreedor.getId()%>><%=acreedor.getEmpresa()%></option>
                                                <%		
                                                	}
                                                	for(Usuario cliente : listUsuarios){
                                                		if(cliente.getEmpresa() != null){
                                               	%>
                                               			<option value=<%=cliente.getId()%>><%=cliente.getEmpresa()%></option> 		
                                                <%		
                                                		}
                                                	}
                                                
                                                %>
                                                
                                                </select>
                                                
                                            </div>
                                        </div>
                                       
                                        
                                        
                                    </div>
                                    <div class="row">
                                    	<div class="col-md-3">
                                            <div class="form-group">
                                                <label>Fecha</label>
                                                <input type="text" class="form-control" placeholder="Fecha" required  id="datepicker"                         
                                                name="fecha" value="<%=fecha%>">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label>Id</label>
                                                <input type="text" class="form-control" placeholder="idAnticipo" name="idAnticipo" required value="<%=idAnticipo%>" 
                                                <%=habilitado%>>
                                            </div>
                                        </div>
                                    	
                                    </div>
									
									<input type="hidden" name="op" value="<%=tipoAnticipo%>"/>
									<input type="hidden" name="id" value="<%=usuario.getId()%>"/>
									<input type="hidden" name="idAnticipo" value="<%=idAnticipo%>"/>
									<button type="submit" class="btn btn-info btn-fill pull-right">Guardar datos</button>
                                    <div class="clearfix"></div>
                                </form>
                                
                                <br>
                                
                                                               
                            </div>
                            
                            <div class="header" style="<%=styleDivDocs%>">
                                
                            	<form action="<%= urlUpload %>" method="post" enctype="multipart/form-data" id="f2" name="f2">
									<div class="form-group">
                                      <label for="fichero2">Fichero de anticipo >> </label>
                                      
                                      <%
                                     // 	if(!"".equals(nomFichAnticipo)){
                                      %>
                                      	 <a target="_blank" href="<%=key%>"><%=nomFichAnticipo %></a>
                                      <%
                                      	//}
                                      %>
                                      
                                      <input type="file" id="ficheroAnticipo" name="ficheroAnticipo">
                                  	</div>	
									
									<input type="hidden" name="id" value="<%=usuario.getId()%>"/>
									<input type="hidden" name="idAnticipo" value="<%=idAnticipo%>"/>
									<input type="hidden" name="empresa" value="<%=idEmpresa%>"/>
									<input type="hidden" name="op" value="ediAnticipo"/>
									<button type="submit" class="btn btn-info btn-fill pull-right">Guardar Documento</button>
                                    <div class="clearfix"></div>
                                </form>
                            
                            </div>
                            
                            <div class="header" style="<%=styleDivDocs%>">
                                <h4 class="title">Facturas</h4>
                            </div> 
                            <div class="content" style="<%=styleDivDocs%>">
                                
                                   <table id="tabla" class="table table-hover" width="100%">
					      
								     	 <thead>
			                                    <th>Factura</th>
			                                    <th>Borrar</th>
			                                    
			                            </thead>
			                             <tbody>
                                      
								            <%
								            if(anticipo != null && anticipo.getFacturas() != null){
								             
								             for(String factura: anticipo.getFacturas()){
								            	 String rutaBorra="/digitalDoc?op=borraFacAnt&id=" + usuario.getId() + "&idAnticipo=" 
								             		+ anticipo.getId() + "&idFactura=" + factura ;
								            	 String url=DocumentosDAO.getUrlAnticipo(factura);
								            	 System.out.println("*url: " + url);
								            %>
								            
								        	<tr> 
								                <td><a target="_blank" href="<%=url%>" ><%=factura%></a></td>
								               	<td><a href="<%=rutaBorra%>" class="confirmation"><i class="pe-7s-trash"></i></a></td>
								            </tr>
								        
								            <%
								             }
								            }
								            
								            %>
								        	
								            
								             </tbody>
                			  
								        
			                            </table>   
			                            
			                            <br>
			                             
                                        <form action="/digitalDoc" method="post">
											<div class="form-group">
		                                      <label for="factura">A�ade Factura</label>
		                                      <select class="form-control" placeholder="Factura" required name="idFactura">
                                                <%
                                              		for(Documento doc : listFacturas){
                                            	%>
                                           			<option value=<%=doc.getId()%>><%=doc.getId()%> - <%=doc.getUsuario().getEmpresa() %></option> 		
                                            	<%		
                                            		
                                            		}
                                            
                                            	%>
                                            </select>
                                            </div>	
											
											<input type="hidden" name="op" value="subeFactAnticipo"/>
											<input type="hidden" name="id" value="<%=usuario.getId()%>"/>
											<input type="hidden" name="idAnticipo" value="<%=idAnticipo%>"/>
											<button type="submit" class="btn btn-info btn-fill pull-right">Insertar Factura</button>
		                                    <div class="clearfix"></div>
		                                </form>
                                   
                                   
								
                                
                                <br>
                                
                                                               
                            </div>
                            
                            
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>


        <footer class="footer">
            <div class="container-fluid">
                <p class="copyright pull-right">
                    &copy; 2020 <a target="_blank" href="https://www.garceray.com">Garceray</a>
                </p>
            </div>
        </footer>

    </div>
</div>


</body>

   
</html>
