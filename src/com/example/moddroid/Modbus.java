package com.example.moddroid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

/**
 * Allows people to read data from VR910 Modbus Server
 * 
 * Must configure it according to this.
 * <p><a href="https://github.com/rcandell/tesim_nivis_setup">https://github.com/rcandell/tesim_nivis_setup</a>
 * @author Rushad Antia
 *
 */
public class Modbus {

	public static final int DEFAULT_PORT = 502;

	private static  AtomicBoolean GO = new AtomicBoolean(true);

	private TCPMasterConnection masterConnection;

	/**
	 * Creates an instance of MODBUS class
	 * @param gatewayIP - the IP of the modbus server
	 * @param port - port number use <tt>Modbus.DEFAULT_PORT<tt>
	 */
	public Modbus(String gatewayIP, int port) {

		try {		
			masterConnection = new TCPMasterConnection(InetAddress.getByName(gatewayIP));
			masterConnection.setPort(port);
			masterConnection.connect();

		} catch (UnknownHostException e) {
			System.out.println("Invalid IP");
		} catch (Exception e) {
			System.out.println("Cannot Connect");
		}
	}

	/**
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * <p> The device state must be a 2 otherwise it will not work 
	 * 
	 * <p>{START ADDRESS, WORD COUNT, EUI64,REGISTER TYPE,BURST MESSAGE,DEVICE VARIABLE CODE,DEVICE STATE}<p>
	 * 
	 * @param startAddress - the address of the input register
	 * @return the current of a sensor in mA (-1 if error)
	 */
	public synchronized float getDataFromInputRegister(int startAddress) {

		ModbusTCPTransaction transaction = new ModbusTCPTransaction(masterConnection);

		try {
			transaction.setRequest(new ReadInputRegistersRequest(startAddress, 3));
			transaction.setReconnecting(true);

			transaction.execute();

			ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
			String[] payload = response.getHexMessage().split(" ");

			String currentString = "";

			for(int i=11;i<=14;i++)
				currentString+=payload[i];

			return Utils.hexToFloat(currentString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}
	/**
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * <p> The device state must be a 2 otherwise it will not work 
	 * 
	 * <p>{START ADDRESS, WORD COUNT, EUI64,REGISTER TYPE,BURST MESSAGE,DEVICE VARIABLE CODE,DEVICE STATE}<p>
	 * 
	 * @param startAddress - the address of the holding register
	 * @return the current of a sensor in mA (-1 if error)
	 */
	public float getDataFromHoldingRegister(int startAddress) {

		ModbusTCPTransaction transaction = new ModbusTCPTransaction(masterConnection);

		try {
			transaction.setRequest(new ReadMultipleRegistersRequest(startAddress, 3));
			transaction.setReconnecting(true);
			transaction.execute();

			ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
			String[] payload = response.getHexMessage().split(" ");

			String currentString = "";

			for(int i=11;i<=14;i++)
				currentString+=payload[i];

			return Utils.hexToFloat(currentString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}


	private void findPollTime(int startaddress) {
		float prev = 0;
	
		
		long start = System.currentTimeMillis();
		
		while(GO.get()) {
			float newf = getDataFromInputRegister(startaddress);
			if(newf!=prev) {
				prev = newf;
				System.out.println("Address: "+startaddress+ " Data: "+newf+ " Time Taken: " + (System.currentTimeMillis()-start)/1000.0);
				start=System.currentTimeMillis();
			}
			continue;
		}
	}
	
	/**
	 * polls the addresses in the array forever until terminate is called
	 * called on a thread so it returns
	 * @param startaddress
	 */
	public void startFindingPollTimes(int... addresses) {
		GO = new AtomicBoolean(true);
		for(final int i : addresses) {
			new Thread(new Runnable() {
				public void run() {
					findPollTime(i);
				}
			}).start();
		}

	}

	/**
	 * Ends all polling 
	 */
	public void terminatePolling() {
		if (GO.get()==false) 
			throw new IllegalAccessError("Must call startfindpollingtimes");	

		GO = new AtomicBoolean(false);
	}


}
