# FreshFold Online Laundry Service

A Spring Boot web application for customer laundry bookings and day-to-day laundry shop management. It follows the supplied project specification and uses Spring MVC, Spring Data JPA, Spring Security, Thymeleaf, MySQL, HTML and CSS.

## Requirements

- Java 17 or newer
- Maven 3.6 or newer
- MySQL 8 or newer

## Run locally

1. Start MySQL and create the database with SQL: CREATE DATABASE laundry_db;
2. Set database connection variables if your local MySQL credentials differ:
   - DB_URL defaults to jdbc:mysql://localhost:3306/laundry_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   - DB_USER defaults to root
   - DB_PASSWORD defaults to an empty password
3. From this project folder, run: mvn spring-boot:run
4. Open http://localhost:8080 and create a customer account from the registration page.

The first application start creates the administrator and starter service and clothing catalogs. The default admin login is admin@laundry.local with password Admin@12345. Set APP_ADMIN_EMAIL and APP_ADMIN_PASSWORD before the first start to choose different credentials. Change the default password for any shared or deployed environment. Schema changes are maintained by Hibernate with ddl-auto=update; use reviewed migrations for production.

## Features

### Customer

- Register, sign in and update name, phone and pickup address
- Browse active laundry services and per-item prices
- Book one clothing type per order with quantity, pickup date, time window and address
- See the calculated estimate, pickup and delivery schedule, payment record, status and laundry notes
- Cancel an order before pickup, record cash payment after completion, and submit one rating and comment per completed order

### Administrator

- Review customer accounts and all bookings
- Update order processing status, payment status, delivery date and customer-facing note
- Add or hide laundry services and clothing types
- Review customer feedback
- Download an order report as CSV

## Order and payment behavior

Orders progress through PLACED, PICKUP_SCHEDULED, PICKED_UP, WASHING, READY_FOR_DELIVERY, OUT_FOR_DELIVERY and COMPLETED; administrators can also mark an order CANCELLED. The customer can cancel before pickup. Payment is recorded as paid/unpaid by the administrator or by the customer after completion. No payment gateway or online card processing is configured, consistent with the supplied specification's description of online payment as a possible future enhancement.

The current booking form creates one order line (one clothing type and quantity) per booking. A customer needing several clothing types can create separate bookings with the same pickup window.

## Publish a free public demo

GitHub stores and shares the source code. GitHub Pages only serves static websites, so it cannot run this Spring Boot backend. The included Render Blueprint deploys the actual web app with an in-memory H2 database, so no paid database is needed for a college demo.

1. Create a free GitHub account and a public repository, then upload this project source (the ZIP can be extracted and published with GitHub Desktop).
2. Create a free Render account and connect it to GitHub.
3. In Render, choose New > Blueprint and select the repository. Render reads render.yaml, configures the free Docker web service, and asks you to set APP_ADMIN_PASSWORD. Enter a new unique password and keep it private.
4. Deploy. Render builds the Dockerfile and provides a public onrender.com address to share with your group.

Render free web services sleep after 15 minutes without traffic and can take about a minute to wake up. The demo database is in memory, so customer accounts and orders reset whenever the service restarts or sleeps. The starter laundry catalog is recreated automatically. This free setup is for a class demonstration, not for retaining real customer information. Select the Free plan and do not add a payment method; if free usage limits are reached, Render may suspend the service instead of charging an account without a payment method. Check [Render's free-service limits](https://render.com/docs/free) before publishing.

For a permanent database, local development continues to use MySQL as configured in application.properties. See [Render's Docker deploy guide](https://render.com/docs/docker) for hosting details.
