package pack.review;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pack.mybatis.SqlMapConfig;

import java.util.HashMap;
import java.util.List;

public class ReviewDao {
    private SqlSessionFactory factory = SqlMapConfig.getSqlSession();

    public List<ReviewDto> getReviewsByMovieId(int movieId) {
        try (SqlSession sqlSession = factory.openSession()) {
            return sqlSession.selectList("pack.review.ReviewMapper.getReviewsByMovieId", movieId);
        }
    }

    public ReviewDto getReplyData(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            return sqlSession.selectOne("pack.review.ReviewMapper.getReviewByNum", num);
        }
    }

    public int saveReplyData(ReviewBean bean) {
        try (SqlSession sqlSession = factory.openSession()) {
            int result = sqlSession.insert("pack.review.ReviewMapper.insertReview", bean);
            sqlSession.commit();
            return bean.getNum(); // useGeneratedKeys 사용했을 경우
        }
    }

    public void updateOnum(int onum) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.updateOnum", onum);
            sqlSession.commit();
        }
    }

    public void updateReviewOnum(int num, int onum) {
        try (SqlSession sqlSession = factory.openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("num", num);
            map.put("onum", onum);
            sqlSession.update("pack.review.ReviewMapper.updateReviewOnum", map);
            sqlSession.commit();
        }
    }

    public void increaseLikeCount(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.increaseLike", num);
            sqlSession.commit();
        }
    }

    public void decreaseLikeCount(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.decreaseLike", num);
            sqlSession.commit();
        }
    }

    public double getAverageRating(int movieId) {
        try (SqlSession sqlSession = factory.openSession()) {
            Double avg = sqlSession.selectOne("pack.review.ReviewMapper.getAverageRating", movieId);
            return avg != null ? avg : 0.0;
        }
    }

    public void deleteCascade(int onum, int nested) {
        try (SqlSession sqlSession = factory.openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("onum", onum);
            map.put("nested", nested);
            sqlSession.delete("pack.review.ReviewMapper.deleteCascade", map);
            sqlSession.commit();
        }
    }

    public void deleteByNum(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.delete("pack.review.ReviewMapper.deleteByNum", num);
            sqlSession.commit();
        }
    }
}
