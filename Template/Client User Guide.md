## Client User Guide
**• Help**

​		Provides information about the available commands

​		Help: list of all commands

​		Help,\<command\>: detailed description of command

**• Add a new flight**

​		Add information about a new flight 

​		AddFlight,\<xid>,\<FlightNumber\>,\<NumberOfSeats\>,\<PricePerSeat\>

**• Add a new car location**

​		 Add information about a new car location 

​		AddCars,\<xid\>,\<Location\>,\<NumberOfCars\>,\<PricePerCar\>

**• Add a new room location**

​		Add information about a new room location 

​		AddRooms,<xid\>,<Location\>,<NumberOfRooms\>,<PricePerRoom\>

**• Add a new customer**	

​		Add a new customer to the database

​		AddCustomer,<xid\>: system provides customer ID 

​		AddCustomerID,\<xid\>,\<CustomerID\>: user provides customer ID

**• Delete a flight**
		Delete information about a flight (fails if reservations present) 

​		DeleteFlight,\<xid\>,\<FlightNumber\>

**• Delete a car location**

​		Delete all cars from a location (fails if reservations present) 

​		DeleteCars,\<xid\>,\<Location\>

**• Delete a room location**

​		Delete all rooms from a location (fails if reservations present) 

​		DeleteRooms,\<xid\>,\<Location\>

**• Delete a customer**

​		Delete a customer from the database as well as their reservations 

​		DeleteCustomer,\<xid\>,\<CustomerID\>

**• Query a flight**

​		Obtain the number of available seats on a flight 

​		QueryFlight,\<xid\>,\<FlightNumber\>

**• Query a car location**

​		Obtain the number of available cars at a location 

​		QueryCars,\<xid\>,\<Location\>

**• Query a room location**

​		Obtain the number of available rooms at a location 

​		QueryRooms,\<xid\>,\<Location\>

**• Query a customer**

​		Generate the bill (based on reservations) of a customer 

​		QueryCustomer,\<xid\>,\<CustomerID\>

**• Query a flight price**

​		Obtain the price of a seat on a flight QueryFlightPrice,\<xid\>,\<FlightNumber\>

**• Query a car price**

​		Obtain the price of a car at a location 

​		QueryCarsPrice,\<xid\>,\<Location\>

**• Query a room price**

​		Obtain the price of a room at a location 

​		QueryRoomsPrice,\<xid\>,\<Location\>

**• Reserve a flight**

​		Reserve a flight number for a customer 

​		ReserveFlight,\<xid\>,\<CustomerID\>,\<FlightNumber\>

**• Reserve a car**

​		Reserve a car for a customer at a location 

​		ReserveCar,\<xid\>,\<CustomerID\>,\<Location\>

**• Reserve a room**
 	   Reserve a room for a customer at a location 

​		ReserveRoom,\<xid\>,\<CustomerID\>,\<Location\>

**• Reserve a bundle**

​		Book 1+ flights, and an optional car/room at a location (0/1 value) for a customer

​		Bundle,\<xid\>,\<CustomerID\>,\<FlightNumber1\>,...,\<FlightNumberN\>, \<Location\>,\<BookCar?\>,\<BookRoom?\>

**• Quitting**

​		Exit the application

​		Quit
