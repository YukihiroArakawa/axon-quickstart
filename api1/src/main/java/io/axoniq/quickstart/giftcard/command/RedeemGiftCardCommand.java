package io.axoniq.quickstart.giftcard.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

/**
 * Command for redeeming an amount from an existing gift card.
 *
 * <p>This command represents the intention to redeem (withdraw) a specific amount from
 * an existing gift card. The redemption reduces the remaining balance on the gift card
 * and can be performed multiple times until the balance is exhausted.</p>
 *
 * <p>The command follows the CQRS pattern and is handled by the {@link io.axoniq.quickstart.giftcard.aggregate.GiftCardAggregate}
 * which validates business rules before applying the corresponding {@link io.axoniq.quickstart.giftcard.event.GiftCardRedeemedEvent}.</p>
 *
 * <p>Business constraints:</p>
 * <ul>
 *   <li>The gift card must exist in the system</li>
 *   <li>The redemption amount must be positive (validated by the aggregate)</li>
 *   <li>The redemption amount cannot exceed the current remaining balance (validated by the aggregate)</li>
 * </ul>
 *
 * <p>Use cases:</p>
 * <ul>
 *   <li>Customer purchasing items using gift card balance</li>
 *   <li>Partial redemptions allowing multiple transactions</li>
 *   <li>Administrative redemptions or refunds</li>
 * </ul>
 *
 * @param giftCardId the unique identifier of the existing gift card to redeem from
 * @param amount the monetary amount to redeem (must be positive and not exceed remaining balance)
 *
 * @see io.axoniq.quickstart.giftcard.aggregate.GiftCardAggregate
 * @see io.axoniq.quickstart.giftcard.event.GiftCardRedeemedEvent
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record RedeemGiftCardCommand(@TargetAggregateIdentifier String giftCardId, BigDecimal amount) {
}