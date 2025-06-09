# 🏢 Condominium Management System

**📅 Duration:** Feb 2025 – Present  
**🛠 Technologies:** Java Swing, JDBC, Oracle, StarUML, Role-Based Access Control (RBAC)

---

## 📌 Overview

The **Condominium Management System** is a desktop application developed to streamline and digitize essential operations of a condominium complex, including:

- Apartment and resident management  
- Service tracking and billing  
- Facility reservation system (e.g., gym, swimming pool)  
- Complaint registration and resolution  
- Role-based access control (RBAC) for secure multi-user access

---

## 🚀 Features

- **Apartment Management**: Add, update, and manage apartment units and associated residents.
- **Resident Records**: Store and retrieve detailed profiles of residents.
- **Service & Billing**: Track utility services (e.g., water, electricity), auto-generate monthly bills.
- **Facility Reservation**: Allow residents to view schedules and book shared facilities such as gyms, pools, meeting rooms.
- **Complaint Handling**: Residents can file complaints; staff can manage and resolve them.
- **Secure RBAC**:
  - **Admin**: Full access to all modules
  - **Staff**: Limited access to management operations
  - **Residents**: Can view bills, file complaints, reserve facilities, and update personal data

---

## 🔐 Security

The system implements **Role-Based Access Control** (RBAC) to restrict access and operations based on user roles. All actions are validated against user permissions to ensure data security.

---

## 🧩 System Design

- **UML Diagrams**:
  - Use Case Diagram
  - Sequence Diagram
  - Activity Diagram
  - Class Diagram
- **ERD**:
  - Normalized schema with 24+ interconnected tables handling apartments, services, facility bookings, billing, users, and access logs.

---

## 📂 Technologies Used

- **Java Swing** – For GUI development  
- **JDBC** – For database connectivity  
- **Oracle Database** – Backend relational data storage  
- **StarUML** – For system modeling and documentation  
- **RBAC** – To manage and enforce user permissions

---

## 📈 Future Improvements

- Implement online payment gateway integration  
- Add notification module via email/SMS  
- Enhance analytics dashboard for admins  
- Add mobile application version for residents
