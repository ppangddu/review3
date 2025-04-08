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

    public void updateGnum(int gnum) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.updateGnum", gnum);
            sqlSession.commit();
        }
    }

    public void updateReviewGnum(int num, int gnum) {
        try (SqlSession sqlSession = factory.openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("num", num);
            map.put("gnum", gnum);
            sqlSession.update("pack.review.ReviewMapper.updateReviewGnum", map);
            sqlSession.commit();
        }
    }

    public void increaseLikeCount(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.increaseLikeCount", num);
            sqlSession.commit();
        }
    }

    public void decreaseLikeCount(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.review.ReviewMapper.decreaseLikeCount", num);
            sqlSession.commit();
        }
    }

    public double getAverageRating(int movieId) {
        try (SqlSession sqlSession = factory.openSession()) {
            Double avg = sqlSession.selectOne("pack.review.ReviewMapper.getAverageRating", movieId);
            return avg != null ? avg : 0.0;
        }
    }

    public void deleteCascade(int gnum, int nested) {
        try (SqlSession sqlSession = factory.openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("gnum", gnum);
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
    
    public int getLikeCount(int num) {
        try (SqlSession sqlSession = factory.openSession()) {
            Integer count = sqlSession.selectOne("pack.review.ReviewMapper.getLikeCount", num);
            return count != null ? count : 0;
        }
    }
}
