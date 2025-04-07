package pack.movie;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pack.mybatis.SqlMapConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDao {
    private SqlSessionFactory factory = SqlMapConfig.getSqlSession();
    private final int pageList = 5;

    // 전체 목록 + 검색 + 페이징
    public List<MovieDto> getDataAll(int page, String searchWord) {
        try (SqlSession sqlSession = factory.openSession()) {
            int start = (page - 1) * pageList;
            HashMap<String, Object> params = new HashMap<>();
            params.put("start", start);
            params.put("limit", pageList);
            params.put("searchWord", searchWord);
            return sqlSession.selectList("pack.movie.MovieMapper.getPagedMovies", params);
        }
    }

    // 총 레코드 수
    public int getTotalCount(String searchWord) {
        try (SqlSession sqlSession = factory.openSession()) {
            return sqlSession.selectOne("pack.movie.MovieMapper.countMovies", searchWord);
        }
    }

    public int getPageSu(int totalRecord) {
        return totalRecord / pageList + (totalRecord % pageList > 0 ? 1 : 0);
    }

    public void insertMovie(MovieBean bean) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.insert("pack.movie.MovieMapper.insertMovie", bean);
            sqlSession.commit();
        }
    }

    public MovieDto getMovieById(int id) {
        try (SqlSession sqlSession = factory.openSession()) {
            return sqlSession.selectOne("pack.movie.MovieMapper.getMovieById", id);
        }
    }

    public void updateMovie(MovieDto dto) {
        try (SqlSession sqlSession = factory.openSession()) {
            sqlSession.update("pack.movie.MovieMapper.updateMovie", dto);
            sqlSession.commit();
        }
    }

    public void deleteMovie(int id) {
        try (SqlSession sqlSession = factory.openSession()) {
            // 리뷰 먼저 삭제
            sqlSession.delete("pack.movie.MovieMapper.deleteReviewsByMovieId", id);
            // 그 다음 영화 삭제
            sqlSession.delete("pack.movie.MovieMapper.deleteMovie", id);
            sqlSession.commit();
        }
    }

    
    public int getMovieCount(String searchWord) {
        try (SqlSession sqlSession = factory.openSession()) {
            return sqlSession.selectOne("pack.movie.MovieMapper.countMovies", searchWord);
        }
    }

    public List<MovieDto> getPagedMovies(int start, int limit, String searchWord) {
        try (SqlSession sqlSession = factory.openSession()) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", start);
            map.put("limit", limit);
            map.put("searchWord", searchWord);
            return sqlSession.selectList("pack.movie.MovieMapper.getPagedMovies", map);
        }
    }

}
