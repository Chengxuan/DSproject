package ie.gmit;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteDataProServiceImpl extends UnicastRemoteObject implements
		RemoteDataProService {
	// implementation of RMI interface
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteDataProcessor srdp; // string processor
	private RemoteDataProcessor frdp; // file processor

	protected RemoteDataProServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public RemoteDataProcessor getStringProcessor() throws RemoteException {
		// initialize srdp as a string processor
		srdp = new RemoteDataProcessorImpl(0);
		return this.srdp;
	}

	@Override
	public RemoteDataProcessor getFileProcessor() throws RemoteException {
		// initialize frdp as a file processor
		frdp = new RemoteDataProcessorImpl(1);
		return this.frdp;
	}

}
