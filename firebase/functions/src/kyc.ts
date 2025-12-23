import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { db, auth } from "./common";

/**
 * Validates and processes a KYC verification review.
 * ONLY executable by users with the 'ADMIN' role.
 * 
 * @param data { userId: string, verdict: 'APPROVED' | 'REJECTED', reason?: string }
 */
export const verifyKYC = onCall(async (request) => {
    // 1. Authentication & Authorization Guard
    if (!request.auth) {
        throw new HttpsError("unauthenticated", "Authentication required.");
    }

    const callerUid = request.auth.uid;
    const callerToken = await auth.getUser(callerUid);
    const callerRole = callerToken.customClaims?.role;

    if (callerRole !== "ADMIN") {
        // Fallback check against a hardcoded admin list or Firestore if claims aren't set
        // For now, strict claim check.
        throw new HttpsError("permission-denied", "Only admins can verify KYC.");
    }

    const { userId, verdict, reason } = request.data;
    if (!userId || !["APPROVED", "REJECTED"].includes(verdict)) {
        throw new HttpsError("invalid-argument", "Valid userId and verdict required.");
    }

    if (verdict === "REJECTED" && !reason) {
        throw new HttpsError("invalid-argument", "Reason required for rejection.");
    }

    try {
        await db.runTransaction(async (transaction) => {
            const userRef = db.collection("users").doc(userId);
            const verificationRef = db.collection("verifications").doc(userId);

            const userSnap = await transaction.get(userRef);
            const verificationSnap = await transaction.get(verificationRef);

            if (!userSnap.exists || !verificationSnap.exists) {
                throw new HttpsError("not-found", "User or verification record not found.");
            }

            const verificationData = verificationSnap.data();
            const targetRole = verificationData?.targetRole || "FARMER"; // Default upgrade

            if (verdict === "APPROVED") {
                // Update User
                transaction.update(userRef, {
                    verificationStatus: "VERIFIED",
                    userType: targetRole, // Apply the role upgrade
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

                await auth.setCustomUserClaims(userId, {
                    role: targetRole,
                    verified: true
                });

            } else {
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

    } catch (error) {
        console.error("verifyKYC failed", error);
        throw new HttpsError("internal", "Verification update failed.");
    }
});
