# Local Greengrocer Desktop Application

This desktop application is developed for the CMPE 343 Project 3, implementing a Local Greengrocer management system using JavaFX GUI and JDBC for database interaction.

## Project Overview

The application provides different interfaces for customers, carriers, and the owner, each with specific functionalities. Users can log in, register, view and purchase products, manage deliveries, and perform various operations based on their roles.

## Important Dates

- **Declaration of Project-3:** 21. December.2023, 16:00-18:00 @ B-501 & Learn.
- **Due Date for Deliverables:** 12. January.2023, 23:59
- **Presentations:** 16. January.2023, 09:00-21:00 @ Cibali Campus.

## Project Features

### Customer Interface

- Login and Registration
- View available vegetables and fruits
- Add products to the shopping cart
- Check out with delivery date and time
- View order history and deliveries
- Edit profile information

### Carrier Interface

- View available, completed, and current/selected deliveries
- Select and complete deliveries
- Enter delivery date after successful delivery

### Owner Interface

- Manage products (add/remove/update)
- Employ/fire carriers
- View all types of orders
- View reports as charts (product/time/money)

### Database Integration

- Utilizes JDBC for seamless interaction with the database
- Stores user data, product details, orders, and delivery information

## Implementation Details

### Login and Authentication

- User authentication with username and password
- Role-based access to interfaces

### Shopping Cart and Ordering

- Dynamic addition/removal of items to/from the shopping cart
- Order summary and finalization with delivery date and time

### Threshold Pricing

- Prices are doubled when stock reaches a defined threshold
- Owner can set and update threshold values

## How to Run the Application

1. Clone the repository: `git clone <[repository_url](https://github.com/emircancapkan/CMPE343-Project-3.git)>`
2. Navigate to the project directory: `cd CMPE343-Project-3`
3. Compile and run the application: `javac App.java && java App`

## Documentation

The technical report for this application includes:

- Screenshots of each interface
- UML class diagram representing the project structure

## Contributors

- [Emircan Çapkan](https://github.com/emircancapkan)
- [Başar Çelebi](https://github.com/celebibasar)
- [Umut Aytuğ Semerci](https://github.com/uaytug)
- [Göktuğ Ateş](https://github.com/goktugates)
  
## License

This project is licensed under the [MIT License](LICENSE).


