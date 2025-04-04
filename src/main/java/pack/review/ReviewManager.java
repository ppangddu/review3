package pack.review;

import pack.movie.MovieDto;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ReviewManager {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private DataSource ds;

    public ReviewManager() {
        try {
            Context context = new InitialContext();
            ds = (DataSource) context.lookup("java:comp/env/jdbc_maria");
        } catch (Exception e) {
            System.out.println("Driver 로딩 실패 : " + e.getMessage());
        }
    }

    public int currentMaxNum() {
        String sql = "select max(num) from review";
        int num = 0;
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) num = rs.getInt(1);
        } catch (Exception e) {
            System.out.println("currentMaxNum err : " + e);
        }
        return num;
    }

    public ReviewDto getReplyData(int num) {
        String sql = "SELECT movie_id, gnum, onum, nested FROM review WHERE num=?";
        ReviewDto dto = null;

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, num);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new ReviewDto();
                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setGnum(rs.getInt("gnum"));
                    dto.setOnum(rs.getInt("onum"));
                    dto.setNested(rs.getInt("nested"));
                }
            }

        } catch (Exception e) {
            System.out.println("getReplyData err : " + e);
        }
        return dto;
    }

    public void updateOnum(int gnum, int onum) {
        String sql = "update review set onum = onum + 1 where onum >= ? and gnum = ?";
        try {
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, onum);
            pstmt.setInt(2, gnum);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("updateOnum err : " + e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e2) {
            }
        }
    }

    public void saveReplyData(ReviewBean bean) {
        String sql = "INSERT INTO review (movie_id, user_id, cdate, gnum, onum, nested, rating, like_count, cont) "
                + "VALUES (?, ?, now(), ?, ?, ?, ?, 0, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bean.getMovieId());
            pstmt.setString(2, bean.getUserId());
            pstmt.setInt(3, bean.getGnum());
            pstmt.setInt(4, bean.getOnum());
            pstmt.setInt(5, bean.getNested());
            pstmt.setInt(6, bean.getRating());
            pstmt.setString(7, bean.getCont());

            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("saveReplyData err : " + e);
        }
    }

    public void increaseLikeCount(int num) {
        String sql = "update review set like_count = like_count + 1 where num=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("increaseLikeCount err : " + e);
        }
    }

    public int getLikeCount(int num) {
        String sql = "SELECT like_count FROM review WHERE num=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("like_count");
            }
        } catch (Exception e) {
            System.out.println("getLikeCount err : " + e);
        }
        return 0;
    }

    public double getAverageRating(int movieId) {
        double avg = 0;
        String sql = "SELECT AVG(rating) FROM review WHERE movie_id=? AND nested=1 AND rating > 0";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                avg = rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("getAverageRating err : " + e);
        }
        return avg;
    }

    public MovieDto getMovieById(int movieId) {
        MovieDto movie = null;
        String sql = "SELECT * FROM movie WHERE id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = new MovieDto();
                    movie.setId(rs.getInt("id"));
                    movie.setTitle(rs.getString("title"));
                    movie.setGenre(rs.getString("genre"));
                    movie.setDescription(rs.getString("description"));
                    movie.setImageUrl(rs.getString("image_url"));
                    movie.setReleaseDate(rs.getString("release_date"));
                    movie.setActorName(rs.getString("actor_name"));
                }
            }
        } catch (Exception e) {
            System.out.println("getMovieById err: " + e);
        }
        return movie;
    }

    public ArrayList<ReviewDto> getReviewsByMovieId(int movieId) {
        ArrayList<ReviewDto> all = new ArrayList<>();
        Map<Integer, List<ReviewDto>> groupMap = new LinkedHashMap<>();

        String sql = "SELECT r.*, u.nickname " +
                "FROM review r JOIN user u ON r.user_id = u.id " +
                "WHERE r.movie_id=? ORDER BY r.gnum ASC, r.onum ASC";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setNum(rs.getInt("num"));
                dto.setCont(rs.getString("cont"));
                dto.setCdate(rs.getTimestamp("cdate"));
                dto.setNested(rs.getInt("nested"));
                dto.setRating(rs.getInt("rating"));
                dto.setLikeCount(rs.getInt("like_count"));
                dto.setOnum(rs.getInt("onum"));
                dto.setGnum(rs.getInt("gnum"));
                dto.setUserId(rs.getString("user_id"));
                dto.setNickname(rs.getString("nickname"));
                all.add(dto);
            }
            rs.close();

        } catch (Exception e) {
            System.out.println("getCommentsByMovieId err : " + e);
        }

        for (ReviewDto dto : all) {
            groupMap.computeIfAbsent(dto.getGnum(), k -> new ArrayList<>()).add(dto);
        }

        List<List<ReviewDto>> grouped = new ArrayList<>(groupMap.values());

        grouped.sort((g1, g2) -> {
            int likeCompare = Integer.compare(g2.get(0).getLikeCount(), g1.get(0).getLikeCount());
            if (likeCompare == 0) {
                return g2.get(0).getCdate().compareTo(g1.get(0).getCdate());
            }
            return likeCompare;
        });

        ArrayList<ReviewDto> result = new ArrayList<>();
        for (List<ReviewDto> group : grouped) {
            result.addAll(group);
        }

        return result;
    }

    public void decreaseLikeCount(int num) {
        String sql = "UPDATE review SET like_count = like_count - 1 WHERE num=? AND like_count > 0";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("decreaseLikeCount err : " + e);
        }
    }
}
