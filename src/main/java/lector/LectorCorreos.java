package lector;

import main.ProcesadorCorreos;

import javax.mail.*;
import java.util.Properties;

public class LectorCorreos {

	private CuentaCorreo cuenta;
	private Session session;
	private Store store;

	public LectorCorreos(CuentaCorreo cta) {
		this.cuenta = cta;
	}

	public Folder abrirInbox() throws LectorException {
		Folder folder;
		try {
			folder = store.getFolder("inbox");
			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
		} catch (MessagingException e) {
			throw new LectorException(e.getMessage(), e);
		}
		return folder;
	}

	public void cerrarFolder(Folder folder) throws LectorException {
		try {
			if (folder.isOpen()) {
				folder.close(false);
			}
		} catch (MessagingException e) {
			throw new LectorException(e.getMessage(), e);
		}
	}

	public void conectar() throws LectorException {
		ProcesadorCorreos.getLogger().info("conectando");
		try {
			store.connect(cuenta.getServidor(), cuenta.getDireccion(), cuenta.getPassword());
		} catch (MessagingException e) {
			throw new LectorException(e.getMessage(), e);
		}
	}

	public void desconectar() throws LectorException {
		ProcesadorCorreos.getLogger().info("desconectando");
		try {
			store.close();
		} catch (MessagingException e) {
			throw new LectorException(e.getMessage(), e);
		}
	}

	public void leerFolder(Folder folder) throws LectorException {
		try {
			int totalMensajes = folder.getMessageCount();
			ProcesadorCorreos.getLogger().info("mensajes en inbox: " + totalMensajes);
			for (int i = 1; i <= totalMensajes; i++) {
				Message msg = folder.getMessage(i);
				ProcesadorCorreos.getLogger().info("mensaje #  " + msg.getSubject());
			}
		} catch (MessagingException e) {
			throw new LectorException(e.getMessage(), e);
		}
	}

	public void leerInbox() throws LectorException {
		setup();
		try {
			conectar();
			Folder folder = abrirInbox();
			leerFolder(folder);
			cerrarFolder(folder);
		} finally {
			desconectar();
		}
	}

	public void setup() throws LectorException {
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
		} catch (NoSuchProviderException e) {
			throw new LectorException(e.getMessage(), e);
		}
	}
}
