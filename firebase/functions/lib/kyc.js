"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.verifyKYC = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
const common_1 = require("./common");
/**
 * Validates and processes a KYC verification review.
 * ONLY executable by users with the 'ADMIN' role.
 *
 * @param data { userId: string, verdict: 'APPROVED' | 'REJECTED', reason?: string }
 */
exports.verifyKYC = (0, https_1.onCall)(async (request) => {
    var _a;
    // 1. Authentication & Authorization Guard
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "Authentication required.");
    }
    const callerUid = request.auth.uid;
    const callerToken = await common_1.auth.getUser(callerUid);
    const callerRole = (_a = callerToken.customClaims) === null || _a === void 0 ? void 0 : _a.role;
    if (callerRole !== "ADMIN") {
        // Fallback check against a hardcoded admin list or Firestore if claims aren't set
        // For now, strict claim check.
        throw new https_1.HttpsError("permission-denied", "Only admins can verify KYC.");
    }
    const { userId, verdict, reason } = request.data;
    if (!userId || !["APPROVED", "REJECTED"].includes(verdict)) {
        throw new https_1.HttpsError("invalid-argument", "Valid userId and verdict required.");
    }
    if (verdict === "REJECTED" && !reason) {
        throw new https_1.HttpsError("invalid-argument", "Reason required for rejection.");
    }
    try {
        await common_1.db.runTransaction(async (transaction) => {
            const userRef = common_1.db.collection("users").doc(userId);
            const verificationRef = common_1.db.collection("verifications").doc(userId);
            const userSnap = await transaction.get(userRef);
            const verificationSnap = await transaction.get(verificationRef);
            if (!userSnap.exists || !verificationSnap.exists) {
                throw new https_1.HttpsError("not-found", "User or verification record not found.");
            }
            const verificationData = verificationSnap.data();
            const targetRole = (verificationData === null || verificationData === void 0 ? void 0 : verificationData.targetRole) || "FARMER"; // Default upgrade
            if (verdict === "APPROVED") {
                // Update User
                transaction.update(userRef, {
                    verificationStatus: "VERIFIED",
                    userType: targetRole,
                    kycVerifiedAt: admin.firestore.FieldValue.serverTimestamp(),
                    kycRejectionReason: admin.firestore.FieldValue.delete(),
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                });
                // Update Verification Request
                transaction.update(verificationRef, {
                    currentStatus: "VERIFIED",
                    reviewedBy: callerUid,
                    reviewedAt: admin.firestore.FieldValue.serverTimestamp(),
                    reviewNotes: "Approved via Admin Panel",
                });
                await common_1.auth.setCustomUserClaims(userId, {
                    role: targetRole,
                    verified: true
                });
            }
            else {
                // Rejected
                transaction.update(userRef, {
                    verificationStatus: "REJECTED",
                    kycRejectionReason: reason,
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                });
                transaction.update(verificationRef, {
                    currentStatus: "REJECTED",
                    reviewedBy: callerUid,
                    reviewedAt: admin.firestore.FieldValue.serverTimestamp(),
                    rejectionReason: reason,
                });
            }
        });
        return { success: true, userId, verdict };
    }
    catch (error) {
        console.error("verifyKYC failed", error);
        throw new https_1.HttpsError("internal", "Verification update failed.");
    }
});
//# sourceMappingURL=kyc.js.map