# ==============================
# Stage 1: Build the Spring Boot JAR
# ==============================
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Ensure mvnw is executable
RUN chmod +x ./mvnw

# Build the JAR (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# ==============================
# Stage 2: Run the built app
# ==============================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Internal Port)
EXPOSE 8080

# Environment variables (Defaults, can be overridden)
ENV MONGODB_URI=""
ENV MAIL_USERNAME=""
ENV MAIL_PASSWORD=""
ENV ADMIN_USERNAME="admin"
ENV ADMIN_PASSWORD="admin123"
ENV FRONTEND_URL="http://localhost:5173"
ENV JWT_SECRET="your_jwt_secret_key_here"
ENV JWT_EXPIRY_HOURS="24"
ENV RAZORPAY_KEY_ID="your_razorpay_key_id_here"
ENV RAZORPAY_KEY_SECRET="your_razorpay_key_secret_here"
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
