# **Animal Shelter Management System**

## **About the Project**

This project was created as part of my learning journey and practice with (Spring Boot, React, Docker, GraphQL, PostgreSQL). All requirements and the architecture were designed and implemented by me based on my understanding of how modern web applications should function. The project demonstrates my ability to work across the full development lifecycle — from database design and backend logic to frontend development and application deployment.

## **Project Description**

The **Animal Shelter Management System** is a comprehensive application designed for managing animal
shelters. It consists of a **frontend** and **two microservices** that handle animals, vaccinations,
users, and newsletter subscribers. The project utilizes modern technologies like **React**, **Spring
Boot**, **Redis**, **Kafka**, and **Docker**.

---

## **Core Features**

### **1. Frontend (React)**

- **Animal and Vaccination Management**:
    - Add new animals;
    - Delete animals;
    - Update animals;
    - View animals list;
    - Add new vaccination;
    - Delete vaccination;
    - Update vaccination;
    - View vaccinations list.
- **User Management**:
    - User registration (will be implemented soon);
    - View user profiles.
- **Newsletter Subscriptions**:
    - Approve or reject subscription requests, including those
      from external applications where the subscriber does not have an explicit approver..
    - Unsubscribe a subscriber

### **2. Animal Shelter Service (Spring Boot + Kafka + GraphQL)**

- **Entity Management**:
    - CRUD operations for animals and vaccinations.
    - Data storage in **PostgreSQL**.
- **Integration with Kafka**:
    - Produces messages events (e.g., animal additions, deletions).
- **Sending Notifications**:
    - Generates and sends messages to the **Notification Service**.

### **3. Notification Service (Spring Boot + Kafka + Redis)**

- **Subscriber Management**:
    - Stores subscriber lists in **Redis**.
    - Handles subscription requests.
- **Notification Delivery**:
    - Processes messages from **Kafka**.
    - Sends emails to subscribers.

---

## **Technologies**

- **Frontend**:
    - React
    - Apollo Client (for GraphQL operations)
    - Materialize CSS (for styling)
- **Backend**:
    - Spring Boot
    - PostgreSQL (database)
    - Redis (subscriber cache)
    - Apache Kafka (message queue)
- **Containerization**:
    - Docker & Docker Compose

---

## **Installation and Running**

### **Prerequisites**

- Docker and Docker Compose installed on your machine.
- Node.js and npm for frontend development.

### **Steps to Run**

1. **Clone the repository**:
   ```bash
   git clone https://github.com/NatOlk/animal-shelter-app.git
   cd animal-shelter-app
   ```
2. **Insert your email to init data script**
   
   Navigate to the *ansh-service\src\main\resources* folder and open the init.sql file.
   Insert your Google email address.
   This email address will be used specifically for sending notifications to subscribers. 
   For now, a Google account is required because of its simplicity in development environments. 
   The reasons for this choice and additional configuration details are explained below.
```bash
   INSERT INTO user_profiles (email, name, password, animal_notif_status)
   VALUES ('your_email_box_address@gmail.com', 'admin', '$2b$12$rAbcG66.Jawjm31LImq3GOjA.oGlYhlWiNaQPL2SEzBcg9SGBvw26', 'NONE');
   ```
   Additionally, this initialization file contains several lines for adding animals along with their vaccination records.
3. **Configure the environment variables file:**
   
   An .env file will be created in the appropriate directory, where you need to update the following
   details:

   ```bash
     POSTGRES_PASSWORD=your_postgres_password
     POSTGRES_PASSWORD_NOTIFICATION=your_postgres_password_notification
     GMAIL_USERNAME=your_gmail_username  
     GMAIL_PASSWORD=your_gmail_password  
     REDIS_PASSWORD=your_redis_password  
     NOTIFICATION_API_KEY=your_api_key  
     JWT_SECRET_KEY=your_jwt_key   
   ```

#### *POSTGRES_PASSWORD and POSTGRES_PASSWORD_NOTIFICATION*

>These are the passwords for your PostgreSQL databases. You need to set these to securely connect
your application to the databases.
How to set up PostgreSQL:
Install PostgreSQL on your machine or use a Docker container to run it.
Create the new databases using the following commands:
>```bash
>      CREATE DATABASE Animal_Shelter;
>      CREATE DATABASE animal_shelter_notification;
>  ```
>Set the POSTGRES_PASSWORD for the main application database (Animal_Shelter) and the
POSTGRES_PASSWORD_NOTIFICATION for the notification
application database (animal_shelter_notification) in your .env file to the passwords you wish to
use for each database.

#### *GMAIL_USERNAME and GMAIL_PASSWORD*

>These are your Gmail credentials used for sending email notifications. In this example, these
credentials are used to send emails related to animal shelter updates.
>
>Note: This is a temporary
solution for development purposes. For production environments, it is recommended to use a more
secure and reliable email service provider instead of a personal Gmail account, such as a dedicated
email server or a third-party email service like SendGrid, Amazon SES, or Mailgun.
>
>How to configure:
> 
>Use your Gmail email address and password to configure these variables.
If you have 2-step verification enabled on your Gmail account, generate an App Password and use that
instead of your regular Gmail password.
> 
>For more detailed instructions on how to set up your Gmail account for sending emails and generating
an App Password, you can refer to the official Google documentation here:
[Google Account Help - App Passwords](https://support.google.com/a/answer/176600?hl=en)

#### *REDIS_PASSWORD* 

>This password is used to secure the Redis instance that your application uses for caching
subscribers and other data.
> You can provide any password of your choice, but ensure it is strong and secure to protect your data.

#### *NOTIFICATION_API_KEY*

>This is an API key used for secure communication between services for sending and receiving
messages. 
> You can generate any key for this purpose. You can generate any key for this purpose, but it should be kept confidential to maintain the security of your system.

#### *JWT_SECRET_KEY*

>This secret key is used for generating and verifying JWT (JSON Web Tokens) in your application. JWT
is typically used for user authentication. 
> You can choose any secret key, but ensure it is complex and stored securely to prevent unauthorized access.

By setting these environment variables correctly, your application will be properly configured for
database access, email notifications, Redis caching,
inter-service communication, and secure authentication.

4. **Launch the application using Docker Compose:**

   Build and start the services using the following command:

 ```bash
    docker-compose up --build
 ```

5. **Open the application in your browser**

   Navigate to http://localhost:3000


## **Future Plans**

- Add different roles for customers

  >The system will be extended to include multiple customer roles, providing different access levels and functionalities based on the user's role.

- Add a personal animal page
  >A personal page will be added for each animal, allowing users to view detailed information about the animal, including adoption status, vaccination records, and other relevant details.

- Enable management of animal adaptation
  >The system will be enhanced to allow managing the adaptation process of animals, including tracking their progress and providing necessary information and updates to potential adopters.
