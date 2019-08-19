package com.bazaarbot.market;

import com.bazaarbot.ICommodity;

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
    public Map<ICommodity, OfferResolutionStatistics> resolve(IOfferExecuter executor, Map<ICommodity, List<Offer>> bids, Map<ICommodity, List<Offer>> asks) {
        HashMap<ICommodity, OfferResolutionStatistics> result = new HashMap<>();

        for(ICommodity c : bids.keySet()) {
            if(!asks.containsKey(c))
                continue;

            OfferResolutionStatistics r = resolveOfferSet(executor, bids.get(c), asks.get(c));
            result.put(c, r);
        }

        return result;
    }

    private OfferResolutionStatistics resolveOfferSet(IOfferExecuter executor, List<Offer> bids, List<Offer> asks) {
        Collections.shuffle(bids, rng);
        Collections.shuffle(asks, rng);
        sortOffers(asks);

        List<OfferExecutionStatistics> resolvedOffers = new ArrayList<>();

        while (bids.size() > 0 && asks.size() > 0) {
            Offer buyer = bids.get(0);
            Offer seller = asks.get(0);

            OfferExecutionStatistics r = executor.execute(buyer, seller);

            if (r.unitsTraded > 0)
            {
                resolvedOffers.add(r);
                seller.units -= r.unitsTraded;
                buyer.units -= r.unitsTraded;
            }

            if (seller.units == 0)
                asks.remove(0);

            if (buyer.units == 0)
                bids.remove(0);
        }

        for(Offer o : bids) {
            executor.rejectBid(o);
        }

        for(Offer o : asks) {
            executor.rejectAsk(o);
        }

        return new OfferResolutionStatistics(resolvedOffers);
    }
}
