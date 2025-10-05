package io.axoniq.quickstart.giftcard.query;

import java.math.BigDecimal;

/**
 * Read model representation of a gift card's current state.
 *
 * <p>This record serves as a Data Transfer Object (DTO) in the CQRS read model,
 * providing a denormalized view of gift card data optimized for query operations.
 * It represents the current state of a gift card as maintained by the
 * {@link GiftCardProjection} through event sourcing.</p>
 *
 * <p>The summary contains both current and historical information to support
 * various UI requirements without requiring additional calculations or lookups:</p>
 * <ul>
 *   <li><strong>Current state</strong>: remaining value after all redemptions</li>
 *   <li><strong>Historical context</strong>: original value when first issued</li>
 *   <li><strong>Derived insights</strong>: enables calculation of total redeemed amount</li>
 * </ul>
 *
 * <p>Usage patterns:</p>
 * <ul>
 *   <li>Returned by {@link FindGiftCardQuery} for individual gift card lookups</li>
 *   <li>Contained within {@link GiftCardSummaryList} for bulk queries</li>
 *   <li>Used in subscription query updates for real-time UI updates</li>
 *   <li>Serialized to JSON for REST API responses</li>
 * </ul>
 *
 * <p>Design considerations:</p>
 * <ul>
 *   <li>Immutable - thread-safe and cacheable</li>
 *   <li>Self-contained - no additional lookups required</li>
 *   <li>JSON-friendly - simple structure for web APIs</li>
 *   <li>Calculation-ready - supports derived metrics (redeemed = initial - remaining)</li>
 * </ul>
 *
 * @param giftCardId the unique identifier of the gift card
 * @param remainingValue the current balance available for redemption (never negative)
 * @param initialValue the original amount when the gift card was first issued (never changes)
 *
 * @see GiftCardProjection
 * @see FindGiftCardQuery
 * @see FindAllGiftCardsQuery
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record GiftCardSummary(String giftCardId, BigDecimal remainingValue, BigDecimal initialValue) {
}