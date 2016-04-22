# Spring Hibernate JPA Multi-Tenancy integration example

1) Mention postgres database password in src/main/resources/db-config.properties file along with other information. <br />
2) Create database tenant1 in postgres db and execute src/main/resources/customer.sql script in tenant1 database (Optional even we can create db during runtime).<br />
3) Generate war using "gradle clean assemble". Deploy the same in tomcat. <br />
4) start tomcat. Please watch console for any errors.<br />
5) on successful start of tomcat. Go to http://localhost:8080/multitenancy should return {"status":"SUCCESS","message":"Hello!! Welcome to Spring Hibernate JPA Multi Tenancy Demo"}<br />
6) Since we need to choose tenant based on request. I am expecting tenant identifier in request URL.tenant Identifier is same to tenant database name. In this example multitenancy is by DATABASE which means every tenant will have their own database in postgres.<br />
7) GET http://localhost:8080/multitenancy/customer?tenant=tenant1 to view all existing customers of tenant1.<br />
8) GET http://localhost:8080/multitenancy/customer/{id}?tenant=tenant1 to view user with this id in tenant1.<br />
9) POST http://localhost:8080/multitenancy/customer?tenant=tenant1 with json payload {"firstName":"test","lastName":"test","email":"test@gmail.com"} to create customer for tenant1.<br />

In this example we integrated Spring, Hibernate Multi-tenancy, JPA<br />

For More on Hibernate Multi-tenancy. Please refer to below link <br />
http://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html/ch16.html <br />