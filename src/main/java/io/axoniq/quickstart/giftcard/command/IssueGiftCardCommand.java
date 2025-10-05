package io.axoniq.quickstart.giftcard.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

/**
 * Command for issuing a new gift card with an initial amount.
 *
 * <p>This command represents the intention to create a new gift card in the system.
 * It contains the unique identifier for the gift card and the initial monetary amount
 * to be loaded onto it.</p>
 *
 * <p>The command follows the CQRS pattern and is handled by the {@link io.axoniq.quickstart.giftcard.aggregate.GiftCardAggregate}
 * which validates the business rules before applying the corresponding {@link io.axoniq.quickstart.giftcard.event.GiftCardIssuedEvent}.</p>
 *
 * <p>Business constraints:</p>
 * <ul>
 *   <li>The gift card ID must be unique within the system</li>
 *   <li>The amount must be positive (validated by the aggregate)</li>
 * </ul>
 *
 * @param giftCardId the unique identifier for the gift card to be created
 * @param amount the initial monetary amount to load onto the gift card (must be positive)
 *
 * @see io.axoniq.quickstart.giftcard.aggregate.GiftCardAggregate
 * @see io.axoniq.quickstart.giftcard.event.GiftCardIssuedEvent
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record IssueGiftCardCommand(@TargetAggregateIdentifier String giftCardId, BigDecimal amount) {
}