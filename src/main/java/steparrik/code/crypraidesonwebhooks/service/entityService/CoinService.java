package steparrik.code.crypraidesonwebhooks.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.entity.Coin;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.CoinRepository;
import steparrik.code.crypraidesonwebhooks.util.BadRequestException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoinService {

    private final CoinRepository coinRepository;
    private final Api api;
    private final UserService userService;

    @Autowired
    public CoinService(CoinRepository coinRepository, Api api, UserService userService) {
        this.coinRepository = coinRepository;
        this.api = api;
        this.userService = userService;
    }


    public Coin save(Coin coin, User user) {
//        Optional<Coin> coinForValidate = coinRepository.findByNameAndUser(coin.getName(), user);

        Set<Coin> coins = user.getPortfolio();
        Coin coinForValidate = null;
        for(Coin coin1:coins){
            if(coin1.getName().equals(coin.getName())){
                coinForValidate = coin1;
            }
        }

        if (coinForValidate!=null) {
            double afterCount = coin.getCount() + coinForValidate.getCount();
            double coinForValidateSpecificPrice = (coinForValidate.getBuyPrice() / coinForValidate.getCount()) * (coinForValidate.getCount() / afterCount);
            double coinSpecificPrice = (coin.getBuyPrice() / coin.getCount()) * (coin.getCount() / afterCount);

            coinForValidate.setCount(afterCount);
            coinForValidate.setUser(user);
            coinForValidate.setBuyPrice(Math.round((coinForValidateSpecificPrice + coinSpecificPrice) * 1000.0 * (coinForValidate.getCount())) / 1000.0);
            coinForValidate.setCurrentPrice(Math.round(api.getPriceOnNameByUSDT(coinForValidate.getName()) * coinForValidate.getCount() * 1000.0) / 1000.0);

            return coinRepository.save(coinForValidate);
        }

        return coinRepository.save(coin);
    }


    public Coin updatePriceByCoin(Coin coin) {
        Coin coinForUpdate = coinRepository.findById(coin.getId()).get();
        coinForUpdate.setCurrentPrice(Math.round(api.getPriceOnNameByUSDT(coinForUpdate.getName()) * coinForUpdate.getCount() * 1000.0) / 1000.0);
        coinRepository.save(coinForUpdate);
        return coinForUpdate;
    }

    public Set<Coin> updatePriceByCoin(Set<Coin> coinsForUpdate) {
        Set<Coin> updateCoins = Collections.newSetFromMap(new ConcurrentHashMap<>());
        coinsForUpdate.parallelStream().forEach(coin ->{
            Coin coinForUpdate = coinRepository.findById(coin.getId()).get();
            coinForUpdate.setCurrentPrice(Math.round(api.getPriceOnNameByUSDT(coinForUpdate.getName()) * coinForUpdate.getCount() * 1000.0) / 1000.0);
            coinRepository.save(coinForUpdate);
            updateCoins.add(coinForUpdate);
        });
        return updateCoins;
    }

    public void deleteById(int id) {
        coinRepository.deleteById(id);
    }


    public Optional<Coin> findByNameAndUSer(String name, User user) {
        return coinRepository.findByNameAndUser(name, user);
    }

    public void deleteCoin(String nameCoin, User globalUser) {
        Optional<Coin> coin = findByNameAndUSer(nameCoin, globalUser);
        if (coin.isPresent()) {
            Iterator<Coin> iterator = globalUser.getPortfolio().iterator();
            while (iterator.hasNext()) {
                Coin coinForDel = iterator.next();
                if (coinForDel.getName().equals(coin.get().getName()) && coinForDel.getId() == coinForDel.getId()) {
                    deleteById(coinForDel.getId());
                    iterator.remove();
                }
            }
        } else {
            throw new BadRequestException("*В вашем портфеле нет токена с таким названием!\n" +
                    "Введите корректное название или очистите портфель полностью.*");
        }

    }

    public void deletePartCoin(String nameCoin, User user, double count) {
        Optional<Coin> coin = findByNameAndUSer(nameCoin, user);
        System.out.println(user.getPortfolio());
        if (coin.isPresent()) {
            Iterator<Coin> iterator = user.getPortfolio().iterator();
            while (iterator.hasNext()) {
                Coin coinForDel = iterator.next();
                if (coinForDel.getName().equals(coin.get().getName()) && coinForDel.getId() == coinForDel.getId()) {
                    iterator.remove();
                }
            }

            double newCount = coin.get().getCount() - count;
            System.out.println(user.getPortfolio());
            coin.get().setCurrentPrice(Math.round(api.getPriceOnNameByUSDT(coin.get().getName()) * newCount * 1000.0) / 1000.0);
            coin.get().setBuyPrice(Math.round((coin.get().getBuyPrice() / coin.get().getCount()) * newCount * 1000.0) / 1000.0);
            coin.get().setCount(newCount);
            coinRepository.save(coin.get());
            System.out.println(coin.get());
            user.getPortfolio().add(coin.get());
            System.out.println(user.getPortfolio());
            userService.save(user);
        } else {
            throw new BadRequestException("*В вашем портфеле нет токена с таким названием!\n" +
                    "Введите корректное название или очистите портфель полностью.*");
        }
    }

    public void deleteAllCoin(User globalUser) {
        Iterator<Coin> iterator = globalUser.getPortfolio().iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            deleteById(coin.getId());
            iterator.remove();
        }
    }

    public Coin coinBuilder(double count, User globalUser) {
        Coin coinBeforeSave = Coin.builder()
                .count(count).buyPrice(Math.round(Double.parseDouble(globalUser.getAddingOptions().get(2).getAdding()) * count * 1000.0) / 1000.0)
                .currentPrice(Math.round(api.getPriceOnNameByUSDT(globalUser.getAddingOptions().get(1).getAdding()) * count * 1000.0) / 1000.0)
                .name(globalUser.getAddingOptions().get(1).getAdding().toUpperCase())
                .user(globalUser)
                .build();

        Coin coinAfterSave = save(coinBeforeSave,  globalUser);


        globalUser.removeCoin(coinBeforeSave);
        globalUser.getPortfolio().add(coinAfterSave);
        userService.save(globalUser);


        return coinAfterSave;


    }



}
