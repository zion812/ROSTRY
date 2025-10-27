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
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.canSendOtp = exports.setPhoneVerifiedClaim = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
if (!admin.apps.length) {
    admin.initializeApp();
}
const db = admin.firestore();
exports.setPhoneVerifiedClaim = (0, https_1.onCall)(async (request) => {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "Sign in required");
    }
    const uid = request.auth.uid;
    // Merge existing claims with phone_verified
    const token = request.auth.token || {};
    await admin.auth().setCustomUserClaims(uid, { ...token, phone_verified: true });
    // Optional audit log
    await db.collection("verification_audits").add({
        type: "PHONE_VERIFY",
        uid,
        timestamp: Date.now(),
        actor: uid,
    }).catch(() => undefined);
    return { ok: true };
});
async function incrementAndCheck(key, windowSec, max) {
    const now = Date.now();
    const bucketStart = Math.floor(now / (windowSec * 1000)) * windowSec * 1000;
    const docId = `${key}-${bucketStart}`;
    const ref = db.collection("otp_throttle").doc(docId);
    await db.runTransaction(async (tx) => {
        const snap = await tx.get(ref);
        const count = (snap.exists ? snap.data()?.count || 0 : 0) + 1;
        if (!snap.exists) {
            tx.set(ref, { count, bucketStart, createdAt: now });
        }
        else {
            tx.update(ref, { count });
        }
    });
    const snap = await ref.get();
    const count = snap.data()?.count || 0;
    return count <= max;
}
exports.canSendOtp = (0, https_1.onCall)(async (request) => {
    const phone = request.data?.phone || "";
    const raw = request.rawRequest;
    const ipHeader = raw?.headers?.["x-forwarded-for"] || raw?.ip || "";
    const ip = ipHeader.split(",")[0].trim();
    if (!phone) {
        throw new https_1.HttpsError("invalid-argument", "phone required");
    }
    // Limits: phone (3/30s, 10/10min), IP (6/30s, 30/10min)
    const ok1 = await incrementAndCheck(`phone:${phone}`, 30, 3);
    const ok2 = await incrementAndCheck(`phone:${phone}`, 600, 10);
    const ok3 = await incrementAndCheck(`ip:${ip}`, 30, 6);
    const ok4 = await incrementAndCheck(`ip:${ip}`, 600, 30);
    if (!(ok1 && ok2 && ok3 && ok4)) {
        throw new https_1.HttpsError("resource-exhausted", "Too many requests");
    }
    return { ok: true };
});
