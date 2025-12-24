# Evidence-Based Order System

## Overview

ROSTRY implements a **trust-based payment system** where the app acts as a trusted middleman for **workflow + proof + agreement**, not for moving money. Money transactions happen outside the app (via UPI/bank/cash), but **trust is created inside the app** through evidence collection and mutual approvals.

## Core Principle

> "Money happens outside the app, but trust is created inside the app via evidence + approvals."

The platform becomes the **record of truth** by maintaining an immutable evidence pack for each transaction.

---

## Order Types (Payment Patterns)

| Type | Description | Advance Required | Risk Level |
|------|-------------|------------------|------------|
| **COD** | Cash on Delivery | No | Higher for seller |
| **Full Advance** | 100% payment before dispatch | Yes (100%) | Lower for seller |
| **Split 50/50** | 50% advance + 50% on delivery | Yes (50%) | Balanced risk |
| **Buyer Pickup** | Buyer comes to seller location | No | Lower disputes |

---

## 10-State Order Workflow

```
┌─────────────────────────────────────────────────────────────────┐
│                    ENQUIRY / REQUESTED                          │
│   Buyer sends enquiry (quantity, location, payment preference)  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      QUOTE_SENT                                  │
│   Seller confirms price + delivery fee + payment type allowed   │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   AGREEMENT_LOCKED                               │
│   Both sides click "Agree" - price locked, no edits allowed     │
└──────────────────────────┬──────────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              │ If advance required     │
              ▼                         ▼
┌─────────────────────────┐   ┌────────────────────┐
│   ADVANCE_PENDING       │   │  (Skip to PREPARING │
│   Buyer must upload     │   │   for COD/Pickup)   │
│   payment proof         │   └────────────────────┘
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────┐
│               PAYMENT_PROOF_SUBMITTED                            │
│   Buyer uploads UPI screenshot / bank slip                      │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                  PAYMENT_VERIFIED                                │
│   Seller confirms "Received ₹X" in bank                         │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│               DISPATCHED / READY_FOR_PICKUP                      │
│   Seller uploads packing photos + dispatch details              │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DELIVERED                                   │
│   Buyer confirms via OTP / Photo / GPS                          │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      COMPLETED                                   │
│   Transaction ends, feedback enabled                            │
└─────────────────────────────────────────────────────────────────┘
```

### Error States
- **DISPUTE**: Disagreement raised by either party
- **ESCALATED**: Dispute escalated to ROSTRY admin
- **CANCELLED**: Order cancelled before completion
- **EXPIRED**: Order expired due to timeout

---

## 2-Step Pricing Agreement

### Step 1: Buyer Creates Enquiry
- Selects product and quantity
- Provides delivery location
- Chooses payment preference

### Step 2: Seller Sends Quote
- Sets base price per unit
- Calculates delivery charge based on distance
- Adds optional packing fee
- Quote expires after 24 hours

### Step 3: Lock Agreement
- Both parties must click "Agree"
- Once locked, **no edits allowed**
- Only cancel and recreate if changes needed

---

## Evidence Pack (Record of Truth)

For each order, we store an immutable **Evidence Pack**:

| Evidence Type | Stage | Uploaded By |
|---------------|-------|-------------|
| Payment Screenshot | Advance Payment | Buyer |
| Bank Slip | Advance Payment | Buyer |
| Transaction Reference | Payment Verification | Buyer |
| Packing Photo | Dispatch | Seller |
| Dispatch Photo | Dispatch | Seller |
| Invoice Photo | Dispatch | Seller |
| Delivery Handover Photo | Delivery | Seller |
| Buyer Confirmation Photo | Delivery | Buyer |
| OTP Confirmation | Delivery | System |
| GPS Location | Delivery | System |
| Dispute Evidence | Dispute | Either |
| Chat Snapshot | Dispute | System |

---

## Fraud Prevention Mechanisms

### 1. Price Lock After Agreement
- No price modifications after both parties agree
- Only option is cancel and recreate

### 2. OTP-Based Delivery Confirmation
- 6-digit OTP generated when order dispatched
- Buyer shares OTP with seller at delivery
- Max 3 attempts, 4-hour expiry

### 3. Partial Payment Tracking
- Advance and balance tracked separately
- Each payment has its own proof requirement

### 4. Automatic Timeouts
- Quote expires after 24 hours without agreement
- Payment proof must be uploaded within X hours
- Buyer must confirm delivery within 24 hours

### 5. Role-Based Restrictions
- Buyer cannot mark "paid" without uploading proof (except COD)
- Seller cannot mark "delivered" without dispatch proof

---

## Database Schema

### New Tables (Version 53)

| Table | Purpose |
|-------|---------|
| `order_evidence` | Stores photos, screenshots, GPS data |
| `order_quotes` | Price agreements and negotiation history |
| `order_payments` | Advance, balance, and full payments |
| `delivery_confirmations` | OTP and delivery confirmation |
| `order_disputes` | Dispute tracking and resolution |
| `order_audit_logs` | Complete audit trail |

---

## API Reference

### Quote Management
- `createEnquiry()` - Buyer initiates order
- `sendQuote()` - Seller sends price quote
- `counterOffer()` - Create counter-offer
- `buyerAgreeToQuote()` / `sellerAgreeToQuote()` - Lock agreement

### Payment Management
- `createPaymentRequest()` - System creates payment request
- `submitPaymentProof()` - Buyer uploads proof
- `verifyPayment()` - Seller confirms receipt
- `rejectPayment()` - Seller rejects proof

### Evidence Management
- `uploadEvidence()` - Upload any evidence type
- `verifyEvidence()` - Verify uploaded evidence
- `getOrderEvidence()` - Get all evidence for order

### Delivery Confirmation
- `generateDeliveryOtp()` - Generate 6-digit OTP
- `verifyDeliveryOtp()` - Verify OTP at delivery
- `confirmDeliveryWithPhoto()` - Photo-based confirmation
- `markBalanceCollected()` - Mark COD payment received

### Disputes
- `raiseDispute()` - Raise a dispute
- `escalateDispute()` - Escalate to admin
- `resolveDispute()` - Close dispute with resolution

---

## Files Added

| File | Description |
|------|-------------|
| `EvidenceOrderTypes.kt` | Enums for status, payment types, evidence types |
| `EvidenceOrderEntities.kt` | Room entities for all tables |
| `EvidenceOrderDaos.kt` | DAOs for database operations |
| `EvidenceOrderRepository.kt` | Business logic and workflow |

---

## Next Steps (UI Integration)

1. **Order Creation Flow** - Replace existing checkout with evidence-based flow
2. **Quote Negotiation Screen** - Back-and-forth quote interface
3. **Payment Proof Upload** - Camera/gallery picker for screenshots
4. **Delivery OTP Screen** - OTP display for buyer, input for seller
5. **Dispute Management** - Raise and track disputes
6. **Seller Dashboard** - Pending verifications widget
