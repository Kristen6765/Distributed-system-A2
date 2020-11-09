package Server.Middleware;

import Server.TransactionManager.*;
import Server.LockManager.*;
import java.util.Vector;
import java.rmi.ConnectException;


import Server.Interface.*;
import java.util.*;
import java.io.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;


import Server.Common.*;


public class Middleware extends ResourceManager {

    public static final String FLIGHT_RM = "Flight";
    public static final String ROOM_RM = "Room";
    public static final String CAR_RM = "Car";
    public static final String CUSTOMER_RM = "Customer";

    protected static ServerConfig s_flightServer;
    protected static ServerConfig s_carServer;
    protected static ServerConfig s_roomServer;


    protected IResourceManager flightRM = null;
    protected IResourceManager carRM = null;
    protected IResourceManager roomRM = null;

    protected TransactionManager traxManager;
    protected LockManager lockManager;

    public Middleware(String p_name)
    {
        super(p_name);

        lockManager = new LockManager();
        traxManager = new TransactionManager();
        this.setTransactionManager(traxManager);
    }

    public long[] start() throws RemoteException{
        long time = getCurrentTime();
        int xid  = traxManager.start();
        Trace.info("Start transaction .... " + xid);
        return new long[] {xid, getCurrentTime() - time};
    }


    public long[] commit(int xid) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        if(!traxManager.isActive(xid))
            throw new InvalidTransactionException(xid, "Middleware commit: transaction is not active");
        Transaction t = traxManager.getActiveTransaction(xid);
        RMHashMap m = t.get_TMPdata();
        boolean[] relatedRM = t.getRelatedRMs();

        if (relatedRM[0]){
            synchronized (flightRM.m_data ){
                Set<String> keyset = m.keySet();
                for (String key : keyset) {
                    System.out.println("Write:(" + key + "," + m.get(key) + ")");
                    flightRM.m_data.put(key, m.get(key));
                }
            }
        }
        if (relatedRM[1]){
            synchronized (flightRM.m_data) {
                Set<String> keyset = m.keySet();
                for (String key : keyset) {
                    System.out.println("Write:(" + key + "," + m.get(key) + ")");
                    flightRM.m_data.put(key, m.get(key));
                }
            }
        }
        if (relatedRM[2]){
            synchronized (flightRM.m_data) {
                Set<String> keyset = m.keySet();
                for (String key : keyset) {
                    System.out.println("Write:(" + key + "," + m.get(key) + ")");
                    flightRM.m_data.put(key, m.get(key));
                }
            }
        }

        //if it is customer, we need all resources managers to work
        if (relatedRM[0] && relatedRM[1] && relatedRM[2]) {
            synchronized (m_data) {
                Set<String> keyset = m.keySet();
                for (String key : keyset) {
                    System.out.println("Write:(" + key + "," + m.get(key) + ")");
                    m_data.put(key, m.get(key));
                }
            }
        }

        traxManager.removeActiveTransaction(xid);
        lockManager.UnlockAll(xid);
        return new long[] {-1000, -1000, -1000};
    }


//    public long[] commit(int xid) throws RemoteException,TransactionAbortedException, InvalidTransactionException
//    {
//
//
//        long time0=getCurrentTime();
//        long timeCar0=0;
//        long timeCar1=0;
//        long timeFlight0=0;
//        long timeFlight1=0;
//        long timeRoom0=0;
//        long timeRoom1=0;
//
//        Transaction t = traxManager.getActiveTransaction(xid);
//
//        if(t == null){
//           // return false;
//           return new long [] {-1.0};
//        }
//        boolean[] relatedRM = t.getRelatedRMs();
//
//        long arrFlight[]=new long[2];
//        long arrRoom[]=new long[2];
//        long arrCar[]=new long[2];
//
//        if (relatedRM[0]){
//                timeFlight0=getCurrentTime();
//                long tf1=getCurrentTime();
//                if(!traxManager.isActive(xid))
//                throw new InvalidTransactionException(xid, "RM: Not a valid transaction");
//
//                Transaction transaction = traxManager.getActiveTransaction(xid);
//                RMHashMap m = transaction.get_TMPdata();
//
//                long tf2=getCurrentTime();
//                synchronized (flightRM.m_data ){
//                    Set<String> keyset = m.keySet();
//                    for (String key : keyset) {
//                        System.out.println("Write:(" + key + "," + m.get(key) + ")");
//                        flightRM.m_data.put(key, m.get(key));
//                    }
//                }
//                 long tf3=getCurrentTime();
//                traxManager.removeActiveTransaction(xid);
//                timeFlight1=timeFlight0-getCurrentTime();
//
//
//                arrFlight[0]=getCurrentTime()-tf1;
//                arrFlight[1]=tf3-tf2;
//
//
//        }
//        if (relatedRM[1]){
//                timeRoom0=getCurrentTime();
//                long tr1=getCurrentTime();
//                if(!traxManager.isActive(xid))
//                throw new InvalidTransactionException(xid, "RM: Not a valid transaction");
//
//                Transaction transaction = traxManager.getActiveTransaction(xid);
//                RMHashMap m = transaction.get_TMPdata();
//
//
//                long tr2=getCurrentTime();
//                synchronized (roomRM.m_data) {
//                    Set<String> keyset = m.keySet();
//                    for (String key : keyset) {
//                        System.out.println("Write:(" + key + "," + m.get(key) + ")");
//                        roomRM.m_data.put(key, m.get(key));
//                    }
//                }
//                long tr3=getCurrentTime();
//                traxManager.removeActiveTransaction(xid);
//                timeRoom1=timeRoom0-getCurrentTime();
//
//                arrRoom[0]=getCurrentTime()-tr1;
//                arrRoom[1]=tr3-tr2;
//
//        }
//        if (relatedRM[2]){
//                timeCar0=getCurrentTime();
//                long tc1=getCurrentTime();
//                if(!traxManager.isActive(xid))
//                throw new InvalidTransactionException(xid, "RM: Not a valid transaction");
//
//                Transaction transaction = traxManager.getActiveTransaction(xid);
//                RMHashMap m = transaction.get_TMPdata();
//
//                long tc2=getCurrentTime();
//                synchronized (carRM.m_data) {
//                    Set<String> keyset = m.keySet();
//                    for (String key : keyset) {
//                        System.out.println("Write:(" + key + "," + m.get(key) + ")");
//                        carRM.m_data.put(key, m.get(key));
//                    }
//                }
//                long tc3=getCurrentTime();
//                traxManager.removeActiveTransaction(xid);
//                timeCar1=timeCar0-getCurrentTime();
//
//                arrCar[0]=getCurrentTime()-tc1;
//                arrCar[1]=tc3-tc2;
//
//        }
//
//        long [] timesr=new long[]{0,0};
//
//        timesr=helperCaltime(arrRoom, arrFlight, arrCar,timesr);
//
//
//        //if it is customer, we need all resources managers to work
//        if (relatedRM[0] && relatedRM[1] && relatedRM[2]) {
//            // RMHashMap m = t.getData();
//            // synchronized (m_data) {
//            //     Set<String> keyset = m.keySet();
//            //     for (String key : keyset) {
//            //         System.out.print("Write:(" + key + "," + m.get(key) + ")");
//            //         m_data.put(key, m.get(key));
//            //     }
//            // }
//        }
//        endTransaction(xid, true);
//
//
//       long endTime=getCurrentTime()- time0;
//
//        if (timeCar1 > 0) endTime -= timeCar1;
//        if (timeRoom1 > 0) endTime -= timeRoom1;
//        if (timeFlight1 > 0) endTime -= timeFlight1;
//
//
//        return new long [] {endTime, timesr[0], timesr[1]};
//       // return true; 默认为true????
//    }

    public long[] helperCaltime(long [] arrRoom, long [] arrFlight, long [] arrCar,  long [] timesr){

        if(arrRoom!=null){
            timesr[0]=timesr[0]+arrRoom[0];
            timesr[1]=timesr[1]+arrRoom[1];
        }

        if(arrFlight!=null){
            timesr[0]=timesr[0]+arrFlight[0];
            timesr[1]=timesr[1]+arrFlight[1];
        }

        if(arrCar!=null){
            timesr[0]=timesr[0]+arrCar[0];
            timesr[1]=timesr[1]+arrCar[1];
        }


        return timesr;
    }

    // public boolean abort(int xid) throws RemoteException, InvalidTransactionException {
    //     System.out.println("Abort transaction:" + xid);
    //     try {
    //         //checkTransaction(xid);
    //     } catch(TransactionAbortedException e) {
    //         throw new InvalidTransactionException(xid, "transaction has been aborted already");
    //     }

    //     Transaction t = traxManager.getActiveTransaction(xid);

    //     if(t == null){
    //         //TODO: print error
    //         return false;
    //     }

    //      boolean[] relatedRM = t.getRelatedRMs();

    //  //   Trace.info("Resource=" + resources);
    //     if (relatedRM[0]){
    //         flightRM.abort(xid);
    //     }
    //     if (relatedRM[1]){
    //         carRM.abort(xid);
    //     }
    //     if (relatedRM[2]){
    //          roomRM.abort(xid);
    //     }
    //     endTransaction(xid, false);
    //     return true;
    // }

    private void endTransaction(int xid, boolean commit) throws RemoteException {
        // Move to inactive transactions
        // TODO: remove commit parameter
        traxManager.removeActiveTransaction(xid);
        // traxManager.writeActiveData(xid, null);
        // traxManager.writeInactiveData(xid, new Boolean(commit));

        lockManager.UnlockAll(xid);
    }

    // private void updateTimeToLive(int xid) {.
    //     traxManager.readActiveData(xid).updateLastAction();
    // }

    public boolean shutdown() throws RemoteException {
       
        carRM.shutdown();
        roomRM.shutdown();
        flightRM.shutdown();

        new Thread() {
            @Override
            public void run() {
                System.out.print("Shutting down...");
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                }
                System.out.println("done");
                System.exit(0);
            }

        }.start();
        return true;
    }

    public  long[] addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException,TransactionAbortedException, InvalidTransactionException {
        long time0=getCurrentTime();
        int id = xid;
        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();

        Trace.info("addFlight - Redirect to Flight Resource Manager");
        //checkTransaction(id);
        lockData(id, Flight.getKey(flightNum), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,FLIGHT_RM);

        long time1=getCurrentTime();
        long r[]= flightRM.addFlight(id, flightNum, flightSeats, flightPrice);
        long time2=getCurrentTime()-time1;
        return new long[] {time1- time0,r[0], r[1] };
       // return flightRM.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    public long[] addCars(int xid, String location, int numCars, int price) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {


        long time0=getCurrentTime();
        int id = xid;

        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();

        Trace.info("addCars - Redirect to Car Resource Manager");
        //checkTransaction(id);
        lockData(id, Car.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,CAR_RM);

        long time1=getCurrentTime();
        long r[]= carRM.addCars(id, location, numCars, price);

        long time2=getCurrentTime()-time1;
        return new long[] {time1- time0,r[0], r[1] };
        //should return a true

       // return carRM.addCars(id, location, numCars, price);

    }

    public long[] addRooms(int xid, String location, int numRooms, int price) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {

        long time0=getCurrentTime();
        int id = xid;

        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();

        Trace.info("addRooms - Redirect to Room Resource Manager");
        //checkTransaction(id);
        lockData(id, Room.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,ROOM_RM);

        long time1=getCurrentTime();
        long r[]= roomRM.addRooms(id, location, numRooms, price);
        long time2=getCurrentTime()-time1;
        return new long[] {time1- time0,r[0], r[1] };

        //return roomRM.addRooms(id, location, numRooms, price);

    }

    public boolean deleteFlight(int xid, int flightNum)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException
    {

       
        int id = xid;
        Trace.info("deleteFlight - Redirect to Flight Resource Manager");
        //checkTransaction(id);

        lockData(id, Flight.getKey(flightNum), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,FLIGHT_RM);


       
      //  return new long[] {getCurrentTime()-time0-time2,r[0] };
        return flightRM.deleteFlight(id, flightNum);

    }

    public boolean deleteCars(int xid, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("deleteCars - Redirect to Car Resource Manager");
        //checkTransaction(id);

        lockData(id, Car.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,CAR_RM);

        return carRM.deleteCars(id, location);
    }

    public boolean deleteRooms(int xid, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("deleteRooms - Redirect to Room Resource Manager");
        //checkTransaction(id);

        lockData(id, Room.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,ROOM_RM);

        return roomRM.deleteRooms(id, location);
    }


    public long[] queryFlight(int xid, int flightNumber) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {

        long time1= getCurrentTime();
        System.out.println("time1 " + time1);
        int id = xid;
        Trace.info("queryFlight - Redirect to Flight Resource Manager");

    

        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();


        lockData(id, Flight.getKey(flightNumber), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,FLIGHT_RM);

        long time2= getCurrentTime();
        System.out.println("time2 " + time2);
        long arr[]= flightRM.queryFlight(id, flightNumber);
        long time3=getCurrentTime()-time2;
        return new long[] {time2- time1, arr[0], arr[1], arr[2]};
    }


    public long[] queryCars(int xid, String location)
            throws RemoteException, TransactionAbortedException, InvalidTransactionException
    {
        long time1= getCurrentTime();
        System.out.println("time1 " + time1);
        int id = xid;
        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();

        Trace.info("queryCars - Redirect to Car Resource Manager");

        lockData(id, Car.getKey(location), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,CAR_RM);


        long time2= getCurrentTime();
        System.out.println("time2 " + time2);
        long arr[]= carRM.queryCars(id, location);
        long time3=getCurrentTime()-time2;
        //return MDWtime, RMtime+DBtime, DBtime
        return new long[] {time2- time1, arr[0], arr[1], arr[2]};

    }


    public long[] queryRooms(int xid, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        long time1= getCurrentTime();
        System.out.println("time1 " + time1);
        int id = xid;
        Trace.info("queryRooms - Redirect to Room Resource Manager");
        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();

        lockData(id, Room.getKey(location), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,ROOM_RM);

        long time2= getCurrentTime();
        System.out.println("time2 " + time2);
        long arr[]= roomRM.queryRooms(id, location);
        long time3=getCurrentTime()-time2;
        return new long[] {time2- time1, arr[0], arr[1], arr[2]};
       
    }


    public String queryCustomerInfo(int xid, int customerID) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        //checkTransaction(xid);
        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CUSTOMER_RM);
        return super.queryCustomerInfo(xid,customerID);
    }


  


    public int queryFlightPrice(int xid, int flightNumber) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("queryFlightPrice - Redirect to Flight Resource Manager");
        //checkTransaction(id);

        lockData(id, Flight.getKey(flightNumber), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,FLIGHT_RM);

        return flightRM.queryFlightPrice(id, flightNumber);
    }


    public int queryCarsPrice(int xid, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("queryCarsPrice - Redirect to Car Resource Manager");
        //checkTransaction(id);

        lockData(id, Car.getKey(location), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,CAR_RM);

        return carRM.queryCarsPrice(id, location);
    }

    public int newCustomer(int xid) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        //checkTransaction(xid);

        Trace.info("RM::newCustomer(" + xid + ") called");
        int cid = Integer.parseInt(String.valueOf(xid) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer customer = new Customer(cid);
        lockData(xid, customer.getKey(), TransactionLockObject.LockType.LOCK_WRITE);
        addResourceManagerUsed(id,CUSTOMER_RM);
        writeData(xid, customer.getKey(), customer);
        Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
        return cid;
    }

    public boolean newCustomer(int xid, int customerID) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        Trace.info("Middleware: newCustomer(" + xid + ", " + customerID + ")");
        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();
        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CUSTOMER_RM);

        Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
        if (customer != null){
            Trace.info("Middleware: customer(" + xid + ", " + customerID + ") already exists");
            return false;
        } else {
            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            customer = new Customer(customerID);
            writeData(xid, customer.getKey(), customer);
            flightRM.newCustomer(xid, customerID);
            carRM.newCustomer(xid, customerID);
            roomRM.newCustomer(xid, customerID);
            Trace.info("Middleware:newCustomer(" + xid + ", " + customerID + ") created");
            return true;
        }
    }



    public boolean deleteCustomer(int xid, int customerID) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") called");
        //checkTransaction(xid);

        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,CUSTOMER_RM);
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
            return false;
        }
        else {
            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            // Increase the reserved numbers of all reservable items which the customer reserved.
            RMHashMap reservations = customer.getReservations();
            for (String reservedKey : reservations.keySet()) {
                String type = reservedKey.split("-")[0];
                ReservedItem reserveditem = customer.getReservedItem(reservedKey);
                if (type.equals(FLIGHT_RM)) {
                    lockData(xid, reservedKey, TransactionLockObject.LockType.LOCK_WRITE);
                    addResourceManagerUsed(id,FLIGHT_RM);
                    flightRM.removeReservation(xid, customerID, reserveditem.getKey(), reserveditem.getCount());
                } else if (type.equals(CAR_RM)) {
                    lockData(xid, reservedKey, TransactionLockObject.LockType.LOCK_WRITE);
                    addResourceManagerUsed(id,CAR_RM);
                    carRM.removeReservation(xid, customerID, reserveditem.getKey(), reserveditem.getCount());
                } else if (type.equals(ROOM_RM)) {
                    lockData(xid, reservedKey, TransactionLockObject.LockType.LOCK_WRITE);
                    addResourceManagerUsed(id,ROOM_RM);
                    roomRM.removeReservation(xid, customerID, reserveditem.getKey(), reserveditem.getCount());
                } else
                    Trace.error("RM::deleteCustomer(" + xid + ", " + customerID + ") failed--reservedKey (" + reservedKey + ") wasn't of expected type.");

            }
            // Remove the customer from the storage
            removeData(xid, customer.getKey());
            Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
            return true;
        }

    }
//--




//    public boolean reserveFlight(int xid, int customerID, int flightNumber) throws RemoteException,TransactionAbortedException, InvalidTransactionException
//    {
//        int id = xid;
//        String key = Flight.getKey(flightNumber);
//
//        Trace.info("RM::reserveFlight(" + xid + ", customer=" + customerID + ", " + key + ") called" );
//        //checkTransaction(xid);
//        // Check customer exists
//
//        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(xid,"Customer");
//        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
//        if (customer == null)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + flightNumber + ")  failed--customer doesn't exist");
//            return false;
//        }
//
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(xid,FLIGHT_RM);
//        int price = flightRM.itemsAvailable(xid, key, 1);
//
//        if (price < 0) {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + flightNumber + ")  failed--item unavailable");
//            return false;
//        }
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_WRITE);
//        if (flightRM.reserveFlight(xid, customerID, flightNumber)) {
//            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
//            customer.reserve(key, String.valueOf(flightNumber), price);
//            writeData(xid, customer.getKey(), customer);
//            return true;
//        }
//        Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + flightNumber + ")  failed--Could not reserve item");
//        return false;
//
//    }
//
//
//    public boolean reserveCar(int xid, int customerID, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
//    {
//        int id = xid;
//        String key = Car.getKey(location);
//
//        Trace.info("RM::reserveCar(" + xid + ", customer=" + customerID + ", " + key + ") called" );
//        //checkTransaction(xid);
//        // Check customer exists
//
//        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(id,"Customer");
//        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
//        if (customer == null)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--customer doesn't exist");
//            return false;
//        }
//
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(id,"Car");
//        int price = carRM.itemsAvailable(xid, key, 1);
//
//        if (price < 0) {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--item unavailable");
//            return false;
//        }
//
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_WRITE);
//        if (carRM.reserveCar(xid, customerID, location)) {
//            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
//            customer.reserve(key, location, price);
//            writeData(xid, customer.getKey(), customer);
//            return true;
//        }
//        Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--Could not reserve item");
//        return false;
//
//    }


    public long[] reserveFlight(int xid, int customerID, int flightNumber) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        long time0=getCurrentTime();
        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();
        String key = Flight.getKey(flightNumber);
        Trace.info("Middleware: reserveFlight(" + xid + ", customer=" + customerID + ", " + key + ")" );

        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CUSTOMER_RM);
        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,FLIGHT_RM);

        long time1=getCurrentTime() - time0;

        long r[] = flightRM.reserveFlight(xid, customerID, flightNumber);
        long time2=getCurrentTime()-time1;

        Trace.info("mdw time "  + time1);

        return new long[] {time1,r[0], r[1] };
    }


    public long[] reserveCar(int xid, int customerID, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        long time0=getCurrentTime();

        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();
        String key = Car.getKey(location);
        Trace.info("Middleware: reserveCar(" + xid + ", customer=" + customerID + ", " + key + ")" );

        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CUSTOMER_RM);
        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CAR_RM);

        long time1=getCurrentTime() - time0;

        long r[] = carRM.reserveCar(xid, customerID, location);
        long time2=getCurrentTime()-time1;

        Trace.info("mdw time "  + time1);

        return new long[] {time1, r[0], r[1] };
    }

    public long[]  reserveRoom(int xid, int customerID, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        long time0=getCurrentTime();

        Transaction trx = traxManager.getActiveTransaction(xid);
        trx.resetTimer();
        String key = Room.getKey(location);
        Trace.info("Middleware: reserveRoom(" + xid + ", customer=" + customerID + ", " + key + ")" );

        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,CUSTOMER_RM);
        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(xid,ROOM_RM);

        long time1=getCurrentTime()-time0;

        Trace.info("mdw time "  + time1);

        long r[] = roomRM.reserveRoom(xid, customerID, location);
        long time2=getCurrentTime()-time1;

        return new long[] {time1,r[0], r[1] };
    }





    public int queryRoomsPrice(int xid, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("queryRoomsPrice - Redirect to Room Resource Manager");
        //checkTransaction(id);

        lockData(id, Room.getKey(location), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,ROOM_RM);

        return roomRM.queryRoomsPrice(id, location);
    }





    
//    public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException,TransactionAbortedException, InvalidTransactionException
//    {
//        int id = xid;
//        String key = Room.getKey(location);
//
//        Trace.info("RM::reserveRoom(" + xid + ", customer=" + customerID + ", " + key + ") called" );
//        //checkTransaction(xid);
//        // Check customer exists
//
//        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(id,"Customer");
//        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
//        if (customer == null)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--customer doesn't exist");
//            return false;
//        }
//
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
//        addResourceManagerUsed(id,"Room");
//        int price = roomRM.itemsAvailable(xid, key, 1);
//
//        if (price < 0) {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--item unavailable");
//            return false;
//        }
//
//        lockData(xid, key, TransactionLockObject.LockType.LOCK_WRITE);
//        if (roomRM.reserveRoom(xid, customerID, location)) {
//            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
//            customer.reserve(key, location, price);
//            writeData(xid, customer.getKey(), customer);
//            return true;
//        }
//        Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + location + ")  failed--Could not reserve item");
//        return false;
//    }

    public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;
        Trace.info("RM::bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ") called" );
        //checkTransaction(xid);

        lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_READ);
        addResourceManagerUsed(id,CUSTOMER_RM);
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--customer doesn't exist");
            return false;
        }
        HashMap<String, Integer> countraxManagerap = countFlights(flightNumbers);
        HashMap<Integer, Integer> flightPrice = new HashMap<Integer, Integer>();
        int carPrice;
        int roomPrice;

        if (car && room) {
            // Check flight availability
            for (String key : countraxManagerap.keySet()) {
                int keyInt;

                try {
                    keyInt = Integer.parseInt(key);
                } catch (Exception e) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--could not parse flightNumber");
                    return false;
                }
                lockData(xid, Flight.getKey(keyInt), TransactionLockObject.LockType.LOCK_READ);
                addResourceManagerUsed(id,FLIGHT_RM);
                int price = flightRM.itemsAvailable(xid, Flight.getKey(keyInt), countraxManagerap.get(key));

                if (price < 0) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--flight-" + key + " doesn't have enough spots");
                    return false;
                } else {
                    flightPrice.put(keyInt, price);
                }
            }
            lockData(xid, Car.getKey(location), TransactionLockObject.LockType.LOCK_READ);
            addResourceManagerUsed(id,CAR_RM);
            carPrice = carRM.itemsAvailable(xid, Car.getKey(location), 1);

            if (carPrice < 0) {
                Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--car-" + location + " doesn't have enough spots");
                return false;
            }

            lockData(xid, Room.getKey(location), TransactionLockObject.LockType.LOCK_READ);
            addResourceManagerUsed(id,ROOM_RM);
            roomPrice = roomRM.itemsAvailable(xid, Room.getKey(location), 1);

            if (roomPrice < 0) {
                Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--room-" + location + " doesn't have enough spots");
                return false;
            }

            lockData(xid, Room.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
            roomRM.reserveRoom(xid, customerID, location);

            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            addResourceManagerUsed(id,CUSTOMER_RM);
            customer.reserve(Room.getKey(location), location, roomPrice);

            writeData(xid, customer.getKey(), customer);

            lockData(xid, Car.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
            carRM.reserveCar(xid, customerID, location);

            // Already have customer LOCK_WRITE
            customer.reserve(Car.getKey(location), location, carPrice);
            writeData(xid, customer.getKey(), customer);




        } else if (car) {
            // Check flight availability
            for (String key : countraxManagerap.keySet()) {
                int keyInt;

                try {
                    keyInt = Integer.parseInt(key);
                } catch (Exception e) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--could not parse flightNumber");
                    return false;
                }
                lockData(xid, Flight.getKey(keyInt), TransactionLockObject.LockType.LOCK_READ);
                addResourceManagerUsed(id,FLIGHT_RM);
                int price = flightRM.itemsAvailable(xid, Flight.getKey(keyInt), countraxManagerap.get(key));

                if (price < 0) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--flight-" + key + " doesn't have enough spots");
                    return false;
                } else {
                    flightPrice.put(keyInt, price);
                }
            }
            lockData(xid, Car.getKey(location), TransactionLockObject.LockType.LOCK_READ);
            addResourceManagerUsed(id,CAR_RM);
            carPrice = carRM.itemsAvailable(xid, Car.getKey(location), 1);

            if (carPrice < 0) {
                Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--car-" + location + " doesn't have enough spots");
                return false;
            }
            lockData(xid, Car.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
            carRM.reserveCar(xid, customerID, location);

            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            addResourceManagerUsed(id,CUSTOMER_RM);
            customer.reserve(Car.getKey(location), location, carPrice);
            writeData(xid, customer.getKey(), customer);


        } else if (room) {
            // Check flight availability
            for (String key : countraxManagerap.keySet()) {
                int keyInt;

                try {
                    keyInt = Integer.parseInt(key);
                } catch (Exception e) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--could not parse flightNumber");
                    return false;
                }
                lockData(xid, Flight.getKey(keyInt), TransactionLockObject.LockType.LOCK_READ);
                addResourceManagerUsed(id,FLIGHT_RM);
                int price = flightRM.itemsAvailable(xid, Flight.getKey(keyInt), countraxManagerap.get(key));

                if (price < 0) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--flight-" + key + " doesn't have enough spots");
                    return false;
                } else {
                    flightPrice.put(keyInt, price);
                }
            }
            lockData(xid, Room.getKey(location), TransactionLockObject.LockType.LOCK_READ);
            addResourceManagerUsed(id,ROOM_RM);
            roomPrice = roomRM.itemsAvailable(xid, Room.getKey(location), 1);

            if (roomPrice < 0) {
                Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--room-" + location + " doesn't have enough spots");
                return false;
            }
            lockData(xid, Room.getKey(location), TransactionLockObject.LockType.LOCK_WRITE);
            roomRM.reserveRoom(xid, customerID, location);

            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            addResourceManagerUsed(id,CUSTOMER_RM);
            customer.reserve(Room.getKey(location), location, roomPrice);
            writeData(xid, customer.getKey(), customer);

        }
        else{
            // Check flight availability
            for (String key : countraxManagerap.keySet()) {
                int keyInt;

                try {
                    keyInt = Integer.parseInt(key);
                } catch (Exception e) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--could not parse flightNumber");
                    return false;
                }
                lockData(xid, Flight.getKey(keyInt), TransactionLockObject.LockType.LOCK_READ);
                addResourceManagerUsed(id,FLIGHT_RM);
                int price = flightRM.itemsAvailable(xid, Flight.getKey(keyInt), countraxManagerap.get(key));

                if (price < 0) {
                    Trace.warn("RM:bundle(" + xid + ", customer=" + customerID + ", " + flightNumbers.toString() + ", " + location + ")  failed--flight-" + key + " doesn't have enough spots");
                    return false;
                } else {
                    flightPrice.put(keyInt, price);
                }
            }
        }

        if (flightPrice.keySet().size() > 0) {
            lockData(xid, Customer.getKey(customerID), TransactionLockObject.LockType.LOCK_WRITE);
            addResourceManagerUsed(id,CUSTOMER_RM);
        }
        // Reserve flights
        for (Integer key : flightPrice.keySet()) {
            for (int i = 0; i < countraxManagerap.get(String.valueOf(key)); i++) {
                int price = flightPrice.get(key);

                lockData(xid, Flight.getKey(key), TransactionLockObject.LockType.LOCK_WRITE);
                flightRM.reserveFlight(xid, customerID, key);
                customer.reserve(Flight.getKey(key), String.valueOf(key), price);
                writeData(xid, customer.getKey(), customer);
            }
        }


        Trace.info("RM:bundle() -- succeeded");
        return true;

    }


    public String Summary(int xid) throws RemoteException,TransactionAbortedException, InvalidTransactionException
    {
        int id = xid;

      //  //checkTransaction(xid);
        Transaction t = traxManager.getActiveTransaction(xid);
        RMHashMap m = t.get_TMPdata();
        Set<String> keyset = new HashSet<String>(m.keySet());
        keyset.addAll(m_data.keySet());

        String summary = "";

        for (String key: keyset) {
            String type = key.split("-")[0];
            if (!type.equals(CUSTOMER_RM))
                continue;
            lockData(xid, key, TransactionLockObject.LockType.LOCK_READ);
            addResourceManagerUsed(id,CUSTOMER_RM);
            Customer customer = (Customer)readData(xid, key);
            if (customer != null)
                summary += customer.getSummary();

        }
        return summary;
    }

    public String getName() throws RemoteException {
        return m_name;
    }

    protected HashMap<String, Integer> countFlights(Vector<String> flightNumbers) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (String flightNumber : flightNumbers) {
            if (map.containsKey(flightNumber))
                map.put(flightNumber, map.get(flightNumber) + 1);
            else
                map.put(flightNumber, 1);
        }
        return map;
    }

    protected void connectServer(String type, String server, int port, String name)
    {
        try {
            boolean first = true;
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(server, port);

                    switch(type) {
                        case FLIGHT_RM: {
                            flightRM = (IResourceManager)registry.lookup(name);
                            break;
                        }
                        case CAR_RM: {
                            carRM = (IResourceManager)registry.lookup(name);
                            break;
                        }
                        case ROOM_RM: {
                            roomRM = (IResourceManager)registry.lookup(name);
                            break;
                        }
                    }
                    System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + name + "]");
                    break;
                }
                catch (NotBoundException|RemoteException e) {
                    if (first) {
                        System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + name + "]");
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

    // protected void //checkTransaction(int xid) throws TransactionAbortedException, InvalidTransactionException{
    //     if(traxManager.readActiveData(xid) != null) {
    //         traxManager.readActiveData(xid).updateLastAction();
    //         return;
    //     }
    //     Trace.info("Transaction is not active: throw error");

    //     Boolean v = traxManager.readInactiveData(xid);
    //     if (v == null)
    //         throw new InvalidTransactionException(xid, "MW: The transaction doesn't exist");
    //     else if (v.booleanValue() == true)
    //         throw new InvalidTransactionException(xid, "MW: The transaction has already been committed");
    //     else
    //         throw new TransactionAbortedException(xid, "MW: The transaction has been aborted");
    // }

    public void lockData(int xid, String data, TransactionLockObject.LockType lockType) throws RemoteException, TransactionAbortedException, InvalidTransactionException{
        try {
            boolean lock = lockManager.Lock(xid, data, lockType);
            if (!lock) {
                Trace.info("LM::lock(" + xid + ", " + data + ", " + lockType + ") Unable to lock");
                throw new InvalidTransactionException(xid, "LockManager-Unable to lock");
            }
        } catch (DeadlockException e) {
            Trace.info("LM::lock(" + xid + ", " + data + ", " + lockType + ") " + e.getLocalizedMessage());
//            Transaction t = traxManager.getActiveTransaction(xid);
//            t.resetTimer();
            traxManager.abort(xid);
            throw new TransactionAbortedException(xid, "The transaction has been aborted due to a deadlock");
        }
    }

    public void addResourceManagerUsed(int xid, String resource) throws RemoteException  {
        Transaction t = traxManager.getActiveTransaction(xid);
        t.setRelatedRM(resource);

        try {
            try {

                switch (resource) {
                    case FLIGHT_RM: {
                        flightRM.addTransaction(xid);
                        break;
                    }
                    case CAR_RM: {
                        carRM.addTransaction(xid);
                        break;
                    }
                    case ROOM_RM: {
                        roomRM.addTransaction(xid);
                        break;
                    }
                    case CUSTOMER_RM: {
                        this.addTransaction(xid);
                        flightRM.addTransaction(xid);
                        carRM.addTransaction(xid);
                        roomRM.addTransaction(xid);
                        break;
                    }
                }

            } catch (ConnectException e) {
                switch (resource) {
                    case FLIGHT_RM: {
                        connectServer(FLIGHT_RM, s_flightServer.host, s_flightServer.port, s_flightServer.name);
                        flightRM.addTransaction(xid);
                        break;
                    }
                    case CAR_RM: {
                        connectServer(CAR_RM, s_carServer.host, s_carServer.port, s_carServer.name);
                        carRM.addTransaction(xid);
                        break;
                    }
                    case ROOM_RM: {
                        connectServer(ROOM_RM, s_roomServer.host, s_roomServer.port, s_roomServer.name);
                        roomRM.addTransaction(xid);
                        break;
                    }
                    case CUSTOMER_RM: {
                        this.addTransaction(xid);
                    }
                }
            }
        } catch (Exception e) {
            Trace.error(e.toString());
        }
    }





}
