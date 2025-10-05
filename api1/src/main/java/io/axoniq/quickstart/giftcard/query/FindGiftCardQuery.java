package io.axoniq.quickstart.giftcard.query;

/**
 * Query for retrieving a specific gift card by its unique identifier.
 *
 * <p>This query follows the CQRS pattern and is used to fetch a single gift card's
 * current state from the read model. The query is handled by the read-side projection hosted
 * in the query service, which maintains an in-memory representation of all gift cards.</p>
 *
 * <p>The query supports both synchronous retrieval and subscription queries for real-time updates.
 * When used as a subscription query, clients will receive the initial state followed by
 * live updates whenever the gift card state changes due to redemptions.</p>
 *
 * <p>Query characteristics:</p>
 * <ul>
 *   <li>Point queries - targets a specific aggregate instance</li>
 *   <li>Read-only - does not modify system state</li>
 *   <li>Eventually consistent - reflects the latest processed events</li>
 *   <li>Subscription-capable - supports real-time updates via Server-Sent Events</li>
 * </ul>
 *
 * <p>Returns {@link GiftCardSummary} containing:</p>
 * <ul>
 *   <li>Gift card ID</li>
 *   <li>Current remaining balance</li>
 *   <li>Original issued amount</li>
 * </ul>
 *
 * @param giftCardId the unique identifier of the gift card to retrieve
 *
 * @see GiftCardSummary
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record FindGiftCardQuery(String giftCardId) {
}
