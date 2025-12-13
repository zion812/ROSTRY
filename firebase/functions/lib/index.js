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
exports.refreshUserClaims = exports.setUserRoleClaim = void 0;
const functions = __importStar(require("firebase-functions"));
const admin = __importStar(require("firebase-admin"));
// Initialize Firebase Admin
admin.initializeApp();
/**
 * Cloud Function to set custom claims when user role changes.
 * Triggered when a user document is created or updated in Firestore.
 *
 * Custom claims set:
 * - role: User's current role (GENERAL, FARMER, ENTHUSIAST)
 * - verified: Whether user is verified (VERIFIED status)
 */
exports.setUserRoleClaim = functions.firestore
    .document("users/{userId}")
    .onWrite(async (change, context) => {
    const userId = context.params.userId;
    // If document was deleted, remove custom claims
    if (!change.after.exists) {
        await admin.auth().setCustomUserClaims(userId, {});
        console.log(`Removed custom claims for deleted user: ${userId}`);
        return;
    }
    const userData = change.after.data();
    if (!userData)
        return;
    const role = userData.userType || "GENERAL";
    const verified = userData.verificationStatus === "VERIFIED";
    try {
        // Set custom claims
        await admin.auth().setCustomUserClaims(userId, {
            role: role,
            verified: verified,
        });
        console.log(`Set custom claims for ${userId}: role=${role}, verified=${verified}`);
        // Update the user document with a timestamp of when claims were set
        // This helps with debugging and can trigger client token refresh
        await change.after.ref.update({
            customClaimsUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
        });
    }
    catch (error) {
        console.error(`Error setting custom claims for ${userId}:`, error);
        throw error;
    }
});
/**
 * Callable function to manually refresh custom claims.
 * Can be called from the client to force a custom claims update.
 */
exports.refreshUserClaims = functions.https.onCall(async (data, context) => {
    // Ensure user is authenticated
    if (!context.auth) {
        throw new functions.https.HttpsError("unauthenticated", "Must be authenticated to refresh claims");
    }
    const userId = context.auth.uid;
    try {
        // Fetch user document from Firestore
        const userDoc = await admin.firestore().collection("users").doc(userId).get();
        if (!userDoc.exists) {
            throw new functions.https.HttpsError("not-found", "User document not found");
        }
        const userData = userDoc.data();
        const role = (userData === null || userData === void 0 ? void 0 : userData.userType) || "GENERAL";
        const verified = (userData === null || userData === void 0 ? void 0 : userData.verificationStatus) === "VERIFIED";
        // Update custom claims
        await admin.auth().setCustomUserClaims(userId, {
            role: role,
            verified: verified,
        });
        console.log(`Refreshed custom claims for ${userId}: role=${role}, verified=${verified}`);
        return {
            success: true,
            role: role,
            verified: verified,
        };
    }
    catch (error) {
        console.error(`Error refreshing claims for ${userId}:`, error);
        throw new functions.https.HttpsError("internal", error.message);
    }
});
//# sourceMappingURL=index.js.map