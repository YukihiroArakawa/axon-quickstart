package io.axoniq.quickstart.giftcard.event;

import java.math.BigDecimal;

/**
 * Event representing the successful redemption of an amount from a gift card.
 *
 * <p>This event is emitted when a specified amount has been successfully redeemed
 * (withdrawn) from an existing gift card, reducing its remaining balance. The event
 * represents an immutable fact that has occurred in the system.</p>
 *
 * <p>The event is produced by the command service when a redemption request succeeds and is
 * consumed by:</p>
 * <ul>
 *   <li>The same aggregate for state reconstruction via event sourcing</li>
 *   <li>Query projections to update read models with new balance</li>
 *   <li>Any other event handlers tracking gift card usage patterns</li>
 *   <li>Reporting systems for analytics and auditing</li>
 * </ul>
 *
 * <p>Event characteristics:</p>
 * <ul>
 *   <li>Immutable - once emitted, represents a permanent transaction</li>
 *   <li>Auditable - provides complete trail of gift card usage</li>
 *   <li>Replay-safe - multiple replays produce consistent state</li>
 *   <li>Business-relevant - represents actual value transfer</li>
 * </ul>
 *
 * <p>Business implications:</p>
 * <ul>
 *   <li>The gift card balance is reduced by the redeemed amount</li>
 *   <li>Multiple redemptions can occur until balance reaches zero</li>
 *   <li>Each redemption creates a permanent audit record</li>
 * </ul>
 *
 * @param giftCardId the unique identifier of the gift card from which the amount was redeemed
 * @param amount the monetary amount that was redeemed (always positive, never exceeds available balance)
 *
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record GiftCardRedeemedEvent(String giftCardId, BigDecimal amount) {
}
