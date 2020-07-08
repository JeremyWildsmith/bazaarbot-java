package com.bazaarbot.statistics.registry;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.IMarket;
import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Nick Gritsenko
 */
public class Registry implements IRegistryRead, IRegistryWrite {
    private final List<Record<Offer>> bidOffers = new ArrayList<>();
    private final List<Record<Offer>> askOffers = new ArrayList<>();
    private final List<Record<IContract>> contractAgreements = new ArrayList<>();

    @Override
    public void addBidOffer(Offer bidOffer) {
        bidOffers.add(new Record<>(bidOffer));
    }

    @Override
    public void addAskOffer(Offer askOffer) {
        askOffers.add(new Record<>(askOffer));
    }

    @Override
    public void addContract(IContract contract) {
        contractAgreements.add(new Record<>(contract));
    }

    @Override
    public void addAskOffers(List<Offer> askOffers) {
        for (Offer offer : askOffers) {
            this.askOffers.add(new Record<>(offer));
        }
    }

    @Override
    public void addBidOffers(List<Offer> bidOffers) {
        for (Offer offer : bidOffers) {
            this.bidOffers.add(new Record<>(offer));
        }
    }

    @Override
    public Stream<Offer> getBidOffersStream() {
        return bidOffers.stream()
                .map(Record::getHistoryObject);
    }

    @Override
    public Stream<Offer> getAskOffersStream() {
        return askOffers.stream()
                .map(Record::getHistoryObject);
    }

    @Override
    public Stream<IContract> getContractAgreementsStream() {
        return contractAgreements.stream()
                .map(Record::getHistoryObject);
    }
}
