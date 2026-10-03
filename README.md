# 🌍 DAAV NGO Website

A responsive, multi-page NGO website for **DAAV**, focused on healthcare programs, community outreach, impact reporting, and a foundation for secure online donations.

The project is based in **Dar es Salaam, Tanzania** and is being developed as both a real-world engineering project and a learning exercise in modern web development, backend systems, security, and automation.

## 📌 Project Overview

The DAAV website presents the organization's healthcare programs and impact information through a responsive frontend.

The project is evolving from a static website into a full-stack application with a dedicated Spring Boot backend for donation and payment functionality.

### Current program areas

- HIV/AIDS Support
- Malaria Care
- TB Treatment
- Mobile Clinics
- Impact Data

## ✨ Current Features

### Frontend

- Responsive multi-page website
- Desktop and mobile navigation
- Program-specific pages
- Interactive donation modal
- Dark/light theme support
- Language toggle
- Smooth scrolling and UI animations
- Typewriter and parallax effects
- Responsive layouts for different screen sizes
- Consistent DAAV branding and visual system

### Backend

The backend is implemented using **Java and Spring Boot**.

Current backend foundations include:

- Donation domain model
- Payment domain model
- Donation status lifecycle
- Payment status lifecycle
- Persistence with Spring Data JPA
- Relational database support
- Currency attribute conversion
- Exchange-rate abstraction
- Configurable TZS → USD development conversion
- Application clock configuration for testable time-dependent logic
- Health/info management endpoints
- Automated unit and persistence tests

> The payment provider integration is being developed incrementally. Production payment credentials and secrets are not stored in the repository.

## 🏗️ Architecture

The project currently consists of two major application layers:

```text
DAAV NGO Website
│
├── Frontend
│   ├── HTML
│   ├── CSS
│   ├── Vanilla JavaScript
│   └── Static assets
│
└── Backend
    ├── Spring Boot
    ├── Spring Data JPA
    ├── Domain models
    ├── Repositories
    ├── Currency / FX handling
    ├── Payment foundation
    └── Automated tests
