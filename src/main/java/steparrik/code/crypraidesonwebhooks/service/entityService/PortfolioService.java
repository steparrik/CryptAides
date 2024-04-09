package steparrik.code.crypraidesonwebhooks.service.entityService;


import org.springframework.stereotype.Service;
import steparrik.code.crypraidesonwebhooks.entity.Coin;


import java.util.List;

@Service
public class PortfolioService {

    public double getSumPrice(List<Coin> list){
        double res = 0.0;
        for(Coin coin : list){
            res+=coin.getCurrentPrice();
        }
        return res;
    }

    public double getCorrectFormat(double num){
        return Math.round((num)*1000.0)/1000.0;
    }


    public StringBuilder getAllResponse(List<Coin> coins){
        StringBuilder response = new StringBuilder();


        coins.parallelStream().forEach((coin)->{
            double oneTokenCurrentPrice = getCorrectFormat(coin.getCurrentPrice()/ coin.getCount());
            double differentPrices = getCorrectFormat(coin.getCurrentPrice()-coin.getBuyPrice());

            String stickerF = getStickerForCoin(differentPrices);
            String util = getUtil(differentPrices);

            double percent = getPercent(coin.getCurrentPrice(), coin.getBuyPrice());

            response.append("*"+stickerF + " #" +coin.getName() + ":* " +oneTokenCurrentPrice+" $ | " +
                    "[График](https://ru.tradingview.com/chart/?symbol=" + coin.getName().toLowerCase() + "usdt)\n" +
                    "            *Количество токенов: *" + coin.getCount() + "\n            *Цена: *" + coin.getCurrentPrice() + " $\n            " +
                    "*PNL: *" +util + percent + " % | "  + util + differentPrices+ " $\n\n\n");
        });
        return response;
    }

    public String getStickerForCoin(double differentPrices){
        String stickerForCoin;
        if(differentPrices>0){
            stickerForCoin = "🟢🔺";
        }else if(differentPrices==0){
            stickerForCoin = "⚫◼️";
        }else {
            stickerForCoin = "🔴🔻";
        }
        return stickerForCoin;
    }

    public String getUtil(double differentPrices){
        String util;
        if(differentPrices>0){
            util = "+";
        }else if(differentPrices==0){
            util = "+";
        }else {
            util= "";
        }
        return util;
    }

    public double getPercent(double curPrice, double buyPrice){

        if(curPrice>buyPrice){
            return getCorrectFormat((curPrice/ buyPrice-1)*100);
        }else {
            return getCorrectFormat( -(buyPrice/ curPrice-1)*100);
        }
    }

    public String getSticker(double v) {
        if (v>0){
             return "📈";
        }else if(v == 0){
             return  "";
        }else {
            return   "📉";
        }
    }
}
