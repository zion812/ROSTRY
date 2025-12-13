# ROSTRY Project: Master Context & Development Guide

## 1. Motive & Vision

**Motive:** To revolutionize the poultry ecosystem in Andhra Pradesh by moving from a fragmented, fraud-prone market to a trusted, transparent digital community. The goal is to economically empower grassroots farmers and protect the heritage of native chicken breeds (Nattu Kodi).

**Vision:** "A New Digital Economy for Tradition"

**The Digital Asset:** Every bird is a digital asset with a unique Digital Record (DR) containing its lineage (family tree), health history (vaccinations), and ownership chain.

**The Trust Cycle:**
*   **For Consumers:** "Farm to Five-Star" transparency. A QR scan reveals the bird's origin, breed, diet, and health, justifying premium pricing for authentic products.
*   **For Farmers:** "The Farm in Your Pocket." Digitization reduces mortality through automated health alerts and data-driven breeding, while direct market access eliminates middlemen.
*   **For Enthusiasts:** A censorship-free social hub for high-value game fowl, preserving bloodlines through immutable digital traceability.

## 2. User Roles & Specific Functionalities

The application serves three distinct user personas, each with unique flows and feature sets.

### A. General User (Urban Consumer)
**Primary Goal:** Trustworthy consumption and sourcing of authentic poultry.

**Key Features:**
*   **Marketplace Exploration:** Filter by nearby, verified, breed, and age group.
*   **Traceability:** Scan QR codes to view product lineage and farmer verification status.
*   **Social Interaction:** Follow farmers, like/comment on posts, direct messaging.
*   **Orders:** Secure payment (online/COD) and order tracking.

### B. Farmer (Rural/Semi-Urban Producer)
**Primary Goal:** Sales, farm management, and community support.

**Key Features:**
*   **Product Listing:** List flocks (groups) or individual birds. Mandatory fields: breed, age, gender, weight, price.
*   **Farm Management:** Basic mortality tracking, medication reminders, and vaccination schedules.
*   **Verification:** KYC for "Verified Badge" to build trust.
*   **Community:** Access to expert advice, "Elects" (polls/decisions), and peer support.

### C. High-Level User (Breeder/Enthusiast)
**Primary Goal:** Brand building, precision breeding, and high-value asset transfer.

**Key Features:**
*   **Advanced Dashboard:** Batch-wise monitoring (Incubation -> Chick -> Adult -> Breeder).
*   **Digital Transfer:** The "Transfer" feature is critical. It transfers the rights and digital family tree of a bird to a new owner, recorded securely.
*   **Breeding Management:** Track lineage (pedigree), egg production stats, and pairing outcomes.
*   **Broadcasting:** Live video streaming for auctions or showcasing prize birds.

## 3. Role-Specific Functionality Fetchers

These are the specific functional units (Fetchers) the codebase must support, mapped to the architecture.

### Core Fetchers (Universal):
*   `#Messaging` (P2P and Group Chat)
*   `#ProfileEditing` (User/Farm details)
*   `#Media` & `#MediaShare` (Image/Video handling with Coil)
*   `#Notifications` & `#Alerts` (WorkManager backed)

### Marketplace & Commerce Fetchers:
*   `#ProductListing` (CRUD for listings)
*   `#MarketListing` (Search/Filter logic)
*   `#Orders` & `#OrdersTracking`
*   `#Payment` (Gateways + COD logic)
*   `#PriceBidding` (Auction logic for high-value birds)
*   `#Fixed-price` (Standard e-commerce logic)

### Farm & Bioscience Fetchers:
*   `#Farm` (Farm profile management)
*   `#MonitoringProduct` (General health tracking)
*   `#GrowthMonitoring` (Weight/Size over time)
*   `#Vaccination` (Schedule & Reminder logic)
*   `#Medication` (Treatment logs)
*   `#Mortality` (Death recording & reasoning)
*   `#Quarantine` (Isolation tracking for sick birds - 12hr update cycles)
*   `#BreedingSection` (Parentage linking)
*   `#Hatching` (Batch creation from eggs)

### Traceability & Social Fetchers:
*   `#ProductTransfer` (The secure ownership handover protocol)
*   `#SharingOwnership` (Co-ownership logic)
*   `#WEBshareAPI` (External sharing via WhatsApp/Insta)
*   `#Verified` (Badge logic based on KYC/Location)
*   `#AdoptionListing` (Specific flow for non-meat/pet birds)
