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
exports.closeAuction = exports.initiateAuction = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
const common_1 = require("./common");
exports.initiateAuction = (0, https_1.onCall)(async (request) => {
    if (!request.auth)
        throw new https_1.HttpsError("unauthenticated", "Auth required.");
    const { productId, durationHours, startPrice } = request.data;
    if (!productId || !durationHours || !startPrice) {
        throw new https_1.HttpsError("invalid-argument", "Missing auction details.");
    }
    try {
        const productRef = common_1.db.collection("products").doc(productId);
        const auctionRef = common_1.db.collection("auctions").doc(productId);
        await common_1.db.runTransaction(async (t) => {
            var _a;
            const productSnap = await t.get(productRef);
            if (!productSnap.exists || ((_a = productSnap.data()) === null || _a === void 0 ? void 0 : _a.sellerId) !== request.auth.uid) {
                throw new https_1.HttpsError("permission-denied", "Not owner.");
            }
            const startTime = admin.firestore.Timestamp.now();
            const endTime = admin.firestore.Timestamp.fromMillis(startTime.toMillis() + (durationHours * 3600 * 1000));
            t.update(productRef, {
                isAuction: true,
                auctionStatus: "ACTIVE",
                currentPrice: startPrice,
                updatedAt: admin.firestore.FieldValue.serverTimestamp()
            });
            t.set(auctionRef, {
                productId,
                sellerId: request.auth.uid,
                startTime: startTime,
                endTime: endTime,
                startPrice: startPrice,
                currentBid: startPrice,
                bidCount: 0,
                status: "ACTIVE",
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
        });
        return { success: true, auctionId: productId };
    }
    catch (e) {
        throw new https_1.HttpsError("internal", e.message);
    }
});
exports.closeAuction = (0, https_1.onCall)(async (request) => {
    if (!request.auth)
        throw new https_1.HttpsError("unauthenticated", "Auth required.");
    const { productId } = request.data;
    try {
        const auctionRef = common_1.db.collection("auctions").doc(productId);
        await common_1.db.runTransaction(async (t) => {
            const auctionSnap = await t.get(auctionRef);
            if (!auctionSnap.exists)
                throw new https_1.HttpsError("not-found", "Auction not found");
            const auction = auctionSnap.data();
            if ((auction === null || auction === void 0 ? void 0 : auction.status) !== "ACTIVE")
                throw new https_1.HttpsError("failed-precondition", "Auction not active");
            // Logic to determine winner
            const highestBidQuery = await common_1.db.collection("auctions")
                .doc(productId)
                .collection("bids")
                .orderBy("amount", "desc")
                .limit(1)
                .get();
            let winnerId = null;
            let finalPrice = auction.startPrice;
            if (!highestBidQuery.empty) {
                const winningBid = highestBidQuery.docs[0].data();
                winnerId = winningBid.userId;
                finalPrice = winningBid.amount;
            }
            t.update(auctionRef, {
                status: "CLOSED",
                winnerId: winnerId,
                finalPrice: finalPrice,
                closedAt: admin.firestore.FieldValue.serverTimestamp()
            });
            t.update(common_1.db.collection("products").doc(productId), {
                auctionStatus: "CLOSED",
                soldTo: winnerId,
                isSold: !!winnerId
            });
            if (winnerId) {
                const orderRef = common_1.db.collection("orders").doc();
                t.set(orderRef, {
                    orderId: orderRef.id,
                    buyerId: winnerId,
                    sellerId: auction.sellerId,
                    items: [{ productId, price: finalPrice, qty: 1 }],
                    totalAmount: finalPrice,
                    status: "PENDING_PAYMENT",
                    type: "AUCTION_WIN",
                    createdAt: admin.firestore.FieldValue.serverTimestamp()
                });
            }
        });
        return { success: true };
    }
    catch (e) {
        throw new https_1.HttpsError("internal", e.message);
    }
});
//# sourceMappingURL=auctions.js.map