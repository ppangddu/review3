package pack.review;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewManager {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private DataSource ds;

    private int recTot;
    private int pageList = 10;
    private int pageTot;

    public ReviewManager() {
        try {
            Context context = new InitialContext();
            ds = (DataSource) context.lookup("java:comp/env/jdbc_maria");
        } catch (Exception e) {
            System.out.println("Driver 로딩 실패 : " + e.getMessage());
        }
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
                    // 필요하면 nickname도 조인해서 추가
                }
            }
        } catch (Exception e) {
            System.out.println("getData err : " + e);
        }
        return dto;
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

    public int getPageSu() {
        pageTot = recTot / pageList;
        if (recTot % pageList > 0) pageTot++;
        return pageTot;
    }

    // 리뷰 목록 가져오기 - 영화 기반으로 변경 예정
    public ArrayList<ReviewDto> getReviewsByMovieId(int movieId) {
        ArrayList<ReviewDto> list = new ArrayList<>();
        String sql = "SELECT r.*, u.nickname FROM review r JOIN user u ON r.user_id = u.id WHERE r.movie_id = ? AND r.nested = 0 ORDER BY r.num DESC";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setNum(rs.getInt("num"));
                dto.setCont(rs.getString("cont"));
                dto.setCdate(rs.getString("cdate"));
                dto.setRating(rs.getInt("rating"));
                dto.setLikeCount(rs.getInt("like_count"));
                dto.setUserId(rs.getString("user_id"));
                dto.setNickname(rs.getString("nickname"));
                dto.setGnum(rs.getInt("gnum"));
                dto.setOnum(rs.getInt("onum"));
                dto.setNested(rs.getInt("nested"));
                list.add(dto);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("getReviewsByMovieId err : " + e);
        }

        return list;
    }

    // 대댓글 포함 전체 댓글 가져오기
    public ArrayList<ReviewDto> getComments(int gnum) {
        ArrayList<ReviewDto> all = new ArrayList<>();
        ArrayList<List<ReviewDto>> grouped = new ArrayList<>();

        String sql = "SELECT r.*, u.nickname FROM review r JOIN user u ON r.user_id = u.id WHERE r.gnum=? ORDER BY r.onum ASC";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gnum);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ReviewDto dto = new ReviewDto();
                dto.setNum(rs.getInt("num"));
                dto.setCont(rs.getString("cont"));
                dto.setCdate(rs.getString("cdate"));
                dto.setNested(rs.getInt("nested"));
                dto.setRating(rs.getInt("rating"));
                dto.setLikeCount(rs.getInt("like_count"));
                dto.setOnum(rs.getInt("onum"));
                dto.setUserId(rs.getString("user_id"));
                dto.setNickname(rs.getString("nickname"));
                all.add(dto);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("getComments err : " + e);
        }

        for (int i = 0; i < all.size(); i++) {
            ReviewDto dto = all.get(i);
            if (dto.getNested() == 1) {
                List<ReviewDto> group = new ArrayList<>();
                group.add(dto);
                for (int j = i + 1; j < all.size(); j++) {
                    ReviewDto next = all.get(j);
                    if (next.getNested() == 1) break;
                    group.add(next);
                }
                List<ReviewDto> replies = group.stream()
                        .filter(r -> r.getNested() > 1)
                        .sorted((r1, r2) -> {
                            int nestedCmp = Integer.compare(r1.getNested(), r2.getNested());
                            if (nestedCmp == 0) return Integer.compare(r2.getLikeCount(), r1.getLikeCount());
                            return nestedCmp;
                        })
                        .collect(Collectors.toList());

                List<ReviewDto> combined = new ArrayList<>();
                combined.add(group.get(0));
                combined.addAll(replies);
                grouped.add(combined);
            }
        }

        grouped.sort((g1, g2) -> Integer.compare(g2.get(0).getLikeCount(), g1.get(0).getLikeCount()));

        ArrayList<ReviewDto> result = new ArrayList<>();
        for (List<ReviewDto> group : grouped) {
            result.addAll(group);
        }

        return result;
    }

    public void increaseLikeCount(int num) {
        String sql = "UPDATE review SET like_count = like_count + 1 WHERE num=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("increaseLikeCount err : " + e);
        }
    }

    public double getAverageRating(int movieId) {
        double avg = 0;
        String sql = "SELECT avg(rating) FROM review WHERE movie_id = ? AND nested = 1 AND rating > 0";
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
}
