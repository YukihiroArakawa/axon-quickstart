package io.axoniq.quickstart.giftcard.aggregate;

import io.axoniq.quickstart.giftcard.command.IssueGiftCardCommand;
import io.axoniq.quickstart.giftcard.command.RedeemGiftCardCommand;
import io.axoniq.quickstart.giftcard.event.GiftCardIssuedEvent;
import io.axoniq.quickstart.giftcard.event.GiftCardRedeemedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Gift Card Aggregate implementing CQRS/Event Sourcing pattern using Axon Framework.
 *
 * <p>This aggregate represents a gift card in the system that can be issued with an initial amount
 * and redeemed in partial amounts until the balance is exhausted. The aggregate follows the
 * event sourcing pattern where state changes are represented as immutable events.</p>
 *
 * <p>Business Rules:</p>
 * <ul>
 *   <li>Gift cards must be issued with a positive amount</li>
 *   <li>Redemptions must be for positive amounts</li>
 *   <li>Redemptions cannot exceed the remaining balance</li>
 *   <li>Gift cards maintain their remaining value after redemptions</li>
 * </ul>
 *
 * <p>The aggregate handles the following commands:</p>
 * <ul>
 *   <li>{@link IssueGiftCardCommand} - Creates a new gift card with initial balance</li>
 *   <li>{@link RedeemGiftCardCommand} - Redeems an amount from existing gift card</li>
 * </ul>
 *
 * <p>The aggregate produces the following events:</p>
 * <ul>
 *   <li>{@link GiftCardIssuedEvent} - Emitted when a gift card is successfully issued</li>
 *   <li>{@link GiftCardRedeemedEvent} - Emitted when an amount is successfully redeemed</li>
 * </ul>
 *
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
@Aggregate
public class GiftCardAggregate {

    private static final Logger logger = LoggerFactory.getLogger(GiftCardAggregate.class);

    /**
     * The unique identifier for this gift card aggregate.
     * This field serves as the aggregate identifier in Axon Framework.
     */
    @AggregateIdentifier
    private String giftCardId;

    /**
     * The remaining balance on this gift card.
     * This value decreases with each redemption and is never negative.
     */
    private BigDecimal remainingValue;

    /**
     * Default constructor required by Axon Framework for aggregate reconstruction.
     * This constructor is used internally by the framework and should not be called directly.
     */
    protected GiftCardAggregate() {
    }

    /**
     * Command handler constructor for issuing a new gift card.
     *
     * <p>This constructor serves as a command handler for {@link IssueGiftCardCommand} and creates
     * a new gift card aggregate instance. It validates the business rules and applies the
     * {@link GiftCardIssuedEvent} if validation succeeds.</p>
     *
     * <p>Business validation:</p>
     * <ul>
     *   <li>Ensures the initial amount is positive (greater than zero)</li>
     * </ul>
     *
     * @param command the command containing gift card ID and initial amount
     * @throws IllegalArgumentException if the amount is not positive
     *
     * @see IssueGiftCardCommand
     * @see GiftCardIssuedEvent
     * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
     */
    @CommandHandler
    public GiftCardAggregate(IssueGiftCardCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gift card amount must be positive");
        }
        logger.info("Issuing gift card {} amount {}", command.giftCardId(), command.amount());
        AggregateLifecycle.apply(new GiftCardIssuedEvent(command.giftCardId(), command.amount()));
    }

    /**
     * Command handler for redeeming an amount from an existing gift card.
     *
     * <p>This method handles {@link RedeemGiftCardCommand} and validates business rules before
     * applying the {@link GiftCardRedeemedEvent}. The redemption reduces the remaining balance
     * of the gift card.</p>
     *
     * <p>Business validation:</p>
     * <ul>
     *   <li>Ensures the redemption amount is positive</li>
     *   <li>Ensures sufficient funds are available (amount ≤ remaining balance)</li>
     * </ul>
     *
     * @param command the command containing gift card ID and redemption amount
     * @throws IllegalArgumentException if the amount is not positive or exceeds remaining balance
     *
     * @see RedeemGiftCardCommand
     * @see GiftCardRedeemedEvent
     * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
     */
    @CommandHandler
    public void handle(RedeemGiftCardCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Redeem amount must be positive");
        }
        if (command.amount().compareTo(remainingValue) > 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        logger.info("Redeeming {} from gift card {} (balance before redemption {})", command.amount(), command.giftCardId(), remainingValue);
        AggregateLifecycle.apply(new GiftCardRedeemedEvent(command.giftCardId(), command.amount()));
    }

    /**
     * Event sourcing handler for gift card issued events.
     *
     * <p>This method is called when a {@link GiftCardIssuedEvent} is applied to reconstruct
     * the aggregate state. It sets the initial state of the gift card with the provided
     * ID and initial balance.</p>
     *
     * @param event the event containing gift card ID and initial amount
     *
     * @see GiftCardIssuedEvent
     * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
     */
    @EventSourcingHandler
    public void on(GiftCardIssuedEvent event) {
        this.giftCardId = event.giftCardId();
        this.remainingValue = event.amount();
    }

    /**
     * Event sourcing handler for gift card redeemed events.
     *
     * <p>This method is called when a {@link GiftCardRedeemedEvent} is applied to reconstruct
     * the aggregate state. It reduces the remaining balance by the redeemed amount.</p>
     *
     * @param event the event containing gift card ID and redeemed amount
     *
     * @see GiftCardRedeemedEvent
     * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
     */
    @EventSourcingHandler
    public void on(GiftCardRedeemedEvent event) {
        this.remainingValue = this.remainingValue.subtract(event.amount());
    }
}
