# ☁️ Cascade File Service

<p align="center">
  <strong>A Secure, Scalable & Production-Oriented File Storage Backend</strong>
</p>

<p align="center">
  Built with Spring Boot, PostgreSQL, AWS S3, Spring Security and JWT
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-7.x-green?style=for-the-badge&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![AWS S3](https://img.shields.io/badge/AWS-S3-orange?style=for-the-badge&logo=amazons3)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-red?style=for-the-badge)

</p>

---

## 📌 Overview

**Cascade File Service** is a backend service designed for secure file storage and management.

The system separates:

- **File metadata** → PostgreSQL
- **Actual file bytes** → Amazon S3
- **Authentication** → Spring Security + JWT
- **Authorization** → Role-Based Access Control
- **Schema management** → Flyway
- **Application instances** → Stateless and horizontally scalable

The architecture is being developed with production-oriented principles such as:

- Security by design
- Stateless authentication
- Private object storage
- Server-generated storage identifiers
- Strong password hashing
- Email verification
- Role-based authorization
- Database migrations
- Ownership-based authorization
- Horizontal scalability
- Observability
- Automated testing
- CI/CD

---

# 🏗️ High-Level Architecture

```text
                              ┌──────────────────────┐
                              │        CLIENT        │
                              │                      │
                              │  Web / Mobile / API  │
                              └──────────┬───────────┘
                                         │
                                         │ HTTPS
                                         ▼
                              ┌──────────────────────┐
                              │    LOAD BALANCER     │
                              │   / REVERSE PROXY    │
                              └──────────┬───────────┘
                                         │
                       ┌─────────────────┼─────────────────┐
                       │                 │                 │
                       ▼                 ▼                 ▼
              ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
              │  Cascade API   │ │  Cascade API   │ │  Cascade API   │
              │   Instance A   │ │   Instance B   │ │   Instance C   │
              └───────┬────────┘ └───────┬────────┘ └───────┬────────┘
                      │                  │                  │
                      └──────────────────┼──────────────────┘
                                         │
                       ┌─────────────────┴─────────────────┐
                       │                                   │
                       ▼                                   ▼
              ┌──────────────────┐                ┌──────────────────┐
              │   PostgreSQL     │                │     AWS S3       │
              │                  │                │                  │
              │ User Metadata    │                │   File Bytes     │
              │ File Metadata    │                │                  │
              │ Auth Data        │                │ Private Bucket   │
              └──────────────────┘                └──────────────────┘

