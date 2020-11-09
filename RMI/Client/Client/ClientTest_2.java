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

    private static int num_clients = 10;
    private static int desired_thput = 1;
    private long timer = 0;
    private int x = 10;
    private static List<long[]> testRes = new ArrayList<>();

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

            for(int i=0; i<num_clients; i++){
                clientThreads[i].join();
            }

            long total_res = 0;
            long total_mdw = 0;
            long total_rm = 0;
            long total_db = 0;

            for(int i=0; i<testRes.size(); i++) {
                total_res += testRes.get(i)[0];
                total_mdw += testRes.get(i)[1];
                total_rm += testRes.get(i)[2];
                total_db += testRes.get(i)[3];
            }

            System.out.println("average response time : " + total_res/testRes.size());
            System.out.println("average mdw time : " + total_mdw/testRes.size());
            System.out.println("average rm time : " + total_rm/testRes.size());
            System.out.println("average db time : " + total_db/testRes.size());
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
        while(this.timer + 3000 < (new Date()).getTime()) {
        }
        boolean plus = true;
        for(int i=0; i<50; i++){
            int xid = (int)Thread.currentThread().getId()+i+1;
            System.out.println("XID: "+xid);
            if(plus) timeInterval += x;
            else timeInterval -= x;
            plus = !plus;
            try{
                long[] res = runE1SingleRM(xid);
                if(timeInterval - res[0] < 0) continue;
                else {
                    testRes.add(res);
                    Thread.sleep(timeInterval-res[0]);
                }
            } catch(Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public long[] runE1SingleRM(int xid) throws Exception {
        long startTime = System.currentTimeMillis();
        // int xid = m_resourceManager.start();
        ArrayList<long[]> total = new ArrayList<>();

        long[] start = m_resourceManager.start();
        if(start[0] == -1.0) {
            System.out.println("Error starting! Aborted");
            return new long[]{-1, -1, -1};
        } else {
            total.add(start);
            total.add(m_resourceManager.addCars(xid, "Montreal", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Montreal"));
            total.add(m_resourceManager.addCars(xid, "Toronto", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Toronto"));
            total.add(m_resourceManager.addCars(xid, "Vancouver", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Vancouver"));
            total.add(m_resourceManager.addCars(xid, "Ottawa", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Ottawa"));
            total.add(m_resourceManager.addCars(xid, "Waterloo", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Waterloo"));
            total.add(m_resourceManager.commit(xid));

            long endTime = System.currentTimeMillis();
            long totalResponseTime = endTime - startTime;
            long MDWTime = 0;
            long totalRMDBTime = 0;
            long totalRMTime = 0;
            long totalDBTime = 0;

            for (int i = 0; i < 11; i++) {
//                System.out.println(i);
                System.out.println(total.get(i)[0]);
                if(i!=0) MDWTime += total.get(i)[0];
                totalRMDBTime += total.get(i)[1];
                if(i!=0) totalDBTime += total.get(i)[2];
            }

            totalRMTime = totalRMDBTime - totalDBTime;

            System.out.println("totalResponseTime : " + totalResponseTime);
            System.out.println("totalMDWTime : " + MDWTime);
            System.out.println("totalRMDBTime : " + totalRMDBTime);
            System.out.println("totalRMTime : " + totalRMTime);
            System.out.println("totalDBTime : " + totalDBTime);
//            System.out.println("totalCommunicationTime : " + totalCommunicationTime);
            return new long[]{totalResponseTime, MDWTime, totalRMTime, totalDBTime};
        }
    }


    public long[] runE1MultipleRM(int xid) throws Exception {
        long startTime = System.currentTimeMillis();
        // int xid = m_resourceManager.start();
        ArrayList<long[]> total = new ArrayList<>();

        long[] start = m_resourceManager.start();
        if(start[0] == -1.0) {
            System.out.println("Error starting! Aborted");
            return new long[]{-1, -1, -1};
        } else {
            total.add(start);
            total.add(m_resourceManager.addCars(xid, "Montreal", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Montreal"));
            total.add(m_resourceManager.addRooms(xid, "Toronto", 1000, 1000));
            total.add(m_resourceManager.queryRooms(xid, "Toronto"));
            total.add(m_resourceManager.addFlight(xid, 1, 1000, 1000));
            total.add(m_resourceManager.queryFlight(xid, 1));
            total.add(m_resourceManager.addCars(xid, "Ottawa", 1000, 1000));
            total.add(m_resourceManager.queryCars(xid, "Ottawa"));
            total.add(m_resourceManager.addFlight(xid, 2, 1000, 1000));
            total.add(m_resourceManager.queryFlight(xid, 2));
            total.add(m_resourceManager.commit(xid));

            long endTime = System.currentTimeMillis();
            long totalResponseTime = endTime - startTime;
            long MDWTime = 0;
            long totalRMDBTime = 0;
            long totalRMTime = 0;
            long totalDBTime = 0;

            for (int i = 0; i < 11; i++) {
                System.out.println(i);
                System.out.println(total.get(i)[0]);
                if(i!=0) MDWTime += total.get(i)[0];
                totalRMDBTime += total.get(i)[1];
                if(i!=0) totalDBTime += total.get(i)[2];
            }

            totalRMTime = totalRMDBTime - totalDBTime;

            System.out.println("totalResponseTime : " + totalResponseTime);
            System.out.println("totalMDWTime : " + MDWTime);
            System.out.println("totalRMDBTime : " + totalRMDBTime);
            System.out.println("totalRMTime : " + totalRMTime);
            System.out.println("totalDBTime : " + totalDBTime);
//            System.out.println("totalCommunicationTime : " + totalCommunicationTime);
            return new long[]{totalResponseTime, MDWTime, totalRMTime, totalDBTime};
        }
    }


    public void setTimer(){
        this.timer = (new Date()).getTime();
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

