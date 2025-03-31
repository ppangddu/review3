package pack.movie;

import pack.review.ReviewDto;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class MovieManager {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private DataSource ds;

    private int recTot;
    private int pageList = 10;
    private int pageTot;

    public MovieManager() {
        try {
            Context context = new InitialContext();
            ds = (DataSource) context.lookup("java:comp/env/jdbc_maria");
        } catch (Exception e) {
            System.out.println("MovieManager DB 연결 실패: " + e.getMessage());
        }
    }
    public int getTotalRecordCount() {
        return recTot;
    }

    public ArrayList<MovieDto> getDataAll(int page, String searchType, String searchWord) {
        ArrayList<MovieDto> list = new ArrayList<>();
        String sql = "SELECT * FROM movie";

        try {
            conn = ds.getConnection();

            if (searchWord == null || searchWord.trim().isEmpty()) {
                sql += " ORDER BY id DESC";
                pstmt = conn.prepareStatement(sql);
            } else {
                sql += " WHERE " + searchType + " LIKE ? ORDER BY id DESC";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + searchWord + "%");
            }

            rs = pstmt.executeQuery();

            for (int i = 0; i < (page - 1) * pageList; i++) {
                rs.next();
            }

            int k = 0;
            while (rs.next() && k < pageList) {
                MovieDto dto = new MovieDto();
                dto.setId(rs.getInt("id"));
                dto.setTitle(rs.getString("title"));
                dto.setDescription(rs.getString("description"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setReleaseDate(rs.getString("release_date"));
                dto.setActorName(rs.getString("actor_name"));
                dto.setGenre(rs.getString("genre"));
                list.add(dto);
                k++;
            }
        } catch (Exception e) {
            System.out.println("getDataAll (Movie) err : " + e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e2) {
            }
        }
        return list;
    }

    public ReviewDto getData(String num) {
        String sql = "select * from review where num=?";
        ReviewDto dto = null;
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, num);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new ReviewDto();
                    dto.setNum(rs.getInt("num"));
                    dto.setCont(rs.getString("cont"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setLikeCount(rs.getInt("like_count"));
                    dto.setUserId(rs.getString("user_id"));
                }
            }
        } catch (Exception e) {
            System.out.println("getData err : " + e);
        }
        return dto;
    }

    // 영화 등록
    public void saveMovie(MovieBean bean) {
        String sql = "INSERT INTO movie (title, description, image_url, release_date, actor_name, genre) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bean.getTitle());
            pstmt.setString(2, bean.getDescription());
            pstmt.setString(3, bean.getImageUrl());
            pstmt.setString(4, bean.getReleaseDate());
            pstmt.setString(5, bean.getActorName());
            pstmt.setString(6, bean.getGenre());

            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("saveMovie err : " + e);
        }
    }

    public int getPageSu() {
        pageTot = recTot / pageList;
        if (recTot % pageList > 0) pageTot++;
        return pageTot;
    }

    public void totalList() {
        String sql = "select count(*) from review where num=gnum";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            recTot = rs.getInt(1);
        } catch (Exception e) {
            System.out.println("totalList err : " + e);
        }
    }



    // 영화 전체 리스트
    public ArrayList<MovieDto> getAllMovies() {
        ArrayList<MovieDto> list = new ArrayList<>();
        String sql = "SELECT * FROM movie ORDER BY id DESC";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MovieDto dto = new MovieDto();
                dto.setId(rs.getInt("id"));
                dto.setTitle(rs.getString("title"));
                dto.setDescription(rs.getString("description"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setReleaseDate(rs.getString("release_date"));
                dto.setActorName(rs.getString("actor_name"));
                list.add(dto);
            }
        } catch (Exception e) {
            System.out.println("getAllMovies err : " + e);
        }
        return list;
    }

    // 특정 영화 정보
    public MovieDto getMovie(int id) {
        String sql = "SELECT * FROM movie WHERE id=?";
        MovieDto dto = null;
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new MovieDto();
                dto.setId(rs.getInt("id"));
                dto.setTitle(rs.getString("title"));
                dto.setDescription(rs.getString("description"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setReleaseDate(rs.getString("release_date"));
                dto.setActorName(rs.getString("actor_name"));
            }

            rs.close();
        } catch (Exception e) {
            System.out.println("getMovie err : " + e);
        }
        return dto;
    }

    // 영화 삭제
    public void deleteMovie(int id) {
        String sql = "DELETE FROM movie WHERE id=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("deleteMovie err : " + e);
        }
    }
}
