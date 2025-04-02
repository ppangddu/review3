package pack.review;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReviewDto {
    private int num, movieId, gnum, onum, nested, rating, likeCount;
    private String userId, cont, nickname;
    private Date cdate;
}
