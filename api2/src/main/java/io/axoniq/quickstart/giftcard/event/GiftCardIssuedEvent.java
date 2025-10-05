package io.axoniq.quickstart.giftcard.event;

import java.math.BigDecimal;

/**
 * Event representing the successful issuance of a new gift card.
 *
 * <p>This event is emitted when a gift card has been successfully created in the system
 * with an initial monetary amount. It represents a fact that has occurred and cannot be
 * changed, following the event sourcing principle of immutable events.</p>
 *
 * <p>The event is produced by the command service when processing an issue request and is
 * consumed by:</p>
 * <ul>
 *   <li>The same aggregate for state reconstruction via event sourcing</li>
 *   <li>Query projections to maintain read models</li>
 *   <li>Any other event handlers interested in gift card lifecycle events</li>
 * </ul>
 *
 * <p>Event characteristics:</p>
 * <ul>
 *   <li>Immutable - once emitted, the event data cannot be changed</li>
 *   <li>Reproducible - replaying this event will recreate the same state</li>
 *   <li>Self-contained - contains all necessary information about the event</li>
 * </ul>
 *
 * @param giftCardId the unique identifier of the newly issued gift card
 * @param amount the initial monetary amount loaded onto the gift card (always positive)
 *
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record GiftCardIssuedEvent(String giftCardId, BigDecimal amount) {
}
