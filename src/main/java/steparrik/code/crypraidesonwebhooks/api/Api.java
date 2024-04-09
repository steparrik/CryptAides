package steparrik.code.crypraidesonwebhooks.api;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import steparrik.code.crypraidesonwebhooks.api.models.CoinDTO;
import steparrik.code.crypraidesonwebhooks.util.BadRequestException;

@Component
public class Api {
    public Double getPriceOnNameByUSDT(String name)  {
        String url = "https://api.coinbase.com/v2/prices/" + name.toUpperCase() + "-USDT/spot";
        RestTemplate restTemplate = new RestTemplate();
        try {
            CoinDTO coinDTO = restTemplate.getForObject(url, CoinDTO.class);
            return Double.parseDouble(coinDTO.getData().getAmount());
        }catch (HttpClientErrorException e){
            throw new BadRequestException("bad name");
        }

    }
}
