package io.axoniq.quickstart.giftcard.query;

import io.axoniq.quickstart.giftcard.event.GiftCardIssuedEvent;
import io.axoniq.quickstart.giftcard.event.GiftCardRedeemedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event-driven projection that maintains an in-memory read model of gift card data.
 *
 * <p>This component implements the CQRS query side by maintaining a denormalized,
 * eventually consistent view of gift card states. It listens to domain events from
 * the {@link io.axoniq.quickstart.giftcard.aggregate.GiftCardAggregate} and updates
 * the read model accordingly, while also supporting real-time query subscriptions
 * through the Axon Framework's query update mechanism.</p>
 *
 * <p><strong>Architecture patterns implemented:</strong></p>
 * <ul>
 *   <li><strong>CQRS Query Side</strong>: Separates read operations from write operations</li>
 *   <li><strong>Event Sourcing Projection</strong>: Rebuilds state from event stream</li>
 *   <li><strong>In-Memory Read Model</strong>: Fast query performance using ConcurrentHashMap</li>
 *   <li><strong>Subscription Queries</strong>: Real-time updates to connected clients</li>
 * </ul>
 *
 * <p><strong>Event handling responsibilities:</strong></p>
 * <ul>
 *   <li>Creates new {@link GiftCardSummary} records when gift cards are issued</li>
 *   <li>Updates existing records when redemptions occur</li>
 *   <li>Emits query updates for real-time client notifications</li>
 *   <li>Maintains referential integrity and data consistency</li>
 * </ul>
 *
 * <p><strong>Query handling capabilities:</strong></p>
 * <ul>
 *   <li>Single gift card lookup by ID ({@link FindGiftCardQuery})</li>
 *   <li>Complete gift card collection retrieval ({@link FindAllGiftCardsQuery})</li>
 *   <li>Subscription query support for both query types</li>
 *   <li>Thread-safe concurrent access using ConcurrentHashMap</li>
 * </ul>
 *
 * <p><strong>Real-time update mechanism:</strong></p>
 * <p>Uses {@link QueryUpdateEmitter} to push updates to active subscription queries,
 * enabling real-time UI updates without polling. When events are processed, relevant
 * subscribers are notified immediately with the updated data.</p>
 *
 * <p><strong>Performance characteristics:</strong></p>
 * <ul>
 *   <li>O(1) query performance for single gift card lookups</li>
 *   <li>O(n) performance for all gift cards queries</li>
 *   <li>Memory usage proportional to number of gift cards</li>
 *   <li>No database I/O for query operations</li>
 * </ul>
 *
 * <p><strong>Consistency model:</strong></p>
 * <p>Provides eventual consistency where the read model reflects the latest processed
 * events. In this quickstart implementation, consistency is typically achieved within
 * milliseconds due to the in-memory, single-JVM deployment model.</p>
 *
 * @see GiftCardSummary
 * @see FindGiftCardQuery
 * @see FindAllGiftCardsQuery
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
@Component
public class GiftCardProjection {

    /**
     * In-memory store of gift card summaries keyed by gift card ID.
     * Uses ConcurrentHashMap for thread-safe concurrent access.
     */
    private final Map<String, GiftCardSummary> giftCards = new ConcurrentHashMap<>();

    /**
     * Axon Framework component for emitting query updates to subscription queries.
     * Enables real-time notifications to connected clients.
     */
    private final QueryUpdateEmitter queryUpdateEmitter;

    /**
     * Constructs a new GiftCardProjection with the required dependencies.
     *
     * @param queryUpdateEmitter the Axon Framework component for emitting query updates
     */
    public GiftCardProjection(QueryUpdateEmitter queryUpdateEmitter) {
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    /**
     * Event handler for gift card issued events.
     *
     * <p>This method is automatically invoked by the Axon Framework when a
     * {@link GiftCardIssuedEvent} is published. It creates a new {@link GiftCardSummary}
     * record in the read model and notifies any active subscription queries.</p>
     *
     * <p>Processing steps:</p>
     * <ul>
     *   <li>Creates a new GiftCardSummary with initial and remaining values equal</li>
     *   <li>Stores the summary in the in-memory map</li>
     *   <li>Emits update to FindGiftCardQuery subscribers for this specific ID</li>
     *   <li>Emits update to FindAllGiftCardsQuery subscribers (all gift cards list)</li>
     * </ul>
     *
     * @param event the gift card issued event containing ID and initial amount
     */
    @EventHandler
    public void on(GiftCardIssuedEvent event) {
        GiftCardSummary giftCard = new GiftCardSummary(
                event.giftCardId(),
                event.amount(),
                event.amount()
        );
        giftCards.put(event.giftCardId(), giftCard);

        queryUpdateEmitter.emit(FindGiftCardQuery.class,
                query -> query.giftCardId().equals(event.giftCardId()),
                giftCard);

        queryUpdateEmitter.emit(FindAllGiftCardsQuery.class,
                query -> true,
                giftCard);
    }

    /**
     * Event handler for gift card redeemed events.
     *
     * <p>This method is automatically invoked by the Axon Framework when a
     * {@link GiftCardRedeemedEvent} is published. It updates the existing
     * {@link GiftCardSummary} record by reducing the remaining balance and
     * notifies subscription query subscribers.</p>
     *
     * <p>Processing steps:</p>
     * <ul>
     *   <li>Retrieves the existing gift card summary from the in-memory map</li>
     *   <li>Creates an updated summary with reduced remaining balance</li>
     *   <li>Preserves the original initial value for historical reference</li>
     *   <li>Updates the in-memory store with the new summary</li>
     *   <li>Emits updates to relevant subscription query subscribers</li>
     * </ul>
     *
     * <p><strong>Defensive programming:</strong> The method includes a null check
     * to handle edge cases where an event might be processed before the corresponding
     * issued event, though this should not occur in normal operation.</p>
     *
     * @param event the gift card redeemed event containing ID and redeemed amount
     */
    @EventHandler
    public void on(GiftCardRedeemedEvent event) {
        GiftCardSummary existingGiftCard = giftCards.get(event.giftCardId());
        if (existingGiftCard != null) {
            GiftCardSummary updatedGiftCard = new GiftCardSummary(
                    event.giftCardId(),
                    existingGiftCard.remainingValue().subtract(event.amount()),
                    existingGiftCard.initialValue()
            );
            giftCards.put(event.giftCardId(), updatedGiftCard);

            queryUpdateEmitter.emit(FindGiftCardQuery.class,
                    query -> query.giftCardId().equals(event.giftCardId()),
                    updatedGiftCard);

            queryUpdateEmitter.emit(FindAllGiftCardsQuery.class,
                    query -> true,
                    updatedGiftCard);
        }
    }

    /**
     * Query handler for retrieving a specific gift card by ID.
     *
     * <p>This method handles {@link FindGiftCardQuery} requests and supports both
     * synchronous queries and subscription queries. For subscription queries,
     * clients will receive the initial result immediately, followed by real-time
     * updates whenever the gift card state changes.</p>
     *
     * @param query the query containing the gift card ID to lookup
     * @return the gift card summary if found, null if no gift card exists with the given ID
     */
    @QueryHandler
    public GiftCardSummary handle(FindGiftCardQuery query) {
        return giftCards.get(query.giftCardId());
    }

    /**
     * Query handler for retrieving all gift cards in the system.
     *
     * <p>This method handles {@link FindAllGiftCardsQuery} requests and returns
     * all gift cards currently stored in the read model. The method supports
     * both synchronous queries and subscription queries for real-time updates.</p>
     *
     * <p><strong>Performance note:</strong> This operation has O(n) time complexity
     * where n is the number of gift cards. For production systems with large
     * datasets, consider implementing pagination or filtering capabilities.</p>
     *
     * @param query the parameterless query for all gift cards
     * @return a GiftCardSummaryList containing all gift card summaries
     */
    @QueryHandler
    public GiftCardSummaryList handle(FindAllGiftCardsQuery query) {
        return new GiftCardSummaryList(giftCards.values().stream().toList());
    }
}