package ar.edu.itba.pod.server;


import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.PartyResults;
import ar.edu.itba.pod.model.Province;
import ar.edu.itba.pod.model.Vote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;

/*
1. A candidate who has reached or exceeded the quota is declared elected.
2. If any such elected candidate has more votes than the quota, the excess votes are transferred
    to other candidates based on their next indicated choice.
    The surplus votes that would have gone to the winner go to the next preference.
3. If no-one new meets the quota, the candidate with the fewest votes is eliminated
    and those votes are transferred to each voter's next preferred candidate.
4. This process repeats until either a winner is found for every seat or
    there are as many seats as remaining candidates.
*/

public class STV {

    private final static int AMOUNT_OF_CANDIDATES = 5;

    public Collection<PartyResults> STVQuery(Map<Long, List<Vote>> votes, Province province){

        long [] partyVotesCounter = new long[Party.values().length];

        long subtotal = votes.entrySet().stream().filter(entrySet -> entrySet.getValue().stream()
                .findFirst().get().getProvince().equals(province))
                .flatMapToLong(e -> LongStream.of(e.getValue().size())).sum();

        final long totalVotes;
           if(subtotal == 0){
              totalVotes = 1;
           }else {
               totalVotes = subtotal;
           }


        /* cuento la cantidad de votos que tiene cada partido*/
        for(Long l : votes.keySet()){
            List<Vote> v1 = votes.get(l);
            v1.forEach(p -> { if (p.getProvince().equals(province)) partyVotesCounter[p.getChoices().get(0).ordinal()]++;});
        }

        /* me fijo si la cantidad de candidatos es suficiente para cubrir las sillas*/

        LongStream ls = Arrays.stream(partyVotesCounter).filter(n->n!=0);
        if(ls.count() < AMOUNT_OF_CANDIDATES){
            System.out.println("There are not enough candidates");
            return null;
        }


        List<List<Vote>> byParty = new ArrayList<>(Party.values().length);

        /* la misma logica del array, pero con listas, para poder tener la proxima eleccion disponible */
        Arrays.stream(Party.values()).forEach(p->byParty.add(new ArrayList<>()));

        /* en cada posicion de la lista de listas (cada posicion indica el party) colocamos los VOTOS que tienen */
        for(Long l : votes.keySet()) {
            List<Vote> v = votes.get(l);
            for (Vote v2 : v) {
                if (v2.getProvince().equals(province)) {
                    byParty.get(v2.getChoices().stream().findFirst().get().ordinal()).add(v2);
                }
            }
        }

        List<PartyResults> partyStats = new ArrayList<>();

        Arrays.stream(Party.values()).forEach(p -> partyStats.add(p.ordinal(), new PartyResults(p,
                ((double) (100 * byParty.get(p.ordinal()).size()) / totalVotes),
                byParty.get(p.ordinal()).size())));


        /* esta es la cantidad de votos que necesita un candidato para ganar */

//        double votesRequired = 100.0 / AMOUNT_OF_CANDIDATES;

        long votesRequired = (long) Math.floor(totalVotes/(AMOUNT_OF_CANDIDATES+1))+1;

        List<PartyResults> candidates = new ArrayList<>();

        /* vamos a iterar hasta obtener la cantidad necesaria de candidatos para suplir las sillas */

        while(candidates.size() < AMOUNT_OF_CANDIDATES){

            Optional<PartyResults> maxPartyOpt = partyStats.stream().filter(p -> p.getVotes() != 0)
                    .max(Comparator.comparing(PartyResults::getVotes));
            PartyResults maxParty;

            if(!maxPartyOpt.isPresent()){
                return candidates;
            }
            maxParty = maxPartyOpt.get();

            Optional<PartyResults> minPartyOpt = partyStats.stream().filter(p->p.getVotes() !=0)
                    .min(Comparator.comparing(PartyResults::getVotes));
            PartyResults minParty;

            if(!minPartyOpt.isPresent()){
                return candidates;
            }
            minParty = minPartyOpt.get();

            if(maxParty.getVotes() >= votesRequired || maxParty.equals(minParty) ||
                    partyStats.stream().filter(v -> v.getVotes() > 0).count() ==
                            (AMOUNT_OF_CANDIDATES - candidates.size())) {

                candidates.add(new PartyResults(maxParty.getParty(), maxParty.getPercentage(), maxParty.getVotes()));
                System.out.println("getting in: " + candidates.get(candidates.size()-1));

                transferVotes(byParty, partyStats, maxParty.getParty().ordinal(), votesRequired, true, totalVotes);

                remove(maxParty, byParty, partyStats);

            }else{

                transferVotes(byParty, partyStats, minParty.getParty().ordinal(), 0, false, totalVotes);

                remove(minParty, byParty, partyStats);
            }

        }

        for(PartyResults c : candidates){
            System.out.println("Party: " + c.getParty() + " Votes: " + c.getVotes() + " Percentage: " + c.getVotes());
        }

        return candidates;
    }

    private void transferVotes(List<List<Vote>> byParty, List<PartyResults> partyStats,
                               int partyValue, long votesRequired, boolean max, long totalVotes){

        /* cantidad de votos a otortogar:
        ((votes second choice) / (total max votes of winner)) * (votos sobreantes, surplus) */


        long surplus = 0;
        if(max){
            if(partyStats.size() >= partyValue){
                surplus = partyStats.get(partyValue).getVotes() - votesRequired;
            }else{return;}

        }

        List<Vote> secondVotersList = byParty.get(partyValue);

        long [] partyVotersCounter = new long[Party.values().length];

        for(Vote s : secondVotersList){
            /* 0 es la primer opcion, 1 es la segunda opcion elegida */
            if(s.getChoices().size() > 1){
                Party secondChoice = s.getChoices().get(1);
                partyVotersCounter[secondChoice.ordinal()]++;
            }
        }


        /* cuantos votos tiene el ganador o el perdedor */
        long winnerVotes = partyStats.get(partyValue).getVotes();
            for(int i=0; i<partyVotersCounter.length; i++){
                if(i != partyValue){
                    if(max) {
                        double division = (partyVotersCounter[i]/(double)(winnerVotes));
                        partyVotersCounter[i] = (long) Math.floor(division*surplus);
                    };
                }
            }
        /* le saco los votos que le sobran al ganador*/
        if(max) partyStats.get(partyValue).setVotes(partyStats.get(partyValue).getVotes() - surplus);
        else partyStats.get(partyValue).setVotes(0);


        /* le doy a los segundos votados los votos correspondientes */
        for(PartyResults ps : partyStats){
            ps.setVotes(ps.getVotes() + partyVotersCounter[ps.getParty().ordinal()]);
            if(ps.getVotes() == 0) ps.setPercentage(0);
            else ps.setPercentage(((double)ps.getVotes()/(double)totalVotes)*100);
            System.out.println("party " + ps.getParty() + " should change votes: " + ps.getVotes() + " per: " + ps.getPercentage());
        }
    }

    private void remove(PartyResults maxParty, List<List<Vote>> byParty, List<PartyResults> partyStats){
        byParty.set(maxParty.getParty().ordinal(), null);
        partyStats.remove(maxParty);
    }

    private static Optional<Party> getNextParty(Iterator<Party> iterator) {
        Objects.requireNonNull(iterator);
        if(iterator.hasNext())
            return Optional.ofNullable(iterator.next());
        return Optional.empty();
    }

}
