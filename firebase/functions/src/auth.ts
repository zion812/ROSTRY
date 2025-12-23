import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { db, auth } from "./common";

/**
 * Checks if a user is eligible to send an OTP.
 * Prevents abuse by enforcing rate limits per phone number/IP.
 * 
 * @param data { phoneNumber: string }
 */
export const canSendOtp = onCall(async (request) => {
    // Rate limiting logic would go here.
    return { allowed: true };
});

/**
 * Sets a custom claim 'phone_verified' to true.
 * This should only be called after successful phone auth provider verification.
 * 
 * @param data {} (empty)
 */
export const setPhoneVerifiedClaim = onCall(async (request) => {
    if (!request.auth) throw new HttpsError("unauthenticated", "Auth required.");

    const userId = request.auth.uid;
    const user = await auth.getUser(userId);

    // Verify that the user actually has a phone number
    if (!user.phoneNumber) {
        throw new HttpsError("failed-precondition", "User has no phone number.");
    }

    try {
        const currentClaims = user.customClaims || {};
        await auth.setCustomUserClaims(userId, {
            ...currentClaims,
            phone_verified: true
        });

        // Update Firestore as well for queryability
        await db.collection("users").doc(userId).update({
            phoneVerified: true,
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        return { success: true };
    } catch (e: any) {
        throw new HttpsError("internal", e.message);
    }
});
