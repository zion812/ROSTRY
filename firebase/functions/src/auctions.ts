import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { db } from "./common";

export const initiateAuction = onCall(async (request) => {
    if (!request.auth) throw new HttpsError("unauthenticated", "Auth required.");

    const { productId, durationHours, startPrice } = request.data;
    if (!productId || !durationHours || !startPrice) {
        throw new HttpsError("invalid-argument", "Missing auction details.");
    }

    try {
        const productRef = db.collection("products").doc(productId);
        const auctionRef = db.collection("auctions").doc(productId);

        await db.runTransaction(async (t) => {
            const productSnap = await t.get(productRef);
            if (!productSnap.exists || productSnap.data()?.sellerId !== request.auth!.uid) {
                throw new HttpsError("permission-denied", "Not owner.");
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
                sellerId: request.auth!.uid,
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
    } catch (e: any) {
        throw new HttpsError("internal", e.message);
    }
});


export const closeAuction = onCall(async (request) => {
    if (!request.auth) throw new HttpsError("unauthenticated", "Auth required.");

    const { productId } = request.data;

    try {
        const auctionRef = db.collection("auctions").doc(productId);

        await db.runTransaction(async (t) => {
            const auctionSnap = await t.get(auctionRef);
            if (!auctionSnap.exists) throw new HttpsError("not-found", "Auction not found");

            const auction = auctionSnap.data();
            if (auction?.status !== "ACTIVE") throw new HttpsError("failed-precondition", "Auction not active");

            // Logic to determine winner
            const highestBidQuery = await db.collection("auctions")
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

            t.update(db.collection("products").doc(productId), {
                auctionStatus: "CLOSED",
                soldTo: winnerId,
                isSold: !!winnerId
            });

            if (winnerId) {
                const orderRef = db.collection("orders").doc();
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
    } catch (e: any) {
        throw new HttpsError("internal", e.message);
    }
});
