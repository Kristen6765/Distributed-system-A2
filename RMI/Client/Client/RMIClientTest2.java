package Client;

import Server.Interface.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.io.*;

public class RMIClientTest2 extends Client implements Runnable
{
    private static String s_serverHost = "localhost";
    private static int s_serverPort = 12345;
    private static String s_serverName = "Middleware";
    private static String s_rmiPrefix = "group_24_";

    private int num_clients = 10;
    private int desired_thput = 1;
    private long timer = 0;
    private int x = 10;
    private List<double[]> testRes = new ArrayList<>();

    public static void main(String args[])
    {
        if (args.length > 0)
        {
            s_serverHost = args[0];
        }
        if (args.length > 1)
        {
            s_serverName = args[1];
        }
        if (args.length > 2)
        {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
            System.exit(1);
        }

        // Set the security policy
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        // Get a reference to the RMIRegister
        try {
            RMIClientTest2[] clientTests = new RMIClientTest2[num_clients];
            Thread[] clientThreads = new Thread[num_clients];
            for(int i=0; i<num_clients; i++){
                clientTests[i]= new RMIClientTest2();
                clientTests[i].connectServer();

                //TODO: add return
              //  List<double> res = clientTests[i].m_resourceManager.start();

                clientTests[i].setTimer();
                clientThreads[i] = new Thread(clientTests[i]);
                clientThreads[i].start();
            }
//            RMIClient client = new RMIClient();
//            client.connectServer();
//            client.start();
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run(){
        int timeInterval = num_clients * 1000 / desired_thput;
        while(this.timer + 3000 < (new Data()).getTime()) {
        }
        boolean plus = true;
        for(int i=0; i<50; i++){
            if(plus) timeInterval += x;
            else timeInterval -= x;
            plus = !plus;
            try{
                double res = singleTest();
                if(timeInterval - res < 0) continue;
                else {
                    testRes.add(singleRMTest());
                    Thread.sleep(timeInterval-res);
                }
            } catch(Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public RMIClientTest2() {
        super();
    }

    public double[] singleRMTest(){

    }

    public int setTimer(){
        this.timer = (new Data()).getTime();
    }

    public void connectServer()
    {
        connectServer(s_serverHost, s_serverPort, s_serverName);
    }

    public void connectServer(String server, int port, String name)
    {
        try {
            boolean first = true;
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(server, port);
                    m_resourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                    System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
                    break;
                }
                catch (NotBoundException|RemoteException e) {
                    if (first) {
                        System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
                        first = false;
                    }
                }
                Thread.sleep(500);
            }
        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

