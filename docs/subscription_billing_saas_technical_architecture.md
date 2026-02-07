# Subscription Billing SaaS - Technical Architecture Plan

**Version:** 1.0  
**Date:** 2026-02-06  
**Author:** Technical Architecture Team  
**Target Audience:** Development Team, Stakeholders

---

## Table of Contents

1. [Technology Stack](#1-technology-stack)
2. [Architecture Overview](#2-architecture-overview)
3. [Detailed Technology Stack](#3-detailed-technology-stack)
4. [Refined Architecture Design](#4-refined-architecture-design)
5. [Core Domain Model](#5-core-domain-model)
6. [API Specifications](#6-api-specifications)
7. [Database Schema](#7-database-schema)
8. [Implementation Roadmap](#8-implementation-roadmap)
9. [Deployment Architecture](#9-deployment-architecture)
10. [Mobile App Recommendations](#10-mobile-app-recommendations)
11. [Quick Start Guide](#11-quick-start-guide)
12. [Cost Estimates](#12-cost-estimates)
13. [Security Considerations](#13-security-considerations)
14. [Monitoring & Observability](#14-monitoring--observability)

---

## 1. Technology Stack

### Backend Architecture

```plaintext
┌─────────────────────────────────────────────────────────┐
│                    API GATEWAY LAYER                     │
│              Spring Cloud Gateway / Kong                 │
│         (Rate Limiting, Auth, Request Routing)           │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  APPLICATION LAYER                       │
├─────────────────────────────────────────────────────────┤
│  Spring Boot 3.x (Java 17/21)                           │
│  - REST Controllers (Spring Web MVC)                     │
│  - Business Logic (Service Layer)                        │
│  - Security (Spring Security + JWT)                      │
│  - Validation (Jakarta Validation)                       │
│  - Async Processing (Spring Async + @Scheduled)          │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  DATA ACCESS LAYER                       │
├─────────────────────────────────────────────────────────┤
│  Spring Data JPA + Hibernate                             │
│  - Repository Pattern                                    │
│  - Query Methods / JPQL / Native Queries                 │
│  - Database Migration: Flyway/Liquibase                  │
│  - Connection Pool: HikariCP                             │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  POSTGRESQL DATABASE                     │
│            (DigitalOcean Managed Database)               │
└─────────────────────────────────────────────────────────┘

┌──────────────────────────���──────────────────────────────┐
│               INTEGRATION LAYER                          │
├─────────────────────────────────────────────────────────┤
│  - RestTemplate / WebClient (HTTP Clients)               │
│  - Spring Integration (Message-driven architecture)      │
│  - Spring Batch (Bulk operations, CSV import)            │
│  - Spring Retry (Resilience)                             │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│             MESSAGING & NOTIFICATIONS                    │
├─────────────────────────────────────────────────────────┤
│  - RabbitMQ / Apache Kafka (async message processing)   │
│  - Spring AMQP / Spring Kafka                            │
│  - Scheduled Jobs: Spring Scheduler / Quartz             │
└─────────────────────────────────────────────────────────┘

┌────────────────────────────────────���────────────────────┐
│                  REPORTING SERVICES                      │
├─────────────────────────────────────────────────────────┤
│  - PDF: Apache PDFBox / iText / Jasper Reports          │
│  - Excel: Apache POI                                     │
│  - Storage: DigitalOcean Spaces (S3-compatible)          │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Architecture Overview

### 2.1 Layered Architecture

```
┌──────────────────────────────────────────────────────┐
│                  Controller Layer                     │
│  @RestController, @RequestMapping                     │
│  - Input validation (@Valid)                          │
│  - Exception handling (@ControllerAdvice)             │
│  - Response mapping (DTOs)                            │
└──────────────────────────────────────────────────────┘
                        ↓
┌──────────────────────────────────────────────────────┐
│                   Service Layer                       │
│  @Service, @Transactional                             │
│  - Business logic                                     │
│  - Transaction management                             │
│  - Security checks                                    │
│  - Event publishing                                   │
└──────────────────────────────────────────────────────┘
                        ↓
┌──────────────────────────────────────────────────────┐
│                  Repository Layer                     │
│  Spring Data JPA Repositories                         │
│  - CRUD operations                                    │
│  - Custom queries (@Query)                            │
│  - Pagination & sorting                               │
└──────────────────────────────────────────────────────┘
                        ↓
┌──────────────────────────────────────────────────────┐
│                    Domain Layer                       │
│  @Entity, JPA entities                                │
│  - Domain model                                       │
│  - Business rules                                     │
│  - Relationships                                      │
└──────────────────────────────────────────────────────┘
```

---

## 3. Detailed Technology Stack

### 3.1 Core Backend Framework

| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| **Language** | Java | 17 LTS or 21 | Modern features, long-term support |
| **Framework** | Spring Boot | 3.2+ | Industry standard, rapid development |
| **Build Tool** | Maven / Gradle | Latest | Gradle for faster builds |
| **REST API** | Spring Web MVC | - | Mature, robust, well-documented |
| **Security** | Spring Security 6 | - | OAuth2, JWT, RBAC support |
| **Data Access** | Spring Data JPA | - | Reduces boilerplate, repository pattern |
| **ORM** | Hibernate | 6.x | Optimized for Spring Boot 3 |
| **Validation** | Jakarta Bean Validation | 3.0 | Declarative validation |
| **API Docs** | SpringDoc OpenAPI 3 | 2.x | Auto-generated Swagger UI |

### 3.2 Database & Persistence

| Component | Technology | Rationale |
|-----------|-----------|-----------|
| **Primary DB** | PostgreSQL 15+ | ACID compliance, JSON support, robust |
| **Migration** | Flyway | Version-controlled schema changes |
| **Connection Pool** | HikariCP | High performance (default in Spring Boot) |
| **Cache** | Spring Cache + Caffeine | In-memory caching for reports |
| **Full-text Search** | PostgreSQL FTS | No need for Elasticsearch initially |

### 3.3 Integration & Messaging

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Message Queue** | RabbitMQ | Async notification processing |
| **Batch Processing** | Spring Batch | CSV imports, bulk billing generation |
| **Scheduling** | Spring Scheduler | Daily billing, payment reminders |
| **HTTP Client** | Spring WebClient | Non-blocking WhatsApp/SMS API calls |
| **Retry Logic** | Spring Retry + Resilience4j | Fault tolerance for external APIs |

### 3.4 Reporting & File Generation

| Component | Technology | Use Case |
|-----------|-----------|----------|
| **PDF Generation** | Apache PDFBox | Invoices, payment receipts |
| **Excel Export** | Apache POI | Transaction reports, aging analysis |
| **CSV Processing** | Apache Commons CSV | Bulk subscriber import |
| **Template Engine** | Thymeleaf | Email/PDF templates |
| **File Storage** | DigitalOcean Spaces SDK | S3-compatible object storage |

### 3.5 Security & Authentication

| Component | Technology | Implementation |
|-----------|-----------|----------------|
| **Authentication** | JWT (JSON Web Tokens) | Stateless auth for mobile/web |
| **Authorization** | Spring Security RBAC | Role: SELLER, SUBSCRIBER, ADMIN |
| **Password Hashing** | BCrypt | Spring Security default |
| **API Security** | OAuth2 Resource Server | For third-party integrations |
| **Rate Limiting** | Bucket4j | Prevent API abuse |
| **HTTPS** | Let's Encrypt + Nginx | Free SSL certificates |

### 3.6 Monitoring & Observability

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Metrics** | Micrometer + Prometheus | Application metrics |
| **Logging** | SLF4J + Logback | Structured logging |
| **Tracing** | Spring Boot Actuator | Health checks, metrics endpoints |
| **APM** | New Relic / Datadog (optional) | Production monitoring |
| **Error Tracking** | Sentry | Exception tracking |

### 3.7 Testing

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Unit Tests** | JUnit 5 | Business logic testing |
| **Integration Tests** | Spring Boot Test | API endpoint testing |
| **Mocking** | Mockito | Service layer mocking |
| **API Testing** | RestAssured | REST API validation |
| **Test Containers** | Testcontainers | Integration tests with real PostgreSQL |
| **Code Coverage** | JaCoCo | Coverage reporting |

---

## 4. Refined Architecture Design

### 4.1 Project Structure

```
subscription-billing-saas/
│
├── src/main/java/com/billing/saas/
│   ├── config/                          # Configuration classes
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   ├── RabbitMQConfig.java
│   │   ├── CacheConfig.java
│   │   └── OpenAPIConfig.java
│   │
│   ├── controller/                      # REST Controllers
│   │   ├── SubscriberController.java
│   │   ├── BillingController.java
│   │   ├── PaymentController.java
│   │   ├── ReportController.java
│   │   └── NotificationController.java
│   │
│   ├── service/                         # Business Logic
│   │   ├── SubscriberService.java
│   │   ├── BillingService.java
│   │   ├── PaymentService.java
│   │   ├── NotificationService.java
│   │   └── ReportService.java
│   │
│   ├── repository/                      # Data Access
│   │   ├── SubscriberRepository.java
│   │   ├── BillingRepository.java
│   │   ├── PaymentRepository.java
│   │   └── SubscriptionPlanRepository.java
│   │
│   ├── domain/                          # JPA Entities
│   │   ├── Seller.java
│   │   ├── Subscriber.java
│   │   ├── SubscriptionPlan.java
│   │   ├── Billing.java
│   │   ├── Payment.java
│   │   └── Notification.java
│   │
│   ├── dto/                             # Data Transfer Objects
│   │   ├── request/
│   │   │   ├── SubscriberCreateRequest.java
│   │   │   ├── PaymentRecordRequest.java
│   │   │   └── BulkImportRequest.java
│   │   └── response/
│   │       ├── SubscriberResponse.java
│   │       ├── BillingResponse.java
│   │       └── DashboardResponse.java
│   │
│   ├── mapper/                          # Entity-DTO Mappers
│   │   ├── SubscriberMapper.java
│   │   └── BillingMapper.java
│   │
│   ├── integration/                     # External API Clients
│   │   ├── whatsapp/
│   │   │   ├── WhatsAppClient.java
│   │   │   └── TwilioWhatsAppService.java
│   │   ├── sms/
│   │   │   └── SMSGatewayClient.java
│   │   └── payment/
│   │       └── PaymentGatewayClient.java
│   │
│   ├── batch/                           # Spring Batch Jobs
│   │   ├── BulkSubscriberImportJob.java
│   │   └── MonthlyBillingGenerationJob.java
│   │
│   ├── scheduler/                       # Scheduled Tasks
│   │   ├── BillingScheduler.java
│   │   └── PaymentReminderScheduler.java
│   │
│   ├── security/                        # Security Components
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   │
│   ├── exception/                       # Exception Handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── BusinessValidationException.java
│   │
│   ├── util/                            # Utilities
│   │   ├── DateUtils.java
│   │   ├── PDFGenerator.java
│   │   ├── ExcelGenerator.java
│   │   └── MessageTemplateEngine.java
│   │
│   └── SubscriptionBillingSaasApplication.java
│
├── src/main/resources/
│   ├── application.yml                  # Main config
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── db/migration/                    # Flyway migrations
│   │   ├── V1__create_tables.sql
│   │   ├── V2__add_indexes.sql
│   │   └── V3__add_audit_columns.sql
│   └── templates/                       # Thymeleaf templates
│       ├── invoice-template.html
│       └── email-notification.html
│
└── src/test/java/
    ├── integration/                     # Integration tests
    ├── unit/                            # Unit tests
    └── testcontainers/                  # Container tests
```

---

## 5. Core Domain Model

### 5.1 Entity Relationship Diagram

```
┌─────────────────┐         ┌──────────────────┐
│     Seller      │1       *│   Subscriber     │
│─────────────────│◄────────│──────────────────│
│ id              │         │ id               │
│ email           │         │ seller_id (FK)   │
│ business_name   │         │ name             │
│ phone           │         │ phone            │
│ whatsapp        │         │ whatsapp         │
│ created_at      │         │ address          │
└─────────────────┘         │ status           │
                            │ created_at       │
        │                   └──────────────────┘
        │                            │
        │1                          │1
        │                           │
        │*                          │*
┌─────────────────────┐    ┌─────────────────────┐
│ SubscriptionPlan    │    │   Subscription      │
│─────────────────────│    │─────────────────────│
│ id                  │    │ id                  │
│ seller_id (FK)      │1  *│ subscriber_id (FK)  │
│ name                │◄───│ plan_id (FK)        │
│ type (DAILY/WEEKLY) │    │ start_date          │
│ rate_per_unit       │    │ end_date            │
│ billing_cycle       │    │ quantity_per_day    │
│ created_at          │    │ status              │
└─────────────────────┘    │ created_at          │
                           └─────────────────────┘
                                    │
                                    │1
                                    │
                                    │*
                           ┌─────────────────────┐
                           │      Billing        │
                           │─────────────────────│
                           │ id                  │
                           │ subscription_id(FK) │
                           │ billing_period      │
                           │ total_amount        │
                           │ due_date            │
                           │ status              │
                           │ created_at          │
                           └─────────────────────┘
                                    │
                                    │1
                                    │
                                    │*
                           ┌─────────────────────┐
                           │      Payment        │
                           │─────────────────────│
                           │ id                  │
                           │ billing_id (FK)     │
                           │ amount              │
                           │ payment_date        │
                           │ payment_method      │
                           │ transaction_ref     │
                           │ created_at          │
                           └─────────────────────┘
```

### 5.2 Sample Entity Classes

#### Subscriber Entity

```java
package com.billing.saas.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscribers", indexes = {
    @Index(name = "idx_seller_id", columnList = "seller_id"),
    @Index(name = "idx_phone", columnList = "phone"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    @Column(nullable = false, length = 10)
    private String phone;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid WhatsApp number")
    @Column(length = 10)
    private String whatsappNumber;

    @Email(message = "Invalid email format")
    @Column(length = 100)
    private String email;

    @Size(max = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriberStatus status = SubscriberStatus.ACTIVE;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subscription> subscriptions = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    // Business methods
    public void markAsInactive() {
        this.status = SubscriberStatus.INACTIVE;
    }

    public void softDelete() {
        this.deleted = true;
        this.status = SubscriberStatus.INACTIVE;
    }
}

enum SubscriberStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
```

#### Billing Entity

```java
package com.billing.saas.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billings", indexes = {
    @Index(name = "idx_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_billing_period", columnList = "billing_period"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_due_date", columnList = "due_date")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(nullable = false, length = 7) // YYYY-MM format
    private String billingPeriod;

    @Column(nullable = false)
    private LocalDate billingFromDate;

    @Column(nullable = false)
    private LocalDate billingToDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false)
    private Integer quantityPerDay;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerUnit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingStatus status = BillingStatus.UNPAID;

    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingAmount;

    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Business methods
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setBilling(this);
        updatePaymentStatus();
    }

    private void updatePaymentStatus() {
        this.paidAmount = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.outstandingAmount = this.totalAmount.subtract(this.paidAmount);

        if (this.outstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = BillingStatus.PAID;
        } else if (this.paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = BillingStatus.PARTIAL;
        } else {
            this.status = BillingStatus.UNPAID;
        }
    }
}

enum BillingStatus {
    UNPAID, PARTIAL, PAID, OVERDUE
}
```

---

## 6. API Specifications

### 6.1 Base Configuration

```
Base URL: https://api.yourdomain.com/api/v1
Authentication: Bearer Token (JWT)
Header: Authorization: Bearer <token>
Content-Type: application/json
```

### 6.2 Core API Endpoints

#### Authentication APIs

```
POST   /auth/register              # Register seller account
POST   /auth/login                 # Login (returns JWT)
POST   /auth/refresh-token         # Refresh JWT
POST   /auth/logout                # Invalidate token
GET    /auth/me                    # Get current user profile
```

#### Subscriber Management APIs

```
GET    /subscribers                # List all subscribers (paginated)
POST   /subscribers                # Create new subscriber
GET    /subscribers/{id}           # Get subscriber details
PUT    /subscribers/{id}           # Update subscriber
DELETE /subscribers/{id}           # Soft delete subscriber
POST   /subscribers/bulk-import    # Import from CSV/Excel
GET    /subscribers/search         # Search subscribers
```

#### Subscription Plan APIs

```
GET    /subscription-plans         # List all plans
POST   /subscription-plans         # Create new plan
PUT    /subscription-plans/{id}    # Update plan
DELETE /subscription-plans/{id}    # Delete plan
```

#### Billing APIs

```
GET    /billings                   # List all billings
POST   /billings/generate          # Generate monthly bills
GET    /billings/{id}              # Get bill details
GET    /billings/subscriber/{id}   # Get subscriber's bills
PUT    /billings/{id}/send         # Send bill via WhatsApp
GET    /billings/pending           # Get unpaid bills
```

#### Payment APIs

```
POST   /payments                   # Record payment
GET    /payments/{id}              # Get payment details
GET    /payments/subscriber/{id}   # Get subscriber's payment history
PUT    /payments/{id}              # Update payment
GET    /payments/outstanding       # Get outstanding payments
```

#### Reporting APIs

```
GET    /reports/dashboard          # Real-time dashboard data
GET    /reports/monthly-summary    # Monthly billing summary
GET    /reports/payment-collection # Collection efficiency
GET    /reports/aging-analysis     # Aging report
POST   /reports/export/pdf         # Export PDF report
POST   /reports/export/excel       # Export Excel report
```

#### Notification APIs

```
POST   /notifications/send         # Send manual notification
GET    /notifications/history      # Notification history
GET    /notifications/templates    # List templates
POST   /notifications/templates    # Create template
```

### 6.3 Sample Request/Response DTOs

#### Create Subscriber Request

```java
package com.billing.saas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
public class SubscriberCreateRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "WhatsApp must be 10 digits")
    private String whatsappNumber;
    
    @Email
    private String email;
    
    @Size(max = 500)
    private String address;
    
    @NotNull(message = "Subscription plan is required")
    private Long subscriptionPlanId;
    
    @NotNull
    @Min(1)
    private Integer quantityPerDay;
    
    @NotNull
    private LocalDate subscriptionStartDate;
}
```

#### Dashboard Response

```java
package com.billing.saas.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private Integer totalSubscribers;
    private Integer activeSubscribers;
    private Integer inactiveSubscribers;
    
    private BigDecimal totalOutstanding;
    private BigDecimal collectedThisMonth;
    private BigDecimal pendingThisMonth;
    
    private Integer paidBillsCount;
    private Integer unpaidBillsCount;
    private Integer partiallyPaidBillsCount;
    
    private Double paymentCollectionRate; // Percentage
    
    private RevenueStats revenueStats;
    private List<PaymentMethodBreakdown> paymentBreakdown;
}

@Data
@Builder
class RevenueStats {
    private BigDecimal currentMonthRevenue;
    private BigDecimal lastMonthRevenue;
    private Double growthPercentage;
}

@Data
@Builder
class PaymentMethodBreakdown {
    private String paymentMethod;
    private BigDecimal amount;
    private Integer count;
}
```

### 6.4 API Response Format

#### Success Response

```json
{
  "success": true,
  "message": "Subscriber created successfully",
  "data": {
    "id": 123,
    "name": "John Doe",
    "phone": "9876543210",
    "status": "ACTIVE"
  },
  "timestamp": "2026-02-06T10:30:00Z"
}
```

#### Error Response

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "phone",
      "message": "Phone must be 10 digits"
    }
  ],
  "timestamp": "2026-02-06T10:30:00Z"
}
```

---

## 7. Database Schema

### 7.1 Flyway Migration Script

```sql
-- V1__create_initial_schema.sql

-- Sellers Table
CREATE TABLE sellers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    whatsapp_number VARCHAR(10),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Subscribers Table
CREATE TABLE subscribers (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    whatsapp_number VARCHAR(10),
    email VARCHAR(100),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_seller FOREIGN KEY (seller_id) REFERENCES sellers(id)
);

-- Subscription Plans Table
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL, -- DAILY, WEEKLY, MONTHLY
    rate_per_unit NUMERIC(10,2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL, -- MONTHLY
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Subscriptions Table
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    subscriber_id BIGINT NOT NULL REFERENCES subscribers(id),
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    start_date DATE NOT NULL,
    end_date DATE,
    quantity_per_day INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Billings Table
CREATE TABLE billings (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id),
    billing_period VARCHAR(7) NOT NULL, -- YYYY-MM format
    billing_from_date DATE NOT NULL,
    billing_to_date DATE NOT NULL,
    total_days INTEGER NOT NULL,
    quantity_per_day INTEGER NOT NULL,
    rate_per_unit NUMERIC(10,2) NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID, PARTIAL, PAID
    paid_amount NUMERIC(10,2) DEFAULT 0,
    outstanding_amount NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Payments Table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    billing_id BIGINT NOT NULL REFERENCES billings(id),
    amount NUMERIC(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CASH, UPI, BANK_TRANSFER
    transaction_reference VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    subscriber_id BIGINT NOT NULL REFERENCES subscribers(id),
    type VARCHAR(50) NOT NULL, -- BILL, REMINDER, PAYMENT_CONFIRMATION
    channel VARCHAR(20) NOT NULL, -- WHATSAPP, SMS, EMAIL
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, SENT, FAILED
    sent_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_subscribers_seller_id ON subscribers(seller_id);
CREATE INDEX idx_subscribers_phone ON subscribers(phone);
CREATE INDEX idx_subscribers_status ON subscribers(status);
CREATE INDEX idx_subscriptions_subscriber_id ON subscriptions(subscriber_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_billings_subscription_id ON billings(subscription_id);
CREATE INDEX idx_billings_status ON billings(status);
CREATE INDEX idx_billings_due_date ON billings(due_date);
CREATE INDEX idx_billings_period ON billings(billing_period);
CREATE INDEX idx_payments_billing_id ON payments(billing_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_notifications_subscriber_id ON notifications(subscriber_id);
CREATE INDEX idx_notifications_status ON notifications(status);

-- Comments for documentation
COMMENT ON TABLE sellers IS 'Stores seller/vendor business information';
COMMENT ON TABLE subscribers IS 'Stores customer/subscriber information';
COMMENT ON TABLE subscription_plans IS 'Stores pricing plans for subscriptions';
COMMENT ON TABLE subscriptions IS 'Active subscriptions linking subscribers to plans';
COMMENT ON TABLE billings IS 'Monthly billing records';
COMMENT ON TABLE payments IS 'Payment transaction records';
COMMENT ON TABLE notifications IS 'Notification logs for all channels';
```

### 7.2 Additional Indexes Migration

```sql
-- V2__add_performance_indexes.sql

-- Composite indexes for common queries
CREATE INDEX idx_billings_status_due_date ON billings(status, due_date);
CREATE INDEX idx_subscriptions_status_start_date ON subscriptions(status, start_date);
CREATE INDEX idx_payments_date_method ON payments(payment_date, payment_method);

-- Partial indexes for active records
CREATE INDEX idx_active_subscribers ON subscribers(seller_id) WHERE deleted = false AND status = 'ACTIVE';
CREATE INDEX idx_unpaid_billings ON billings(subscription_id, due_date) WHERE status IN ('UNPAID', 'PARTIAL');

-- Full-text search index for subscriber names
CREATE INDEX idx_subscribers_name_trgm ON subscribers USING gin(name gin_trgm_ops);

-- Enable trigram extension for fuzzy search
CREATE EXTENSION IF NOT EXISTS pg_trgm;
```

---

## 8. Implementation Roadmap

### Phase 1: Foundation (Week 1-2)

**✅ Setup & Configuration**
- Initialize Spring Boot project (Gradle/Maven)
- Configure PostgreSQL connection
- Setup Flyway migrations
- Configure Spring Security + JWT
- Setup Swagger/OpenAPI documentation
- Configure logging (SLF4J + Logback)
- Docker containerization

**✅ Core Domain & Data Layer**
- Create JPA entities (Seller, Subscriber, Plan, Billing, Payment)
- Implement repositories
- Write Flyway migrations
- Setup Testcontainers for integration tests

**Deliverables:**
- Running Spring Boot application
- Database schema created
- Health check endpoint active
- API documentation accessible

---

### Phase 2: Subscriber & Plan Management (Week 3)

**✅ APIs**
- Subscriber CRUD operations
- Subscription plan CRUD
- Bulk CSV import (Spring Batch)
- Search and filter APIs

**✅ Business Logic**
- Validation rules
- Data integrity checks
- Soft delete implementation

**Deliverables:**
- Subscriber management APIs functional
- CSV import working
- Unit tests (80%+ coverage)

---

### Phase 3: Billing Engine (Week 4)

**✅ Billing Calculation**
- Automatic monthly billing generation
- Prorated billing logic
- Support for multiple pricing tiers
- Scheduled jobs (Spring @Scheduled)

**✅ APIs**
- Generate bills API
- List bills (paginated)
- Bill details API

**Deliverables:**
- Billing engine operational
- Automated billing generation
- Integration tests complete

---

### Phase 4: Payment Tracking (Week 5)

**✅ Payment Recording**
- Record payment API
- Update billing status (PAID/PARTIAL/UNPAID)
- Payment history
- Outstanding balance calculation

**✅ APIs**
- Payment CRUD operations
- Payment history by subscriber
- Outstanding payments report

**Deliverables:**
- Payment tracking functional
- Payment history APIs working
- Dashboard showing outstanding amounts

---

### Phase 5: Notification System (Week 6)

**✅ WhatsApp Integration**
- Integrate Twilio/Meta WhatsApp Business API
- Message template management
- Async message processing (RabbitMQ)

**✅ Notification Workflows**
- Monthly bill notifications
- Payment reminders (configurable)
- Payment confirmations
- Retry logic for failed messages

**Deliverables:**
- WhatsApp notifications working
- Message queue operational
- Notification history tracking

---

### Phase 6: Reporting & Analytics (Week 7)

**✅ Dashboard APIs**
- Real-time statistics
- Revenue trends
- Collection efficiency metrics

**✅ Report Generation**
- PDF reports (Apache PDFBox)
- Excel exports (Apache POI)
- Aging analysis
- Monthly summary reports

**Deliverables:**
- Dashboard API functional
- PDF/Excel reports generated
- Report download working

---

### Phase 7: Testing & Deployment (Week 8)

**✅ Testing**
- Unit tests (80%+ coverage)
- Integration tests (Testcontainers)
- API testing (RestAssured)
- Load testing (JMeter)

**✅ Deployment**
- DigitalOcean Kubernetes setup
- CI/CD pipeline (GitHub Actions)
- Production configuration
- Monitoring setup (Prometheus + Grafana)

**Deliverables:**
- Application deployed to production
- CI/CD pipeline operational
- Monitoring dashboards active
- Documentation complete

---

## 9. Deployment Architecture

### 9.1 DigitalOcean Kubernetes Deployment

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: billing-saas-api
  namespace: production
spec:
  replicas: 2
  selector:
    matchLabels:
      app: billing-saas-api
  template:
    metadata:
      labels:
        app: billing-saas-api
    spec:
      containers:
      - name: api
        image: registry.digitalocean.com/your-registry/billing-saas:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
        - name: DATABASE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: billing-saas-service
  namespace: production
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
    protocol: TCP
  selector:
    app: billing-saas-api
```

### 9.2 RabbitMQ Deployment

```yaml
# rabbitmq-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rabbitmq
  namespace: production
spec:
  replicas: 1
  selector:
    matchLabels:
      app: rabbitmq
  template:
    metadata:
      labels:
        app: rabbitmq
    spec:
      containers:
      - name: rabbitmq
        image: rabbitmq:3-management
        ports:
        - containerPort: 5672
        - containerPort: 15672
        env:
        - name: RABBITMQ_DEFAULT_USER
          value: "admin"
        - name: RABBITMQ_DEFAULT_PASS
          valueFrom:
            secretKeyRef:
              name: rabbitmq-credentials
              key: password
        volumeMounts:
        - name: rabbitmq-data
          mountPath: /var/lib/rabbitmq
      volumes:
      - name: rabbitmq-data
        persistentVolumeClaim:
          claimName: rabbitmq-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: rabbitmq-service
  namespace: production
spec:
  ports:
  - name: amqp
    port: 5672
    targetPort: 5672
  - name: management
    port: 15672
    targetPort: 15672
  selector:
    app: rabbitmq
```

### 9.3 Application Configuration

```yaml
# application-prod.yml
spring:
  application:
    name: subscription-billing-saas
  
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
  
  rabbitmq:
    host: ${RABBITMQ_HOST}
    port: 5672
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
  
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=10m

# JWT Configuration
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days

# External API Configuration
twilio:
  account-sid: ${TWILIO_ACCOUNT_SID}
  auth-token: ${TWILIO_AUTH_TOKEN}
  whatsapp-from: ${TWILIO_WHATSAPP_FROM}

# DigitalOcean Spaces
digitalocean:
  spaces:
    endpoint: ${DO_SPACES_ENDPOINT}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket: ${DO_SPACES_BUCKET}

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

# Logging
logging:
  level:
    root: INFO
    com.billing.saas: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/billing-saas/application.log
    max-size: 10MB
    max-history: 30
```

---

## 10. Mobile App Recommendations

### 10.1 Technology Options

**Option 1: Flutter (Recommended)**
- ✅ Single codebase for iOS, Android, Web
- ✅ Fast development with hot reload
- ✅ Beautiful UI components
- ✅ Great for MVP
- ⏱️ Development time: 6-8 weeks

**Option 2: React Native**
- ✅ JavaScript/TypeScript
- ✅ Large ecosystem
- ✅ Easy to find developers
- ⏱️ Development time: 8-10 weeks

**Option 3: Native Android (Kotlin)**
- ✅ Leverage Java knowledge
- ✅ Best performance
- ✅ Full Android API access
- ⚠️ Need separate iOS app later
- ⏱️ Development time: 6-8 weeks (Android only)

### 10.2 Mobile App Features

**Seller App:**
- Dashboard (outstanding, revenue, collection rate)
- Subscriber management (add, edit, view)
- Bulk subscriber import
- View bills and payments
- Send notifications manually
- Generate and download reports
- View notification history

**Subscriber App (Optional):**
- View billing history
- Make payment (integrate payment gateway)
- View subscription details
- Download invoices
- Contact seller

---

## 11. Quick Start Guide

### 11.1 Initialize Spring Boot Project

```bash
# Using Spring Initializr CLI
spring init \
  --dependencies=web,data-jpa,postgresql,security,actuator,validation,flyway \
  --group-id=com.billing \
  --artifact-id=subscription-billing-saas \
  --name=SubscriptionBillingSaas \
  --package-name=com.billing.saas \
  --java-version=17 \
  --build=gradle \
  subscription-billing-saas

cd subscription-billing-saas
```

### 11.2 Gradle Dependencies

```gradle
dependencies {
    // Core Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Database
    implementation 'org.postgresql:postgresql'
    implementation 'org.flywaydb:flyway-core'
    implementation 'com.zaxxer:HikariCP'
    
    // JWT & Security
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    
    // OpenAPI/Swagger
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
    
    // Messaging
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
    implementation 'org.springframework.kafka:spring-kafka'
    
    // Batch Processing
    implementation 'org.springframework.boot:spring-boot-starter-batch'
    
    // Reporting
    implementation 'org.apache.pdfbox:pdfbox:2.0.29'
    implementation 'org.apache.poi:poi-ooxml:5.2.3'
    implementation 'org.apache.commons:commons-csv:1.10.0'
    
    // HTTP Client
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    
    // Resilience
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.1.0'
    
    // Cache
    implementation 'com.github.ben-manes.caffeine:caffeine'
    
    // Utils
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.testcontainers:postgresql:1.19.1'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.1'
    testImplementation 'io.rest-assured:rest-assured:5.3.2'
}
```

### 11.3 Docker Setup

```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN ./gradlew clean build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.billing.saas.SubscriptionBillingSaasApplication"]
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: billing_saas
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management-alpine
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DATABASE_URL: jdbc:postgresql://postgres:5432/billing_saas
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: postgres
      RABBITMQ_HOST: rabbitmq
      RABBITMQ_USERNAME: admin
      RABBITMQ_PASSWORD: admin
    depends_on:
      - postgres
      - rabbitmq

volumes:
  postgres-data:
  rabbitmq-data:
```

### 11.4 Run Commands

```bash
# Local development
./gradlew bootRun

# Build Docker image
docker build -t billing-saas:latest .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Run tests
./gradlew test

# Generate test coverage report
./gradlew jacocoTestReport
```

---

## 12. Cost Estimates

### 12.1 Infrastructure Costs (DigitalOcean)

| Component | Specification | Monthly Cost |
|-----------|--------------|--------------|
| **Kubernetes Cluster** | 2 nodes (Basic, 2GB RAM) | $24 |
| **PostgreSQL DB** | Managed (2GB RAM, 25GB storage) | $15 |
| **Load Balancer** | 1 LB | $12 |
| **Container Registry** | 1 repository | $5 |
| **Spaces (Storage)** | 250GB storage + CDN | $5 |
| **Monitoring** | Basic metrics | Free |
| **Backups** | Automated daily backups | $3 |
| **Total Infrastructure** | | **$64/month** |

### 12.2 Third-Party Service Costs

| Service | Provider | Monthly Cost |
|---------|----------|--------------|
| **WhatsApp Messages** | Twilio | $0.005/msg (~$15 for 3000 msgs) |
| **SMS Backup** | Twilio | $0.02/msg (~$10 for 500 msgs) |
| **Email Service** | SendGrid (Free tier) | $0 (12k emails/month) |
| **Error Tracking** | Sentry (Free tier) | $0 |
| **SSL Certificate** | Let's Encrypt | Free |
| **Total Services** | | **~$25/month** |

### 12.3 Development Costs (One-time)

| Item | Estimated Cost | Timeline |
|------|---------------|----------|
| **Backend Development** | Self (no cost) | 8 weeks |
| **Mobile App Development** | $3000-5000 (Flutter developer) | 6-8 weeks |
| **UI/UX Design** | $500-1000 | 2 weeks |
| **Testing & QA** | $500 | 1 week |
| **Total Development** | **$4000-6500** | **10-12 weeks** |

**Grand Total:**
- **Development:** $4000-6500 (one-time)
- **Monthly Operations:** ~$90/month
- **Per-user cost:** ~$0.50/month (for notifications)

---

## 13. Security Considerations

### 13.1 Authentication & Authorization

- JWT-based stateless authentication
- Role-based access control (SELLER, SUBSCRIBER, ADMIN)
- Token expiration and refresh mechanism
- Secure password storage (BCrypt)
- API key management for third-party integrations

### 13.2 Data Protection

- HTTPS only (TLS 1.2+)
- Database encryption at rest
- Sensitive data masking in logs
- PII data protection compliance
- Regular security audits

### 13.3 API Security

- Rate limiting per IP/user
- Request validation and sanitization
- SQL injection prevention (JPA)
- XSS protection
- CORS configuration
- API key rotation policy

### 13.4 Security Headers

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .contentSecurityPolicy("default-src 'self'")
                .xssProtection()
                .frameOptions().deny()
                .httpStrictTransportSecurity()
            )
            .csrf(csrf -> csrf.disable()) // For REST API
            .cors()
            .and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 14. Monitoring & Observability

### 14.1 Application Metrics

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
```

### 14.2 Key Metrics to Monitor

**Application Metrics:**
- Request rate (requests/sec)
- Response time (p50, p95, p99)
- Error rate (4xx, 5xx)
- JVM memory usage
- Database connection pool utilization

**Business Metrics:**
- Daily active sellers
- Subscribers added/day
- Bills generated/day
- Payments recorded/day
- Notification success rate
- API usage per endpoint

### 14.3 Logging Strategy

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/billing-saas/application.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/billing-saas/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### 14.4 Alerting Rules

**Critical Alerts:**
- Application downtime > 2 minutes
- Error rate > 5% for 5 minutes
- Database connection pool exhausted
- Memory usage > 90%
- Notification failure rate > 20%

**Warning Alerts:**
- Response time p95 > 2 seconds
- Database query time > 1 second
- Disk usage > 80%
- Payment collection rate drops > 10%

---

## 15. CI/CD Pipeline

### 15.1 GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml
name: Build and Deploy

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle') }}
          restore-keys: ${{ runner.os }}-gradle
      
      - name: Run tests
        run: ./gradlew test jacocoTestReport
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: docker build -t billing-saas:${{ github.sha }} .
      
      - name: Log in to DigitalOcean Container Registry
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}
      
      - name: Push image to registry
        run: |
          doctl registry login
          docker tag billing-saas:${{ github.sha }} registry.digitalocean.com/your-registry/billing-saas:latest
          docker push registry.digitalocean.com/your-registry/billing-saas:latest

  deploy:
    needs: build
    runs-on: ubuntu-latest
    
    steps:
      - name: Deploy to Kubernetes
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}
      
      - name: Update deployment
        run: |
          doctl kubernetes cluster kubeconfig save your-cluster
          kubectl rollout restart deployment/billing-saas-api -n production
          kubectl rollout status deployment/billing-