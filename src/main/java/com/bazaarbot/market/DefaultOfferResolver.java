package com.bazaarbot.market;

import com.bazaarbot.ICommodity;
import com.bazaarbot.agent.BasicAgent;

import java.util.*;

public class DefaultOfferResolver implements IOfferResolver {

    private final Random rng;

    public DefaultOfferResolver(Random rng) {
        this.rng = rng;
    }

    public DefaultOfferResolver() {
        this(new Random());
    }

    private static void sortOffers(List<Offer> offers) {
        offers.sort((Offer a, Offer b) -> {
            if (a.unit_price < b.unit_price)
                return -1;

            if (a.unit_price > b.unit_price)
                return 1;

            return 0;
        });
    }

    @Override
    public Map<ICommodity, OfferResolutionStatistics> matchOffers(IOfferExecuter executor, Map<ICommodity, List<Offer>> bids, Map<ICommodity, List<Offer>> asks) {
        HashMap<ICommodity, OfferResolutionStatistics> result = new HashMap<>();

        for(ICommodity c : bids.keySet()) {
            if(!asks.containsKey(c))
                continue;

            OfferResolutionStatistics r = resolveOfferSet(executor, c, bids.get(c), asks.get(c));
            result.put(c, r);
        }

        return result;
    }

    private OfferResolutionStatistics resolveOfferSet(IOfferExecuter executor, ICommodity good, List<Offer> bids, List<Offer> asks) {
        Collections.shuffle(bids, rng);
        Collections.shuffle(asks, rng);
        sortOffers(asks);

        int successfulTrades = 0;
        double moneyTraded = 0;

        while (bids.size() > 0 && asks.size() > 0) {
            Offer buyer = bids.get(0);
            Offer seller = asks.get(0);
            Double quantity_traded = Math.min(seller.units, buyer.units);

            if (quantity_traded > 0) {
                OfferExecutionStatistics r = executor.execute(buyer, seller);

                if (r.unitsTraded > 0)
                {
                    moneyTraded += r.moneyTraded;
                    seller.units -= quantity_traded;
                    buyer.units -= quantity_traded;
                    successfulTrades++;
                }
            }

            if (seller.units == 0)
            {
                asks.remove(0);
            }

            if (buyer.units == 0)
            {
                //buyer is out of offered good
                bids.remove(0);
            }

        }

        for(Offer o : bids)
            executor.rejectBid(o);

        for(Offer o : asks)
            executor.rejectAsk(o);

        return new OfferResolutionStatistics(successfulTrades, moneyTraded);
    }
}
