package ie.gmit;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDataProService extends Remote, Serializable {
	// RMI service Interface
	public RemoteDataProcessor getStringProcessor() throws RemoteException;

	public RemoteDataProcessor getFileProcessor() throws RemoteException;
}
