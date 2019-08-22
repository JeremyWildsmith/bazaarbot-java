package com.bazaarbot.history;

import com.bazaarbot.contract.IContract;
import com.bazaarbot.market.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Gritsenko
 */
public class HistoryRegistry implements IHistoryRegistryRead, IHistoryRegistryWrite{
    private final List<HistoryRecord<Offer>> bidOffers = new ArrayList<>();
    private final List<HistoryRecord<Offer>> askOffers = new ArrayList<>();
    private final List<HistoryRecord<IContract>> contractAgreements = new ArrayList<>();

    public HistoryRegistry() {
    }

    @Override
    public void addBidOffer(Offer bidOffer) {
        bidOffers.add(new HistoryRecord<>(bidOffer));
    }

    @Override
    public void addAskOffer(Offer askOffer) {
        askOffers.add(new HistoryRecord<>(askOffer));
    }

    @Override
    public void addContract(IContract contract) {
        contractAgreements.add(new HistoryRecord<>(contract));
    }

    @Override
    public void addAskOffers(List<Offer> askOffers) {
        for(Offer offer : askOffers) {
            this.askOffers.add(new HistoryRecord<>(offer));
        }
    }

    @Override
    public void addBidOffers(List<Offer> bidOffers) {
        for(Offer offer : bidOffers) {
            this.bidOffers.add(new HistoryRecord<>(offer));
        }
    }

    @Override
    public List<HistoryRecord<Offer>> getBidOffers() {
        return bidOffers;
    }
    @Override
    public List<HistoryRecord<Offer>> getAskOffers() {
        return askOffers;
    }
    @Override
    public List<HistoryRecord<IContract>> getContractAgreements() {
        return contractAgreements;
    }
}
