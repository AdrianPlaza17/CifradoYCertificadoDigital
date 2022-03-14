import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.Cipher;
 
public class Principal {
	public static void main(String[] args) {
		Firma mf;
		try {
			FileInputStream fichero = new FileInputStream("firma.obj");
			ObjectInputStream buffer = new ObjectInputStream(fichero);
			mf = (Firma) buffer.readObject();
			buffer.close();
			fichero.close();
		} catch (Exception e) {
			System.out.println("Error al leer fichero de firma digital");
			System.out.println("Excepción de tipo: " + e.getClass().getName());
			System.out.println(e.getMessage());
			return;
		}
		Scanner lector = new Scanner(System.in);
		try {
			Signature firmadorVerificador = Signature.getInstance("SHA1withRSA");
			firmadorVerificador.initVerify(mf.getClavePublica());
			System.out.println("¿Cuál es el mensaje secreto?");
			//MENSAJE SECRETO: Campusfp2022
			String mensajeSecreto = lector.nextLine();
			firmadorVerificador.update(mensajeSecreto.getBytes());
			boolean ok = firmadorVerificador.verify(mf.getFirma());
			if (ok) {
				System.out.println("Verificación OK, Bienvenido al sistema");
				crearMensajeCifrado(mf.getClavePublica(), lector);
			} else {
				System.out.println("Certificacion erronea");
			}
			lector.close();
		} catch (GeneralSecurityException e) {
			System.out.println("No tienes acceso al sistema de generacion de mensajes");
			System.out.println("Excepción de tipo: " + e.getClass().getName());
			System.out.println(e.getMessage());
			lector.close();
			return;
		}
	}
	
	
 
	private static void crearMensajeCifrado(PublicKey clavePublica, Scanner lector) {

		try {
			Cipher cifrador = Cipher.getInstance("RSA");
			cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);
			ArrayList<byte[]> mensajes = new ArrayList<byte[]>();
			ArrayList<String> mensajesCadena = new ArrayList<String>();
			String mensaje="";
			String respuesta="";
			boolean salir=false;
			while(salir != true) {
				System.out.println("Escribe un mensaje: (escribe 0 para salir)");
				mensaje = lector.nextLine();
				
			if(mensaje.equals("0")) {
				System.out.println("Estos son los mensajes");
				for (int i = 0; i < mensajesCadena.size(); i++) {
					System.out.println(mensajesCadena.get(i));
				}
				
				System.out.println("¿Quieres añadirlos?(Si/No)");
				respuesta = lector.nextLine();
				if(respuesta.equalsIgnoreCase("Si")) {
					FileOutputStream fichero = new FileOutputStream("mensajes.dat");
					ObjectOutputStream buffer = new ObjectOutputStream(fichero);
					buffer.writeObject(mensajes);
					buffer.close();
					fichero.close();
					System.out.println("Mensajes cifrados");
					System.out.println("Los mensaje se han grabado en el fichero mensajes.dat");
					salir = true;
					System.out.println("Fin del programa");
				}else {
					salir = true;
					System.out.println("Fin del programa");
				}
				
			}
			
			byte[] bytesMensajeCifrado = cifrador.doFinal(mensaje.getBytes());
			
			mensajes.add(bytesMensajeCifrado);	
			mensajesCadena.add(mensaje);
			}
			
		} catch (Exception e) {
			System.out.println("Error al grabar el fichero mensajes.dat");
			System.out.println("Excepción de tipo: " + e.getClass().getName());
			System.out.println(e.getMessage());
		}
	}
}
