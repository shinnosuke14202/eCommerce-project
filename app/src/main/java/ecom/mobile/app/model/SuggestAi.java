package ecom.mobile.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestAi {
    private int id;
    private int click_after_suggest_byai;
    private String title;

}
