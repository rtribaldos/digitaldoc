package com.digitaldoc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.KeyManagerFactory;

import com.digitaldoc.model.AnticipoFile;
import com.digitaldoc.model.CancelFile;
import com.digitaldoc.model.MediaObject;
import com.digitaldoc.model.Usuario;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

public class UtilDigitalDoc {
	
	public static final String KEYSTORE = "src/main/resources/ks";
	public static final char[] PASSWORD = "password".toCharArray();
	public static final String SRC = "src/main/resources/hello.pdf";
	public static final String DEST = "results/chapter2/hello_signed%s.pdf";
	
	public static void cargaMailEmpleado(StringBuffer asunto, StringBuffer cuerpo, Usuario user){
		asunto.append("Su nómina correspondiente al mes de x está disponible");
		cuerpo.append("<html><body>Hola " + user.getNombre() +",<br>");
		cuerpo.append("Su n&oacute;mina correspondiente al mes de x está disponible en nuestro sistema Online, puede consultar y descargarla "
				+ " desde nuestro sistema Online.<br>");
		cuerpo.append("http://www.sercatrans-online.com/");
		cuerpo.append("<br>Les recordamos que toda la documentaci&oacute;n descargada desde nuestro sistema, est&aacute; firmada digitalmente con la firma de Sercatrans "
				+ "Log&iacute;stica 2000 S.L.<br>");
		cuerpo.append("Muchas gracias.");
		cuerpo.append("</body></html>");
	}
	
	public static void cargaMailCliente(StringBuffer asunto, StringBuffer cuerpo, Usuario user, String factura){
		asunto.append("Su factura " + factura +" está disponible");
		cuerpo.append("<html><body>Muy Sres. Nuestros,<br><br>");
		cuerpo.append("Su factura " + factura +" est&aacute; disponible en nuestro sistema Online, puede consultar y descargar "
				+ " toda la documentaci&oacute;n correspondiente a esta factura desde nuestro sistema Online.<br><br>");
		cuerpo.append("http://www.sercatrans-online.com/");
		cuerpo.append("<br>Les recordamos que toda la documentaci&oacute;n descargada desde nuestro sistema, est&aacute; firmada digitalmente con la firma de Sercatrans "
				+ "Log&iacute;stica 2000 S.L.<br><br>");
		cuerpo.append("Recuerde que para entrar en nuestro sistema debe usar su correo como usuario.<br><br>");
		cuerpo.append("Muchas gracias.");
		cuerpo.append("</body></html>");
	}
	
	public static void cargaMailAcreedor(StringBuffer asunto, StringBuffer cuerpo, Usuario user, String factura){
		asunto.append("Solicitud anticipo " + factura +" está disponible");
		cuerpo.append("<html><body>Muy Sres. Nuestros,<br><br>");
		cuerpo.append("Solicitud anticipo " + factura +" est&aacute; disponible en nuestro sistema Online, puede consultar y descargar "
				+ " toda la documentaci&oacute;n correspondiente a esta solicitud desde nuestro sistema Online.<br><br>");
		cuerpo.append("http://www.sercatrans-online.com/");
		cuerpo.append("<br><br>Les recordamos que toda la documentaci&oacute;n descargada desde nuestro sistema, est&aacute; firmada digitalmente con la firma de Sercatrans "
				+ "Log&iacute;stica 2000 S.L.<br><br>");
		cuerpo.append("Recuerde que para entrar en nuestro sistema debe usar su correo como usuario.<br>");
		cuerpo.append("Muchas gracias.");
		cuerpo.append("</body></html>");
	}
	
	
	public static void cargaMailCancel(StringBuffer asunto, StringBuffer cuerpo, Usuario user, String factura){
		asunto.append("SOLICITUD CANCELACION ANTICIPO  " + factura +" est&aacute; disponible");
		cuerpo.append("<html><body>Muy Sres. Nuestros,<br>");
		cuerpo.append("Les solicitamos la cancelaci&oacute;n del anticipo, según el detalle del archivo adjunto. <br> Muchas gracias.<br>");
		cuerpo.append("http://www.sercatrans-online.com/");
		cuerpo.append("</body></html>");
	}
	
	
	public static void enviaCorreo(String email, String sHtml, String subject, byte[] attachmentBytes, String attachmentName, boolean copiaConta){
	 try {	
		 MailService mailService = MailServiceFactory.getMailService();
		 MailService.Message message = new MailService.Message();
		 message.setSender("garceray.online@gmail.com");
		
		 /*
		  * <html><body>Adjuntamos factura</body></html>
		  * 
		  */
		 
		 message.setSubject(subject.toString());
		 
		 List<String> correos = new ArrayList<String>();
		 email = email.replace(";", ",");
		 StringTokenizer st = new StringTokenizer(email, ",");

		 while(st.hasMoreTokens()) {
			 correos.add(st.nextToken());
		 }			 
		 //correos.add(email);
		 message.setTo(correos);
		/* if(copiaConta){
			 message.setCc("contabilidad@sercatrans.com");
		 }else{
			 message.setCc("info@sercatrans.com");
		 }*/
		 
		 if(attachmentBytes != null){
			 MailService.Attachment attachment=new MailService.Attachment(attachmentName,attachmentBytes);
			 message.setAttachments(attachment);
		 }
		 
		// message.setTextBody("Le adjuntamos factura, atentamente Sercatrans.");
		 message.setHtmlBody(sHtml);
		
		 // MailService.Attachment attachment =
		 //        new MailService.Attachment("pic.jpg", picture);
		 //message.setAttachments(attachment);
		 mailService.send(message);
		 } catch (Exception e) {
				e.printStackTrace();
		}
		
	}
	
	public static void enviaCorreo(String email, String sHtml, String subject, Map<String, byte[]> ficheros, String attachmentName, boolean copiaConta){
		 try {	
			 MailService mailService = MailServiceFactory.getMailService();
			 MailService.Message message = new MailService.Message();
			 message.setSender("garceray.online@gmail.com");
			 message.setSubject(subject.toString());
			 
			 List<String> correos = new ArrayList<String>();
			 email = email.replace(";", ",");
			 StringTokenizer st = new StringTokenizer(email, ",");

			 while(st.hasMoreTokens()) {
				 correos.add(st.nextToken());
			 }			 
			 //correos.add(email);
			 message.setTo(correos);
			/* if(copiaConta){
				 message.setCc("contabilidad@sercatrans.com");
			 }else{
				 message.setCc("info@sercatrans.com");
			 }*/
			 
			 if(ficheros != null){
				 List<MailService.Attachment> attachments = new ArrayList<>();
				 for (Map.Entry<String, byte[]> entry : ficheros.entrySet()) {
					 MailService.Attachment attachment=new MailService.Attachment(entry.getKey(),entry.getValue());
					 attachments.add(attachment);
				 }
				 message.setAttachments(attachments);
			 }
			 
			// message.setTextBody("Le adjuntamos factura, atentamente Sercatrans.");
			 message.setHtmlBody(sHtml);
			
			 // MailService.Attachment attachment =
			 //        new MailService.Attachment("pic.jpg", picture);
			 //message.setAttachments(attachment);
			 mailService.send(message);
			 } catch (Exception e) {
					e.printStackTrace();
			}
			
		}

	
	public static byte[] signFactura(MediaObject doc){
		try {
			 InputStreamReader is = new InputStreamReader(new BlobstoreInputStream(doc.getBlob()));
			 BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			 BlobstoreInputStream stream = new BlobstoreInputStream(doc.getBlob());
			 byte[]  bytesArray = new byte[(int) blobInfo.getSize()];
			 stream.read(bytesArray);
			    
			 KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
	         KeyStore keystore = KeyStore.getInstance("PKCS12");
	         char[] password= "sergio".toCharArray();

	         URL resource = UtilDigitalDoc.class.getResource("/resources/sercatrans.pfx");
	 		 File fPfx = new File(resource.toURI());
	         
	         keystore.load(new FileInputStream(fPfx),password);
	         kmf.init(keystore, password);
			 
			 String alias = (String)keystore.aliases().nextElement();
		     PrivateKey pk = (PrivateKey) keystore.getKey(alias, password);
		     Certificate[] chain = keystore.getCertificateChain(alias);
			
		     return sign(bytesArray, chain, pk, DigestAlgorithms.SHA1, "SunJSSE" , CryptoStandard.CMS, 
						"Firma", "Sercatrans");
		     
		   
		     //blobstoreService.serve(blobKey, resp);
		     
			// doc.setBlob(blobKey);
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] signAnticipo(AnticipoFile doc){
		try {
			 InputStreamReader is = new InputStreamReader(new BlobstoreInputStream(doc.getBlob()));
			 BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			 BlobstoreInputStream stream = new BlobstoreInputStream(doc.getBlob());
			 byte[]  bytesArray = new byte[(int) blobInfo.getSize()];
			 stream.read(bytesArray);
			    
			 KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
	         KeyStore keystore = KeyStore.getInstance("PKCS12");
	         char[] password= "sergio".toCharArray();

	         URL resource = UtilDigitalDoc.class.getResource("/resources/sercatrans.pfx");
	 		 File fPfx = new File(resource.toURI());
	         
	         keystore.load(new FileInputStream(fPfx),password);
	         kmf.init(keystore, password);
			 
			 String alias = (String)keystore.aliases().nextElement();
		     PrivateKey pk = (PrivateKey) keystore.getKey(alias, password);
		     Certificate[] chain = keystore.getCertificateChain(alias);
			
		     return sign(bytesArray, chain, pk, DigestAlgorithms.SHA1, "SunJSSE" , CryptoStandard.CMS, 
						"Firma", "Sercatrans");
		     
		   
		     //blobstoreService.serve(blobKey, resp);
		     
			// doc.setBlob(blobKey);
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] signCancelacion(CancelFile doc){
		try {
			 InputStreamReader is = new InputStreamReader(new BlobstoreInputStream(doc.getBlob()));
			 BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(doc.getBlob());
			 BlobstoreInputStream stream = new BlobstoreInputStream(doc.getBlob());
			 byte[]  bytesArray = new byte[(int) blobInfo.getSize()];
			 stream.read(bytesArray);
			    
			 KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
	         KeyStore keystore = KeyStore.getInstance("PKCS12");
	         char[] password= "sergio".toCharArray();

	         URL resource = UtilDigitalDoc.class.getResource("/resources/sercatrans.pfx");
	 		 File fPfx = new File(resource.toURI());
	         
	         keystore.load(new FileInputStream(fPfx),password);
	         kmf.init(keystore, password);
			 
			 String alias = (String)keystore.aliases().nextElement();
		     PrivateKey pk = (PrivateKey) keystore.getKey(alias, password);
		     Certificate[] chain = keystore.getCertificateChain(alias);
			
		     return sign(bytesArray, chain, pk, DigestAlgorithms.SHA1, "SunJSSE" , CryptoStandard.CMS, 
						"Firma", "Sercatrans");
		     
		   
		     //blobstoreService.serve(blobKey, resp);
		     
			// doc.setBlob(blobKey);
	 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static byte[] sign(byte[] src, Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider, CryptoStandard subfilter, 
			String reason, String location)	throws GeneralSecurityException, IOException, DocumentException {
		
        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(src);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setVisibleSignature(new Rectangle(516, 860, 144, 800), 1, "sig");
        // Creating the signature
        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, provider);
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, subfilter);
        
        return os.toByteArray();
	}
 
/*	public static void main(String[] args) throws GeneralSecurityException, IOException, DocumentException, URISyntaxException {
	
		
		 KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
         KeyStore keystore = KeyStore.getInstance("PKCS12");
         char[] password= "sergio".toCharArray();

         URL resource = UtilSerca.class.getResource("/resources/sercatrans.pfx");
 		File file = new File(resource.toURI());
         
         keystore.load(new FileInputStream(file),password);
         //keystore.load(new FileInputStream(certificate), password);
         kmf.init(keystore, password);
		 
		 String alias = (String)keystore.aliases().nextElement();
	     PrivateKey pk = (PrivateKey) keystore.getKey(alias, password);
	     Certificate[] chain = keystore.getCertificateChain(alias);
		
	     String pdf="C:\\Users\\lydia\\Downloads\\autorizacion.pdf";
	     String pdf2="C:\\Users\\lydia\\Downloads\\firmado.pdf";
	     
	     File fPdf = new File(pdf);
	   //init array with file length
	   byte[] bytesArray = new byte[(int) file.length()];

	   FileInputStream fis = new FileInputStream(file);
	   fis.read(bytesArray); //read file into bytes[]
	   fis.close();
	   
	   
	  byte[] firmado= sign(bytesArray, chain, pk, DigestAlgorithms.SHA1, "SunJSSE" , CryptoStandard.CMS, 
				"Firma", "Sercatrans");

	  System.out.println(new String(firmado));
	  
	
		try {
			//String alias = findKeyAlias(ks, myString.getBytes(), password.toCharArray());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			
		
	}*/
	
	
	public static String findKeyAlias(KeyStore store, byte[] storeFile, char[] password) throws Exception {
	   	 
		store.load(new ByteArrayInputStream(storeFile), password);

    	Enumeration e = store.aliases();
    	String keyAlias = null;

    	while (e.hasMoreElements()) {
        	String alias = (String) e.nextElement();

        	if (store.isKeyEntry(alias)) {
            	keyAlias = alias;
        	}
    	}

    	if (keyAlias == null) {
        	throw new IllegalArgumentException("can't find a private key in keyStore");
    	}

    	return keyAlias;
	   		 
	}

	
}
