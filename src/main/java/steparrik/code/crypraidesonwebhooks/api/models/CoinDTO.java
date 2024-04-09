package steparrik.code.crypraidesonwebhooks.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinDTO {
    private Data data;

    @Override
    public String toString() {
        return "CoinDTO{" +
                "data=" + data +
                '}';
    }
}
