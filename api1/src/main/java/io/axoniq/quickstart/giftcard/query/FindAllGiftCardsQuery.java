package io.axoniq.quickstart.giftcard.query;

/**
 * Query for retrieving all gift cards from the system.
 *
 * <p>This parameterless query follows the CQRS pattern and is used to fetch all gift cards
 * currently stored in the read model. The query is handled by the read-side projection running
 * in the query service, which maintains an in-memory view of all gift card states.</p>
 *
 * <p>The query supports both synchronous retrieval and subscription queries for real-time updates.
 * As a subscription query, clients will receive the initial list of all gift cards followed by
 * real-time notifications whenever any gift card is issued, redeemed, or modified.</p>
 *
 * <p>Query characteristics:</p>
 * <ul>
 *   <li>Collection query - returns multiple aggregate instances</li>
 *   <li>Read-only - does not modify system state</li>
 *   <li>Eventually consistent - reflects the latest processed events</li>
 *   <li>Subscription-capable - supports real-time updates for the entire collection</li>
 *   <li>Efficient - operates on in-memory projection for fast retrieval</li>
 * </ul>
 *
 * <p>Returns {@link GiftCardSummaryList} containing:</p>
 * <ul>
 *   <li>List of all {@link GiftCardSummary} objects</li>
 *   <li>Each summary includes gift card ID, remaining balance, and original amount</li>
 *   <li>Ordered by creation time (most recent first)</li>
 * </ul>
 *
 * <p>Use cases:</p>
 * <ul>
 *   <li>Dashboard display showing all active gift cards</li>
 *   <li>Real-time monitoring of gift card inventory</li>
 *   <li>Administrative reporting and analytics</li>
 *   <li>Live updates for management interfaces</li>
 * </ul>
 *
 * @see GiftCardSummary
 * @see GiftCardSummaryList
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record FindAllGiftCardsQuery() {
}
