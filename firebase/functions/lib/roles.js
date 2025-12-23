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
const https_1 = require("firebase-functions/v2/https");
const firestore_1 = require("firebase-functions/v2/firestore");
const admin = __importStar(require("firebase-admin"));
const common_1 = require("./common");
/**
 * Cloud Function to set custom claims when user role changes.
 * Triggered when a user document is created or updated in Firestore.
 */
exports.setUserRoleClaim = (0, firestore_1.onDocumentWritten)("users/{userId}", async (event) => {
    var _a, _b, _c, _d;
    const userId = event.params.userId;
    const dataBefore = (_a = event.data) === null || _a === void 0 ? void 0 : _a.before.data();
    const dataAfter = (_b = event.data) === null || _b === void 0 ? void 0 : _b.after.data();
    // If document was deleted, remove custom claims
    if (!((_c = event.data) === null || _c === void 0 ? void 0 : _c.after.exists)) {
        await common_1.auth.setCustomUserClaims(userId, {});
        console.log(`Removed custom claims for deleted user: ${userId}`);
        return;
    }
    if (!dataAfter)
        return;
    const newRole = dataAfter.userType || "GENERAL";
    const newVerified = dataAfter.verificationStatus === "VERIFIED";
    // Idempotency check: Only update if relevant fields changed
    const oldRole = (dataBefore === null || dataBefore === void 0 ? void 0 : dataBefore.userType) || "GENERAL";
    const oldVerified = (dataBefore === null || dataBefore === void 0 ? void 0 : dataBefore.verificationStatus) === "VERIFIED";
    if (((_d = event.data) === null || _d === void 0 ? void 0 : _d.before.exists) && newRole === oldRole && newVerified === oldVerified && dataAfter.customClaimsUpdatedAt) {
        console.log(`Claims already up to date for ${userId}. Skipping.`);
        return;
    }
    try {
        await common_1.auth.setCustomUserClaims(userId, {
            role: newRole,
            verified: newVerified,
        });
        console.log(`Set custom claims for ${userId}: role=${newRole}, verified=${newVerified}`);
        await event.data.after.ref.update({
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
exports.refreshUserClaims = (0, https_1.onCall)(async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "Must be authenticated to refresh claims");
    }
    const userId = request.auth.uid;
    try {
        const userDoc = await admin.firestore().collection("users").doc(userId).get();
        if (!userDoc.exists) {
            throw new https_1.HttpsError("not-found", "User document not found");
        }
        const userData = userDoc.data();
        const role = (userData === null || userData === void 0 ? void 0 : userData.userType) || "GENERAL";
        const verified = (userData === null || userData === void 0 ? void 0 : userData.verificationStatus) === "VERIFIED";
        await common_1.auth.setCustomUserClaims(userId, {
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
        throw new https_1.HttpsError("internal", error.message);
    }
});
//# sourceMappingURL=roles.js.map