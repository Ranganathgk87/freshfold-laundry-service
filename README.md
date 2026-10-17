# FreshFold Online Laundry Service

FreshFold is a Spring Boot laundry booking and management application. Customers create an account, put several clothing types into one pickup booking, choose the service for each type, and follow the order. Administrators manage bookings, prices, clothing types, payments, delivery dates, and feedback.

## Main features

### Customers

- Register, sign in, and maintain contact and pickup details.
- Add up to 20 clothing rows to one pickup. Each row has its own care service, clothing type, and quantity.
- See the estimated total before confirming. The server validates every selection and calculates the saved total from the current catalog prices.
- Review the itemized order, pickup schedule, delivery date, payment status, order status, and laundry notes.
- Cancel before pickup, record cash payment after completion, and submit one rating per completed order.

### Administrators

- Review bookings, customer details, and feedback.
- Change order and payment status, set a delivery date, and leave a customer note.
- Add or hide laundry services and clothing types.
- Download an itemized CSV report.

The order workflow is `PICKUP_SCHEDULED`, `PICKED_UP`, `WASHING`, `READY_FOR_DELIVERY`, `OUT_FOR_DELIVERY`, and `COMPLETED`; an administrator can also mark an order cancelled. Payment is recorded as cash paid/unpaid. There is no card or online payment integration.

## Run locally

Requirements: Java 17, Maven, and MySQL 8. Local configuration uses MySQL by default. Create the database, then set `DB_URL`, `DB_USER`, and `DB_PASSWORD` if the defaults in `application.properties` do not match your MySQL setup.

Set a private administrator password before starting with a new database. On Windows PowerShell, set it for the current terminal session with `$env:APP_ADMIN_PASSWORD = "your-own-strong-password"`, then run `mvn spring-boot:run`. Open `http://localhost:8080` and create a customer account.

For a quick local demo without MySQL, start with the `demo` profile: set `APP_ADMIN_PASSWORD` as above and run `mvn spring-boot:run -Dspring-boot.run.profiles=demo`. This uses an in-memory H2 database and loses its accounts and bookings when the process stops. Never publish a real password in this repository or send it in chat.

## Public demo, database, and availability

The source is in the [FreshFold GitHub repository](https://github.com/Ranganathgk87/freshfold-laundry-service). The Render Blueprint deploys the app at the free service URL. If `DB_URL`, `DB_USER`, and `DB_PASSWORD` are not configured, the demo profile uses in-memory H2.

Render Free services sleep after 15 minutes without traffic and may need about one minute to wake. Free Render files are temporary, so the in-memory H2 demo can lose accounts and bookings when the service restarts or sleeps. This is a hosting-plan limitation; a code change cannot make a free Render web service stay awake. See [Render's free service limits](https://render.com/docs/free).

To preserve records without paying for a database, the app can connect to an external PostgreSQL database by setting `DB_URL`, `DB_USER`, and `DB_PASSWORD` in the Render service's Environment settings. The current [Neon Free plan](https://neon.com/pricing) lists $0 pricing, 1 GB of database storage per project, and compute that scales to zero when idle. Its free usage has monthly limits, so review the provider's current limits before using it. Set `DB_URL` to a JDBC URL such as `jdbc:postgresql://HOST/DATABASE?sslmode=require`, using the host and database name shown by the provider, and set the separate username and password from its connection details. Keep all database credentials private. Switching away from the temporary H2 database does not copy its existing demo accounts or orders.

Even with a persistent database, the free Render web service can still sleep. A first visit after an idle period can be slow. The app's health endpoint is `/health`.

## Google search setup

The public homepage includes a search description and `WebSite` metadata. `/robots.txt` allows the homepage and excludes sign-in, registration, customer, and admin pages. `/sitemap.xml` lists the public homepage. Add the site as a URL-prefix property in [Google Search Console](https://search.google.com/search-console), choose HTML-tag verification, and copy the verification token into the Render `GOOGLE_SITE_VERIFICATION` environment variable. After the new deploy is Live, verify the property, submit `https://freshfold-laundry-demo.onrender.com/sitemap.xml`, and request indexing for the homepage. Google says crawling can take days to weeks and a request does not guarantee search inclusion; see [Google's recrawl guidance](https://developers.google.com/search/docs/crawling-indexing/ask-google-to-recrawl).

Render Free returns a restrictive `robots.txt` response while a web service is spun down, so Google may need to retry after the service wakes. Search placement and indexing cannot be guaranteed by the app.

